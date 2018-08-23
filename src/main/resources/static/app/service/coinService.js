angular.module('app').service('coinService', function($http) {
	
	var coinService = {
			
			getAllCoins: function(cbSuccess, cbError){
				return $http.get('https://bittrex.com/api/v1.1/public/getmarkets')
						.then( function success(response){ 
							cbSuccess(response.data);
						}, function error(){
							cbError();
							console.log("Error!");
						});
			},
	}
	
	return coinService;
	
})
