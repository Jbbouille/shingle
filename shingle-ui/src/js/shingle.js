'use strict';

var angular = require('angular'),
  _ = require('lodash'),
  $ = require('jquery'),
  Search = require('./search.js');

let model = require('../root/model.html');

angular.module('shingle', []).config(["$locationProvider", function ($locationProvider) {
  $locationProvider.html5Mode({
    enabled: true,
    requireBase: false
  });
}]);

angular.module('shingle').controller('ShingleController', ["$scope", "$http", "$sce", "$location", "$timeout", function ($scope, $http, $sce, $location, $timeout) {
  var requestedResourceId = getQueryVariable('id');

  $scope.embedded = getQueryVariable('embedded');
  $scope.sectionHidden = {};
  $scope.resourcesFiltered = {};

  $scope.selected = {};
  $scope.inputTypes = [
    {type: 'path', label: 'Path parameters'},
    {type: 'query', label: 'Query parameters'},
    {type: 'header', label: 'Header parameters'},
    {type: 'cookie', label: 'Cookie parameters'},
    {type: 'form', label: 'Form parameters'},
    {type: 'body', label: 'Body'}];

  $scope.hasInputs = function () {
    return $scope.selected.resource && _.size($scope.selected.resource.inputs) > 0;
  };

  $scope.filterResourceWithReference = function (resource) {
    if (!$scope.search || $scope.search.replace(/\s+/g, '') === '') {
      $scope.resourcesFiltered = {};
      return true;
    }

    return Search.matches(resource, $scope.search);
  };

  $scope.selectResource = function (r) {
    $scope.selected.resource = r;
    $scope.resourceExtensions = getResourceExtensions();
    var id = r.verb.toLowerCase() + '_' + r.path;
    if (r.id) {
      id += '_' + r.id;
    }
    parent.postMessage({
      op: 'hash',
      id: id
    }, '*');

    $location.search('id', id);
  };

  function getQueryVariable(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split('&');
    for (var i = 0; i < vars.length; i++) {
      var pair = vars[i].split('=');
      if (decodeURIComponent(pair[0]) == variable) {
        return decodeURIComponent(pair[1]);
      }
    }
  }

  var doc = getQueryVariable('doc');
  if (!doc) {
    return;
  }
  $scope.busy = true;
  $http.get(doc).success(function (doc) {
    var rawResources = doc.resources;

    var tags = _(rawResources).map('tags').flatten().uniq().filter(_.identity).sort().push('others').value();
    var resources = {};
    var expandedTag;

    _.forEach(rawResources, function (r) {
      if (r.summary) {
        r.rawSummary = r.summary;
        r.summary = $sce.trustAsHtml(r.summary);
      }
      if (r.description) {
        r.description = $sce.trustAsHtml(r.description);
      }
      _.forEach(r.outputs, function (o) {
        o.description = $sce.trustAsHtml(o.description);

        if (o.status === 0) {
          o.status = "Unknown";
          o.description = $sce.trustAsHtml("Default Response.")
        }
      });

      var thisTags = r.tags || ['others'];
      thisTags.forEach(function (t) {
        if (!resources[t]) {
          resources[t] = [];
        }

        resources[t].push(r);
      });

      r.inputs = _(r.inputs).groupBy('type').value();

      var resourceId = r.verb.toLowerCase() + '_' + r.path;
      if (r.id) {
        resourceId += '_' + r.id;
      }
      r.safeId = resourceId.replace(/[\/\{}]/g, '_');
      if (resourceId === requestedResourceId && !$scope.selected.resource) {
        $scope.selectResource(r);
        expandedTag = thisTags[0];

        $timeout(function () {
          var to = $('#' + r.safeId),
            container = $('nav');
          container[0].scrollTop = to.offset().top - container.offset().top + container.scrollTop() - 60;
        });
      }
    });

    _.forEach(tags, function (t) {
      resources[t] = _.sortBy(resources[t], 'path', function (r) {
        return {
          GET: 1,
          POST: 2,
          PUT: 3,
          DELETE: 4
        }[r.verb] || 100;
      });
    });

    Search.initializeSearchIndex(resources);

    $scope.resources = resources;

    $scope.tags = _.map(tags, function (t, idx) {
      return {
        name: t,
        expanded: expandedTag ? t === expandedTag : idx === 0
      };
    });

    $scope.busy = false;
  }).error(function (err) {
    $scope.busy = false;
    $scope.error = err;
  });


  var extensions = {};

  var custo = getQueryVariable('cust');
  if (custo) {
    $http.get(custo).success(function (c) {
      extensions = c;
    })
  }

  function getResourceExtensions() {
    var resource = $scope.selected.resource;
    if (resource && resource.extensions) {
      var sections = [];
      _.forEach(extensions.resourceExtensions, function (ext) {
        var section = {
          name: ext.name
        };

        var resAnnot = _.find(resource.extensions, {name: ext.extensionName}) ||
          _.find(resource.extensions, {annotationName: ext.extensionName});

        if (resAnnot) {
          section.description = resAnnot.description;
          section.args = _(ext.args).map(function (arg) {
            if (!resAnnot.values) {
              return null;
            }
            var value = resAnnot.values[arg.key];
            if (!value) {
              return null;
            }
            return {
              label: arg.label,
              value: value
            }
          }).filter(_.identity).value();
          sections.push(section);
        }
      });
      return sections;
    }
  };


}]);

