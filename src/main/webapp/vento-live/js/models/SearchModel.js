define([
    'underscore',
    'backbone'
], function (_, Backbone) {

    var SearchModel = Backbone.Model.extend({
        urlRoot: "http://localhost:8889/searchHistory",
        defaults: {
            value: '',
            timestamp: ''
        },
        initialize: function () {
            //this.bind("error", function (model, error) {});
        }/*,
        validate: function (attributes) {
            if (attributes.value == null || attributes.timestamp == null) {
                return "Wrong data";
            }
        }*/
    });

    return SearchModel;
});