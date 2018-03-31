var express = require('express');
var router = express.Router();
var path = require('path');

//get model for community
var Community = require('mongoose').model('Community');

//get model for user
var User = require('mongoose').model('User');

//index page
router.get('/', ensureAuthenticated, function (httpRequest, httpResponse) {
    httpResponse.sendFile(path.resolve('index.html'))
});

//login and signup page
router.get('/login', function (httpRequest, httpResponse) {
    httpResponse.sendFile(path.resolve('login.html'))
});

//get user Instruments list
router.get('/instruments', ensureAuthenticated, function (httpRequest, httpResponse) {
    httpResponse.json(httpRequest.user.instruments)
});


//get user Instruments list
router.post('/instruments/:instrumentId', ensureAuthenticated, function (httpRequest, httpResponse) {

    find = false;

    existInstruments = httpRequest.user.instruments;

    for (i = 0; i < existInstruments.length; i++)
        if (existInstruments[i] === httpRequest.params.instrumentId)
            find = true;

    if (find) {
        httpResponse.send("already exists");
        return;
    }

    User.findOneAndUpdate({username: httpRequest.user.username},
        {
            $push: {instruments: httpRequest.params.instrumentId}
        }, function (err) {
            if (err) {
                httpResponse.status(500).send("Failed to add new instrument");
            }
        });
    httpResponse.send("Successfully add a instrument");
});


//get user Instruments list
router.delete('/instruments/:instrumentId', ensureAuthenticated, function (httpRequest, httpResponse) {

    User.findOneAndUpdate({username: httpRequest.user.username},
        {
            $pull: {instruments: httpRequest.params.instrumentId}
        }, function (err) {
            if (err) {
                httpResponse.status(500).send("Failed to remove instrument");
            }
        });
    httpResponse.send("Successfully remove a instrument");
});


//get comments
router.get('/comments/:instrumentId', function (httpRequest, httpResponse) {
    instrumentId = httpRequest.params.instrumentId;

    Community.findOne({instrument: instrumentId}, function(err, result){
        if(err){
            httpResponse.status(500).send("Failed to load comments");
            return;
        }

        if(!result){
            httpResponse.json([]);
        }
        else{
            httpResponse.json(result.comments);
        }

    });


});

//post a new comment
router.post('/comments/:instrumentId', function (httpRequest, httpResponse) {
    instrumentId = httpRequest.params.instrumentId;

    Community.findOne({instrument: instrumentId}, function(err, result){
        if(err){
            httpResponse.status(500).send("Failed to add comment");
            return;
        }
        if(!result){
            //create a new community
            Community.create({instrument: instrumentId}, function (err) {
                if (err) {
                    res.status(404).send(err.message);
                    return;
                }
                //insert after create a new one
                Community.findOneAndUpdate({instrument: instrumentId},
                    {
                        $push: {comments: httpRequest.body.comment}
                    }, function (err) {
                        if (err) {
                            httpResponse.status(500).send("Failed to add comment");
                            return;
                        }
                        httpResponse.send("Successfully add a comment");
                        return;
                    });
            });
        }

        Community.findOneAndUpdate({instrument: instrumentId},
            {
                $push: {comments: httpRequest.body.comment}
            }, function (err) {
                if (err) {
                    httpResponse.status(500).send("Failed to add comment");
                    return;
                }
                httpResponse.send("Successfully add a comment");
            });
    });

});

function ensureAuthenticated(req, res, next) {
    if (req.isAuthenticated()) {
        return next();
    } else {
        res.redirect('/login');
    }
}

module.exports = router;