require.config({
    shim: {
        underscore: {
            exports: '_'
        },
        backbone: {
            deps: ['underscore', 'jquery'],
            exports: 'Backbone'
        }
    },
    paths: {
        jquery: 'libs/jquery/jquery.min',
        underscore: 'libs/underscore/underscore',
        backbone: 'libs/backbone/backbone',
        bootstrap: 'libs/bootstrap/bootstrap.min',
        templates: '../templates'
    } });

require([
    'app'

], function (App) {
    App.initialize();
});