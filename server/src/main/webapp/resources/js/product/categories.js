window.KubikProductCategories = function(options){
	this.app = options.app;
	this.$modal = options.$modal;
	this.$container = options.$container;
	this.categorySelectedCallback = options.categorySelectedCallback;
	
	this.categoriesUrl = options.categoriesUrl;
	
	if(this.categoriesUrl == undefined){
		this.categoriesUrl = "/category";
	}
	
	this.init();
};

window.KubikProductCategories.prototype.init = function(){
	var kubikProductCategories = this;

	this.app.controller("KubikProductCategoriesController", function($scope, $http, $timeout){		
		$scope.addCategory = function(parentCategory){
			$http.get(kubikProductCategories.categoriesUrl + "?newName").success(function(name){
				var newCategory = {name : name};
				
				if(parentCategory != undefined){
					newCategory.parentCategory = { id : parentCategory.id};
					newCategory.rootCategory = false;
				}else{
					newCategory.rootCategory = true;
				}
				
				$scope.saveCategory(newCategory);
			});
		}
		
		$scope.cancelEditCategory = function(){
			$scope.loadCategories();
			$scope.error = null;
	
			$(".edit-category-modal").modal("hide");
		}
		
		$scope.categorySelected = function(category, $event){
			$event.stopPropagation();
			
			if($scope.kubikProductCategories.categorySelectedCallback != undefined){
				$scope.kubikProductCategories.categorySelectedCallback(category);
			}
		}
		
		$scope.categoryHovered = function(category, $event){
			$scope.hoveredCategory = category;			
			$event.stopPropagation();
		}
		
//		$scope.categorySelected = function(category, $event){
//			$scope.selectedCategory = category;
//			
//			$event.stopPropagation();
//		};
		
		$scope.confirmDeleteCategory = function(category, $event){
			$event.stopPropagation();
			
			$http.get("/product?category=" + category.id).success(function(productCount){
				$scope.category = category;
				$scope.productCount = productCount;
				
				$(".confirm-delete-category-modal").modal();
				
			});
		};
		
		$scope.deleteCategory = function(category){
			$scope.loading = true;
			
			var parentCategory = $scope.childParentCategoriesMap[category.id];
			
			$http.delete(kubikProductCategories.categoriesUrl + "/" + category.id + "/product").success(function(){
//				if(category.rootCategory){
					$http.delete(kubikProductCategories.categoriesUrl + "/" + category.id).success(function(){
						$(".confirm-delete-category-modal").modal("hide");
						
						$scope.loadCategories();
					}).error(function(data){
						$scope.error = data.message;
					}).finally(function(){
						$scope.loading = false;
					});
//				}else{
//					parentCategory.childCategories.splice(parentCategory.childCategories.indexOf(category), 1);
//					
//					$http.post(kubikProductCategories.categoriesUrl, parentCategory).success(function(){
//						$(".confirm-delete-category-modal").modal("hide");
//					}).error(function(data){
//						$scope.error = data.message;
//					}).finally(function(){
//						$scope.loading = false;
//						
//						$scope.loadCategories();
//					});
//				}
			});
		}
		
		$scope.editCategory = function(category){
			$scope.category = category;
	
			$(".edit-category-modal").modal();
		}
		
		$scope.hideError = function(){
			$scope.error = null;
		}
		
		$scope.loadCategories = function(successCallback){
			$scope.loading = true;
			
			$http.get(kubikProductCategories.categoriesUrl).success(function(categories){
				$scope.categories = categories;
	
				var calculateLevel = function(category, level){
					category.level = level;
	
					angular.forEach(category.childCategories, function(childCategory, key){
						$scope.childParentCategoriesMap[childCategory.id] = category;
						calculateLevel(childCategory, level + 1);
					});
				};
			
				angular.forEach(categories, function(category, key ){
					category.level = 1;
	
					angular.forEach(category.childCategories, function(childCategory, key){
						$scope.childParentCategoriesMap[childCategory.id] = category;
						calculateLevel(childCategory, 2);
					});
				});
							
				if(successCallback != undefined){
					successCallback();
				}
			}).finally(function(){
				$scope.loading = false;
			});
		};
		
		$scope.saveCategory = function(category){			
			$scope.hideError();
			
			if(category.parentCategory != undefined && category.parentCategory.id == undefined){
				category.parentCategory = {id : category.parentCategory};
			}
			
			$http.post(kubikProductCategories.categoriesUrl, category).success(function(){
				$(".edit-category-modal").modal("hide");
			}).error(function(data){
				$scope.error = data.message;
			}).finally(function(){
				$scope.loadCategories();
			});
		};
		
		$scope.kubikProductCategories = kubikProductCategories;
		
		$scope.childParentCategoriesMap = {};
		$scope.categoriesTree = {};
		$scope.categories = [];
		$scope.loadCategories();
	});
};

window.KubikProductCategories.prototype.closeModal = function(){
	this.$modal.modal("hide");
};

window.KubikProductCategories.prototype.openModal = function(searchQuery){
	this.$modal.modal({
		backdrop : "static",
		keyboard : false
	});
};