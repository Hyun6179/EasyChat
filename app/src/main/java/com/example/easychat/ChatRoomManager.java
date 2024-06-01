package com.example.easychat;

import com.example.easychat.model.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRoomManager {
    public static void main(String[] args) {
        // 사용자 정보를 가져와서 처리하는 코드를 작성합니다.
        List<UserModel> users = getUsersFromDatabase(); // 데이터베이스에서 사용자 정보를 가져옴

        // countryCode를 기준으로 사용자를 그룹화합니다.
        Map<String, List<UserModel>> usersByCountry = groupUsersByCountry(users);

        // 각 countryCode에 대해 채팅방을 생성합니다.
        for (String countryCode : usersByCountry.keySet()) {
            createChatRoom(countryCode);
        }
    }

    // 데이터베이스에서 사용자 정보를 가져오는 메서드
    private static List<UserModel> getUsersFromDatabase() {
        // 실제 데이터베이스 연동 코드를 작성합니다.
        // 여기서는 임시로 빈 리스트를 반환합니다.
        return new ArrayList<>();
    }

    // countryCode를 기준으로 사용자를 그룹화하는 메서드
    private static Map<String, List<UserModel>> groupUsersByCountry(List<UserModel> users) {
        Map<String, List<UserModel>> usersByCountry = new HashMap<>();
        for (UserModel user : users) {
            String countryCode = user.getCountryCode();
            usersByCountry.computeIfAbsent(countryCode, k -> new ArrayList<>()).add(user);
        }
        return usersByCountry;
    }

    // countryCode에 따라 채팅방을 생성하는 메서드
    private static void createChatRoom(String countryCode) {
        // 실제로 채팅방을 생성하는 코드를 작성해야 합니다.
        // 여기서는 단순히 콘솔에 메시지를 출력하는 코드를 작성합니다.
        System.out.println("Chat room created for country code: " + countryCode);
    }
}
