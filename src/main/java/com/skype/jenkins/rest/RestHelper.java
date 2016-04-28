package com.skype.jenkins.rest;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;

import com.skype.jenkins.logger.Logger;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


public class RestHelper {

    private static RestTemplate restTemplate;

    protected RestHelper() {
    }

    protected static RestTemplate getRestTemplate() {
        if (null == restTemplate) {
            restTemplate = new RestTemplate();
            restTemplate.setRequestFactory(createHttpClientFactory());
        }
        return restTemplate;
    }

    private static ClientHttpRequestFactory createHttpClientFactory() {
        int timeout = 60 * 1000;
        HttpComponentsClientHttpRequestFactory clientFactory = new HttpComponentsClientHttpRequestFactory(
                DefaultSecureHttpClient.getSecureClient());
        return clientFactory;
    }
    
    protected static HttpHeaders getHeadersWithJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return headers;
    }
    
    protected static HttpEntity<String> getHttpEntity(final HttpHeaders headers) {
        return new HttpEntity<>(headers);
    }
    
    protected static HttpEntity<String> getHttpEntityWithHeaders() {
        return new HttpEntity<>(getHeadersWithJson());
    }
    
    protected static URI getUriFromString(final String uri) {
        return UriComponentsBuilder.fromHttpUrl(uri).build().toUri();
    }

    protected static URI getUriFromString(final String uri, final Map<String, String> uriParameters) {
        MultiValueMap<String, String> mvm = new LinkedMultiValueMap<>();
        mvm.setAll(uriParameters);
        return UriComponentsBuilder.fromHttpUrl(uri).queryParams(mvm).build().toUri();
    }
    
    protected static ResponseEntity<String> send(final URI uri, final HttpMethod httpMethod, final HttpEntity<?> entity) {
        return getRestTemplate().exchange(uri, httpMethod, entity, String.class);
    }

    protected static ResponseEntity<String> sendAndGetResponse(final URI uri, final HttpMethod httpMethod,
            final HttpEntity<?> entity, final HttpStatus code) {
        ResponseEntity<String> response = null;
        try {
            response = send(uri, httpMethod, entity);
        } catch (final HttpClientErrorException | HttpServerErrorException e) {
            Logger.out.error(e);
        }
        return response;
    }

    protected static ResponseEntity<String> sendAndGetResponse(final String uri, final HttpMethod httpMethod,
            final HttpEntity<?> entity, final HttpStatus code) {
        return sendAndGetResponse(getUriFromString(uri), httpMethod, entity, code);
    }
}
