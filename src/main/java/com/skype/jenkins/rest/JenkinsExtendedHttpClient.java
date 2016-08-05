package com.skype.jenkins.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.client.validator.HttpResponseValidator;
import com.skype.jenkins.logger.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

public class JenkinsExtendedHttpClient extends JenkinsHttpClient {

    private CloseableHttpClient client;
    private HttpResponseValidator httpResponseValidator;

    public JenkinsExtendedHttpClient(final URI uri) {
        super(uri, DefaultSecureHttpClient.getSecureClient());
        this.client = DefaultSecureHttpClient.getSecureClient();
        this.httpResponseValidator = new HttpResponseValidator();
    }

    public JenkinsExtendedHttpClient(final String jenkinsHost) {
        this(prepareUri(jenkinsHost));

    }

    private static URI prepareUri(final String jenkinsHost) {
        URI uri = null;
        try {
            uri = new URI(jenkinsHost);// +"/jenkins");
        } catch (URISyntaxException e) {
            Logger.out.error(e);
        }
        return uri;
    }

    @Override
    public String get(final String path) throws IOException {
        if (!path.contains("thucydides")) {
            return super.get(path);
        } else {
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

        }
        return null;
    }

}
