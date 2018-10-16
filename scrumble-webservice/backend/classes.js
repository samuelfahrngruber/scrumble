module.exports.Project = function (id, name, productowner,currentSprint) {
    this.projectid = id;
    this.name = name;
    this.productowner = productowner;
    this.currentSprint = currentSprint;

}

module.exports.Sprint = function (id, number,startdate,deadline,idproject) {
    this.sprintid = id;
    this.number = number;
    this.startdate = startdate;
    this.deadline = deadline;
    this.idproject = idproject;
}

module.exports.Task = function (id, name, info, rejections,position,idresponible,idverify,idsprint,idproject) {
    this.taskid = id;
    this.name = name;
    this.info = info;
    this.rejections = rejections;
    this.position = position;
    this.idresponible = idresponible;
    this.idverify = idverify;
    this.idsprint = idsprint;
    this.idproject = idproject;

}

module.exports.User = function (id, username, password) {
    this.userid = id;
    this.username = username;
    this.password = password;
}
