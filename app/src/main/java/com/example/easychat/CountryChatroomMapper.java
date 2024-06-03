package com.example.easychat;

import java.util.HashMap;
import java.util.Map;

public class CountryChatroomMapper {

    private static final Map<String, String> COUNTRY_CHATROOM_MAP;

    static {
        COUNTRY_CHATROOM_MAP = new HashMap<>();
        COUNTRY_CHATROOM_MAP.put("en", "en");
        COUNTRY_CHATROOM_MAP.put("ko", "ko");
        COUNTRY_CHATROOM_MAP.put("ja", "ja");
        COUNTRY_CHATROOM_MAP.put("ru", "ru");
        COUNTRY_CHATROOM_MAP.put("fr", "fr");
        COUNTRY_CHATROOM_MAP.put("es", "es");
        COUNTRY_CHATROOM_MAP.put("it", "it");
        COUNTRY_CHATROOM_MAP.put("de", "de");
        COUNTRY_CHATROOM_MAP.put("th", "th");
        COUNTRY_CHATROOM_MAP.put("vi", "vi");
        COUNTRY_CHATROOM_MAP.put("zh-CN", "zh-CN");
        COUNTRY_CHATROOM_MAP.put("zh-TW", "zh-TW");
    }

    public static String getChatroomIdForCountry(String countryCode) {
        return COUNTRY_CHATROOM_MAP.getOrDefault(countryCode, "default_chatroom");
    }

    public static String getCountryCodeForChatroom(String chatroomId) {
        for (Map.Entry<String, String> entry : COUNTRY_CHATROOM_MAP.entrySet()) {
            if (entry.getValue().equals(chatroomId)) {
                return entry.getKey();
            }
        }
        return "default";
    }
}
