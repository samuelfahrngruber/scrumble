// packages
let express = require('express'),
    error = require('./misc/error'),
    changeSchema = require('../data-layer/schemas/changeSchema'),
    mongoConnection = require('../data-layer/mongoDataAccess'),
    mongoose = require('mongoose'),
    router = express.Router({
        mergeParams: true
    });

router.get('/', (req, res, next) => {
    let projectid = Number(req.params.projectid);
    let timestamp = new Date(req.query.timestamp);

    if (!projectid || !req.query.timestamp) error.respondWith(res, new Error("Bad Request"));
    else {
        changeSchema.find({
                "project": projectid,
            })
            .where("timestamp").gt(req.query.timestamp)
            .exec((err, changes) => {
                if (err) return error.respondWith(res, err);

                if (changes == null || changes.length < 1)
                    error.respondWith(res, new Error("NO CONTENT"));
                else
                    res.json(changes);
            })
    }
});

module.exports = router;