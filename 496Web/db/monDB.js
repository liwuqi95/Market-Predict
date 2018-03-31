
var url = "mongodb://pp:ppblackjack@ds127139.mlab.com:27139/309-blackjack";

var mongoose = require('mongoose');
mongoose.connect(url);


var db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function() {
    console.log("Connected to db!")
});

var Schema   = mongoose.Schema;

var user = new Schema({
    username: {type: String, required : true, unique : true},
    password: {type: String, required : true},
    key:{type: String, required : true},
    instruments:[{
        type: String, unique: true
    }]
});

mongoose.model('User', user);

var community = new Schema({
    instrument:{type: String, required : true},
    comments: [{
        type: String
    }]
});

mongoose.model('Community', community);