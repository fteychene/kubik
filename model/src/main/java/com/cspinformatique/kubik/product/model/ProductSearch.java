package com.cspinformatique.kubik.product.model;

import java.util.List;

public class ProductSearch {
	private List<Product> products;
	
	public ProductSearch(){
		
	}

	public ProductSearch(List<Product> products) {
		this.products = products;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
}
