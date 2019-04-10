var mongoose = require('mongoose');

var connection = mongoose.connect('mongodb://spogss:spogss1234@ds241065.mlab.com:41065/scrumblemongodb');

module.exports = connection;