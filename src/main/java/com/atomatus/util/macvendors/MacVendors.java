package com.atomatus.util.macvendors;

import com.atomatus.connection.http.HttpConnection;
import com.atomatus.connection.http.Parameter;
import com.atomatus.connection.http.Response;
import com.atomatus.util.RegExp;
import com.google.gson.Gson;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Find Vendor by mac address using https://macvendors.co/ API.
 * @author Carlos Matos
 */
public final class MacVendors {

    private static final MacVendors instance;

    private final Map<String, Vendor> vendors;

    public static MacVendors getInstance(){
        return instance;
    }

    static {
        instance = new MacVendors();
    }

    private MacVendors() {
        vendors = new ConcurrentHashMap<>();
    }

    /**
     * Try to find vendor by mac address.
     * @param macAddress valid mac address.
     * @return return a vendor if found on macvendors.co database, otherwise return null.
     * @throws NullPointerException throws when macAddress is null.
     */
    public synchronized Vendor find(String macAddress) {
        Vendor v = null;
        if (RegExp.isValidMacAddress(Objects.requireNonNull(macAddress)) &&
                (v = vendors.get(macAddress)) == null) {
            try (Response resp = new HttpConnection()
                    .changeReadTimeOut(8000/*8s*/)
                    .setAcceptHttpResponseCode(HttpConnection.StatusCode.HTTP_ACCEPTED)
                    .getContent("https://macvendors.co/api/{0}/json",
                            Parameter.buildQuery(macAddress.toUpperCase()))) {
                if (resp.isSuccess()) {
                    String json = resp.getContent();
                    Vendor.Result result = new Gson().fromJson(json, Vendor.Result.class);
                    v = result.getVendor();
                    vendors.put(macAddress, v);
                    v.requireNonError();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return v == null || v.hasError() ? null : v;
    }
}