angular.module('shingle').filter('objectName', function () {
  return function (id) {
    if (!id) {
      return 'Object';
    }
    return _.last(id.split(':'));
  }
});

angular.module('shingle').directive('shingleToggle', function () {
  return {
    restrict: 'A',
    scope: {
      hidden: '=shingleToggle'
    },
    link: function ($scope, elem) {
      function updateClass() {
        elem.toggleClass('expanded', !$scope.hidden).toggleClass('collapsed', $scope.hidden);
      }

      updateClass();

      elem.on('click', function (ev) {
        $scope.hidden = !$scope.hidden;
        updateClass();
        ev.preventDefault();
        $scope.$apply();
      });
    }
  }
});

angular.module('shingle').directive('shingleModel', ["$sce", function ($sce) {
  return {
    restrict: 'A',
    template: model,
    scope: {
      model: '&shingleModel',
      modelAllowedValues: '&allowedValues',
    },
    controller: ["$scope", function ($scope) {
      $scope.kindOf = function (schema) {
        if (!schema) {
          return 'simple';
        }
        if (schema.enum) {
          return 'enum'
        }
        if (schema.type == 'object' || schema.type === 'array') {
          return schema.type;
        }
        return 'simple';
      };


      var m = $scope.model();
      if (m) {
        $scope.schema = m.schema;
      }

      $scope.allowedValues = function (schema) {
        if (!schema) {
          return;
        }
        if (m && schema === m.schema) {
          return $scope.modelAllowedValues();
        }
      };


      $scope.toggleExample = function () {
        $scope.showExample = !$scope.showExample;
      };

      var generators = {
        string: [userExample, enumExample, 'a string value'],
        number: [toNumber(userExample), 42],
        integer: [toNumber(userExample), 42],
        boolean: [toBool(userExample), boolExample]
      };

      function generateExample(schema) {
        if (!schema) {
          return;
        }
        if (schema.type === 'object') {
          var ores = {};
          _.each(schema.properties, function (v, k) {
            ores[k] = generateExample(v);
          });
          return ores;
        } else if (schema.type === 'array') {
          return [generateExample(schema.items)];
        } else {
          return firstExampleOf(schema, generators[schema.type]);
        }
      }

      if (m && (m.schema.type === 'object' || m.schema.type === 'array')) {
        var example = generateExample(m.schema);
        var json = JSON.stringify(example, null, 4);
        json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
        json = json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
          var cls = 'number';
          if (/^"/.test(match)) {
            if (/:$/.test(match)) {
              cls = 'key';
            } else {
              cls = 'string';
            }
          } else if (/true|false/.test(match)) {
            cls = 'boolean';
          } else if (/null/.test(match)) {
            cls = 'null';
          }
          return '<span class="' + cls + '">' + match + '</span>';
        });
        $scope.example = $sce.trustAsHtml(json);
      }

      function firstExampleOf(schema, generators) {
        generators = generators || [];
        for (var i = 0; i < generators.length; i++) {
          var gen = generators[i];
          if (_.isFunction(gen)) {
            gen = gen(schema);
          }
          if (!_.isUndefined(gen)) {
            return gen;
          }
        }
      }

      function userExample(schema) {
        if (schema.extensions && schema.extensions.example) {
          return schema.extensions.example;
        }
      }

      function enumExample(schema) {
        if (schema.enum) {
          var idx = Math.floor(Math.random() * schema.enum.length);
          return schema.enum[idx]
        }
      }

      function boolExample() {
        return Math.random() > 0.5;
      }

      function toNumber(f) {
        return function (schema) {
          var raw = f(schema);
          return Number(raw);
        }
      }

      function toBool(f) {
        return function (schema) {
          var raw = f(schema);
          return Boolean(raw);
        }
      }
    }]
  };
}]);