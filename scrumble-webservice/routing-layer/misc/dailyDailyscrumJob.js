let dailyScrumSchema = require('../../data-layer/schemas/dailyScrumSchema'),
    changeSchema = require('../../data-layer/schemas/changeSchema')
oracleConnection = require('../../data-layer/oracleDataAccess'),
    classParser = require('../../data-layer/classParser'),
    classes = require('../../data-layer/classes'),
    mongoose = require('mongoose'),
    changeJob = require('./changeJob');

let lastChanges = {};

module.exports = {
    createDailyScrums: (projectid, sprintid, callback, newStartdate = null, newDeadline = null) => {
        var doInsert = true;
        
        let query = "SELECT datum FROM sc_lastupdate WHERE sc_lastupdate.idproject = :projectid";
        oracleConnection.execute(query, [projectid])
            .then((result) => {
                if (result.rows.length > 0) {
                    lastChanges[projectid] = new Date(result.rows[0][0]);
                    doInsert = false;
                } else {
                    lastChanges[projectid] = null;
                    doInsert = true;
                }
                checkAndExecute(projectid, sprintid, newStartdate, newDeadline, callback, doInsert, newStartdate == null);
            })
            .catch((err) => {
                throw err;
            });
    }
};

function checkAndExecute(projectid, sprintid, newStartdate, newDeadline, callback, doInsert, doDrop) {
    let today = new Date(new Date().toDateString());
    var query = "select * from sc_user inner join sc_teammember on iduser = sc_user.id where idproject = :projectid";

    oracleConnection.execute(query, [projectid])
        .then((userResult) => {
            let users = classParser(userResult.rows, classes.User);
            if (newStartdate == null && newDeadline == null) {
                query = "SELECT * from sc_sprint where idproject = :projectid";
                oracleConnection.execute(query, [projectid])
                    .then((result) => {

                        if (result.rows.length > 0) {
                            let sprints = classParser(result.rows, classes.Sprint);

                            for (idx = 0; idx < result.rows.length; idx++) {
                                prepareMongoCall(projectid, sprints[idx], users, today, callback, newStartdate, newDeadline, idx == (result.rows.length - 1), doInsert, doDrop);
                            }
                        }
                        else {
                            if(doDrop) {
                                dropChanges(today, projectid, doInsert);
                                lastChanges[projectid] = today;
                            }
                            callback();
                        }
                    })
                    .catch((err) => {
                        throw err;
                    });
            } else {
                query = "SELECT * from sc_sprint where idproject = :projectid and id = :sprintid";
                oracleConnection.execute(query, [projectid, sprintid])
                    .then((result) => {
                        let sprint = classParser(result.rows, classes.Sprint)[0];
                        prepareMongoCall(projectid, sprint, users, today, callback, newStartdate, newDeadline, true, doInsert, doDrop);
                    })
                    .catch((err) => {
                        throw err;
                    });
            }
        })
        .catch((err) => {
            throw err;
        });
}

function prepareMongoCall(projectid, sprint, users, today, callback, newStartdate, newDeadline, doCall, doInsert, doDrop) {
    let startdate = new Date(new Date(sprint.startdate).toDateString());
    let deadline = new Date(new Date(sprint.deadline).toDateString());
    let ldd = lastChanges[projectid];

    if (newStartdate == null) newStartdate = startdate;
    else newStartdate = new Date(newStartdate.toDateString());
    if (newDeadline == null) newDeadline = deadline;
    else newDeadline = new Date(newDeadline.toDateString());

    if (ldd != null && ldd.getTime() == today.getTime() &&
        startdate.getTime() == newStartdate.getTime() && deadline.getTime() == newDeadline.getTime() && doDrop) {
        if (doCall) callback();
        return;
    }

    if (startdate.getTime() == newStartdate.getTime() && startdate.getTime() > today.getTime()) {
        if (doCall) {
            if(doDrop) {
                dropChanges(today, projectid, doInsert);
                lastChanges[projectid] = today;
            }
            callback();
        };
        return;
    }

    mongoCall(projectid, sprint, today, users, newStartdate, newDeadline, doCall, callback, doInsert, doDrop);
}

