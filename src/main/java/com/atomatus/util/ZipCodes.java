package com.atomatus.util;

import java.util.Locale;
import java.util.Objects;

/**
 * <p>
 * Zip Code list by country.
 * </p>
 * <i>Created by chcmatos on 21, janeiro, 2022</i>
 *
 * @author Carlos Matos {@literal @chcmatos}
 */
public enum ZipCodes {
    /** Zip code for US. */
    UNITED_STATES("US", "#####;#####-####", "Zip Code", true),
    /** Zip code for CA. */
    CANADA("CA", "?#? #?#;?#?-#?#", "Postal Code", true),
    /** Zip code for GB. */
    UNITED_KINGDOM("GB", "??? #??;??## #??;??# #??", "Postcode", false),
    /** Zip code for AR. */
    ARGENTINA("AR", "?####???", "CPA", true),
    /** Zip code for AU. */
    AUSTRALIA("AU", "####", "Postcode", false),
    /** Zip code for AT. */
    AUSTRIA("AT", "####", "Postal Code", true),
    /** Zip code for BE. */
    BELGIUM("BE", "####", "Code Postal", true),
    /** Zip code for BR. */
    BRAZIL("BR", "#####;#####-###", "CEP", true),
    /** Zip code for BU.*/
    BULGARIA("BU", "####", "Postal Code", true),
    /** Zip code for KH.*/
    CAMBODIA("KH", "#####", "Postal Code", false),
    /** Zip code for CN.*/
    CHINA("CN", "######", "郵政編碼", true),
    /** Zip code for CL.*/
    CHILE("CL", "#######;###-####", "Código postal", false),
    /** Zip code for CO.*/
    COLOMBIA("CO", "######", "Código postal", false),
    /** Zip code for HR.*/
    CROATIA("HR", "#####", "Postal Code", true),
    /** Zip code for CR.*/
    COSTA_RICA("CR", "#####;#####-####", "Código postal", true),
    /** Zip code for CZ.*/
    CZECH_REPUBLIC("CZ", "### ##;###-##;#####", "Postal Code", false),
    /** Zip code for DK.*/
    DENMARK("DK", "####;##", "Postal Code", true),
    /** Zip code for DO.*/
    DOMINICAN_REPUBLIC("DO", "#####", "Postal Code", true),
    /** Zip code for EC.*/
    ECUADOR("EC", "######", "Código postal", true),
    /** Zip code for EG.*/
    EGYPT("EG", "#####", "Postal Code", true),
    /** Zip code for ET.*/
    ESTONIA("ET", "#####", "Postiindeks", true),
    /** Zip code for FI.*/
    FINLAND("FI", "#####", "Postinumero", true),
    /** Zip code for FR.*/
    FRANCE("FR", "#####", "Postal Code", false),
    /** Zip code for DE.*/
    GERMANY("DE", "#####", "Postal Code;PLZ", false),
    /** Zip code for EL.*/
    GREECE("EL", "## ##", "tachydromikós kódikas", false),
    /** Zip code for GT.*/
    GUATEMALA("GT", "#####", "Postal Code", false),
    /** Zip code for HK.*/
    HONG_KONG("HK", "######", "郵政編碼", true),
    /** Zip code for HU.*/
    HUNGARY("HU", "####", "Postal Code", true),
    /** Zip code for IN.*/
    INDIA("IN", "### ###;######", "Postcode", true),
    /** Zip code for ID.*/
    INDONESIA("ID", "#####", "Kode Pos", true),
    /** Zip code for IE.*/
    IRELAND("IE", "?## ?#?#;?## ??##;?## ?#??", "Eircode", false),
    /** Zip code for IT.*/
    ITALY("IT", "#####", "CAP", true),
    /** Zip code for LIL.*/
    ISRAEL("IL", "#######", "מיקוד", true),
    /** Zip code for JP.*/
    JAPAN("JP", "###-####", "郵便番", true),
    /** Zip code for JO.*/
    JORDAN("JO", "#####", "Postal Code", true),
    /** Zip code for KW.*/
    KUWAIT("KW", "#####", "Postal Code", true),
    /** Zip code for LV.*/
    LATVIA("LV", "####", "Pasta indekss", true),
    /** Zip code for LT.*/
    LITHUANIA("LT", "#####", "Pašto kodas", true),
    /** Zip code for LU.*/
    LUXEMBOURG("LU", "#####", "Code Postal", true),
    /** Zip code for MY.*/
    MALAYSIA("MY", "#####", "Polkod", true),
    /** Zip code for MT.*/
    MALTA("MT", "??? ####", "Post Code", true),
    /** Zip code for MX.*/
    MEXICO("MX", "#####", "Postal Code", true),
    /** Zip code for ME.*/
    MONTENEGRO("ME", "#####", "Postal Code", true),
    /** Zip code for MA.*/
    MOROCCO("MA", "#####", "الرمز البريدي", true),
    /** Zip code for NL.*/
    NETHERLANDS("NL", "#### ??;####??", "Postal Code", false),
    /** Zip code for NZ.*/
    NEW_ZEALAND("NZ", "####", "Postcode", true),
    /** Zip code for NO.*/
    NORWAY("NO", "####", "Post Number", true),
    /** Zip code for OM.*/
    OMAN("OM", "###", "Postal Code", true),
    /** Zip code for PK.*/
    PAKISTAN("PK", "#####", "Post Code", true),
    /** Zip code for PY.*/
    PARAGUAY("PY", "####", "Código postal", true),
    /** Zip code for PE.*/
    PERU("PE", "#####", "Código postal", false),
    /** Zip code for PPH.*/
    PHILIPPINES("PH", "####", "ZIP Code", true),
    /** Zip code for PL.*/
    POLAND("PL", "##-###", "Postal Code", true),
    /** Zip code for PT.*/
    PORTUGAL("PT", "####-###", "Código postal", true),
    /** Zip code for RO.*/
    ROMANIA("RO", "######", "Postal Code", true),
    /** Zip code for RU.*/
    RUSSIA("RU", "######", "индekc", true),
    /** Zip code for SA.*/
    SAUDI_ARABIA("SA", "#####-####", "الرمز البريدي", true),
    /** Zip code for RS.*/
    SERBIA("RS", "#####", "Post Code", true),
    /** Zip code for SG.*/
    SINGAPORE("SG", "######", "Postal Code", true),
    /** Zip code for SK.*/
    SLOVAK_REPUBLIC("SK", "#####;### ##", "PSC", true),
    /** Zip code for SI.*/
    SLOVENIA("SI", "####", "Poštna številka", true),
    /** Zip code for ZA.*/
    SOUTH_AFRICA("ZA", "####", "Postal Code", false),
    /** Zip code for KR.*/
    SOUTH_KOREA("KR", "#####", "Postal Code", false),
    /** Zip code for ES.*/
    SPAIN("ES", "#####", "Código postal", true),
    /** Zip code for SE.*/
    SWEDEN("SE", "SE-###-##;###-##;### ##;#####", "Postcode", false),
    /** Zip code for CH.*/
    SWITZERLAND("CH", "####", "PLZ", true),
    /** Zip code for TW.*/
    TAIWAN("TW", "###;######;###-###", "Postal Code", false),
    /** Zip code for TZ.*/
    TANZANIA("TZ", "#####", "NAPS", false),
    /** Zip code for TH.*/
    THAILAND("TH", "#####", "Postal Code", true),
    /** Zip code for TT.*/
    TRINIDAD_AND_TOBAGO("TT", "######", "Postal Code", false),
    /** Zip code for TN.*/
    TUNISIA("TN", "####", "Postal Code", true),
    /** Zip code for TR.*/
    TURKEY("TR", "#####", "Posta kodu", true),
    /** Zip code for UA.*/
    UKRAINE("UA", "#####", "Postal Code", true),
    /** Zip code for UY.*/
    URUGUAY("UY", "#####", "Código Postal", true),
    /** Zip code for VE.*/
    VENEZUELA("VE", "####;####-?", "Postal Code", true),
    /** Zip code for VN.*/
    VIETNAM("VN", "######", "Mã Bưu Chính", false);

