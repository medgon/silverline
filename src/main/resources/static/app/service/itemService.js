angular.module('app').service('itemService', function($http) {
	
	var itemService = {
			
			getItem: function(cbSuccess, cbError){
				return $http.get('/silverline/items/' +itemId)
						.then( function success(response){ 
							var item = response.data; 
							itemService.parseFields(item);
							cbSuccess(item);
						}, function error(){
							cbError();
							console.log("Error!");
						});
			},
	}
	
	return item;
	
})
