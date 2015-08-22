window.KubikProductSearch = function(options){
	if(options.modal == undefined){
		options.modal = false;
	}
	
	if(options.resultPerPage == undefined){
		options.resultPerPage = 50;		
	}
	
	if(options.defaultSortBy == undefined){
		options.defaultSortBy = "extendedLabel";		
	}
	
	if(options.defaultSortDirection == undefined){
		options.defaultSortDirection = "ASC";		
	}
	
	if(options.productCreationAllowed == undefined){
		options.productCreationAllowed = true;
	}
	
	this.app = options.app;
	this.modal = options.modal;
	this.$container = options.$container;
	this.productCreationAllowed = options.productCreationAllowed;
	
	this.productUrl = options.productUrl;
	this.resultPerPage = options.resultPerPage;
	this.defaultSortBy = options.defaultSortBy;
	this.defaultSortDirection = options.defaultSortDirection;
	this.productSelected = options.productSelected;
	
	this.init();
};

window.KubikProductSearch.prototype.init = function(){
	var kubikProductSearch = this;	
	this.app.controller("KubikProductSearchController", function($scope, $http, $timeout){
		$scope.$on("openProductCard", function(event, product){
			$scope.kubikProductCard.openCard(product);
		})
		
		$scope.$on("search", function(event){
			$scope.search();
		});
		
		$scope.changePage = function(page){
			$scope.page = page;
			
			$scope.search();
		}
		
		$scope.productSelected = function(product){
			if(kubikProductSearch.productSelected != undefined){
				kubikProductSearch.productSelected(product);
			}
		};
		
		$scope.newProduct = function(){
			$scope.$emit("openProductCard", {});
		};
		
		$scope.openCard = function($event, product){
			try{
				$scope.$emit("openProductCard", product);
			}finally{
				$event.stopPropagation();
			}
		};
		
		$scope.sort = function(sortBy, direction){
			$scope.sortBy = sortBy;
			$scope.direction = direction;
			
			$scope.search();
		}
		
		$scope.search = function(){
			var params = {
				search : "",
				query : $scope.query,
				page : $scope.page,
				resultPerPage : $scope.resultPerPage,
				sortBy : $scope.sortBy,
				direction : $scope.direction 
			};
			
			$http.get(kubikProductSearch.productUrl + "?" + $.param(params)).success(function(searchResult){
				$scope.searchResult = searchResult;

				$timeout(function(){
					for(var productIndex in searchResult.content){
						var product = searchResult.content[productIndex];
						
						$("#product-image-" + product.id).attr(
							"src", 
							"http://images1.centprod.com/" + $scope.company.ean13 + "/" + product.imageEncryptedKey + "-cover-thumb.jpg"
						);
					}					
				});
			});
		};
		
		$scope.query = "";
		$scope.page = 0;
		$scope.resultPerPage = kubikProductSearch.resultPerPage
		$scope.sortBy = kubikProductSearch.defaultSortBy;
		$scope.direction = kubikProductSearch.defaultSortDirection;
		$scope.productCreationAllowed = kubikProductSearch.productCreationAllowed;

		$.get("/company").success(function(company){
			$scope.company = company;
			
			$scope.search();
		});
		
		kubikProductSearch.$scope = $scope;
		
		$scope.kubikProductCard = kubikProductSearch.kubikProductCard;
		$scope.kubikProductCard.productSaved = function(){
			$scope.$broadcast("search");
		};
	});
	
	this.kubikProductCard = new KubikProductCard({
		app : kubikProductSearch.app,
		productUrl : "/product"
	});
};

window.KubikProductSearch.prototype.closeSearchModal = function(){
	this.modal.modal("hide");
};

window.KubikProductSearch.prototype.openSearchModal = function(searchQuery){
	var KubikProductSearch = this;
	this.modal.modal({
		backdrop : "static",
		keyboard : false
	}).on("shown.bs.modal", function(){
		$("#search-product-query").focus();
		
		if(searchQuery != undefined){
			KubikProductSearch.$scope.query = searchQuery;
			KubikProductSearch.$scope.search();	
		}
	});
};