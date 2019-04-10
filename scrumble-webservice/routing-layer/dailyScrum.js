// packages
let express = require('express'),
    error = require('./misc/error'),
    dailyScrumSchema = require('../data-layer/schemas/dailyScrumSchema'),
    dailyDailyscrumJob = require('./misc/dailyDailyscrumJob'),
    changeJob = require('./misc/changeJob'),
    mongoConnection = require('../data-layer/mongoDataAccess'),
    mongoose = require('mongoose'),
    router = express.Router({
        mergeParams: true
    });

router.get('/', (req, res, next) => {
    if (!req.params.projectid && !req.params.taskid && !req.params.userid && !req.params.sprintid) {
        dailyScrumSchema.find(function (err, dailyscrums) {
            if (err) return error.respondWith(res, err);
            sendDailyScrums(dailyscrums, res);
        })
    } else if (req.params.projectid && !req.params.taskid && !req.params.userid && !req.params.sprintid) {
        if (!isNaN(req.params.projectid)) {
            if (req.query.sprint) {
                dailyDailyscrumJob.createDailyScrums(Number(req.params.projectid), Number(req.query.sprint), () => {
                    saveDailyScrums({
                        "project": req.params.projectid
                    }, res);
                });
            } else
            dailyDailyscrumJob.createDailyScrums(Number(req.params.projectid), null, () => {
                saveDailyScrums({
                    "project": req.params.projectid
                }, res);
            });
        } else
            next();
    } else if (!req.params.projectid && req.params.taskid && !req.params.userid && !req.params.sprintid) {
        if (!isNaN(req.params.taskid)) {
            saveDailyScrums({
                "task.id": req.params.taskid
            }, res);
        } else
            next();
    } else if (!req.params.projectid && !req.params.taskid && req.params.userid && !req.params.sprintid) {
        if (!isNaN(req.params.userid)) {
            saveDailyScrums({
                "user.id": req.params.userid
            }, res);
        } else
            next();
    } else if (!req.params.projectid && !req.params.taskid && !req.params.userid && req.params.sprintid) {
        if (!isNaN(req.params.sprintid)) {
            saveDailyScrums({
                "sprint": req.params.sprintid
            }, res);
        } else
            next();
    }
});

router.post('/', (req, res, next) => {
    if (req.params.taskid || req.params.projectid || req.params.userid || req.params.sprintid)
        next();
    else {
        var object = {
            _id: new mongoose.Types.ObjectId(),
            user: req.body.user,
            date: req.body.date,
            description: req.body.description,
            project: req.body.project,
            sprint: req.body.sprint,
            task: req.body.task
        };
        var dailyScrum = new dailyScrumSchema(object);

        dailyScrum.save(function (err) {
            if (err) throw err;
            changeJob.change(req.body.projectid, "DAILYSCRUM", "POST", object)
            res.status(201).json({
                id: dailyScrum._id
            });
        });
    }
});
router.put('/:dailyScrumId', (req, res) => {
    dailyScrumSchema.findById(req.params.dailyScrumId, function (err, dailyScrum) {
        if (err) return error.respondWith(res, err);

        var object = {
            _id: new mongoose.Types.ObjectId(req.params.dailyScrumId),
            user: req.body.user,
            date: req.body.date,
            description: req.body.description,
            project: req.body.project,
            sprint: req.body.sprint,
            task: req.body.task
        };
        dailyScrum.set(object);
        dailyScrum.save(function (err) {
            if (err) return error.respondWith(res, err);
            changeJob.change(req.body.project, "DAILYSCRUM", "PUT", object);
            res.status(200).end();
        });
    });
});

function saveDailyScrums(object, res) {
    dailyScrumSchema.find(object, function (err, dailyscrums) {
        if (err) 
            error.respondWith(res, err);
        else
            sendDailyScrums(dailyscrums, res);
    });
}

function sendDailyScrums(dailyScrums, res) {
    if (dailyScrums == null || dailyScrums.length < 1)
        error.respondWith(res, new Error("NO CONTENT"));
    else
        res.json(dailyScrums);
}


module.exports = router;