app.controller('MarketController', function ($filter, $scope, apiService, $q) {
    var chain = $q.when();

    //get a list of instrument that is tradeble
    chain = chain.then(function () {
        return apiService.getUserInstrumentList().then(function (response) {
            $scope.instrumentList = response.data;
            //assign the current instrument to the first
            $scope.current_instrument = $scope.instrumentList[0];
        });
    });

    //preload some history data for chart
    chain = chain.then(function () {
        return $scope.resetChart();
    });

    //function triggerd when changing instrument
    $scope.changeInstrument = function (newInstrument) {
        $scope.current_instrument = newInstrument;
        $scope.resetChart();
    };

    //update the chart every 5s
    setInterval(function () {
        apiService.getInstrumentPrice($scope.current_instrument).then(function (response) {

            //push the new value into chart
            $scope.labels.push($filter('date')(response.data.time, 'HH:mm:ss'));
            $scope.data[0].push(response.data.prices[0].closeoutAsk);

            //shift the chart after getting a new data
            $scope.labels.shift();
            $scope.data[0].shift();
        });
    }, 5000);


    //options for plotting chart
    $scope.options = {
        animation: {
            duration: 0
        },
        elements: {
            line: {
                borderWidth: 0.5
            },
            point: {
                radius: 0
            }
        },

        scales: {
            xAxes: [{
                display: true
            }],
            yAxes: [{
                display: true
            }],
            gridLines: {
                display: false
            }
        },
        tooltips: {
            enabled: true
        }
    };

    //load instrument history data for chart
    $scope.resetChart = function () {
        return apiService.getInstrumentHistory($scope.current_instrument).then(function (response) {

            //initialize or cleaning data
            $scope.labels = [];
            $scope.data = [[]];

            //plotting
            for (i = 0; i < response.data.candles.length; i++) {
                $scope.labels.push($filter('date')(response.data.candles[i].time, 'HH:mm:ss'));
                $scope.data[0].push(response.data.candles[i].mid.c);
            }
        });
    };

    $scope.action = function (type) {
        // distingush buy and sell
        if (type == 'sell') i = -1;
        else i = 1;

        //check if the quantity is correct
        if ($scope.quant != null && $scope.quant > 0) {
            apiService.newOrder($scope.current_instrument, $scope.quant * i).then(function (response) {
                //success
                $scope.message = 'Your TRADE has been placed successfully';
                $scope.messageType = 'alert-success';
            }, function (response) {
                //error occurs
                $scope.message = response.data.errorMessage;
                $scope.messageType = 'alert-danger';
            });
        }
    };


});




app.controller('NewsController', function ($filter, $scope, apiService, $q) {
    var chain = $q.when();

    //get a list of instrument that is tradeble
    chain = chain.then(function () {
        return apiService.getUserInstrumentList().then(function (response) {
            $scope.instrumentList = response.data;
            //assign the current instrument to the first
            $scope.current_instrument = $scope.instrumentList[0];
        });
    });

    chain = chain.then(function () {
        return apiService.getNews($scope.current_instrument).then(function (response) {
        $scope.listNews = response.data;
        });
    });



    //function triaggerd when changing instrument
    $scope.changeInstrument = function (newInstrument) {
        $scope.current_instrument = newInstrument;
        apiService.getNews($scope.current_instrument).then(function (response) {
            $scope.listNews = response.data;
        })
    };

});

app.controller('commentController', function ($filter, $scope, apiService, $q) {
    var chain = $q.when();

    //get a list of instrument that is tradeble
    chain = chain.then(function () {
        return apiService.getUserInstrumentList().then(function (response) {
            $scope.instrumentList = response.data;
            //assign the current instrument to the first
            $scope.current_instrument = $scope.instrumentList[0];
        });
    });

    chain = chain.then(function () {
        return apiService.getComments($scope.current_instrument).then(function (response) {
            $scope.commentsList = response.data;
        });
    });

    $scope.message = {comment:''};

    $scope.addComment = function(){
        apiService.addComment($scope.current_instrument, $scope.message).then(function(){
            $scope.message  = {comment:''};
            apiService.getComments($scope.current_instrument).then(function (response) {
                $scope.commentsList = response.data;
            });
        })
    };


    //function triaggerd when changing instrument
    $scope.changeInstrument = function (newInstrument) {
        $scope.current_instrument = newInstrument;
        $scope.message  = {comment:''};
        apiService.getComments($scope.current_instrument).then(function (response) {
            $scope.commentsList = response.data;
        });
    };

});