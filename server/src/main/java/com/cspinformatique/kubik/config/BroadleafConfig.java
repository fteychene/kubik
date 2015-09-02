package com.cspinformatique.kubik.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import com.cspinformatique.broadleaf.client.BroadleafTemplate;
import com.cspinformatique.kubik.domain.broadleaf.processor.CategoryNotificationProcessor;
import com.cspinformatique.kubik.domain.broadleaf.processor.NotificationProcessors;
import com.cspinformatique.kubik.domain.broadleaf.processor.ProductNotificationProcessor;
import com.cspinformatique.kubik.model.broadleaf.BroadleafNotification.Type;

@Configuration
public class BroadleafConfig {
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private CategoryNotificationProcessor categoryNotificationProcessor;

	@Autowired
	private ProductNotificationProcessor productNotificationProcessor;

	@Resource
	private Environment env;

	public @Bean NotificationProcessors notificationProcessors() {
		return new NotificationProcessors().addProcessor(Type.PRODUCT, productNotificationProcessor)
				.addProcessor(Type.CATEGORY, categoryNotificationProcessor);
	}

	public @Bean BroadleafTemplate broadleafTemplate() {
		return new BroadleafTemplate(env.getRequiredProperty("broadleaf.url"), env.getProperty("broadleaf.username"),
				env.getProperty("broadleaf.password"), restTemplate);
	}
}
