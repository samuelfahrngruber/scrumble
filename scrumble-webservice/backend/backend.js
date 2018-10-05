var express = require('express');
var app = express();
var bodyParser = require('body-parser');

// enable bodyparser, frontend and logger
app.use(bodyParser.json());
app.use(function(req, res, next) {
    console.log(req.method + ' ' + req.url);
    next();
});

// SET UP ENVIRONMENT
// =============================================================================
var port = 8080;
process.env.HOST = process.env.HOST || "http://localhost:" + port;

var defaultRouter = require("./defaultRouter");
app.use('/api', defaultRouter);

app.listen(port);
console.log('Server started on port ' + port);