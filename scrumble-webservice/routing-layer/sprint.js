// packages
let express = require('express'),
    oracleConnection = require('../data-layer/oracleDataAccess'),
    classParser = require('../data-layer/classParser'),
    classes = require('../data-layer/classes'),
    error = require('./misc/error'),
    dailyDailyscrumJob = require('./misc/dailyDailyscrumJob'),
    changeJob = require('./misc/changeJob'),
    router = express.Router({
        mergeParams: true
    });
host = process.env.HOST;

router.get('/', (req, res) => {
    let query = "SELECT * from sc_sprint",
        param = [];

    if (req.params.projectid) {
        if (isNaN(req.params.projectid))
            next();
        query = "select * from sc_sprint where idproject = :projectid",
            param = [req.params.projectid];
    }

    oracleConnection.execute(query, param)
        .then((result) => {
            if (result.rows.length < 1)
                throw new Error("NO CONTENT");
            res.json(classParser(result.rows, classes.Sprint))
        })
        .catch((err) => error.respondWith(res, err));
});
router.get('/:sprintid', (req, res, next) => {
    if (!isNaN(req.params.sprintid)) {
        let query = "SELECT * from sc_sprint where id = :sprintid",
            param = [req.params.sprintid];
        if (req.params.projectid) {
            if (isNaN(req.params.projectid))
                next();
            query = "select * from sc_sprint where idproject = :projectid and id = :sprintid";
            param = [req.params.projectid, req.params.sprintid];
        }

        oracleConnection.execute(query, param)
            .then((result) => res.json(classParser(result.rows, classes.Sprint)[0]))
            .catch((err) => error.respondWith(res, err));
    } else {
        next();
    }
});
router.post('/', (req, res, next) => {
    if (req.params.sprintid || req.params.projectid)
        next();
    else {
        let startdate = new Date(req.body.startdate);
        let startdatestr = "" + startdate.getFullYear() + "-" + (startdate.getMonth() + 1) + "-" + startdate.getDate();

        let deadline = new Date(req.body.deadline);
        let deadlinestr = "" + deadline.getFullYear() + "-" + (deadline.getMonth() + 1) + "-" + deadline.getDate();
        let query = "insert into sc_sprint values(null, :sprintnumber, to_date(:startdate, 'YYYY-MM-DD'), to_date(:deadline, 'YYYY-MM-DD'), :project)",
            param = [req.body["number"], startdatestr, deadlinestr, req.body.project],
            object = getSprintObject(req, -1);

        oracleConnection.execute(query, param)
            .then((result) => {
                query = "select max(id) from sc_sprint order by id",
                    param = [];

                oracleConnection.execute(query, param)
                    .then((result) => {
                        let id = result.rows[0][0];
                        object.id = id;
                        changeJob.change(req.body.project, "SPRINT", "POST", object);
                        dailyDailyscrumJob.createDailyScrums(object.project, object.id, () => {
                            res.status(201).json({ id: id });
                        }, new Date(object.startdate), new Date(object.deadline));                       
                    })
                    .catch((err) => res.status(404).json({
                        message: err.message,
                        details: err
                    }));
            })
            .catch((err) => error.respondWith(res, err));
    }
});
router.put('/:sprintid', (req, res, next) => {
    if (isNaN(req.params.sprintid) || req.params.projectid) {
        next();
    } else {
        let startdate = new Date(req.body.startdate);
        let startdatestr = "" + startdate.getFullYear() + "-" + (startdate.getMonth() + 1) + "-" + startdate.getDate();

        let deadline = new Date(req.body.deadline);
        let deadlinestr = "" + deadline.getFullYear() + "-" + (deadline.getMonth() + 1) + "-" + deadline.getDate();
        let query = "update sc_sprint set sprintnumber = :sprintnumber, startdate = to_date(:startdate, 'YYYY-MM-DD'), deadline = to_date(:deadline, 'YYYY-MM-DD'), idproject = :project where id = :sprintid",
            param = [req.body["number"], startdatestr, deadlinestr, req.body.project, req.params.sprintid]
        object = getSprintObject(req, req.params.sprintid);

        oracleConnection.execute(query, param)
            .then((result) => {
                changeJob.change(req.body.project, "SPRINT", "PUT", object);
                dailyDailyscrumJob.createDailyScrums(object.project, object.id, () => {
                    res.status(200).end();
                }, new Date(object.startdate), new Date(object.deadline));
            })
            .catch((err) => error.respondWith(res, err));
    }
});

function getSprintObject(req, id) {
    return {
        id: Number(id),
        number: Number(req.body["number"]),
        startdate: new Date(req.body.startdate).toISOString(),
        deadline: new Date(req.body.deadline).toISOString(),
        project: Number(req.body.project)
    };
}


module.exports = router;