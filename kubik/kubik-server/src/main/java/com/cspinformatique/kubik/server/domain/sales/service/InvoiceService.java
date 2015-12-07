package com.cspinformatique.kubik.server.domain.sales.service;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.cspinformatique.kubik.server.model.sales.CashRegisterSession;
import com.cspinformatique.kubik.server.model.sales.Invoice;
import com.cspinformatique.kubik.server.model.sales.InvoiceDetail;
import com.cspinformatique.kubik.server.model.sales.InvoiceStatus;

public interface InvoiceService {
	void calculateInvoiceTaxes(Invoice invoice);
	
	InvoiceDetail findDetailByInvoiceIdAndProductEan13(int invoiceId, String ean13);
	
	Iterable<Invoice> findInvoiceByCashRegisterSessionAndInDraft(CashRegisterSession session);
	
	Invoice generateNewInvoice(CashRegisterSession session);
	
	Page<Invoice> findAll(Pageable pageable);
	
	Invoice findByNumber(String number);
	
	List<Invoice> findByPaidDate(Date paidDate);
	
	Page<Invoice> findByPaidDateBetweenAndStatus(Date startPaidDate,
			Date startEndDate, InvoiceStatus status, Pageable pageable);
	
	Page<Invoice> findByStatus(InvoiceStatus status, Pageable pageable);
	
	Invoice findFirstPaidInvoice();
	
	Integer findNext(int id);
	
	Invoice findOne(int id);
	
	Integer findPrevious(int id);
	
	double findProductQuantitySold(int productId);
	
	void initializeInvoiceNumbers();
	
	void recalculateInvoiceTaxes();
	
	Invoice save(Invoice invoice);
}