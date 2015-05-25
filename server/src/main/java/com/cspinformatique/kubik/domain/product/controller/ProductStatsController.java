package com.cspinformatique.kubik.domain.product.controller;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cspinformatique.kubik.domain.product.service.ProductStatsService;
import com.cspinformatique.kubik.model.product.ProductStats;

@Controller
@RequestMapping("/productStats")
public class ProductStatsController {
	@Autowired
	private ProductStatsService productStatsService;

	@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Page<ProductStats> findProductStats(
			@RequestParam(required = false) Date startDate,
			@RequestParam(required = false) Date endDate,
			@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "1000") Integer resultPerPage) {
		if (startDate == null) {
			startDate = new Date(0);
		}
		if (endDate == null) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, 1000);

			endDate = cal.getTime();
		}

		return this.productStatsService.findAll(startDate, endDate,
				new PageRequest(page, resultPerPage, new Sort(Direction.DESC,
						"sold")));
	}

	@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
	public String getProductsStatsPage() {
		return "product/product-stats";
	}
}
