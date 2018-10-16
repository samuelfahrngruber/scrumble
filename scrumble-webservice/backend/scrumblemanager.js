//scrumblemanager
module.exports = (function () {
    var dataAccessSQL = require("./dataAccessSQL");

    return {
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
        dataAccessSQL.login(username,password, callback);
    }

    function _register(username, password, callback) {
        dataAccessSQL.register(username, password, callback);
    }

    function _getProjectsByUser(userid, callback) {
        dataAccessSQL.getProjectsByUser(userid, callback);
    }

    function _getUser(projectid,callback) {
        dataAccessSQL.getUser(projectid,callback);
    }

    function _addUser(userid,projectid,callback) {
        dataAccessSQL.addUser(userid,projectid,callback);
    }

    function _getBacklog(projectid, callback) {
        dataAccessSQL.getBacklog(projectid, callback);
    }

    function _changeTaskState(taskid,newState,callback) {
        dataAccessSQL.changeTaskState(taskid,newState,callback);
    }

    function _addTaskToSprint(sprintid,idresponsible,idverify,storyname,info,state,position,callback) {
        dataAccessSQL.addTaskToSprint(sprintid,idresponsible,idverify,storyname,info,state,position, callback);
    }

    function _addSprint(projectid,sprintnumber,startdate,deadline,callback) { 
        dataAccessSQL.addSprint(projectid,sprintnumber,startdate,deadline,callback);
    }

    function _getSprint(projectid, callback) {
        dataAccessSQL.getSprint(projectid, callback);
    }

    function _addProject(name,productownerid,callback) {
        dataAccessSQL.addProject(name,idproductownerid, callback);
    }


})();