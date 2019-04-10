var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var userSchema = require('./userSchema')
var taskSchema = require('./taskSchema')

var dailyScrumSchema = new Schema({
    _id: mongoose.Types.ObjectId,
    user: userSchema.schema,
    date: String,
    description: String,
    project: Number,
    sprint: Number,
    task: taskSchema.schema
  }, { versionKey: false });
  

  module.exports = mongoose.model('DailyScrum', dailyScrumSchema);