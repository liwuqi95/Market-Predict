
// apiSevice provides all http request we need
app.service('apiService', function ($http) {

    const url = 'https://api-fxpractice.oanda.com/v3';

    const lab_url = 'https://api-fxpractice.oanda.com/labs/v1/calendar';

    //set thte api_key for demo purpose
    var api_key = '6a4c4e506d8e279754621e0a9726dcf3-0aa3dc0a019025e0fbfa32a2f8c4809e';

    //set the account id for demo purpose
    var id = '/101-002-4966033-001';

    //header for http request
    var auth_header = '';

    //header getter
    this.getHeader = function(){
        return {'Authorization': 'Bearer ' + api_key};
    };

    //set api_key
    this.setApiKey = function(key){
        api_key = key;
        return $http.get(url + '/accounts',{
            headers: this.getHeader()});
    };

    //set account ID
    this.setAccountId = function(newid){
        id ='/' + newid;
    };

    //get the account summary
    this.getSummary = function (){
        return $http.get(url + '/accounts' + id + '/summary',{
            headers: this.getHeader()});
    };

    //get a list of instruments that is able to trade
    this.getInstrumentList = function (){
        return $http.get(url + '/accounts' + id + '/instruments',{
            headers: this.getHeader()});
    };

    //get all the orders
    this.getOrders = function (){
        return $http.get(url + '/accounts' + id +'/orders',{
            headers: this.getHeader()});
    };

    //get all the active trades
    this.getTrades = function (){
        return $http.get(url + '/accounts' + id + '/trades',{
            headers: this.getHeader()});
    };

    //get the instrument's current price
    this.getInstrumentPrice = function (instrument){
        return $http.get(url + '/accounts' + id + '/pricing',{
            headers: this.getHeader(),
            params: {instruments: [instrument]}
        });
    };

    //get instrument history price
    this.getInstrumentHistory = function (instrument){
        return $http.get(url + '/instruments/' + instrument + '/candles',{
            headers: this.getHeader()
        });
    };

    //place a new order
    this.newOrder = function (instrument, unit){
        return $http.post(url + '/accounts' + id + '/orders',
            {order:{type : 'MARKET' ,instrument: instrument, units:unit}},
            {headers: this.getHeader()});
    };

    this.getNews = function(instrument){
      return $http.get(lab_url,{
          headers: this.getHeader(),
          params:{instrument:instrument, period: 7776000}
      } )
    };


    /** API from our server */

    this.signup = function(user){
        return $http.post('/signup', user);
    };

    this.login = function(user){
        return $http.post('/login', user);
    };

    this.logOut = function(){
        return $http.post('/logout');
    };

    this.getUserInstrumentList = function(){
        return $http.get('/instruments');
    };

    this.addUserInstrument = function(instrument){
        return $http.post('/instruments/' + instrument);
    };

    this.removeUserInstrument = function(instrument){
        return $http.delete('/instruments/' + instrument);
    };

    this.getComments = function(instrument){
        return $http.get('/comments/' + instrument);
    };

    this.addComment = function(instrument,comment){
        return $http.post('/comments/' + instrument, comment);
    };

    this.predict = function(instrument, period){
        return $http.get('http://127.0.0.1:8000/' + instrument +'/' + period);
    }

});
