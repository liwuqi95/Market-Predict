//login controller to control login view

//login controller to control login view
app.controller('LoginController', function ($scope, apiService) {

    $scope.user = {
        username: '', password: ''
    };

    // function triggered by a key submit
    $scope.submit = function () {
        $scope.error = false;

        apiService.login($scope.user).then(function () {
            window.location.href = '/'
        }, function (response) {
            $scope.error = true;
            $scope.errorMessage = "Your email or password is not correct.";
        });
    }
});

//signup Controller
app.controller('SignupController', function ($scope, apiService) {

    $scope.user = {
        username: '', password: '', key: '', instruments:[]
    };

    // function triggered by a key submit
    $scope.submit = function () {
        $scope.error = false;
        apiService.setApiKey($scope.user.key).then(function (response) {

            apiService.signup($scope.user).then(function () {
                apiService.login($scope.user).then(function(){
                    window.location.href = '/'
                })

            }, function (response) {
                $scope.error = true;
                $scope.errorMessage = response.data;
            })

        }, function () {
            $scope.error = true;
            $scope.errorMessage = "Your Oanda key is not valid! "
        });
    }
});