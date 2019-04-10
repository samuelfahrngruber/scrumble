var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var taskSchema = new Schema({
    id: Number,
    responsible: Number,
    verify: Number,
    name: String,
    info: String,
    rejections: Number,
    state: String,
    position: Number,
    sprint: Number,
    project: Number,
    color: String
  }, { versionKey: false });
  

  module.exports = mongoose.model('Task', taskSchema);