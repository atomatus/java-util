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
    UNITED_STATES("US", "#####;#####-####", "Zip Code", true),
    CANADA("CA", "?#? #?#;?#?-#?#", "Postal Code", true),
    UNITED_KINGDOM("GB", "??? #??;??## #??;??# #??", "Postcode", false),
    ARGENTINA("AR", "?####???", "CPA", true),
    AUSTRALIA("AU", "####", "Postcode", false),
    AUSTRIA("AT", "####", "Postal Code", true),
    BELGIUM("BE", "####", "Code Postal", true),
    BRAZIL("BR", "#####;#####-###", "CEP", true),
    BULGARIA("BU", "####", "Postal Code", true),
    CAMBODIA("KH", "#####", "Postal Code", false),
    CHINA("CN", "######", "郵政編碼", true),
    CHILE("CL", "#######;###-####", "Código postal", false),
    COLOMBIA("CO", "######", "Código postal", false),
    CROATIA("HR", "#####", "Postal Code", true),
    COSTA_RICA("CR", "#####;#####-####", "Código postal", true),
    CZECH_REPUBLIC("CZ", "### ##;###-##;#####", "Postal Code", false),
    DENMARK("DK", "####;##", "Postal Code", true),
    DOMINICAN_REPUBLIC("DO", "#####", "Postal Code", true),
    ECUADOR("EC", "######", "Código postal", true),
    EGYPT("EG", "#####", "Postal Code", true),
    ESTONIA("ET", "#####", "Postiindeks", true),
    FINLAND("FI", "#####", "Postinumero", true),
    FRANCE("FR", "#####", "Postal Code", false),
    GERMANY("DE", "#####", "Postal Code;PLZ", false),
    GREECE("EL", "## ##", "tachydromikós kódikas", false),
    GUATEMALA("GT", "#####", "Postal Code", false),
    HONG_KONG("HK", "######", "郵政編碼", true),
    HUNGARY("HU", "####", "Postal Code", true),
    INDIA("IN", "### ###;######", "Postcode", true),
    INDONESIA("ID", "#####", "Kode Pos", true),
    IRELAND("IE", "?## ?#?#;?## ??##;?## ?#??", "Eircode", false),
    ITALY("IT", "#####", "CAP", true),
    ISRAEL("IL", "#######", "מיקוד", true),
    JAPAN("JP", "###-####", "郵便番", true),
    JORDAN("JO", "#####", "Postal Code", true),
    KUWAIT("KW", "#####", "Postal Code", true),
    LATVIA("LV", "####", "Pasta indekss", true),
    LITHUANIA("LT", "#####", "Pašto kodas", true),
    LUXEMBOURG("LU", "#####", "Code Postal", true),
    MALAYSIA("MY", "#####", "Polkod", true),
    MALTA("MT", "??? ####", "Post Code", true),
    MEXICO("MX", "#####", "Postal Code", true),
    MONTENEGRO("ME", "#####", "Postal Code", true),
    MOROCCO("MA", "#####", "الرمز البريدي", true),
    NETHERLANDS("NL", "#### ??;####??", "Postal Code", false),
    NEW_ZEALAND("NZ", "####", "Postcode", true),
    NORWAY("NO", "####", "Post Number", true),
    OMAN("OM", "###", "Postal Code", true),
    PAKISTAN("PK", "#####", "Post Code", true),
    PARAGUAY("PY", "####", "Código postal", true),
    PERU("PE", "#####", "Código postal", false),
    PHILIPPINES("PH", "####", "ZIP Code", true),
    POLAND("PL", "##-###", "Postal Code", true),
    PORTUGAL("PT", "####-###", "Código postal", true),
    ROMANIA("RO", "######", "Postal Code", true),
    RUSSIA("RU", "######", "индekc", true),
    SAUDI_ARABIA("SA", "#####-####", "الرمز البريدي", true),
    SERBIA("RS", "#####", "Post Code", true),
    SINGAPORE("SG", "######", "Postal Code", true),
    SLOVAK_REPUBLIC("SK", "#####;### ##", "PSC", true),
    SLOVENIA("SI", "####", "Poštna številka", true),
    SOUTH_AFRICA("ZA", "####", "Postal Code", false),
    SOUTH_KOREA("KR", "#####", "Postal Code", false),
    SPAIN("ES", "#####", "Código postal", true),
    SWEDEN("SE", "SE-###-##;###-##;### ##;#####", "Postcode", false),
    SWITZERLAND("CH", "####", "PLZ", true),
    TAIWAN("TW", "###;######;###-###", "Postal Code", false),
    TANZANIA("TZ", "#####", "NAPS", false),
    THAILAND("TH", "#####", "Postal Code", true),
    TRINIDAD_AND_TOBAGO("TT", "######", "Postal Code", false),
    TUNISIA("TN", "####", "Postal Code", true),
    TURKEY("TR", "#####", "Posta kodu", true),
    UKRAINE("UA", "#####", "Postal Code", true),
    URUGUAY("UY", "#####", "Código Postal", true),
    VENEZUELA("VE", "####;####-?", "Postal Code", true),
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
