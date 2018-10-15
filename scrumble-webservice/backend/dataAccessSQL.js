// srDataAccessSQL.js
'esversion:6';

// TODO better way of making sql statements

// call srObjects
const sql = require('mssql');
const classes = require('./classes');

// database connection config
const config = {
    user: 'd5a17',
    password: 'd5a',
    server: '192.168.192.217\\SQLEXPRESS',
    database: 'SessionRatingDB',
    options: {
        instanceName: 'SQLEXPRESS',
    }
};

var srDataAccessSQL = (function () {
    var dataAccess = {
        login: _login,
        register: _register,
        getProjectsByUser: _getProjectsByUser,
        getUser: _getUser,
        addUser: _addUser,
        getBacklog: _getBacklog,
        changeTaskState: _changeTaskState,
        addTaskToSprint: _addTaskToSprint,
        addTaskToBacklog: _addTaskToBacklog,
        addSprint: _addSprint,
        getSprint: _getSprint,
        addProject: _addProject
    };

    function _login(username, password, callback) {
        var sqlString = 'select * from sc_user where username like \''+username+'\' and password like \''+password+'\';';
        connectAndQuery(sqlString, callback);
    }

    function _register(username, password, callback) {
        var sqlString = 'insert into sc_user values(null, \''+username+'\',\''+password+'\');';
        connectAndQuery(sqlString, callback);
        var sqlString = 'select id from sc_user where username like \''+username+'\';';
        connectAndQuery(sqlString, callback);
    }

    function _getProjectsByUser(userid, callback) {
        var sqlString = 'TODO';
        connectAndQuery(sqlString, function (err, result) {
            if (err || result.recordset.length == 0) {
                callback(err);
            } else {
                var projects = result.recordset.map(record => new classes.Project(record.id, record.name,record.productowner,record.currentsprint));
                callback(err, projects);
            }
        });
    }

    function _getUser(projectid,callback) {
        var sqlString = 'select * from sc_user inner join sc_teammember on id = iduser where idproject = \''+projectid+'\';';
        connectAndQuery(sqlString, function (err, result) {
            if (err || result.recordset.length == 0) {
                callback(err);
            } else {
                var users = result.recordset.map(record => new classes.User(record.id, record.username,record.password));
                callback(err, users);
            }
        });
    }

    function _addUser(userid,projectid,callback) {
        var sqlString = 'insert into sc_user values(null, \''+username+'\',\''+password+'\');';
        connectAndQuery(sqlString, callback);
    }

    function _getBacklog(projectid, callback) {
        var sqlString = 'select * from sc_userstory inner join sc_project on idproject = sc_project.id where idproject = \''+projectid+'\' and state = \'PRODUCT_BACKLOG\';';
        connectAndQuery(sqlString, function (err, result) {
            if (err || result.recordset.length == 0) {
                callback(err);
            } else {
                var backlog = result.recordset.map(record => new classes.Task(record.id, record.name,record.info,record.rejections,record.position,record.idresponible,record.idverify,record.idsprint,record.idproject));
                callback(err, backlog);
            }
        });
    }

    function _changeTaskState(taskid,newState,callback) {
        connectAndQuery('update sc_userstory set state = \''+newState+'\' where id = \''+taskid+'\';');
    }

    function _addTaskToSprint(sprintid,idresponsible,idverify,storyname,info,state,position,callback) {
        var sqlString = 'insert into sc_userstory values(null, \''+idresponsible+'\', \''+idverify+'\', \''+storyname+'\', \''+info+'\', \''+state+'\',\''+position+'\', \''+sprintid+'\');';
        connectAndQuery(sqlString, callback);
    }

    function _addSprint(projectid,sprintnumber,startdate,deadline,callback) { 
        var sqlString = 'insert into sc_sprint values(null, \''+sprintnumber+'\', \''+startdate+'\', \''+deadline+'\', \''+deadline+'\';';
        connectAndQuery(sqlString, callback);
    }

    function _getSprint(projectid, callback) {
        var sqlString = 'TODO';
        connectAndQuery(sqlString, function (err, result) {
            if (err || result.recordset.length == 0) {
                callback(err);
            } else {
                var sprints = result.recordset.map(record => new classes.Sprint(record.id, record.number,record.startdate,record.deadline,record.idproject));
                callback(err, sprints);
            }
        });
    }

    function _addProject(name,productownerid,callback) {
        var sqlString = 'insert into sc_project values(null, \''+name+'\'}, \''+productownerid+'\';';
        connectAndQuery(sqlString, callback);
    }

    return dataAccess;
})();

module.exports = srDataAccessSQL;