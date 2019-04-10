// packages
let express = require('express'),
    oracleConnection = require('../data-layer/oracleDataAccess'),
    classParser = require('../data-layer/classParser'),
    classes = require('../data-layer/classes'),
    error = require('./misc/error'),
    changeJob = require('./misc/changeJob'),
    dailyDailyscrumJob = require('./misc/dailyDailyscrumJob'),
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
    if (!isNaN(req.params.projectid)) {
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
        param = [req.body.name, req.body.productowner],
        object = getProjectObject(req, -1, null);

    oracleConnection.execute(query, param)
        .then((result) => {
            query = "select max(id) from sc_project order by id",
            param = [];

            oracleConnection.execute(query, param)
                .then((result) => {
                    let id = result.rows[0][0];
                    object.id = id;
                    changeJob.change(id, "PROJECT", "POST", object);
                    res.json({
                        id: id
                    })
                })
                .catch((err) => error.respondWith(res, err));
        })
        .catch((err) => error.respondWith(res, err));
});
router.put('/:projectid', (req, res) => {
    var query = "update sc_project set name = :name, idproductowner = :productowner, idcurrentsprint = :currentsprint where id = :projectid",
        param = [req.body.name, req.body.productowner, req.body.currentsprint, req.params.projectid],
        object = getProjectObject(req, req.params.projectid, req.body.currentsprint);

    oracleConnection.execute(query, param)
        .then((result) => {
            changeJob.change(req.params.projectid, "PROJECT", "PUT", object);
            res.status(200).end();
        })
        .catch((err) => error.respondWith(res, err));
});
router.post('/:projectid/user', (req, res, next) => {
    if (!isNaN(req.params.projectid)) {
        let query = "insert all",
            param = [],
            data = req.body,
            objects = [];

        for (var idx = 0; idx < data.length; idx++) {
            query += " into sc_teammember values(:id, :projectid)";
            param.push(data[idx].id);
            param.push(req.params.projectid);
            objects.push(data[idx]);
        }
        query += " select 1 from dual";

        oracleConnection.execute(query, param)
            .then((result) => { 
                changeJob.change(req.params.projectid, "USER", "POST", objects);
                res.status(201).end(); 
            })
            .catch((err) => error.respondWith(res, err));
    } else {
        next();
    }
});

router.delete('/:projectid/user/:userid', (req, res, next) => {
    if (!isNaN(req.params.projectid)) {
        let query = "delete from sc_teammember where iduser = :userid and idproject = :projectid",
            param = [req.params.userid, req.params.projectid];

        oracleConnection.execute(query, param)
            .then((result) => {
                changeJob.change(req.params.projectid, "USER", "DELETE", {
                    id: Number(req.params.userid)
                });
                res.status(200).end();
            })
            .catch((err) => error.respondWith(res, err));
    } else {
        next();
    }
});

function getProjectObject(req, id, currentsprint) {
    return {
        id: Number(id),
        name: req.body.name,
        productowner: Number(req.body.productowner),
        currentsprint: currentsprint != null ? Number(currentsprint) : null
    };
}

module.exports = router;