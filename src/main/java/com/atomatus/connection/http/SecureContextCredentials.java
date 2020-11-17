package com.atomatus.connection.http;

import com.atomatus.connection.http.exception.SecureContextCredentialsException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.*;
import java.util.Objects;

@SuppressWarnings("unused")
final class SecureContextCredentials extends SecureContextCredentialsParams {

    public static class Builder extends SecureContextCredentialsParams {

        private String keystoreFile;

        private Builder checkInitFromFile() throws SecureContextCredentialsException {
            if(keystoreFile != null) {
                try (InputStream in = new FileInputStream(keystoreFile)) {
                    KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
                    keystore.load(in, password == null ? null : password.toCharArray());
                    keystoreFile = null;
                    return addClientKeyStore(keystore);
                } catch (Throwable e) {
                    throw new SecureContextCredentialsException(e);
                }
            }
            return this;
        }

        public Builder addClientKeyStore(KeyStore clientKeyStore) {
            this.clientKeyStore = Objects.requireNonNull(clientKeyStore);
            return this;
        }

        public Builder addClientPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder addClientTrustStore(KeyStore clientTrustStore) {
            this.clientTrustStore = Objects.requireNonNull(clientTrustStore);
            return this;
        }

        public Builder addClientKeyStore(String keystoreFile) {
            this.keystoreFile = keystoreFile;
            return this;
        }

        public SecureContextCredentials build() {
            try{
               return new SecureContextCredentials(checkInitFromFile());
            } finally {
                this.close();
            }
        }

        @Override
        protected void onClose() {
            keystoreFile = null;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private SecureContextCredentials(Builder builder) {
        super(builder);
    }

    SSLContext initContext(HttpConnection.SecureProtocols protocol) {
        Objects.requireNonNull(protocol);
        try{
            KeyManagerFactory keyFactory = null;
            TrustManagerFactory trustFactory = null;

            if(clientKeyStore != null) {
                // build the KeyManager (SSL client credentials we can send)
                keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyFactory.init(clientKeyStore, password == null ? null : password.toCharArray());
            }

            if(clientTrustStore != null || clientKeyStore != null) {
                // build the TrustManager (Server certificates we trust)
                trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustFactory.init(clientTrustStore != null ? clientTrustStore : clientKeyStore);
            }

            SSLContext sslContext = SSLContext.getInstance(protocol.getProtocol());
            sslContext.init(
                    keyFactory != null ? keyFactory.getKeyManagers() : null,
                    trustFactory != null ? trustFactory.getTrustManagers() : null,
                    new SecureRandom());
            return sslContext;
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            throw new SecureContextCredentialsException(e);
        }
    }
}