    private final String country;
    private final String format;
    private final String desc;
    private final boolean required;

    /**
     * Zip code constructor.
     *
     * @param country  – An ISO 3166 alpha-2 country code or a UN M.49 numeric-3 area code. See the Locale class description about valid country values.
     * @param formats  - zip code formats separated by semicolon.
     * @param desc     zip code name in his self country
     * @param required is zip code required.
     */
    ZipCodes(String country, String formats, String desc, boolean required) {
        this.country = country;
        this.format = formats;
        this.desc = desc;
        this.required = required;
    }

    /**
     * An ISO 3166 alpha-2 country code or a UN M.49 numeric-3 area code. See the Locale class description about valid country values.
     *
     * @return country code.
     */
    public String country() {
        return country;
    }

    /**
     * Zip code formats separated.
     *
     * @return zip code formats.
     */
    public String[] formats() {
        return ArrayHelper.filter(format.split(";"),
                StringUtils::isNonNullAndNonEmpty);
    }

    /**
     * Zip code name in his self country.
     *
     * @return code name.
     */
    public String desc() {
        return desc;
    }

    /**
     * Is zip code required.
     *
     * @return true, required, otherwise false.
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Attempt to find more appropriate format for input candidate code.
     * @param candidate candidate zip code unformatted.
     * @return found.
     */
    public String findFormat(String candidate) {
        int len = StringUtils.digitLettersOnly(candidate).length();
        String[] arr = formats();
        int currIndex = 0;
        if(len > 0) {
            int currDiff = -1;
            for (int i = 0, l = arr.length; i < l; i++) {
                String aux = arr[i].replaceAll("[^#?\\w]", "");
                int diff = Math.abs(len - aux.length());
                if (currDiff == -1 || diff < currDiff) {
                    currIndex = i;
                    currDiff = diff;
                    if(diff == 0) {
                        break;
                    }
                }
            }
        }
        return arr[currIndex];
    }

    /**
     * Attempt to recovery zip code by locale otherwise null.
     * @param locale target locale.
     * @return recovered zip code for locale, otherwise null.
     */
    public static ZipCodes byLocale(Locale locale) {
        String country = Objects.requireNonNull(locale,
                "Locale is null!").getCountry();
        for(ZipCodes zc : values()) {
            if(zc.country.equalsIgnoreCase(country)) {
                return zc;
            }
        }
        return null;//not found.
    }
}
