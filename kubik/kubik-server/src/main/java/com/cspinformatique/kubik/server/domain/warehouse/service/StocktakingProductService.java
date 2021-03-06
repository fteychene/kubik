package com.cspinformatique.kubik.server.domain.warehouse.service;

import java.util.List;

import com.cspinformatique.kubik.server.model.product.Product;
import com.cspinformatique.kubik.server.model.warehouse.Stocktaking.Status;
import com.cspinformatique.kubik.server.model.warehouse.StocktakingProduct;

public interface StocktakingProductService {
	StocktakingProduct addProductToCategory(int productId, long categoryId);

	int countCategoriesWithProduct(int productId, long categoryId);

	void delete(long id);
	
	void delete(StocktakingProduct stocktakingProduct);

	List<StocktakingProduct> findByProduct(Product product);
	
	List<StocktakingProduct> findByProductAndStocktakingStatus(Product product, Status status);

	StocktakingProduct updateQuantity(long id, double quantity);

	StocktakingProduct save(StocktakingProduct stocktakingProduct);
}
