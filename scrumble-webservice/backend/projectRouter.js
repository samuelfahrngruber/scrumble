// speakers.js
// call packages
var express = require('express');
var bodyParser = require('body-parser');
var scrumbleManager = require('./scrumblemanager');
var responder = require("./misc/responder");

// create new router
var projectRouter = express.Router();

//add Routes
projectRouter.route('/')
    .get((req, res) => {
        scrumbleManager.getProject(responder.bind(undefined, req, res));
    })
    .post((req, res) => {
        scrumbleManager.addProject(req.body.name, responder.bind(undefined, req, res));
    });

speakerRouter.route('/:projectid/sprint')
    .get((req, res) => {
        scrumbleManager.getSprint(req.params.id, responder.bind(undefined, req, res));
    })
    .post((req, res) => {
        scrumbleManager.addSprint(req.params.id, responder.bind(undefined, req, res));
    });

speakerRouter.route('/:projectid/user')
    .get((req, res) => {
        scrumbleManager.getUser(req.params.id, responder.bind(undefined, req, res));
    })
    .post((req, res) => {
        scrumbleManager.addUser(req.params.id, responder.bind(undefined, req, res));
    });

speakerRouter.route('/:projectid/task/:taskid')
    .put((req, res) => {
        scrumbleManager.changeTaskState(req.params.id, responder.bind(undefined, req, res));
    })

speakerRouter.route('/:projectid/backlog')
    .get((req, res) => {
        scrumbleManager.getBacklog(req.params.id, responder.bind(undefined, req, res));
    })

speakerRouter.route('/:projectid/sprint/task')
    .post((req, res) => {
        scrumbleManager.addTaskToSprint(req.params.id, responder.bind(undefined, req, res));
    })



module.exports = projectRouter;