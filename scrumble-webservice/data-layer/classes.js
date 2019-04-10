class User {
    constructor(id, name, password) {
        this.id = id;
        this.name = name;
        //this.password = password;
    }
}

class Task {
    constructor(id, responsible, verify, name, info, rejections, state, position, sprint, project, color) {
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
        this.color = color;
    }
}

class Project {
    constructor(id, name, productowner, currentSprint) {
        this.id = id;
        this.name = name;
        this.productowner = productowner;
        this.currentsprint = currentSprint;
    }
}

class Sprint {
    constructor(id, number, startdate, deadline, project) {
        this.id = id;
        this.number = number;
        this.startdate = startdate;
        this.deadline = deadline;
        this.project = project;
    }
}

class DailyScrum {
    constructor(_id, user, date, description, sprint, project, task) {
        this._id = _id;
        this.user = user;
        this.date = date;
        this.description = description;
        this.project = project;
        this.sprint = sprint;
        this.task = task;
    }
}


module.exports = {
    User: User,
    Sprint: Sprint,
    Project: Project,
    Task: Task,
    DailyScrum: DailyScrum
};