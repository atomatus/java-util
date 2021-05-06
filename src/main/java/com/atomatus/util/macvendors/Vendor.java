package com.atomatus.util.macvendors;

import com.google.gson.annotations.SerializedName;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Product vendor data.
 */
@XStreamAlias("result")
public final class Vendor extends Error {

    /**
     * Company name.
     */
    @XStreamAlias("company")
    @SerializedName("company")
    private String company;

    /**
     * Mac Address prefix.
     */
    @XStreamAlias("mac_prefix")
    @SerializedName("mac_prefix")
    private String macPrefix;

    /**
     * Company address.
     */
    @XStreamAlias("address")
    @SerializedName("address")
    private String address;

    /**
     * Mac Address starting group at.
     */
    @XStreamAlias("start_hex")
    @SerializedName("start_hex")
    private String startHex;

    /**
     * Mac Address ending group at.
     */
    @XStreamAlias("end_hex")
    @SerializedName("end_hex")
    private String endHex;

    /**
     * Country acronym.
     */
    @XStreamAlias("country")
    @SerializedName("country")
    private String country;

    /**
     * Vendor type.
     */
    @XStreamAlias("type")
    @SerializedName("type")
    private String type;

    /**
     * Vendor company name.
     * @return company name.
     */
    public String getCompany() {
        return company;
    }

    /**
     * Set vendor company name.
     * @param company company name.
     */
    public void setCompany(String company) {
        this.company = company;
    }

    /**
     * Recover vendor mac address prefix.
     * @return vendor mac address prefix
     */
    public String getMacPrefix() {
        return macPrefix;
    }

    /**
     * Set vendor mac address prefix.
     * @param macPrefix vendor mac address prefix.
     */
    public void setMacPrefix(String macPrefix) {
        this.macPrefix = macPrefix;
    }

    /**
     * Recover vendor address.
     * @return vendor address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set vendor address.
     * @param address vendor address.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Recover start hex macaddress.
     * @return start macaddress.
     */
    public String getStartHex() {
        return startHex;
    }

    /**
     * Set Start hex macaddress
     * @param startHex start hex macaddress
     */
    public void setStartHex(String startHex) {
        this.startHex = startHex;
    }

    /**
     * Recover end hex macaddress.
     * @return end hex macaddress.
     */
    public String getEndHex() {
        return endHex;
    }

    /**
     * Set end hex macaddress.
     * @param endHex end hex macaddress.
     */
    public void setEndHex(String endHex) {
        this.endHex = endHex;
    }

    /**
     * Get country acronym.
     * @return country acronym.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Set country acronym.
     * @param country country acronym.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Get vendor type.
     * @return vendor type.
     */
    public String getType() {
        return type;
    }

    /**
     * Set vendor type.
     * @param type vendor type.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Company name when defined it, otherwhise default toString().
     * @return company name when or default toString().
     */
    @Override
    public String toString() {
        return company == null ? super.toString() : company;
    }
}
