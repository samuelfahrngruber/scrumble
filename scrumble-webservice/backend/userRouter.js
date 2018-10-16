// speakers.js
// call packages
var express = require('express');
var bodyParser = require('body-parser');
var scrumbleManager = require('./scrumblemanager');
var responder = require("./misc/responder");

// create new router
var userRouter = express.Router();

//add Routes
userRouter.route('/login')
    .post((req, res) => {
        scrumbleManager.login(req.body.name, responder.bind(undefined, req, res));
    });

userRouter.route('/register')
    .post((req, res) => {
        scrumbleManager.register(req.params.id, responder.bind(undefined, req, res));
    });

userRouter.route('/:userid/projects')
    .get((req, res) => {
        scrumbleManager.getProjectsByUser(req.params.id, responder.bind(undefined, req, res));
    });



module.exports = userRouter;