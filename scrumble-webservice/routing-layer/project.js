// packages
const express = require('express'),
    oracleConnection = require('../data-layer/oracleDataAccess'),
    classParser = require('../data-layer/classParser'),
    classes = require('../data-layer/classes'),
    error = require('./misc/error'),
    router = express.Router({
        mergeParams: true
    });
host = process.env.HOST;

router.get('/', (req, res) => {
    let query = 'SELECT * from sc_project',
        param = [];

    if (req.params.userid) {
        query = "select id, name, idproductowner, idcurrentsprint from sc_project inner join sc_teammember on idproject = id where iduser = :userid";
        param = [req.params.userid];
    }

    oracleConnection.execute(query, param)
        .then((result) => {
            if (result.rows.length < 1)
            throw new Error("NO CONTENT");
            res.json(classParser(result.rows, classes.Project))
        })
        .catch((err) => error.respondWith(res, err));
});
router.get('/:projectid', (req, res, next) => {
    if (!isNaN(req.params.projectid)) {
        let query = 'SELECT * from sc_project where id = :projectid',
            param = [req.params.projectid];

        oracleConnection.execute(query, param)
            .then((result) => res.json(classParser(result.rows, classes.Project)[0]))
            .catch((err) => error.respondWith(res, err));
    } else {
        next();
    }
});
router.get('/:projectid/current', (req, res, next) => {
    if(!isNaN(req.params.projectid)){
    let query = "select * from sc_sprint where id = (select idcurrentsprint from sc_project where id = :projectid)",
        param = [req.params.projectid];

    oracleConnection.execute(query, param)
        .then((result) => {
            if (result.rows.length < 1)
            throw new Error("NO CONTENT");
            res.json(classParser(result.rows, classes.Sprint))
        })
        .catch((err) => error.respondWith(res, err));
    } else {
        next();
    }
});
router.post('/', (req, res) => {
    let query = "insert into sc_project values(null, :name, :productowner, null)",
        param = [req.body.name, req.body.productowner];

    oracleConnection.execute(query, param)
        .then((result) =>  {
            query = "select max(id) from sc_project order by id",
            param = [];

            oracleConnection.execute(query, param)
            .then((result) => res.json({id: result.rows[0][0]}))
            .catch((err) => error.respondWith(res, err));
        })
        .catch((err) => error.respondWith(res, err));
});
router.put('/', (req, res) => {
    let query = "update sc_project set name = :name, idproductowner: productowner, idcurrentsprint = :currentsprint",
        param = [req.body.name, req.body.productowner, req.body.currentsprint];

    oracleConnection.execute(query, param)
        .then((result) => res.status(200).end())
        .catch((err) => error.respondWith(res, err));
});
router.post('/:projectid/user', (req, res, next) => {
    if(!isNaN(req.params.projectid)){
    let query = "insert into sc_teammember values(:iduser, :projectid)",
        param = [req.body.iduser, req.params.projectid];

    oracleConnection.execute(query, param)
        .then((result) => res.status(201).end())
        .catch((err) => error.respondWith(res, err));
    } else {
        next();
    }
});


module.exports = router;