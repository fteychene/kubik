package com.cspinformatique.kubik.model.product;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Category.class)
public class Category {
	private int id;
	private String name;
	private Category parentCategory;
	private List<Category> childCategories;
	private Boolean rootCategory;
	private Long broadleafId;

	public Category() {

	}

	public Category(int id, String name, Category parentCategory, List<Category> childCategories, Boolean rootCategory,
			Long broadleafId) {
		this.id = id;
		this.name = name;
		this.parentCategory = parentCategory;
		this.childCategories = childCategories;
		this.rootCategory = rootCategory;
		this.broadleafId = broadleafId;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(unique = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne
	public Category getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(Category parentCategory) {
		this.parentCategory = parentCategory;
	}

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "parentCategory")
	public List<Category> getChildCategories() {
		return childCategories;
	}

	public void setChildCategories(List<Category> childCategories) {
		this.childCategories = childCategories;
	}

	public Boolean getRootCategory() {
		return rootCategory;
	}

	public void setRootCategory(Boolean rootCategory) {
		this.rootCategory = rootCategory;
	}

	public Long getBroadleafId() {
		return broadleafId;
	}

	public void setBroadleafId(Long broadleafId) {
		this.broadleafId = broadleafId;
	}
}
