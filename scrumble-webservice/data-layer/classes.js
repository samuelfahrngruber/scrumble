class User {
    constructor(id, name, password) {
        this.id = id;
        this.name = name;
        //this.password = password;
    }
}

class Task {
    constructor(id, responsible, verify, name, info, rejections,state,position,sprint,project) {
    this.id = id;
    this.responsible = responsible;
    this.verify = verify;
    this.name = name;
    this.info = info;
    this.rejections = rejections;
    this.state = state;
    this.position = position;
    this.sprint = sprint;
    this.project = project;
    }
}

class Project {
    constructor(id, name, productowner, currentSprint) {
    this.id = id;
    this.name = name;
    this.productowner = productowner;
    this.currentSprint = currentSprint;
    }
}

class Sprint {
    constructor(id, number,startdate,deadline,project) {
    this.id = id;
    this.number = number;
    this.startdate = new Date(startdate).toUTCString();
    this.deadline = new Date(deadline).toUTCString();
    this.project = project;
    }
}


module.exports = {
    User: User,
    Sprint: Sprint,
    Project: Project,
    Task: Task
};