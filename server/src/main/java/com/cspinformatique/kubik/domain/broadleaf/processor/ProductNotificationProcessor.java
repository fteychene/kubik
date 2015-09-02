package com.cspinformatique.kubik.domain.broadleaf.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.cspinformatique.broadleaf.client.model.BlcProduct;
import com.cspinformatique.kubik.domain.broadleaf.repository.BroadleafNotificationBaseRepository;
import com.cspinformatique.kubik.domain.broadleaf.repository.ProductNotificationRepository;
import com.cspinformatique.kubik.domain.product.service.ProductService;
import com.cspinformatique.kubik.model.broadleaf.BroadleafNotification.Status;
import com.cspinformatique.kubik.model.broadleaf.ProductNotification;
import com.cspinformatique.kubik.model.product.Product;

@Component
public class ProductNotificationProcessor extends NotificationProcessor<ProductNotification> {
	@Autowired
	private ProductService productService;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ProductNotificationRepository productNotificationRepository;

	@Value("${kubik.ean13}")
	private String companyEan13;

	@Override
	protected Status process(ProductNotification broadleafNotification) {
		Product product = productService.findOne(broadleafNotification.getProduct().getId());

		BlcProduct blcProduct = broadleafTemplate.exchange("/catalog/product", HttpMethod.POST, product,
				BlcProduct.class);

		if (product.isDilicomReference()) {
			try {
				ResponseEntity<byte[]> imageResponse = restTemplate.getForEntity("http://images1.centprod.com/"
						+ companyEan13 + "/" + product.getImageEncryptedKey() + "-cover-medium.jpg", byte[].class);

				if (imageResponse.getStatusCode().equals(HttpStatus.OK)) {
					broadleafTemplate.exchange("/catalog/product" + blcProduct.getId() + "/image?ext=jpg",
							HttpMethod.POST, imageResponse, Long.class);
				}
			} catch (HttpClientErrorException httpClientErrorEx) {
				if (!httpClientErrorEx.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
					throw httpClientErrorEx;
				}
			}
		}

		if (product.getBroadleafId() == null) {
			product.setBroadleafId(blcProduct.getId());

			productService.save(product, true);
		}

		return Status.PROCESSED;
	}

	@Override
	protected BroadleafNotificationBaseRepository<ProductNotification> getRepository() {
		return productNotificationRepository;
	}

}
