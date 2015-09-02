package com.cspinformatique.kubik.domain.broadleaf.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.cspinformatique.broadleaf.client.model.BlcCategory;
import com.cspinformatique.kubik.domain.broadleaf.repository.BroadleafNotificationBaseRepository;
import com.cspinformatique.kubik.domain.broadleaf.repository.CategoryNotificationRepository;
import com.cspinformatique.kubik.domain.product.service.CategoryService;
import com.cspinformatique.kubik.model.broadleaf.BroadleafNotification.Action;
import com.cspinformatique.kubik.model.broadleaf.BroadleafNotification.Status;
import com.cspinformatique.kubik.model.broadleaf.CategoryNotification;
import com.cspinformatique.kubik.model.product.Category;

@Component
public class CategoryNotificationProcessor extends NotificationProcessor<CategoryNotification> {
	@Autowired
	private CategoryNotificationRepository categoryNotifiocationRepository;

	@Autowired
	private CategoryService categoryService;

	@Override
	protected BroadleafNotificationBaseRepository<CategoryNotification> getRepository() {
		return categoryNotifiocationRepository;
	}

	@Override
	protected Status process(CategoryNotification broadleafNotification) {
		if (broadleafNotification.getAction().equals(Action.UPDATE)) {
			Category category = categoryService.findOne(broadleafNotification.getCategoryId());
			
			BlcCategory blcCategory = broadleafTemplate.exchange("/catalog/category", HttpMethod.POST, category,
					BlcCategory.class);

			if (category.getBroadleafId() == null) {
				category.setBroadleafId(blcCategory.getId());

				categoryService.save(category, true);
			}
		} else {
			broadleafTemplate.exchange("/catalog/category/" + broadleafNotification.getBroadleafId(), HttpMethod.DELETE, null,
					Void.class);
		}

		return Status.PROCESSED;
	}
}
