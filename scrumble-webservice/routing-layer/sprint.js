// packages
const express = require('express'),
    oracleConnection = require('../data-layer/oracleDataAccess'),
    classParser = require('../data-layer/classParser'),
    classes = require('../data-layer/classes'),
    error = require('./misc/error'),
    router = express.Router({ mergeParams: true });
host = process.env.HOST;

router.get('/', (req, res) => {
    let query = "SELECT * from sc_sprint", 
    param = [];

    if(req.params.projectid) {
            if(isNaN(req.params.projectid))
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
    if(!isNaN(req.params.sprintid)) {
        let query = "SELECT * from sc_sprint where id = :sprintid",
        param = [req.params.sprintid];
        if(req.params.projectid) {
            if(isNaN(req.params.projectid))
                next();
            query = "select * from sc_sprint where idproject = :projectid and id = :sprintid"; //todo sprintnumber
            param = [req.params.projectid, req.params.sprintid];
        }

        oracleConnection.execute(query, param)
            .then((result) => res.json(classParser(result.rows, classes.Sprint)[0]))
            .catch((err) => error.respondWith(res, err));
    } 
    else {
        next();
    }  
});
router.post('/', (req, res, next) => {
    if(req.params.sprintid || req.params.projectid)
        next();
    else{
    let query = "insert into sc_sprint values(null, :sprintnumber, to_date(:startdate, 'YYYY-MM-DD'), to_date(:deadline, 'YYYY-MM-DD'), :project)",
        param = [req.body.sprintnumber, req.body.startdate, req.body.deadline, req.body.project];

    oracleConnection.execute(query, param)
        .then((result) => {
            query = "select max(id) from sc_sprint order by id",
            param = [];

            oracleConnection.execute(query, param)
            .then((result) => res.status(201).json({id: result.rows[0][0]}))
            .catch((err) => res.status(404).json({
                message: err.message,
                details: err
            }));
        })
        .catch((err) => error.respondWith(res, err));
    }
});
router.put('/:sprintid', (req, res, next) => {
    if(isNaN(req.params.sprintid) || req.params.projectid) {
        next();
    }
    else{
        let query = "update sc_sprint set sprintnumber = :sprintnumber, startdate = to_date(:startdate, 'YYYY-MM-DD'), deadline = to_date(:deadline, 'YYYY-MM-DD'), idproject = :project where id = :sprintid",
        param = [req.body.sprintnumber, req.body.startdate, req.body.deadline, req.body.project, req.params.sprintid];
        oracleConnection.execute(query, param)
            .then((result) => res.status(200).end())
            .catch((err) => error.respondWith(res, err));
    }
});


module.exports = router;