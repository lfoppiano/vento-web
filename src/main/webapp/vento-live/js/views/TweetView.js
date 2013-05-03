define([
    'jquery',
    'underscore',
    'backbone',
    'models/TweetModel',
    'text!templates/tweet/tweetTemplate.html'
], function ($, _, Backbone, TweetModel, TweetTemplate) {


    var TweetView = Backbone.View.extend({
        el : $("#tweets"),

        render: function () {
            var compiledTemplate = _.template(TweetTemplate, this.model.toJSON());

            $(this.el).append(compiledTemplate)
        }
    });

    return TweetView;

});