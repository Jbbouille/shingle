'use strict';

var _ = require('lodash');

function initializeSearchIndex(resources) {
    _.forEach(resources, function (resourcesByTag) {
        _.map(resourcesByTag, function (resource) {
            resource.searchIndex = createSearchIndex(resource.verb, resource.path, resource.tags, resource.rawSummary, resource.consumes);
            delete resource.rawSummary;
            return resource;
        });
    });
}

function tokenizePath(input) {
    return input.replace(/\/+/g, ' ')
        .replace(/{+/g, ' ')
        .replace(/}+/g, ' ')
        .toLocaleLowerCase()
        .split(/\s/);
}

var stopWords = require('./stopwords.json');

function tokenizeText(input) {
    var parts = input.toLocaleLowerCase().split(/\s/);
    var args = [parts];
    args = args.concat(stopWords);
    var filtered = _.without.apply(parts, args);
    return _.uniq(filtered);
}

function createSearchIndex(verb, path, tags, summary, consumes) {
    var indexes = [];

    if (consumes && consumes.length > 0) {
        _.each(consumes, function (consume) {
            indexes = indexes.concat(tokenizePath(consume));
        });
    }

    if (summary) {
        indexes = indexes.concat(tokenizeText(summary));
    }

    if (verb) {
        indexes.push(verb.toLowerCase());
    }

    var paths;
    if (path) {
        indexes.push(path);

        paths = tokenizePath(path);

        paths = _.filter(paths, function (path) {
            return path !== "";
        });
    }

    if (path && paths.length > 0) {
        indexes = indexes.concat(paths);
    }

    if (tags && tags.length > 0) {
        var cleanedTags = _.flatten(_.map(tags, function (tag) {
            return tokenizePath(tag);
        }));
        indexes = indexes.concat(cleanedTags);
    }

    return indexes;
}

module.exports = {
    initializeSearchIndex: initializeSearchIndex,
    matches: function (resource, query) {
        var tokensToSearch = tokenizeText(query);

        var hitsCount = 0;

        _.each(tokensToSearch, function (token) {
            _.each(resource.searchIndex, function (index) {
                if (index.startsWith(token)) {
                    hitsCount++;
                    return false;
                }
            });
        });

        return hitsCount === tokensToSearch.length;
    }
};