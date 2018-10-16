var express = require('express');
var defaultRouter = express.Router();
var host = process.env.HOST;

// configure defaultRouter
defaultRouter.get('/', function (req, res) {
    res.json({
        message: 'api-scrumble-webservice',
    });
});

module.exports = defaultRouter;