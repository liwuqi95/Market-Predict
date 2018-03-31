var express = require('express');
var router = express.Router();

//get model for user
var User = require('mongoose').model('User');

//to encrypt password
var passwordHash = require('password-hash');

//passport to handle session
var passport = require('passport');

//define strategy
var LocalStrategy = require('passport-local').Strategy;



//handle register request
router.post('/signup', function (req, res) {
    console.log("new instrument is " + req.body.instruments);

    var password = req.body.password;
    req.body.password = passwordHash.generate(password);

    User.create(req.body, function (err) {
        if (err) {
            //catch duplicate email error
            if (err.code === 11000 ) {
                res.status(500).send("The email you entered is already exists.");
                return;
            }
            //catch other errors
            res.status(404).send(err.message);
        } else {
            //successfully create a user
            res.send("Successfully create user.");
        }
    });
});

//handle login request
router.post('/login',
    passport.authenticate('local', {successRedirect: '/', failureRedirect: '/not_login'}),
    function (req, res) {
        res.redirect('/');
    });

//handle logout request
router.post('/logout', function (req, res) {
    req.logout();
    res.redirect('/login');
});


//define password compare strategy
passport.use(new LocalStrategy(
    function (username, password, done) {
        User.findOne({username: username}, function (err, user) {
            if (err) throw err;

            if (!user) {
                return done(null, false, {message: 'Unknown User'});
            }

            if (passwordHash.verify(password, user.password)) {
                return done(null, user);
            } else {
                return done(null, false, {message: 'Invalid password'});
            }
        });
    }));


passport.serializeUser(function(user, done) {
    done(null, user.id);
});

passport.deserializeUser(function(id, done) {
    User.findById(id, function(err, user) {
        done(err, user);
    });
});

module.exports = router;