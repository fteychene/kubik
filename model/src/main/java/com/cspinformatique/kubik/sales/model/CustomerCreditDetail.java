package com.cspinformatique.kubik.sales.model;

import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.cspinformatique.kubik.product.model.Product;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class CustomerCreditDetail {
	private Integer id;
	private CustomerCredit customerCredit;
	private Product product;
	private double quantity;
	private double maxQuantity;
	private Map<Double, InvoiceTaxAmount> taxesAmounts;
	private Double unitPrice;
	private Double totalAmount;
	private Double totalTaxAmount;
	private Double totalTaxLessAmount;
	
	public CustomerCreditDetail(){
		
	}

	public CustomerCreditDetail(Integer id, CustomerCredit customerCredit,
			Product product, double quantity, double maxQuantity,
			Map<Double, InvoiceTaxAmount> taxesAmounts, Double unitPrice,
			Double totalAmount, Double totalTaxAmount, Double totalTaxLessAmount) {
		this.id = id;
		this.customerCredit = customerCredit;
		this.product = product;
		this.quantity = quantity;
		this.maxQuantity = maxQuantity;
		this.taxesAmounts = taxesAmounts;
		this.unitPrice = unitPrice;
		this.totalAmount = totalAmount;
		this.totalTaxAmount = totalTaxAmount;
		this.totalTaxLessAmount = totalTaxLessAmount;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JsonBackReference
	public CustomerCredit getCustomerCredit() {
		return customerCredit;
	}

	public void setCustomerCredit(CustomerCredit customerCredit) {
		this.customerCredit = customerCredit;
	}

	@ManyToOne
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getMaxQuantity() {
		return maxQuantity;
	}

	public void setMaxQuantity(double maxQuantity) {
		this.maxQuantity = maxQuantity;
	}

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval=true)
	public Map<Double, InvoiceTaxAmount> getTaxesAmounts() {
		return taxesAmounts;
	}

	public void setTaxesAmounts(Map<Double, InvoiceTaxAmount> taxesAmounts) {
		this.taxesAmounts = taxesAmounts;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Double getTotalTaxAmount() {
		return totalTaxAmount;
	}

	public void setTotalTaxAmount(Double totalTaxAmount) {
		this.totalTaxAmount = totalTaxAmount;
	}

	public Double getTotalTaxLessAmount() {
		return totalTaxLessAmount;
	}

	public void setTotalTaxLessAmount(Double totalTaxLessAmount) {
		this.totalTaxLessAmount = totalTaxLessAmount;
	}
}