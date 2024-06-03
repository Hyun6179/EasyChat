package com.example.easychat.papago_trans;

import static android.content.ContentValues.TAG;

import static com.example.easychat.utils.FirebaseUtil.getUserCountryCode;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.easychat.CountryChatroomMapper;
import com.example.easychat.model.ChatMessageModel;
import com.example.easychat.utils.FirebaseUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChatMessageProcessor {
    // Other existing methods...

    public static void processMessage(String senderId, String receiverId, String message) {
        // 메시지를 보내는 사용자의 countryCode 가져오기
        getUserCountryCode(senderId, senderCountryCode -> {
            if (senderCountryCode != null) {
                // 수신자의 UserModel 가져오기
                FirebaseUtil.getUserModel(receiverId, userModel -> {
                    if (userModel != null) {
                        String receiverCountryCode = userModel.getCountryCode();
                        if (receiverCountryCode != null) {
                            // 메시지 저장 및 전송
                            FirebaseUtil.sendMessageToUser(senderId, receiverId, message, receiverCountryCode);
                        } else {
                            Log.e(TAG, "Receiver's countryCode is null");
                        }
                    } else {
                        Log.e(TAG, "Failed to get userModel for receiver");
                    }
                });
            } else {
                Log.e(TAG, "Failed to get countryCode for sender");
            }
        });
    }

    private static final String TAG = "ChatMessageProcessor";

    public static void sendMessageWithTranslation(ChatMessageModel chatMessage, String senderCountryCode, String receiverCountryCode) {
        String senderId = chatMessage.getSenderId();
        String receiverId = chatMessage.getReceiverId();

        if (receiverId == null || receiverId.isEmpty()) {
            Log.e(TAG, "Receiver ID cannot be null or empty");
            return;
        }

        String chatroomIdSender = CountryChatroomMapper.getChatroomIdForCountry(senderCountryCode) + "_" + CountryChatroomMapper.getChatroomIdForCountry(receiverCountryCode);
        String chatroomIdReceiver = CountryChatroomMapper.getChatroomIdForCountry(receiverCountryCode) + "_" + CountryChatroomMapper.getChatroomIdForCountry(senderCountryCode);

        // 원본 메시지 저장
        // FirebaseUtil.saveMessage(chatroomIdSender, chatMessage);

        // 번역 요청 및 번역 메시지 저장
        SelectLanguage language = new SelectLanguage(senderCountryCode, receiverCountryCode, chatMessage.getMessage());
        PapagoTranslator.translate(language, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Translation request failed", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Translation request unsuccessful: " + response);
                    return;
                }

                try {
                    String responseBody = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    String translatedText = jsonObject.getJSONObject("message").getJSONObject("result").getString("translatedText");

                    chatMessage.setTranslatedMessage(translatedText);

                    // 번역된 메시지를 수신자의 채팅방에 저장
                    FirebaseUtil.saveMessage(chatroomIdReceiver, chatMessage);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to parse translation response", e);
                }
            }
        });
    }




    private static void sendUntranslatedMessage(String senderId, String receiverId, String message, String senderCountryCode, String receiverCountryCode) {
        // 사용자의 countryCode로 메시지를 전송합니다.
        FirebaseUtil.saveOriginalMessage(senderId, receiverId, message, senderCountryCode, receiverCountryCode);
    }



    private static void translateAndSendMessage(String senderId, String receiverId, String sourceText, String senderCountryCode, String receiverCountryCode, String chatroomId) {
        PapagoTranslator.translate(new SelectLanguage(senderCountryCode, receiverCountryCode, sourceText), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // 번역 실패 시 원본 메시지를 전송
                Log.e(TAG, "Translation API request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String translatedMessage = jsonObject.getJSONObject("message").getJSONObject("result").getString("translatedText");
                        // 번역된 메시지를 저장
                        FirebaseUtil.saveTranslatedMessage(chatroomId, sourceText, translatedMessage, senderId);
                        Log.d(TAG, "Translated message: " + translatedMessage);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSONException: " + e.getMessage());
                    }
                } else {
                    // 응답 실패 시 원본 메시지를 전송
                    Log.e(TAG, "Translation API response failed: " + response.message());
                }
            }
        });
    }
}