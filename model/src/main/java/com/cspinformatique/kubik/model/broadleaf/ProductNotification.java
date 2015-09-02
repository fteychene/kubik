package com.cspinformatique.kubik.model.broadleaf;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.cspinformatique.kubik.model.product.Product;

@Entity
public class ProductNotification extends BroadleafNotification {
	private Product product;

	public ProductNotification() {

	}

	public ProductNotification(int id, Action action, Status status, Date creationDate, Date processedDate, Date errorDate,
			String error, Product product) {
		super(id, status, Type.PRODUCT, action, creationDate, processedDate, errorDate, error);

		this.product = product;
	}

	@ManyToOne
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
}
