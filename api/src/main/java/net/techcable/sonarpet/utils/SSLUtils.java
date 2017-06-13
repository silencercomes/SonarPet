package net.techcable.sonarpet.utils;

import lombok.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SSLUtils {
    private SSLUtils() {}

    /**
     * Trust certificates signed by <a href="https://letsencrypt.org/">letsencrypt</a>,
     * by trusting their root
     */
    public static final boolean TRUST_LETSENCRYPT = true;
    @Nullable // Lazy-loaded
    private static X509Certificate cachedLetsencryptCertificate;
    @Nonnull
    public static X509Certificate getLetsEncryptCertificate() throws IOException, CertificateException {
        X509Certificate result = cachedLetsencryptCertificate;
        if (result == null) {
            synchronized (SSLUtils.class) {
                result = cachedLetsencryptCertificate;
                if (result == null) {
                    try (InputStream in = SSLUtils.class.getResourceAsStream("/certs/letsencrypt-ISRG-Root-X1.der")) {
                        result = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(in);
                        cachedLetsencryptCertificate = result;
                    }
                }
            }
        }
        return result;
    }
    @Nonnull // Eagerly loaded
    private static final ImprovedTrustManager IMPROVED_TRUST_MANAGER = new ImprovedTrustManager(findDelegateTrustManager());
    @SneakyThrows({NoSuchAlgorithmException.class, KeyManagementException.class})
    public static SSLContext createSslContext() {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(
                null,
                new TrustManager[]{IMPROVED_TRUST_MANAGER}, // NOTE: Only the first X509TrustManager is used, so pass ours
                null
        );
        return context;
    }
    public static URLConnection openConnection(URL url) throws IOException {
        if (url.getProtocol().equals("https")) {
            return openSecureConnection(url);
        } else {
            return url.openConnection();
        }
    }
    public static HttpsURLConnection openSecureConnection(URL url) throws IOException {
        if (!url.getProtocol().equals("https")) {
            throw new IllegalArgumentException("URL isn't https: " + url);
        }
        SSLSocketFactory socketFactory = createSslContext().getSocketFactory();
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setSSLSocketFactory(socketFactory);
        return connection;
    }

    private static X509TrustManager findDelegateTrustManager() {
        TrustManagerFactory factory = getTrustFactory();
        for (TrustManager trustManager : factory.getTrustManagers()) {
            if (trustManager != null && trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        throw new RuntimeException("Unable to find delegate X509TrustManager!");
    }
    @SneakyThrows({NoSuchAlgorithmException.class, KeyStoreException.class})
    private static TrustManagerFactory getTrustFactory() {
        TrustManagerFactory factory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm()
        );
        factory.init((KeyStore) null);
        return factory;
    }

    static class ImprovedTrustManager implements X509TrustManager {
        private final X509TrustManager delegate;

        ImprovedTrustManager(X509TrustManager delegate) {
            this.delegate = Objects.requireNonNull(delegate);
        }

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            delegate.checkClientTrusted(x509Certificates, s);
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            delegate.checkServerTrusted(x509Certificates, s);
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            List<X509Certificate> result = new ArrayList<>();
            Collections.addAll(result, delegate.getAcceptedIssuers());
            if (TRUST_LETSENCRYPT) {
                try {
                    // If we trust letsencrypt (we do by default), then inject their certificate
                    result.add(getLetsEncryptCertificate());
                } catch (IOException | CertificateException e) {
                    throw new RuntimeException("Unable to load letsencrypt certificate", e);
                }
            }
            return result.toArray(new X509Certificate[result.size()]);
        }
    }
}
