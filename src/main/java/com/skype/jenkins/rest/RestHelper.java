package com.skype.jenkins.rest;

import java.net.URI;
import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


public class RestHelper {

    private static RestTemplate restTemplate;

    protected RestHelper() {
    }

    protected static synchronized RestTemplate getRestTemplate() {
        if (null == restTemplate) {
            restTemplate = new RestTemplate();
            restTemplate.setRequestFactory(createHttpClientFactory());
        }
        return restTemplate;
    }

    private static synchronized ClientHttpRequestFactory createHttpClientFactory() {
        HttpComponentsClientHttpRequestFactory clientFactory = new HttpComponentsClientHttpRequestFactory(
                DefaultSecureHttpClient.getSecureClient());
        return clientFactory;
    }

    protected static synchronized HttpHeaders getHeadersWithJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return headers;
    }

    protected static HttpEntity<String> getHttpEntityWithHeaders() {
        return new HttpEntity<>(getHeadersWithJson());
    }


    private static synchronized ResponseEntity<String> send(final URI uri, final HttpMethod httpMethod, final HttpEntity<?> entity) {
        return getRestTemplate().exchange(uri, httpMethod, entity, String.class);
    }

    protected static synchronized ResponseEntity<String> sendAndGetResponse(final String uri, final HttpMethod httpMethod,
                                                                            final HttpEntity<?> entity) {
        ResponseEntity<String> response = null;
        try {
            response = send(UriComponentsBuilder.fromHttpUrl(uri).build().toUri(), httpMethod, entity);
        } catch (final HttpClientErrorException | HttpServerErrorException e) {
        }
        return response;
    }
}
