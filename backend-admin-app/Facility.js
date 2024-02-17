var mongoose = require('mongoose');

// DO NOT CHANGE THE URL FOR THE DATABASE!
// Please speak to the instructor if you need to do so or want to create your own instance
mongoose.connect('mongodb://mongo.cs.swarthmore.edu:27017/group08_hamster');

var Schema = mongoose.Schema;

var FacilitySchema = new Schema({
	name: {type: String, required: true},
	location: String,
    phone: String
    });

// export classFacilitySchema as a class called FacilitySchema
module.exports = mongoose.model('Facility', FacilitySchema);

// this is so that the names are case-insensitive
FacilitySchema.methods.standardizeName = function() {
    this.name = this.name.toLowerCase();
    return this.name;
}