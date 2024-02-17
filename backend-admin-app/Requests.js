var mongoose = require('mongoose');

// DO NOT CHANGE THE URL FOR THE DATABASE!
// Please speak to the instructor if you need to do so or want to create your own instance
mongoose.connect('mongodb://mongo.cs.swarthmore.edu:27017/group08_hamster');

var Schema = mongoose.Schema;

var RequestSchema = new Schema({
	name: String,
	nodes: String,
    category: String,
    feedback: String
    });

// export RequesSchema as a class called Reques
module.exports = mongoose.model('Request', RequestSchema);

// this is so that the names are case-insensitive
RequestSchema.methods.standardizeName = function() {
    this.name = this.name.toLowerCase();
    return this.name;
}