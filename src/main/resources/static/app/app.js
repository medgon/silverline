(function(angular) {
	'use strict';

	angular.module('app', ['kendo.directives', 'ui.router', 'ui.router.components', 'angular.filter']).run(function($state, $location){
		
		//set default state of the page
		if ($location.path() == ""){
			$state.go("initalState");
		}
		
		//this is a hack to fix an issue in Chrome where the column menu closes when the mouse pointer is over an input. The latest kendo versions fixed it, but we don't have that yet
		//http://www.telerik.com/forums/issue-with-grid-filter-(on-chrome-55-0-2883-75)
	    kendo.ui.Grid.fn.options.columnMenuInit = function(e){
	        var menu = e.container.find(".k-menu").data("kendoMenu");
	        menu.bind('activate', function(e){
	          if(e.item.is(':last-child')){
	            // if an element in the submenu is focused first, the issue is not observed
	            e.item.find('span.k-dropdown.k-header').first().focus();
	            // e.item.find('input').first().focus();
	          }
	        });
	      }
	})

})(window.angular);
