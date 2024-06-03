package com.example.easychat;

import java.util.HashMap;
import java.util.Map;

public class CountryChatroomMapper {

    // 국가별 채팅방 매핑 정보를 포함하는 맵
    private static final Map<String, String> COUNTRY_CHATROOM_MAP;

    // 국가별 채팅방 매핑 정보 초기화
    static {
        COUNTRY_CHATROOM_MAP = new HashMap<>();
        COUNTRY_CHATROOM_MAP.put("en", "chatroom_en");
        COUNTRY_CHATROOM_MAP.put("ko", "chatroom_ko");
        COUNTRY_CHATROOM_MAP.put("ja", "chatroom_ja");
        COUNTRY_CHATROOM_MAP.put("ru", "chatroom_ru");
        COUNTRY_CHATROOM_MAP.put("fr", "chatroom_fr");
        COUNTRY_CHATROOM_MAP.put("es", "chatroom_es");
        COUNTRY_CHATROOM_MAP.put("it", "chatroom_it");
        COUNTRY_CHATROOM_MAP.put("de", "chatroom_de");
        COUNTRY_CHATROOM_MAP.put("th", "chatroom_th");
        COUNTRY_CHATROOM_MAP.put("vi", "chatroom_vi");
        COUNTRY_CHATROOM_MAP.put("zh-CN", "chatroom_zh-CN");
        COUNTRY_CHATROOM_MAP.put("zh-TW", "chatroom_zh-TW");
        // 추가적인 국가와 채팅방 ID 매핑 정보를 여기에 추가할 수 있습니다.
    }

    // 국가 코드에 해당하는 채팅방 ID를 가져오는 메서드
    public static String getChatroomIdForCountry(String countryCode) {
        return COUNTRY_CHATROOM_MAP.getOrDefault(countryCode, "default_chatroom");
    }
}
