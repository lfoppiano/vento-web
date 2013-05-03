define([
    'jquery',
    'underscore',
    'backbone',
    'models/SearchModel',
    'models/TweetModel',
    'views/TweetView',
    'text!templates/search/searchFormTemplate.html'
], function ($, _, Backbone, SearchModel, TweetModel, TweetView, SearchFormTemplate) {

    var SearchView = Backbone.View.extend({
        el: $("#search_container"),

        initialize: function () {
            this.render();
        },
        render: function () {
            var compiledTemplate = _.template(SearchFormTemplate, {});
            this.$el.html(compiledTemplate);
        },
        events: {
            'keypress input[type=text]': 'doSearch'
        },
        doSearch: function (event) {
            if (event.keyCode != 13) return;
            console.log("Starting search of " + $("#search_input").val());

            var searchHistory = new SearchModel({
                value: $("#search_input").val(),
                timestamp: new Date().getTime()
            });

            searchHistory.save()

            //event.preventDefault();
            //var self = this;

            $.getJSON(
                'http://localhost:8889/classification?lang=en&search=' + $("#search_input").val(),
                function (data) {
                    $("#tweets li").fadeOut();
                    for (var i in data) {
                        var tweet = new TweetModel(data[i]);
                        var tweetView = new TweetView({model: tweet});
                        tweetView.render();
                    }
                });
        }
    });

    return SearchView;

});