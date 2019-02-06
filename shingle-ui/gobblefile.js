'use strict';

var gobble = require('gobble'),
    ngAnnotate = require("ng-annotate");

function ngannotate(code, options) {
    var res = ngAnnotate(code, {add: true});
    return {
        code: res.src,
        map: res.map
    };
}

module.exports = gobble([
    gobble('src/root'),

    gobble('src/styles').transform('sass', {
        src: 'shingle.scss',
        dest: 'shingle.css'
    }),
    gobble('src/js')
        .transform('es6-transpiler')
        .transform(ngannotate)
        .transform('browserify', {
            entries: 'shingle.js',
            dest: 'shingle.js'
        })
        .transform('uglifyjs')
]);