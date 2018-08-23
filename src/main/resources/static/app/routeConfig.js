(function(angular) {
	'use strict';

	angular.module('app').config(function($stateProvider, $urlRouterProvider) {
		  // An array of state definitions
		  var states = [
				{	name: 'initalState',			
					url: '/',					
					component: 'reportView',
				},
				
		  ]
		  
		  // Loop over the state definitions and register them
		  states.forEach(function(state) {
		    $stateProvider.state(state);
		  });
		  //TODO handle 404 state here 
		  $urlRouterProvider.otherwise( '/' );
		  
		});
	
})(window.angular);