package com.cspinformatique.kubik.model.broadleaf;

import java.util.Date;

import javax.persistence.Entity;

@Entity
public class CategoryNotification extends BroadleafNotification {
	private int categoryId;
	private Long broadleafId;

	public CategoryNotification() {

	}

	public CategoryNotification(int id, Action action, Status status, Date creationDate, Date processedDate, Date errorDate,
			String error, int categoryId, Long broadleafId) {
		super(id, status, Type.CATEGORY, action, creationDate, processedDate, errorDate, error);

		this.categoryId = categoryId;
		this.broadleafId = broadleafId;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public Long getBroadleafId() {
		return broadleafId;
	}

	public void setBroadleafId(Long broadleafId) {
		this.broadleafId = broadleafId;
	}
}
