package com.example.easychat.papago_trans;

public enum Language {
    AFRIKAANS("af"),
    ARABIC("ar"),
    BENGALI("bn"),
    CHINESE_SIMPLIFIED("zh", "CN", "Hans"),
    CHINESE_TRADITIONAL("zh", "TW", "Hant"),
    CZECH("cs"),
    DANISH("da"),
    DUTCH("nl"),
    ENGLISH("en"),
    FARSI("fa"),
    FRENCH("fr"),
    GERMAN("de"),
    GREEK("el"),
    GUJARATI("gu"),
    HEBREW("iw"),
    HINDI("hi"),
    INDONESIA("in"),
    ITALIAN("it"),
    JAPANESE("ja"),
    KAZAKH("kk"),
    KOREAN("ko"),
    MARATHI("mr"),
    POLISH("pl"),
    PORTUGUESE("pt"),
    PUNJABI("pa"),
    RUSSIAN("ru"),
    SLOVAK("sk"),
    SLOVENIAN("si"),
    SPANISH("es"),
    SWEDISH("sv"),
    TAGALOG("tl"),
    TURKISH("tr"),
    UKRAINIAN("uk"),
    URDU("ur"),
    UZBEK("uz"),
    VIETNAMESE("vi");

    private String code;
    private String country;
    private String script;

    Language(String code, String country, String script) {
        this.code = code;
        this.country = country;
        this.script = script;
    }

    Language(String code) {
        this.code = code;
    }

    public static Language fromCode(String code) {
        for (Language lang : Language.values()) {
            if (lang.code.equals(code)) {
                return lang;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }



}

