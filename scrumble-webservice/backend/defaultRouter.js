var express = require('express');
var defaultRouter = express.Router();
var host = process.env.HOST;

// configure defaultRouter
defaultRouter.get('/', function (req, res) {
    res.json({
        message: 'api-session-rater-kandut-webhofer',
        speakers: host + '/api/speakers',
        sessions: host + '/api/sessions',
        ratings: host + '/api/ratings'
    });
});

module.exports = defaultRouter;