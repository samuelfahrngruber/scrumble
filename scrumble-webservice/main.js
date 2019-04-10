// packages
const express = require('express'),
    bodyParser = require('body-parser'),
    logger = require('./routing-layer/middleware/logger'),
    app = express();

// middleware
app.use(bodyParser.json());
app.use(logger);
app.use(require('cors')());

// environment
const port = process.env.PORT || 8080; 
process.env.HOST = process.env.HOST || 'http://localhost' + ':' + port;

// routing
const defaultRouter = require('./routing-layer/default');
const projectRouter = require('./routing-layer/project');
const userRouter = require('./routing-layer/user');
const taskRouter = require('./routing-layer/task');
const sprintRouter = require('./routing-layer/sprint');
const dailyScrumRouter = require('./routing-layer/dailyScrum');
const changesRouter = require('./routing-layer/change');

projectRouter.use('/:projectid/dailyScrum', dailyScrumRouter);
taskRouter.use('/:taskid/dailyScrum', dailyScrumRouter);
userRouter.use('/:userid/dailyScrum', dailyScrumRouter);
sprintRouter.use('/:sprintid/dailyscrum', dailyScrumRouter);
sprintRouter.use('/:sprintid/task', taskRouter);
userRouter.use('/:userid/project', projectRouter);
projectRouter.use('/:projectid/sprint', sprintRouter);
projectRouter.use('/:projectid/task', taskRouter);
projectRouter.use('/:projectid/user', userRouter);
projectRouter.use('/:projectid/changes', changesRouter);
defaultRouter.use('/project', projectRouter);
defaultRouter.use('/user', userRouter);
defaultRouter.use('/task', taskRouter);
defaultRouter.use('/sprint', sprintRouter);
defaultRouter.use('/dailyScrum', dailyScrumRouter);

app.use('/scrumble', defaultRouter);

// start
app.listen(port);
console.log('Server started on port ' + port);