app.controller('predictionController', function ($filter, $scope, apiService, $q) {
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

    $scope.period = 1;

    $scope.periods = [1,3,7,15,30,60,90];

    $scope.showMessage = false;

    $scope.predict = function(){
      apiService.predict($scope.current_instrument, $scope.period).then(function(response){
          $scope.showMessage = true;
          $scope.value = parseFloat(response.data);
          console.log("current price is "  + $scope.current) ;
          console.log("predict price is "  + $scope.value) ;

          if($scope.value < $scope.current / 1.02){
            $scope.message = "Prediction result is Strong Sell";
              $scope.alertStyle = "danger";
          }

          else if($scope.value < $scope.current / 1.005){
              $scope.message = "Prediction result is Sell";
              $scope.alertStyle = "danger";
          }

          else if($scope.value > $scope.current / 0.98)
          {
              $scope.alertStyle = "success";
              $scope.message = "Prediction result is Strong Buy"

          }

          else if($scope.value > $scope.current / 0.995)
          {
              $scope.alertStyle = "success";
              $scope.message = "Prediction result is Buy";
          }
          else {
              $scope.alertStyle = "info";
              $scope.message = "Prediction result is Neutral";
          }


      })


    };


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

                if(i === response.data.candles.length-1)
                    $scope.current = response.data.candles[i].mid.c
            }
            $scope.showMessage = false;
        });
    };



});