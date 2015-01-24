package com.cspinformatique.kubik.sales.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cspinformatique.kubik.jasper.service.ReportService;
import com.cspinformatique.kubik.sales.model.Invoice;
import com.cspinformatique.kubik.sales.model.Payment;
import com.cspinformatique.kubik.sales.service.InvoiceService;
import com.cspinformatique.kubik.sales.service.InvoiceStatusService;
import com.cspinformatique.kubik.sales.service.PaymentService;

@Controller
@RequestMapping("/invoice")
public class InvoiceController {
	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private InvoiceStatusService invoiceStatusService;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private ReportService reportService;

	@RequestMapping(value = "/{invoiceId}/receipt", method = RequestMethod.GET, produces = "application/pdf")
	public void generatePdfReport(@PathVariable int invoiceId,
			HttpServletResponse response) {
		try {
			JasperExportManager.exportReportToPdfStream(this.reportService
					.generateReceiptReport(invoiceService.findOne(invoiceId)),
					response.getOutputStream());
		} catch (JRException | IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@RequestMapping(method = RequestMethod.GET, params = "status", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Page<Invoice> findByStatus(
			@RequestParam String status,
			@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "50") Integer resultPerPage,
			@RequestParam(required = false) Direction direction,
			@RequestParam(defaultValue = "paidDate") String sortBy) {
		return this.invoiceService.findByStatus(invoiceStatusService
				.findOne(status), new PageRequest(page, resultPerPage,
				direction != null ? direction : Direction.DESC, sortBy));
	}

	@RequestMapping(value = "/{invoiceId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Invoice findOne(@PathVariable int invoiceId) {
		return this.invoiceService.findOne(invoiceId);
	}

	@RequestMapping(value = "/{invoiceId}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public String getInvoiceDetailsPage() {
		return "sales/invoice-details";
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public String getInvoicesPage() {
		return "sales/invoices-page";
	}

	@RequestMapping(method = { RequestMethod.POST, RequestMethod.PUT }, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Invoice save(@RequestBody Invoice invoice) {
		return this.invoiceService.save(invoice);
	}

	@RequestMapping(value = "/{invoiceId}/payment", method = {
			RequestMethod.POST, RequestMethod.PUT }, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Iterable<Payment> savePayments(
			Iterable<Payment> payments) {
		return this.paymentService.save(payments);
	}
}