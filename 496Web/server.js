var express = require('express');
var db = require('./db/monDB.js');
var app = express();

var bodyParser = require('body-parser');
var cookieParser = require('cookie-parser');
var session = require('express-session');
var passport = require('passport');


app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());


var auth = require('./routes/authentication');
var router = require('./routes/router');



// Express Session
app.use(session({
    secret: 'secret',
    saveUninitialized: true,
    resave: true
}));

// Passport init
app.use(passport.initialize());
app.use(passport.session());


app.use(express.static('public'));

app.use('/', auth);
app.use('/', router);


var port = process.env.PORT || 8080;


app.listen(port);