package com.skype.jenkins.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.client.util.UrlUtils;
import com.offbytwo.jenkins.client.validator.HttpResponseValidator;
import com.offbytwo.jenkins.model.BaseModel;
import com.skype.jenkins.logger.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

//used for custom rest for thucydides(serenity) report
public class JenkinsExtendedHttpClient extends JenkinsHttpClient {

    private CloseableHttpClient client;
    private HttpResponseValidator httpResponseValidator;
    private URI uri;
    private String context;
    private HttpContext localContext;
    private ObjectMapper mapper;


    public JenkinsExtendedHttpClient(final URI uri) {
        super(uri, DefaultSecureHttpClient.getSecureClient());
        this.client = DefaultSecureHttpClient.getSecureClient();
        this.httpResponseValidator = new HttpResponseValidator();
        this.uri = uri;
        this.context = uri.getPath();
        if (!this.context.endsWith("/")) {
            this.context = this.context + "/";
        }
        this.localContext = new BasicHttpContext();
        this.localContext.setAttribute("preemptive-auth", new BasicScheme());
        this.mapper = getDefaultMapper();
    }

    public JenkinsExtendedHttpClient(final String jenkinsHost) {
        this(prepareUri(jenkinsHost));
    }

    private static URI prepareUri(final String jenkinsHost) {
        URI uri = null;
        try {
            uri = new URI(jenkinsHost);
        } catch (URISyntaxException e) {
            Logger.out.error(e);
        }
        return uri;
    }

    public String getSerenityReport(final String path) throws IOException {
        try {
            HttpGet getMethod = new HttpGet(new URI(path));
            HttpResponse response = client.execute(getMethod);

            try {
                httpResponseValidator.validateResponse(response);
                return IOUtils.toString(response.getEntity().getContent());
            } finally {
                EntityUtils.consume(response.getEntity());
                getMethod.releaseConnection();
            }
        } catch (URISyntaxException e) {
            Logger.out.error(e);
        }
        return null;
    }

    @Override
    public <T extends BaseModel> T get(String path, Class<T> cls) throws IOException {
        URI uri;
        if(path.contains("/api/json")){
            uri = UrlUtils.toNoApiUri(this.uri, this.context, path);
        }else {
            uri = UrlUtils.toJsonApiUri(this.uri, this.context, path);
        }
        HttpGet getMethod = new HttpGet(uri);
        getMethod.setHeader("cache-control","no-cache");
        HttpResponse response = this.client.execute(getMethod, this.localContext);

        BaseModel var5;
        try {
            this.httpResponseValidator.validateResponse(response);
            var5 = this.objectFromResponse(cls, response);
        } finally {
            EntityUtils.consume(response.getEntity());
            getMethod.releaseConnection();
        }

        return (T)var5;
    }

    private <T extends BaseModel> T objectFromResponse(Class<T> cls, HttpResponse response) throws IOException {
        InputStream content = response.getEntity().getContent();
        byte[] bytes = ByteStreams.toByteArray(content);
        T result = mapper.readValue(bytes, cls);
        result.setClient(this);
        return result;
    }

    private ObjectMapper getDefaultMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }
}
