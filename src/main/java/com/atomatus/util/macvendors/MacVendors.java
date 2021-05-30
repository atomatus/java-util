package com.atomatus.util.macvendors;

import com.atomatus.util.RegExp;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Find Vendor by mac address using any of following mac Lookup types.
 * <ul>
 *     <li><a href="https://macvendors.co/api">macvendors.co/api</a>;</li>
 *     <li><a href="https://macvendors.com/api">macvendors.com/api</a>;</li>
 *     <li><a href="https://api.macaddress.io">api.macaddress.io</a></li>
 * </ul>
 * @author Carlos Matos
 */
public final class MacVendors {

    /**
     * Mac Lookup types.
     */
    public enum MacLookupTypes {

        /**
         * macvendors.co/api
         */
        MAC_VENDORS_CO,

        /**
         * macvendors.com/api
         */
        MAC_VENDORS_COM,

        /**
         * api.macaddress.io
         */
        MACADDRESS_IO
    }

    private static final MacVendors instance;

    private final Map<String, Vendor> vendors;
    private final Map<MacLookupTypes, String> apiKeys;

    /**
     * Mac vendors instance (Singleton pattern).
     * @return singleton instance.
     */
    public static MacVendors getInstance(){
        return instance;
    }

    static {
        instance = new MacVendors();
    }

    private MacVendors() {
        this.vendors = new ConcurrentHashMap<>();
        this.apiKeys = new ConcurrentHashMap<>();
    }

    /**
     * Setup your personal API Key for macLookup type target.
     * @param  type maclookup type to find vendor.
     * @param apiKey api key for maclookup usage (some maclookup does not need setup an api key).
     * @return current macVendors instance.
     */
    public synchronized MacVendors setAPIKey(MacLookupTypes type, String apiKey) {
        apiKeys.put(type, apiKey);
        return this;
    }

    /**
     * Recover maclookup by type.
     * @param type macklookup type
     * @return mac lookup instance.
     */
    private MacLookup getMacLookup(MacLookupTypes type) {
        switch (type) {
            case MAC_VENDORS_CO:
                return MacLookup.Factory.getMacLookupAsMacVendorsCo();
            case MAC_VENDORS_COM:
                return MacLookup.Factory.getMacLookupAsMacVendorsCom();
            case MACADDRESS_IO:
                return MacLookup.Factory.getMacLookupAsMacAddressIo();
            default:
                throw new UnsupportedOperationException("MacLookup not supported for: " + type);
        }
    }

    /**
     * Attempt to find vendor by maclookup type and target macAddress.
     * @param type macLookup type
     * @param macAddress target mac address
     * @return found vendor, otherwise null.
     */
    public synchronized Vendor findAt(MacLookupTypes type, String macAddress) {
        if(RegExp.isValidMacAddress(Objects.requireNonNull(macAddress))) {
            Vendor v = vendors.get(macAddress);
            if(v == null) {
                v = getMacLookup(type).apiKey(apiKeys.get(type)).find(macAddress);
                if (v != null && !v.hasError()) {
                    vendors.put(macAddress, v);
                    return v;
                }
            } else {
                return v;
            }
        }
        return null /*invalid mac address or error*/;
    }

    /**
     * Attempt to find vendor by any maclookup type and target macAddress.
     * @param macAddress target mac address
     * @return found vendor, otherwise null.
     */
    public synchronized Vendor find(String macAddress) {
        for(MacLookupTypes type : MacLookupTypes.values()) {
            try {
                Vendor v = findAt(type, macAddress);
                if (v != null) return v;//first found
            } catch (Exception ignored) { }
        }
        return null; //not found
    }
}
