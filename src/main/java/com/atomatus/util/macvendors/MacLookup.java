package com.atomatus.util.macvendors;

import com.atomatus.connection.http.HttpConnection;
import com.atomatus.connection.http.Parameter;
import com.atomatus.connection.http.Response;
import com.atomatus.util.StringUtils;

import java.io.Serializable;

/**
 * <p>
 *     <strong>Mac Lookup Types</strong>
 * </p>
 * <p>
 * <ul>
 *     <li><a href="https://macvendors.co/api">macvendors.co/api</a>;</li>
 *     <li><a href="https://macvendors.com/api">macvendors.com/api</a>;</li>
 *     <li><a href="https://api.macaddress.io">api.macaddress.io</a></li>
 * </ul>
 * </p>
 * <i>Created by chcmatos on 30, maio, 2021</i>
 *
 * @author Carlos Matos {@literal @chcmatos}
 */
abstract class MacLookup {

    private static final int READ_TIMEOUT_IN_MILLIS;

    protected String apiKey;

    static {
        READ_TIMEOUT_IN_MILLIS = 3000/*3s*/;
    }

    //region factory

    //region impl
    private static class MacLookupImplMacVendorsCo extends MacLookup {
        @Override
        public Vendor find(String macAddress) {
            try(Response resp = new HttpConnection()
                    .changeReadTimeOut(READ_TIMEOUT_IN_MILLIS)
                    .setSecureProtocol(HttpConnection.SecureProtocols.SSL)
                    .getContent("https://macvendors.co/api/{0}/json",
                            Parameter.buildQuery(macAddress.toUpperCase()))) {
                Vendor v = resp.parse("result", Vendor.class);
                if(v != null) v.requireNonError();
                return v;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class MacLookupImplMacVendorsCom extends MacLookup {

        private Vendor buildVendor(String macAddress) {
            macAddress = macAddress.replaceAll("[^0-9A-F]", "");
            Vendor v = new Vendor();
            v.setMacPrefix(
                    macAddress.substring(0, 2) + ":" +
                            macAddress.substring(2, 4) + ":" +
                            macAddress.substring(4, 6));
            v.setStartHex(macAddress.substring(0, 6) + "000000");
            v.setEndHex(macAddress.substring(0, 6) + "FFFFFF");
            v.setType("MA-L");
            return v;
        }

        @Override
        public Vendor find(String macAddress) {
            try(Response resp = new HttpConnection()
                    .changeReadTimeOut(READ_TIMEOUT_IN_MILLIS)
                    .setSecureProtocol(HttpConnection.SecureProtocols.TLS)
                    .getContent("https://api.macvendors.com/{0}",
                            Parameter.buildQuery(macAddress.toUpperCase()))) {
                if(resp.isSuccess()) {
                    String company = resp.getContent();
                    Vendor v = buildVendor(macAddress);
                    v.setCompany(company);
                    return v;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class MacLookupImplMacAddressIo extends MacLookup {

        private static final String API_KEY;

        static {
            API_KEY = "at_yUSiBZ70wGBOyokSQQ6hGmBQgFrfz";
        }

        protected MacLookupImplMacAddressIo() {
            this.apiKey = API_KEY;
        }

        /**
         * Result for macLookup API MacAddress.io
         */
        static class Result implements Serializable {

            /**
             * Result vendor details
             */
            static class VendorDetails implements Serializable {
                String oui;
                String companyName;
                String companyAddress;
                String countryCode;
            }

            /**
             * Result block details
             */
            static class BlockDetails implements Serializable {
                boolean blockFound;
                String borderLeft;
                String borderRight;
                String assignmentBlockSize;
            }

            /**
             * Result mac address details.
             */
            static class MacAddressDetails implements Serializable {
                String searchTerm;
                boolean isValid;
            }

            VendorDetails vendorDetails;
            BlockDetails blockDetails;
            MacAddressDetails macAddressDetails;

            Vendor toVendor() {
                if(!macAddressDetails.isValid || !blockDetails.blockFound){
                    return null;
                }

                Vendor v = new Vendor();
                v.setAddress(macAddressDetails.searchTerm);
                v.setStartHex(blockDetails.borderLeft);
                v.setEndHex(blockDetails.borderRight);
                v.setMacPrefix(vendorDetails.oui);
                v.setCompany(vendorDetails.companyName);
                v.setAddress(vendorDetails.companyAddress);
                v.setCountry(vendorDetails.countryCode);
                v.setType(blockDetails.assignmentBlockSize);
                return v;
            }
        }

        @Override
        protected Vendor find(String macAddress) {
            try(Response resp = new HttpConnection()
                    .changeReadTimeOut(READ_TIMEOUT_IN_MILLIS)
                    .setSecureProtocol(HttpConnection.SecureProtocols.TLS)
                    .getContent("https://api.macaddress.io/v1?apiKey={0}&output=json&search={1}",
                            Parameter.buildQuery(apiKey),
                            Parameter.buildQuery(macAddress.toUpperCase()))) {
                if(resp.isSuccess()) {
                    return resp.parse(Result.class).toVendor();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    //endregion

    /**
     * MacLookup Factory.
     */
    protected static class Factory {

        protected static MacLookup getMacLookupAsMacVendorsCo() {
            return new MacLookupImplMacVendorsCo();
        }

        protected static MacLookup getMacLookupAsMacVendorsCom() {
            return new MacLookupImplMacVendorsCom();
        }

        protected static MacLookup getMacLookupAsMacAddressIo() {
            return new MacLookupImplMacAddressIo();
        }
    }
    //endregion

    /**
     * Define apiKey usage for request authentication.
     * @param apiKey valid api key
     * @return current instance.
     */
    protected MacLookup apiKey(String apiKey) {
        if(!StringUtils.isNullOrWhitespace(apiKey)) {
            this.apiKey = apiKey;
        }
        return this;
    }

    /**
     * Attempt to find Vendor by macAddress.
     * @param macAddress mac address valid, non null.
     * @return found vendor, otherwise null.
     */
    protected abstract Vendor find(String macAddress);

}
