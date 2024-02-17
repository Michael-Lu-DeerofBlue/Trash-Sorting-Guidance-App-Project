var mongoose = require('mongoose');

// DO NOT CHANGE THE URL FOR THE DATABASE!
// Please speak to the instructor if you need to do so or want to create your own instance
mongoose.connect('mongodb://mongo.cs.swarthmore.edu:27017/group08_hamster');

var Schema = mongoose.Schema;

var wasteSchema = new Schema({
	name: {type: String, required: true},
	nodes: String,
    category: String
    

    });

// export personSchema as a class called Person
module.exports = mongoose.model('Waste', wasteSchema);

// this is so that the names are case-insensitive
wasteSchema.methods.standardizeName = function() {
    this.name = this.name.toLowerCase();
    return this.name;
}