function mongoCall(projectid, sprint, today, users, startdate, deadline, doCall, callback, doInsert, doDrop) {
    Promise.all([
        dailyScrumSchema.find({
            "project": projectid,
            "sprint": sprint.id
        }).sort("-date").limit(1),
        dailyScrumSchema.find({
            "project": projectid,
            "sprint": sprint.id
        }).sort("date").limit(1),
    ]).then(([max, min]) => {
        if (min.length > 0 && min[0] != undefined && min[0] != null) {
            let minDate = new Date(new Date(min[0].date).toDateString())
            if (minDate.getTime() > startdate.getTime())
                generateDailyScrums(startdate, new Date(minDate.setDate(minDate.getDate() - 1)), users, projectid, sprint.id, () => {}, false);
        }

        let enddate = today.getTime() > deadline.getTime() ? deadline : today;
        let loopDate = new Date();

        if (max.length > 0 && max[0] != undefined && max[0] != null) {
            let maxDate = new Date(new Date(max[0].date).toDateString());

            if (maxDate.getTime() == today.getTime() || maxDate.getTime() == deadline) {
                if (doCall) {
                    if(doDrop) {
                        dropChanges(today, projectid, doInsert);
                        lastChanges[projectid] = today;
                    }
                    callback();
                };
                return;
            }

            loopDate = new Date(maxDate.setDate(maxDate.getDate() + 1));
        } else
            loopDate = startdate;

        generateDailyScrums(loopDate, enddate, users, projectid, sprint.id, callback, doCall, !doDrop);

        if(doDrop) {
            dropChanges(today, projectid, doInsert);
            lastChanges[projectid] = today;
        }
    });
}

function generateDailyScrums(loopDate, enddate, users, projectid, sprintid, callback, doCall, saveInChanges) {
    if (loopDate > enddate && doCall) {
        callback();
        return;
    }

    var dailyScrums = [];

    while (loopDate <= enddate) {
        for (var idx = 1; idx <= users.length; idx++) {
            let month = loopDate.getMonth() + 1;
            let day = loopDate.getDate();
            var dailyScrumObj = {
                _id: new mongoose.Types.ObjectId(),
                user: users[idx - 1],
                date: `${loopDate.getFullYear()}-${(month < 10 ? "0" : "") + month}-${(day < 10 ? "0" : "") + day}`,
                description: "",
                project: projectid,
                sprint: sprintid,
                task: null
            };

            let dailyScrum = new dailyScrumSchema(dailyScrumObj);

            if (loopDate.getTime() != enddate.getTime() || idx != users.length) {
                dailyScrums.push(dailyScrumObj);
                dailyScrum.save((err) => {
                    if (err) throw err;
                });
            } else {
                dailyScrums.push(dailyScrumObj);
                dailyScrum.save((err) => {
                    if (err) throw err;
                    if (doCall) {
                        if(saveInChanges)
                            changeJob.change(projectid, "DAILYSCRUM", "POST", dailyScrums);    
                        callback();
                    }
                });
            }
        }

        loopDate = new Date(loopDate.setDate(loopDate.getDate() + 1));
    }
}

function dropChanges(date, projectid, doInsert) {
    var lastProjectChangeDrop = lastChanges[projectid];
    if (lastProjectChangeDrop == undefined || lastProjectChangeDrop == null ||
        new Date(new Date(lastProjectChangeDrop).toDateString()).getTime() < date.getTime()) {
        changeSchema.deleteMany({
            "project": projectid
        }, (err) => {
            if (err) throw err;
            let dateToInsert = new Date(new Date(date).toDateString());

            var query = "UPDATE sc_lastupdate set datum = :lastProjectChangeDrop WHERE idproject = :projectid",
                param = [dateToInsert, projectid];

            if (doInsert) {
                query = "INSERT INTO sc_lastupdate VALUES(:projectid, :lastProjectChangeDrop)";
                param = [projectid, dateToInsert];
            }

            oracleConnection.execute(query, param)
                .then(() => {})
                .catch((err) => {
                    if (err) throw err;
                });
        });
    }
}