var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var changeSchema = new Schema({
    timestamp: String,
    project: Number,
    object: String,
    method: String,
    change: Object
  }, { versionKey: false });
  

  module.exports = mongoose.model('Change', changeSchema);