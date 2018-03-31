//global application to control all front view
var app = angular.module("myApp", ["ngRoute", 'chart.js']);

//root controller to control nav bar
app.controller('AppController', function ($scope, apiService) {

    //all the views we are supporting
    $scope.views = ['Profile', 'Market', 'Orders', 'About Us', 'Contact Us', 'Change KEY'];

    //a variable to record current view
    $scope.view = $scope.views[5];

    //view changing
    $scope.logOut = function () {
        apiService.logOut().then(function(){
            window.location.href = '/login'
        })
    };
});



//profile controller to control profile view
app.controller('ProfileController', function ($scope, apiService, $q) {

    var chain = $q.when();

    //preload user's account summury
    chain = chain.then(function () {
        return apiService.getSummary().then(function (response) {
            $scope.summary = response.data;
        });
    });

    //preload user's order summury
    chain = chain.then(function () {
        return apiService.getOrders().then(function (response) {
            $scope.orders = response.data;
        });
    });

    //preload user's trades summury
    chain = chain.then(function () {
        return apiService.getTrades().then(function (response) {
            $scope.trades = response.data.trades;
        });
    });

    //function triggered by clicking learn more button
    $scope.learnMore = function () {
        $scope.$parent.changeView('About Us');
    }

});



app.controller('OrderController', function ($scope, apiService, $q) {

    var chain = $q.when();

    //load the order data
    chain = chain.then(function () {
        return apiService.getOrders().then(function (response) {
            $scope.orders = response.data.orders;
        });
    });
});


app.controller('instrumentController', function ($scope, apiService, $q,$filter) {

    var chain = $q.when();


    //load all instruments
    chain = chain.then(function () {
        return apiService.getUserInstrumentList().then(function (response) {
            $scope.userInstrumentList = response.data;
        });
    });

    //load all instruments
    chain = chain.then(function () {
        return apiService.getInstrumentList().then(function (response) {
            $scope.instrumentList = $filter('orderBy')(response.data.instruments, 'name');
            $scope.leftInstrumentList = $scope.reRange();
        });
    });



    $scope.addInstrument = function(instrument){
        apiService.addUserInstrument(instrument).then(function(){
            apiService.getUserInstrumentList().then(function (response) {
                $scope.userInstrumentList = response.data;
                $scope.leftInstrumentList = $scope.reRange();
            });
        })
    };

    $scope.removeInstrument = function(instrument){
        apiService.removeUserInstrument(instrument).then(function(){
            apiService.getUserInstrumentList().then(function (response) {
                $scope.userInstrumentList = response.data;
                $scope.leftInstrumentList = $scope.reRange();
            });
        })
    };

    $scope.reRange = function(){
        result = [];
        for(i = 0; i < $scope.instrumentList.length; i ++){
            find = false;

            for( j = 0; j<$scope.userInstrumentList.length; j++){
                if($scope.userInstrumentList[j] === $scope.instrumentList[i].name){
                    find = true;
                }
            }
            if(!find)
                result.push($scope.instrumentList[i]);

        }

        return result;
    }




});


