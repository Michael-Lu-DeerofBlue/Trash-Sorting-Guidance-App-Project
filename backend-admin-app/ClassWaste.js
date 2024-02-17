var mongoose = require('mongoose');

// DO NOT CHANGE THE URL FOR THE DATABASE!
// Please speak to the instructor if you need to do so or want to create your own instance
mongoose.connect('mongodb://mongo.cs.swarthmore.edu:27017/group08_hamster');

var Schema = mongoose.Schema;

var classWasteSchema = new Schema({
	name: {type: String, required: true},
	subclass: String
    });

// export classWasteSchema as a class called ClassWaste
module.exports = mongoose.model('ClassWaste', classWasteSchema);

// this is so that the names are case-insensitive
classWasteSchema.methods.standardizeName = function() {
    this.name = this.name.toLowerCase();
    return this.name;
}