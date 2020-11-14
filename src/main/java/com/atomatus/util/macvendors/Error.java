package com.atomatus.util.macvendors;

import com.google.gson.annotations.SerializedName;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.io.Serializable;

@SuppressWarnings("unused")
abstract class Error implements Serializable {

    @XStreamAlias("error")
    @SerializedName("error")
    private String error;

    String getError() {
        return error;
    }

    void setError(String error) {
        this.error = error;
    }

    boolean hasError(){
        return error != null && !error.isEmpty();
    }

    void requireNonError(){
        if (hasError()) {
            throw new RuntimeException(getError());
        }
    }
}
