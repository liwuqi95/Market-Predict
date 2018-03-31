app.config(function ($routeProvider) {
    $routeProvider

        .when("/", {
            templateUrl: "/view/profile.html"
        })
        .when("/profile", {
            templateUrl: "/view/profile.html"
        })
        .when("/market", {
            templateUrl: "/view/market.html"
        })
        .when("/prediction", {
            templateUrl: "/view/prediction.html"
        })
        .when("/news", {
            templateUrl: "/view/news.html"
        })
        .when("/community", {
            templateUrl: "/view/community.html"
        })
        .when("/orders", {
            templateUrl: "/view/orders.html"
        })
        .when("/about", {
            templateUrl: "/view/about.html"
        })
        .when("/login", {
            templateUrl: "/view/login.html"
        })
        .when("/instruments", {
            templateUrl: "/view/instruments.html"
        });
});