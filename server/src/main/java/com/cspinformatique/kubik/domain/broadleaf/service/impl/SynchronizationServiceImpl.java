package com.cspinformatique.kubik.domain.broadleaf.service.impl;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cspinformatique.broadleaf.client.BroadleafTemplate;
import com.cspinformatique.broadleaf.client.model.BlcCategory;
import com.cspinformatique.broadleaf.client.model.BlcProduct;
import com.cspinformatique.broadleaf.client.model.BroadleafException;
import com.cspinformatique.kubik.domain.broadleaf.service.BroadleafNotificationService;
import com.cspinformatique.kubik.domain.broadleaf.service.SynchronizationService;
import com.cspinformatique.kubik.domain.product.service.CategoryService;
import com.cspinformatique.kubik.domain.product.service.ProductService;
import com.cspinformatique.kubik.model.broadleaf.BroadleafNotification.Status;
import com.cspinformatique.kubik.model.product.Category;
import com.cspinformatique.kubik.model.product.Product;

@Service
public class SynchronizationServiceImpl implements SynchronizationService {
	@Resource
	private BroadleafNotificationService broadleafNotificationService;

	@Autowired
	private BroadleafTemplate broadleafTemplate;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Override
	@Transactional
	public void executeInitialLoad() {
		for (Category category : categoryService.findByParentCategory(null)) {
			this.updateCategory(category);
		}

		Page<Product> productPage = null;
		Pageable pageable = new PageRequest(0, 100);
		do {
			productPage = productService.findByCategoryNotNull(pageable);

			for (Product product : productPage.getContent()) {
				try {
					product.setBroadleafId(broadleafTemplate.exchange("/catalog/product?kubikId=" + product.getId(),
							HttpMethod.GET, null, BlcProduct.class).getId());

				} catch (BroadleafException broadleafEx) {
					productService.resetBroadleafId(product.getId());

					if (broadleafEx.getErrorCode() != HttpStatus.NOT_FOUND.value()) {
						throw new RuntimeException(broadleafEx);
					}
				}
				productService.save(product);
			}

			pageable = pageable.next();
		} while (productPage.hasNext());
	}

	private void updateCategory(Category category) {
		try {
			category.setBroadleafId(broadleafTemplate
					.exchange("/catalog/category?name=" + category.getName(), HttpMethod.GET, null, BlcCategory.class)
					.getId());
		} catch (BroadleafException broadleafEx) {
			categoryService.resetBroadleafId(category.getId());

			if (broadleafEx.getErrorCode() != HttpStatus.NOT_FOUND.value()) {
				throw new RuntimeException(broadleafEx);
			}
		}

		// Generate a notification for the category.
		categoryService.save(category);

		for (Category childCategory : category.getChildCategories()) {
			updateCategory(childCategory);
		}
	}
}
