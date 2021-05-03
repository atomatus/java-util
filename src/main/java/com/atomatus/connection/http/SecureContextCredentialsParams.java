package com.atomatus.connection.http;

import java.io.Closeable;
import java.security.KeyStore;

abstract class SecureContextCredentialsParams implements Closeable {

    protected KeyStore clientKeyStore;
    protected KeyStore clientTrustStore;
    protected String password;

    protected SecureContextCredentialsParams() { }

    protected SecureContextCredentialsParams(SecureContextCredentialsParams other) {
        this.clientKeyStore = other.clientKeyStore;
        this.clientTrustStore = other.clientTrustStore;
        this.password = other.password;
    }

    protected void onClose() { }

    @Override
    public final void close() {
        this.onClose();
        this.clientKeyStore = null;
        this.clientTrustStore = null;
        this.password = null;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }
}
