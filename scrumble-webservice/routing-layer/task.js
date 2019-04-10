// packages
let express = require('express'),
    oracleConnection = require('../data-layer/oracleDataAccess'),
    classParser = require('../data-layer/classParser'),
    classes = require('../data-layer/classes'),
    error = require('./misc/error'),
    changeJob = require('./misc/changeJob'),
    router = express.Router({
        mergeParams: true
    });
host = process.env.HOST;

router.get('/', (req, res) => {
    let query = "SELECT * from sc_task",
        param = [];

    if (req.params.projectid && !req.params.sprintid) {
        if (isNaN(req.params.projectid))
            next();
        query = "select * from sc_task where idproject = :projectid";
        param = [req.params.projectid];
    } else if (req.params.projectid && req.params.sprintid) {
        if (isNaN(req.params.projectid) || isNaN(req.params.sprintid))
            next();
        if (req.query.state) {
            query = "select * from sc_task where idproject = :projectid and idsprint = :sprintid and state = :state";
            console.log(req.query.state);
            param = [req.params.projectid, req.params.sprintid, req.query.state];
        } else {
            query = "select * from sc_task where idproject = :projectid and idsprint = :sprintid";
            param = [req.params.projectid, req.params.sprintid];
        }
    } else if (!req.params.projectid && req.params.sprintid) {
        if (isNaN(req.params.sprintid))
            next();
        if (req.query.state) {
            query = "select * from sc_task where idsprint = :sprintid and state = :state";
            param = [req.params.sprintid, req.query.state];
        } else {
            query = "select * from sc_task where idsprint = :sprintid";
            param = [req.params.sprintid];
        }
    }

    oracleConnection.execute(query, param)
        .then((result) => {
            if (result.rows.length < 1)
                throw new Error("NO CONTENT");
            res.json(classParser(result.rows, classes.Task))
        })
        .catch((err) => error.respondWith(res, err));
});
router.get('/backlog', (req, res, next) => {
    if (req.params.sprintid)
        next();
    else {
        let query = "SELECT * from sc_task where state = 'PRODUCT_BACKLOG'",
            param = [];

        if (req.params.projectid) {
            if (isNaN(req.params.projectid))
                next();
            query = "SELECT * from sc_task where state = 'PRODUCT_BACKLOG' and idproject = :projectid";
            param = [req.params.projectid];
        }

        oracleConnection.execute(query, param)
            .then((result) => {
                if (result.rows.length < 1)
                    throw new Error("NO CONTENT");

                res.json(classParser(result.rows, classes.Task));
            })
            .catch((err) => {
                error.respondWith(res, err)
            });
    }
});
router.get('/:taskid', (req, res, next) => {
    if (!isNaN(req.params.taskid)) {
        let query = "SELECT * from sc_task where id = :taskid",
            param = [req.params.taskid];

        if (req.params.projectid && !req.params.sprintid) {
            if (isNaN(req.params.projectid))
                next();
            query = "select * from sc_task where idproject = :projectid and id = :taskid";
            param = [req.params.projectid, req.params.taskid];
        } else if (req.params.projectid && req.params.sprintid) {
            if (isNaN(req.params.projectid) || isNaN(req.params.sprintid))
                next();
            query = "select * from sc_task where idproject = :projectid and idsprint = :idsprint and id = :taskid";
            param = [req.params.projectid, req.params.sprintid, req.params.taskid];
        } else if (!req.params.projectid && req.params.sprintid) {
            if (isNaN(req.params.sprintid))
                next();
            query = "select * from sc_task where idsprint = :sprintid and id = :taskid";
            param = [req.params.sprintid, req.params.taskid];
        }

        oracleConnection.execute(query, param)
            .then((result) => res.json(classParser(result.rows, classes.Task)[0]))
            .catch((err) => error.respondWith(res, err));
    } else {
        next();
    }
});
router.post('/', (req, res, next) => {
    if (req.params.sprintid || req.params.projectid)
        next();
    else {
        let query = "insert into sc_task values(null, :responsible, :verify, :name, :info, 0, :state, :position, :sprint, :project, :color)",
            param = [req.body.responsible, req.body.verify, req.body.name, req.body.info, req.body.state, req.body.position, req.body.sprint, req.body.project, req.body.color],
            object = getTaskObject(req, -1, 0);

        oracleConnection.execute(query, param)
            .then((result) => {
                query = "select max(id) from sc_task order by id",
                    param = [];

                oracleConnection.execute(query, param)
                    .then((result) => {
                        let id = result.rows[0][0];
                        object.id = id;
                        changeJob.change(req.body.project, "TASK", "POST", object);
                        res.status(201).json({
                            id: id
                        });
                    })
                    .catch((err) => error.respondWith(res, err));
            })
            .catch((err) => error.respondWith(res, err));
    }
});
router.put('/:taskid', (req, res, next) => {
    if (isNaN(req.params.taskid) || req.params.projectid || req.params.sprintid) {
        next();
    } else {
        let query = "begin SC_PROC_UPDATE_TASK(:taskid, :responsible, :verify, :name, :info, :rejections, :state, :position, :sprint, :project, :color); end;",
            param = [req.params.taskid, req.body.responsible, req.body.verify, req.body.name, req.body.info, req.body.rejections, req.body.state, req.body.position, req.body.sprint, req.body.project, req.body.color],
            object = getTaskObject(req, req.params.taskid, req.body.rejections);

        oracleConnection.execute(query, param)
            .then((result) => {
                changeJob.change(req.body.project, "TASK", "PUT", object);
                res.status(200).end()
            })
            .catch((err) => error.respondWith(res, err));
    }
});
router.delete('/:taskid', (req, res, next) => {
    if (isNaN(req.params.taskid) || req.params.projectid || req.params.sprintid) {
        next();
    } else {
        let query = "SELECT idproject FROM sc_task WHERE id = :taskid",
            param = [req.params.taskid],
            object = {
                id: Number(req.params.taskid)
            };

        oracleConnection.execute(query, param)
            .then((result) => {
                query = "begin SC_PROC_DELETE_TASK(:taskid); end;";

                oracleConnection.execute(query, param)
                    .then((oracleResult) => {
                        changeJob.change(result.rows[0][0], "TASK", "DELETE", object);
                        res.status(200).end()
                    })
                    .catch((err) => error.respondWith(res, err));
            })
            .catch((err) => error.respondWith(res, err));
    }
});

function getTaskObject(req, id, rejections) {
    return {
        id: Number(id),
        responsible: req.body.responsible,
        verify: req.body.verify,
        name: req.body.name,
        info: req.body.info,
        rejections: rejections,
        state: req.body.state,
        position: req.body.position,
        sprint: req.body.sprint,
        project: req.body.project,
        color: req.body.color
    };
}


module.exports = router;