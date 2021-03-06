package com.cspinformatique.kubik.server.domain.kos.rest;

import java.util.Base64;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.cspinformatique.kubik.common.rest.RestTemplateExecutor;

public class KosTemplate {
	private RestTemplateExecutor restTemplateExecutor;
	private String kosUrl;
	private HttpHeaders authorizationHeader;

	public KosTemplate(Environment environment) {
		this.restTemplateExecutor = new RestTemplateExecutor();

		kosUrl = environment.getRequiredProperty("kos.notification.url");
		authorizationHeader = KosTemplate.createBasicAuthHeader(
				environment.getRequiredProperty("kos.notification.username"),
				environment.getRequiredProperty("kos.notification.password"));
	}

	public <T> T exchange(String ressource, HttpMethod method, ParameterizedTypeReference<T> responseType) {
		return restTemplateExecutor.execute(restTemplate -> restTemplate.exchange(kosUrl + ressource, method,
				new HttpEntity<>(authorizationHeader), responseType));
	}

	public <T> T exchange(String resource, MultiValueMap<String, String> parametersMap, HttpMethod method,
			ParameterizedTypeReference<T> responseType) {
		StringBuilder parametersBuilder = new StringBuilder();

		boolean first = true;
		for (Entry<String, List<String>> entry : parametersMap.entrySet()) {
			for (String value : entry.getValue()) {
				if (!first)
					parametersBuilder.append("&");
				else
					first = false;

				String key = entry.getKey();
				if (key.endsWith("[]"))
					key = key.substring(0, key.length() - 2);

				parametersBuilder.append(key).append("=").append(value);
			}
		}

		String parameters = parametersBuilder.toString();

		return restTemplateExecutor.execute(restTemplate -> restTemplate.exchange(
				kosUrl + resource + (!parameters.isEmpty() ? "?" + parameters : ""), method,
				new HttpEntity<>(authorizationHeader), responseType));
	}

	public <T> T exchange(String resource, MultiValueMap<String, String> parameters, HttpMethod method,
			Class<T> responseType) {
		return restTemplateExecutor.execute(restTemplate -> restTemplate.exchange(
				UriComponentsBuilder.fromHttpUrl(kosUrl + resource).queryParams(parameters).toUriString(), method,
				new HttpEntity<>(authorizationHeader), responseType));
	}

	public <T> T exchange(String ressource, HttpMethod method, Class<T> responseType) {
		return restTemplateExecutor.execute(restTemplate -> restTemplate.exchange(kosUrl + ressource, method,
				new HttpEntity<>(authorizationHeader), responseType));
	}

	public <T, S> T exchange(String ressource, HttpMethod method, S body, Class<T> responseType) {
		return restTemplateExecutor.execute(restTemplate -> restTemplate.exchange(kosUrl + ressource, method,
				new HttpEntity<>(body, authorizationHeader), responseType));
	}

	public <T, S> T exchange(String ressource, HttpMethod method, S body, ParameterizedTypeReference<T> responseType) {
		return restTemplateExecutor.execute(restTemplate -> restTemplate.exchange(kosUrl + ressource, method,
				new HttpEntity<>(body, authorizationHeader), responseType));
	}

	private static HttpHeaders createBasicAuthHeader(final String username, final String password) {
		return new HttpHeaders() {
			private static final long serialVersionUID = 1766341693637204893L;

			{
				String auth = username + ":" + password;
				byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
				String authHeader = "Basic " + new String(encodedAuth);
				this.set("Authorization", authHeader);
			}
		};
	}
}
