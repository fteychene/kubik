window.KubikProductCard = function(options){
	if(options == undefined){
		options = {};
	}
	if(options.$modalContainer == undefined){
		options.$modalContainer = $(".product-card");
	}
	
	this.app = app;
	this.$modalContainer = options.$modalContainer;
	this.productUrl = options.productUrl;
	this.productSaved = options.productSaved;
	
	this.init();
};

window.KubikProductCard.prototype.init = function(){
	var kubikProductCard = this;
	var kubikProductCategories;
	
	this.app.controller("KubikProductCardController", function($scope, $http, $timeout){
		kubikProductCard.$modalScope = $scope;
		$scope.editMode = false;
		
		$scope.cancelModify = function(){
			$scope.product = $scope.originalProduct;
			
			$scope.endEditMode();
		};
		
		$scope.changeInventoryTab = function(tabClassName){
			if($scope.inventoryTab != tabClassName){
				$scope.inventoryTab = tabClassName;
			}
		};
		
		$scope.changePage = function(page){
			$scope.inventoryTabs[$scope.inventoryTab].searchParams.page = page;
			
			$scope.loadInventoryTab($scope.inventoryTab);
		}
		
		$scope.endEditMode = function(){
			$scope.editMode = false;
			
			$scope.$saveBtn.addClass("hidden");
			$scope.$cancelBtn.addClass("hidden");
			$scope.$modifyBtn.removeClass("hidden");
			$scope.$closeBtn.removeClass("hidden");
			
			$scope.refreshModalBackdrop();
		};
		
		$scope.getSupplier = function(id){
			for(var supplierIndex in $scope.suppliers){
				var supplier = $scope.suppliers[supplierIndex];
				if(supplier.id == id){
					return supplier;
				}
			}
			
			return null;
		};
		
		$scope.loadInventoryTab = function(tabName){			
			$http.get(kubikProductCard.productUrl + "/" + $scope.product.id + "/" + tabName + "?" + $.param($scope.inventoryTabs[tabName].searchParams)).success(function(searchResult){
				$scope.inventoryTabs[tabName].result = searchResult;
			});
		};
		
		$scope.loadInventoryTabs = function(){
			for(var inventoryTabIndex in $scope.inventoryTabs){
				$scope.loadInventoryTab(inventoryTabIndex);
			}
		}
		
		$scope.modify = function(){
			$scope.editMode = true;
			
			$scope.$saveBtn.removeClass("hidden");
			$scope.$cancelBtn.removeClass("hidden");
			$scope.$modifyBtn.addClass("hidden");
			$scope.$closeBtn.addClass("hidden");
			
			$scope.originalProduct = $.extend(true, {}, $scope.product);
			
			$timeout(function(){
				if($scope.editMode){
					$(".date-published, .publish-end-date").each(function(index, element){
						var $element = $(element);
						
						if($element.val() != ""){
							$element.val(moment(parseInt($element.val())).format("DD/MM/YYYY"))							
						}
						
						$element.datepicker({format : 'dd/mm/yyyy'});
					});
				}
				
				var supplier = null;
				if($scope.product.supplier != null) {
					supplier = $scope.getSupplier($scope.product.supplier.id);
				}
				if(supplier == null){
					supplier = $scope.suppliers[0];
				}
				
				$("#product-supplier-" + supplier.id).attr("SELECTED", "SELECTED");
				
				$scope.$apply();
			});
			
			$scope.refreshModalBackdrop();
		}
		
		$scope.openCard = function(product){
			$scope.product = product;
			$scope.productTab = "product";
			$scope.inventoryTab = "reception";

			$timeout(function(){
				kubikProductCard.$modal = kubikProductCard.$modalContainer.find(".kubikProductCard").modal({
					backdrop : "static",
					keyboard : false
				});
				
				if(product.id == undefined){
					$scope.modify();
				}else{
					$scope.loadInventoryTabs();
				}
			});
		}
		
		$scope.refreshModalBackdrop = function(){
			$timeout(function(){
				kubikProductCard.$modalContainer.find(".modal-backdrop")
			      .css('height', 0)
			      .css('height', kubikProductCard.$modal[0].scrollHeight);
			});
		};
		
		$scope.save = function(){
			$scope.product.datePublished = $(".date-published").datepicker("getDate");
			$scope.product.publishEndDate = $(".publish-end-date").datepicker("getDate");
		
			if(!$scope.product.dilicomReference){
				$scope.product.supplier = $scope.getSupplier($("select.product-supplier").val());
			}
			
			$http.post(kubikProductCard.productUrl, $scope.product).success(function(product){
				$scope.product = product;
				$scope.endEditMode();
				if(kubikProductCard.productSaved != undefined){
					kubikProductCard.productSaved(product);
				}
			})
		};
		
		$.get("/company").success(function(company){
			$scope.company = company;
		});

		$scope.$saveBtn = kubikProductCard.$modalContainer.find(".save")
		$scope.$modifyBtn = kubikProductCard.$modalContainer.find(".modify")
		$scope.$closeBtn = kubikProductCard.$modalContainer.find(".closeModal")
		$scope.$cancelBtn = kubikProductCard.$modalContainer.find(".cancel")
		
		$scope.inventoryTabs = {	customerCredit : {	searchParams : {
											page : 0,
											resultPerPage : 20,
											sortBy : "customerCredit.completeDate",
											direction : "DESC" }
									}, 
									invoice : {	searchParams : {
										page : 0,
										resultPerPage : 20,
										sortBy : "invoice.paidDate",
										direction : "DESC"  }
									}, 
									reception : {	searchParams : {
										page : 0,
										resultPerPage : 20,
										sortBy : "reception.dateReceived",
										direction : "DESC"  }
									}, 
									rma : {	searchParams : {
										page : 0,
										resultPerPage : 20,
										sortBy : "rma.shippedDate",
										direction : "DESC"  }
									}};
		
		$http.get("/supplier").success(function(suppliers){
			$scope.suppliers = suppliers;
		});

		$scope.kubikProductCategories = kubikProductCategories;
		$scope.kubikProductCategories.categorySelectedCallback = function(category){
			$scope.product.category = category;
			
			$scope.kubikProductCategories.closeModal();
		};
	});
	
	kubikProductCategories = new KubikProductCategories({
		app : this.app,
		$modal : $(".categories-modal"),
		categoriesUrl : "/category",
		categorySelectedCallback : {}
	});
};

window.KubikProductCard.prototype.openCard = function(product){
	this.$modalScope.openCard(product);
}