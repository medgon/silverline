(function(angular) {
	'use strict';
	angular.module('app').component('itemInfo', {
		templateUrl: '/silverline/app/components/itemInfo/itemInfo.html', 
		controller: ItemController
	});
	
	function ItemController(itemService) { 

		this.getItems = function(){

				itemService.getItem(function(data){
					this.item = data;
					console.log(this.item);

				}.bind(this));

			}
			
			this.init = function(){
	            this.getItems();
			};

			this.init();
		}
	
	
})(window.angular);	
