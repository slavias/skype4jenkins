package com.skype.jenkins.rest;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.skype.jenkins.logger.Logger;

import org.apache.http.client.CookieStore;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public final class DefaultSecureHttpClient {

    private static CloseableHttpClient secureHttpClient;

    private DefaultSecureHttpClient() {
    }

    public static synchronized CloseableHttpClient getSecureClient() {
        if (secureHttpClient == null) {
            createDefaultSSLClient();
            Logger.out.info("New rest client created (ignore certs, TLS)");
        }
        return secureHttpClient;
    }

    private static void createDefaultSSLClient() {
        SSLContext sslContext = null;
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            sslContext = SSLContext.getInstance("TLS");
            TrustManager trustManager = new CustomX509TrustManager();
            sslContext.init(null, new TrustManager[] { trustManager }, null);
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException | CertificateException
                | IOException e) {
            throw new IllegalStateException("SSL certification exception", e);
        }
        SSLConnectionSocketFactory secureSocketFactory = new SSLConnectionSocketFactory(sslContext,
                new NoopHostnameVerifier());
        HttpClientBuilder builder = HttpClientBuilder.create();

        builder.setSSLSocketFactory(secureSocketFactory);
        builder.setDefaultCookieStore(new CustomCookieStore());
        secureHttpClient = builder.build();
    }

    public static class CustomX509TrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(final X509Certificate[] x509Certificates, final String s)
                throws CertificateException {
            /*
             * This method does not need to be implemented.
             */
        }

        @Override
        public void checkServerTrusted(final X509Certificate[] x509Certificates, final String s)
                throws CertificateException {
            /*
             * This method does not need to be implemented.
             */
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

    }

    public static class CustomCookieStore implements CookieStore {

        @Override
        public void addCookie(final Cookie cookie) {
            // TODO Auto-generated method stub

        }

        @Override
        public List<Cookie> getCookies() {
            // TODO Auto-generated method stub
            return new ArrayList<Cookie>();
        }

        @Override
        public boolean clearExpired(final Date date) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void clear() {
            // TODO Auto-generated method stub

        }
    }
}
