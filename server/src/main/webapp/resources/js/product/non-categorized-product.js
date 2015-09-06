(function(){
	angular
		.module("Kubik")
		.controller("KubikNonCategorizedProductController", KubikNonCategorizedProductController);
	
	function KubikNonCategorizedProductController($scope, $http){
		$scope.loadRandomProduct = function(){
			$http.get("/product?category&random").success(function(product){			
				$scope.product = product;
			});
		};
		
		$scope.pass = function(){
			$scope.loadRandomProduct();
		};
		
		$scope.loadRandomProduct();
		
		$scope.kubikProductCategories = kubikProductCategories;
		$scope.kubikProductCategories.categorySelectedCallback = function(category){
			$scope.product.category = category;
			$http.post("/product", $scope.product).success(function(product){
				$scope.loadRandomProduct();
			});
		};
	}
})();