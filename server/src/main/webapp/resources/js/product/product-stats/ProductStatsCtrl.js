(function(){
	var orderId = window.location.pathname.split("/")[2];
	
	var app = angular
				.module("KubikProductsStats")
				.controller("ProductsStatsCtrl", ProductsStatsCtrl);
	
	function ProductsStatsCtrl($scope, $http, $timeout, $controller){
		var productCardCtrl = $controller("ProductCardCtrl", {$scope : $scope);
		
		$scope.$on("openProductCard", function(event, product){
			$scope.productCardCtrl.openCard(product);
		});
		
		$scope.changePage = function(page){
			$scope.page = page;
			
			$scope.loadProductsStats();
		}
		
		$scope.loadProductsStats = function(){
			var params = {	page : $scope.page,
							resultPerPage : $scope.resultPerPage};
			
			if($scope.startDate != undefined) params.startDate = moment($scope.startDate, "DD-MM-YYYY").toDate();
			if($scope.endDate != undefined) params.endDate = moment($scope.endDate, "DD-MM-YYYY").toDate();
			
			$http.get("productStats?" + $.param(params)).success(function(productsStatsPage){
				$scope.productsStatsPage = productsStatsPage;
			});
		};
		
		$scope.page = 0;
		$scope.resultPerPage = 200;
		
		$scope.loadProductsStats();
		
		$(".date").each(function(index, element){
			var $element = $(element);
			
			$element.val(moment(parseInt($element.val())).format("DD/MM/YYYY")).datepicker({format : 'dd/mm/yyyy'});
		});
	}
})();