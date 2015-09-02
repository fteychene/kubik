package com.cspinformatique.kubik.domain.product.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cspinformatique.kubik.domain.broadleaf.service.BroadleafNotificationService;
import com.cspinformatique.kubik.domain.product.exception.CategoryNameAlreadyUsedException;
import com.cspinformatique.kubik.domain.product.repository.CategoryRepository;
import com.cspinformatique.kubik.domain.product.service.CategoryService;
import com.cspinformatique.kubik.domain.product.service.ProductService;
import com.cspinformatique.kubik.model.broadleaf.BroadleafNotification;
import com.cspinformatique.kubik.model.broadleaf.BroadleafNotification.Action;
import com.cspinformatique.kubik.model.broadleaf.CategoryNotification;
import com.cspinformatique.kubik.model.product.Category;
import com.cspinformatique.kubik.model.product.Product;

@Service
public class CategoryServiceImpl implements CategoryService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CategoryServiceImpl.class);

	private static final String DEFAULT_NAME = "Nouvelle Catégorie";

	@Autowired
	private BroadleafNotificationService broadleafNotificationService;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ProductService productService;

	private void assertNameNotAlreadyUsed(Category category) throws CategoryNameAlreadyUsedException {
		String name = category.getName();

		List<Category> existingCategory = categoryRepository.findByName(name);

		if (!existingCategory.isEmpty() && existingCategory.get(0).getId() != category.getId()) {
			throw new CategoryNameAlreadyUsedException(
					"Le nom " + category.getName() + " est déjà utilisé pour une autre catégorie.");
		}

	}

	@Override
	public void delete(Category category) {
		categoryRepository.delete(category);

		// Create a broadleaf notification for the category.
		if (category.getBroadleafId() != null) {
			this.broadleafNotificationService
					.save(new CategoryNotification(0, Action.DELETE, BroadleafNotification.Status.TO_PROCESS, null,
							null, null, null, category.getId(), category.getBroadleafId()));
		} else {
			LOGGER.error("Category " + category.getId()
					+ " deletion will not be notified to broadleaf since no broadleaf reference is defined.");
		}
	}

	@Override
	public void deleteProductCategories(Category category) {
		for (Product product : productService.findByCategory(category)) {
			product.setCategory(null);

			productService.save(product);
		}
	}

	@Override
	public List<Category> findAll() {
		return categoryRepository.findAll();
	}

	@Override
	public Page<Category> findAll(Pageable pageable) {
		return categoryRepository.findAll(pageable);
	}

	@Override
	public List<Category> findByParentCategory(Category category) {
		return categoryRepository.findByParentCategory(category);
	}

	@Override
	public List<Category> findByRootCategory(boolean rootCategory) {
		return categoryRepository.findByRootCategory(true);
	}

	@Override
	public Category findOne(int id) {
		return categoryRepository.findOne(id);
	}

	@Override
	public String generateNewName() {
		String name = null;
		int attempt = 1;
		do {
			name = DEFAULT_NAME;

			if (attempt != 1) {
				name += " " + attempt;
			}

			List<Category> category = categoryRepository.findByName(name);

			if (!category.isEmpty()) {
				name = null;
			}

			++attempt;
		} while (name == null);

		return name;
	}

	@Override
	public void resetBroadleafId(int categoryId) {
		Category category = findOne(categoryId);
		
		category.setBroadleafId(null);

		categoryRepository.save(category);
	}

	@Override
	public Category save(Category category) {
		return save(category, false);
	}

	@Override
	public Category save(Category category, boolean skipBroadleafNotification) {
		assertNameNotAlreadyUsed(category);

		if (category.getId() != 0) {
			category.setBroadleafId(findOne(category.getId()).getBroadleafId());
		}

		category = categoryRepository.save(category);

		if (!skipBroadleafNotification) {
			// Create a broadleaf notification for the category.
			this.broadleafNotificationService
					.save(new CategoryNotification(0, Action.UPDATE, BroadleafNotification.Status.TO_PROCESS, null,
							null, null, null, category.getId(), category.getBroadleafId()));
		}

		return category;
	}
}
