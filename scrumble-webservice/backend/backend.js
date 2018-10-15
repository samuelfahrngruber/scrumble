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

// ROUTERS
// =============================================================================
var defaultRouter = require("./defaultRouter");
var userRouter = require("./userRouter");
var projectRouter = require("./projectRouter");

// REGISTER OUR ROUTES
// =============================================================================
app.use('/scrumble', defaultRouter);
defaultRouter.use('/user', userRouter)
defaultRouter.use('/project', projectRouter);
app.get('/', (req, res) => res.redirect("views"));

// START THE SERVER
// =============================================================================
app.listen(port);
console.log('Server started on port ' + port);