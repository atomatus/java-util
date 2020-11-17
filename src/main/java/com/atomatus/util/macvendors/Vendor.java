package com.atomatus.util.macvendors;

import com.google.gson.annotations.SerializedName;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("result")
public final class Vendor extends Error {

    @XStreamAlias("company")
    @SerializedName("company")
    private String company;

    @XStreamAlias("mac_prefix")
    @SerializedName("mac_prefix")
    private String macPrefix;

    @XStreamAlias("address")
    @SerializedName("address")
    private String address;

    @XStreamAlias("start_hex")
    @SerializedName("start_hex")
    private String startHex;

    @XStreamAlias("end_hex")
    @SerializedName("end_hex")
    private String endHex;

    @XStreamAlias("country")
    @SerializedName("country")
    private String country;

    @XStreamAlias("type")
    @SerializedName("type")
    private String type;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getMacPrefix() {
        return macPrefix;
    }

    public void setMacPrefix(String macPrefix) {
        this.macPrefix = macPrefix;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStartHex() {
        return startHex;
    }

    public void setStartHex(String startHex) {
        this.startHex = startHex;
    }

    public String getEndHex() {
        return endHex;
    }

    public void setEndHex(String endHex) {
        this.endHex = endHex;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return company == null ? super.toString() : company;
    }
}
