// packages
const express = require('express'),
    oracleConnection = require('../data-layer/oracleDataAccess'),
    classParser = require('../data-layer/classParser'),
    classes = require('../data-layer/classes'),
    error = require('./misc/error'),
    router = express.Router({ mergeParams: true });
host = process.env.HOST;

router.get('/', (req, res) => {
    let query = "SELECT * from sc_user", 
    param = [];

    if(req.query.name)
    {
        query = "SELECT * from sc_user where lower(username) = lower(:name)", 
        param = [req.query.name]; 
    }
    else if(req.params.projectid) {
        if(isNaN(req.params.projectid))
            next();
        query = "select * from sc_user inner join sc_teammember on iduser = sc_user.id where idproject = :projectid";
        param = [req.params.projectid]
    }

    oracleConnection.execute(query, param)
        .then((result) => res.json(classParser(result.rows, classes.User)))
        .catch((err) => error.respondWith(res, err));
});
router.get('/:userid', (req, res, next) => {
    if(!isNaN(req.params.userid)){
    let query = 'SELECT * from sc_user where id = :userid',
    param = [req.params.userid];

    if(req.params.projectid) {
        query = "select * from sc_user inner join sc_teammember on iduser = sc_user.id where idproject = :projectid and sc_user.id = :userid";
        param = [req.params.projectid, req.params.userid]
    }

    oracleConnection.execute(query, param)
        .then((result) => {
            if (result.rows.length < 1)
            throw new Error("NO CONTENT");
            res.json(classParser(result.rows, classes.User)[0])
        })
        .catch((err) => error.respondWith(res, err));
    }
    else {
        next();
    } 
});
router.post('/:projectid/user', (req, res, next) => {
    if(!isNaN(req.params.projectid)){
    let query = "insert into sc_teammember values(:iduser, :idproject)",
    param = [req.body.iduser, req.body.idproject];

    oracleConnection.execute(query, param)
        .then((result) => res.status(201).end())
        .catch((err) => error.respondWith(res, err));
    }
    else {
        next();
    }
});

module.exports = router;