package com.cspinformatique.kubik.product.service.impl;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cspinformatique.kubik.product.model.AvailabilityCode;
import com.cspinformatique.kubik.product.model.BarcodeType;
import com.cspinformatique.kubik.product.model.PriceType;
import com.cspinformatique.kubik.product.model.Product;
import com.cspinformatique.kubik.product.model.ProductType;
import com.cspinformatique.kubik.product.model.ReturnType;
import com.cspinformatique.kubik.product.model.Supplier;
import com.cspinformatique.kubik.product.repository.ProductRepository;
import com.cspinformatique.kubik.product.service.ImageService;
import com.cspinformatique.kubik.product.service.ProductService;
import com.cspinformatique.kubik.product.service.SupplierService;
import com.cspinformatique.kubik.reference.model.Reference;
import com.cspinformatique.kubik.reference.service.ReferenceService;

@Service
public class ProductServiceImpl implements ProductService {
	private static final Logger logger = LoggerFactory
			.getLogger(ProductServiceImpl.class);

	@Autowired
	ImageService imageService;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ReferenceService referenceServive;

	@Autowired
	private SupplierService supplierService;

	@Override
	public Product buildProductFromReference(Reference reference) {
		Supplier supplier = this.supplierService.findByEan13(reference
				.getSupplierEan13());
		Integer productId = null;
		if (reference.isImportedInKubik()) {
			Product product = this.findByEan13AndSupplier(reference.getEan13(),
					supplier);

			if (product != null) {
				productId = product.getId();
			}
		}

		return new Product(productId, reference.getEan13(), supplier,
				AvailabilityCode.parseByCode(reference.getAvailability()),
				reference.getPriceType() != null ? PriceType
						.parseByCode(reference.getPriceType()) : null,
				reference.getPriceTaxIn(), reference.getSchoolbook(),
				reference.getTvaRate1(), reference.getPriceTaxOut1(),
				reference.getTvaRate2(), reference.getPriceTaxOut2(),
				reference.getTvaRate3(), reference.getPriceTaxOut3(),
				reference.getReturnType() != null ? ReturnType
						.parseByCode(reference.getReturnType()) : null,
				reference.getAvailableForOrder(), reference.getDatePublished(),
				ProductType.parseByCode(reference.getProductType()),
				reference.getPublishEndDate(), reference.getStandardLabel(),
				reference.getCashRegisterLabel(), reference.getThickness(),
				reference.getWidth(), reference.getHeight(),
				reference.getWeight(), reference.getExtendedLabel(),
				reference.getPublisher(), reference.getCollection(),
				reference.getAuthor(), reference.getPublisherPresentation(),
				reference.getIsbn(), reference.getSupplierReference(),
				reference.getCollectionReference(), reference.getTheme(),
				reference.getPublisherIsnb(),
				reference.getReplacingAReference(),
				reference.getReplacedByAReference(),
				reference.getReplacesEan13(), reference.getReplacedByEan13(),
				reference.getOrderableByUnit(),
				reference.getBarcodeType() != null ? BarcodeType
						.parseByCode(reference.getBarcodeType()) : null,
				reference.getMainReference(),
				reference.getSecondaryReference(),
				reference.getReferencesCount(), null);
	}

	@Override
	public Page<Product> findAll(Pageable pageable) {
		Page<Product> productPage = this.productRepository.findAll(pageable);

		for (Product product : productPage.getContent()) {
			this.calculateImageEncryptedKey(product);
		}

		return productPage;
	}

	@Override
	public Product findByEan13AndSupplier(String ean13, Supplier supplier) {
		return this.productRepository.findByEan13AndSupplier(ean13, supplier);
	}

	@Override
	public Product findOne(int id) {
		Product product = this.productRepository.findOne(id);

		this.calculateImageEncryptedKey(product);

		return product;
	}

	private void calculateImageEncryptedKey(Product product) {
		product.setImageEncryptedKey(this.imageService.getEncryptedUrl(
				product.getEan13(), product.getSupplier().getEan13()));
	}

	@PostConstruct
	protected void init() throws InterruptedException {
		Thread.sleep(2000);

		this.generateProductsFromImportedReferences();
	}

	@Override
	public Product generateProductIfNotFound(String ean13, String supplierEan13) {
		Supplier supplier = this.supplierService.findByEan13(supplierEan13);

		if (supplier == null) {
			supplier = this.supplierService
					.generateSupplierIfNotFound(supplierEan13);
		}

		Product product = this.findByEan13AndSupplier(ean13, supplier);
		if (product == null) {
			Reference reference = this.referenceServive
					.findByEan13AndSupplierEan13(ean13, supplierEan13);

			logger.info("Generating product " + ean13 + " from supplier "
					+ supplierEan13 + ".");

			product = this.save(this.buildProductFromReference(reference));

			reference.setImportedInKubik(true);

			referenceServive.save(reference);
		}

		return product;
	}

	private void generateProductsFromImportedReferences() {
		Pageable pageRequest = new PageRequest(0, 100);
		Page<Reference> page = null;
		do {
			for (Reference reference : this.referenceServive
					.findByImportedInKubik(true, pageRequest)) {
				this.generateProductIfNotFound(reference.getEan13(),
						reference.getSupplierEan13());
			}

			pageRequest = pageRequest.next();
		} while (page != null && page.hasNext());
	}

	@Override
	public Product save(Product product) {
		return this.productRepository.save(product);
	}

	@Override
	public Page<Product> search(String query, Pageable pageable) {
		Page<Product> page = this.productRepository.search("%" + query + "%",
				pageable);

		for (Product product : page.getContent()) {
			this.calculateImageEncryptedKey(product);
		}

		return page;
	}
}