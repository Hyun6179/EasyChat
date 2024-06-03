package com.example.easychat.utils;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.easychat.CountryChatroomMapper;
import com.example.easychat.model.ChatMessageModel;
import com.example.easychat.model.ChatroomModel;
import com.example.easychat.model.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FirebaseUtil {

    // 현재 사용자의 ID를 가져오는 메서드
    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    // 사용자가 로그인되어 있는지 확인하는 메서드
    public static boolean isLoggedIn() {
        return currentUserId() != null;
    }

    // 현재 사용자의 정보를 가져오는 메서드
    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    // 모든 사용자의 컬렉션 레퍼런스를 가져오는 메서드
    public static CollectionReference allUserCollectionReference() {
        return FirebaseFirestore.getInstance().collection("users");
    }


    // 두 사용자의 ID를 사용하여 채팅방 ID를 생성하는 메서드
    public static String getChatroomId(String userId1, String user1CountryCode, String userId2, String user2CountryCode) {
        String chatroomId1 = CountryChatroomMapper.getChatroomIdForCountry(user1CountryCode);
        String chatroomId2 = CountryChatroomMapper.getChatroomIdForCountry(user2CountryCode);
        return chatroomId1 + "_" + chatroomId2;
    }

    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }



    public static CollectionReference getChatroomMessageReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId).collection("messages");
    }



    // 모든 채팅방의 컬렉션 레퍼런스를 가져오는 메서드
    public static CollectionReference allChatroomCollectionReference() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    // 채팅방에 있는 다른 사용자의 정보를 가져오는 메서드
    public static DocumentReference getOtherUserFromChatroom(List<String> userIds) {
        if (userIds.get(0).equals(FirebaseUtil.currentUserId())) {
            return allUserCollectionReference().document(userIds.get(1));
        } else {
            return allUserCollectionReference().document(userIds.get(0));
        }
    }
    // 타임스탬프를 문자열로 변환하는 메서드
    public static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp.toDate());
    }


    // 로그아웃하는 메서드
    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    // 현재 사용자의 프로필 사진 저장소 레퍼런스를 가져오는 메서드
    public static StorageReference getCurrentProfilePicStorageRef() {
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtil.currentUserId());
    }

    // 다른 사용자의 프로필 사진 저장소 레퍼런스를 가져오는 메서드
    public static StorageReference getOtherProfilePicStorageRef(String otherUserId) {
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(otherUserId);
    }

    // Firestore 인스턴스 생성
    private static FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    // 특정 사용자의 Firestore 레퍼런스를 가져오는 메서드
    public static DocumentReference getUserReference(String userId) {
        return firestore.collection("users").document(userId);
    }

    public static void getCurrentUserCountryCode(CountryCodeCallback callback) {
        String currentUserId = currentUserId();
        getUserCountryCode(currentUserId, callback);
    }

    // 사용자의 국가 코드를 가져오는 메서드
    public static void getUserCountryCode(String userId, CountryCodeCallback callback) {
        FirebaseFirestore.getInstance().collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String countryCode = document.getString("countryCode");
                            callback.onCountryCodeReceived(countryCode);
                        } else {
                            Log.e(TAG, "User document not found");
                            callback.onCountryCodeReceived(null);
                        }
                    } else {
                        Log.e(TAG, "Failed to get user document: " + task.getException().getMessage());
                        callback.onCountryCodeReceived(null);
                    }
                });
    }

    public interface CountryCodeCallback {
        void onCountryCodeReceived(String countryCode);
    }

    // FirebaseUtil 클래스에 추가
    public static void getUserModel(String userId, UserModelCallback callback) {
        FirebaseFirestore.getInstance().collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            UserModel userModel = document.toObject(UserModel.class);
                            callback.onUserModelReceived(userModel);
                        } else {
                            Log.e(TAG, "User document not found");
                            callback.onUserModelReceived(null);
                        }
                    } else {
                        Log.e(TAG, "Failed to get user document: " + task.getException().getMessage());
                        callback.onUserModelReceived(null);
                    }
                });
    }

    // UserModelCallback 인터페이스 추가
    public interface UserModelCallback {
        void onUserModelReceived(UserModel userModel);
    }

    // 사용자 국가 코드에 따른 채팅방 할당 메서드 추가
    public static void assignChatroom(String userId1, String userId2, AssignChatroomCallback callback) {
        getUserCountryCode(userId1, user1CountryCode -> {
            if (user1CountryCode == null) {
                Log.e(TAG, "User 1 country code not found");
                callback.onAssignChatroom(null);
                return;
            }

            getUserCountryCode(userId2, user2CountryCode -> {
                if (user2CountryCode == null) {
                    Log.e(TAG, "User 2 country code not found");
                    callback.onAssignChatroom(null);
                    return;
                }

                String chatroomId = FirebaseUtil.getChatroomId(userId1, user1CountryCode, userId2, user2CountryCode);
                DocumentReference chatroomRef = FirebaseUtil.getChatroomReference(chatroomId);

                FirebaseFirestore.getInstance().runTransaction((Transaction.Function<Void>) transaction -> {
                            DocumentSnapshot snapshot = transaction.get(chatroomRef);
                            if (!snapshot.exists()) {
                                ChatroomModel chatroom = new ChatroomModel(chatroomId, Arrays.asList(userId1, userId2), null, null, null);
                                transaction.set(chatroomRef, chatroom);
                            }
                            return null;
                        }).addOnSuccessListener(aVoid -> callback.onAssignChatroom(chatroomId))
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error assigning chatroom", e);
                            callback.onAssignChatroom(null);
                        });
            });
        });
    }


    public interface AssignChatroomCallback {
        void onAssignChatroom(String chatroomId);
    }

    public static void getUserLastMessageCountryCode(String userId, LastMessageCountryCodeCallback callback) {
        FirebaseFirestore.getInstance().collection("users").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String lastMessageCountryCode = document.getString("lastMessageCountryCode");
                            callback.onLastMessageCountryCodeReceived(lastMessageCountryCode);
                        } else {
                            Log.e(TAG, "User document not found");
                            callback.onLastMessageCountryCodeReceived(null);
                        }
                    } else {
                        Log.e(TAG, "Failed to get user document: " + task.getException().getMessage());
                        callback.onLastMessageCountryCodeReceived(null);
                    }
                });
    }

    public interface LastMessageCountryCodeCallback {
        void onLastMessageCountryCodeReceived(String lastMessageCountryCode);
    }


    // 메시지 모델 클래스
    public static class Message {
        private String message;
        private String translatedMessage;
        private Timestamp timestamp;
        private String senderId;

        public Message() {
        }

        public Message(String message, String translatedMessage, Timestamp timestamp, String senderId) {
            this.message = message;
            this.translatedMessage = translatedMessage;
            this.timestamp = timestamp;
            this.senderId = senderId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getTranslatedMessage() {
            return translatedMessage;
        }

        public void setTranslatedMessage(String translatedMessage) {
            this.translatedMessage = translatedMessage;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
        }

        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }
    }

    public static void saveMessage(String chatroomId, ChatMessageModel chatMessage) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chatrooms").document(chatroomId).collection("messages").add(chatMessage)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Message saved successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving message", e));
    }

    public static void updateMessage(String translatedMessage, Timestamp timestamp) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference messagesRef = db.collection("messages");

        // 메시지를 timestamp를 기준으로 내림차순으로 정렬하여 가장 최근 메시지를 가져옵니다.
        messagesRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            String messageId = documentSnapshot.getId();

                            // 메시지 업데이트
                            messagesRef.document(messageId).update(
                                    "translatedMessage", translatedMessage,
                                    "timestamp", timestamp
                            ).addOnSuccessListener(aVoid -> {
                                Log.d("FirebaseUtil", "메시지 업데이트 성공: " + messageId);
                            }).addOnFailureListener(e -> {
                                Log.e("FirebaseUtil", "메시지 업데이트 실패: " + messageId, e);
                            });
                        } else {
                            Log.e("FirebaseUtil", "가장 최근 메시지를 찾을 수 없습니다.");
                        }
                    } else {
                        Log.e("FirebaseUtil", "메시지를 가져오는 동안 오류가 발생했습니다.", task.getException());
                    }
                });
    }





    // 번역되지 않은 메시지를 저장하는 메서드
    public static void saveOriginalMessage(String senderId, String receiverId, String message, String senderCountryCode, String receiverCountryCode) {
        // 채팅방 ID 가져오기
        String chatroomId = getChatroomId(senderId, senderCountryCode, receiverId, receiverCountryCode);

        // 메시지 데이터 구성
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("message", message);
        messageData.put("senderId", senderId);
        messageData.put("timestamp", Timestamp.now());

        // 메시지 추가
        getChatroomMessageReference(chatroomId).add(messageData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Original message successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing original message", e));

        // 채팅방의 마지막 메시지 업데이트
        FirebaseUtil.getChatroomReference(chatroomId).update(
                        "lastMessage", message,
                        "lastMessageSenderId", senderId,
                        "lastMessageTimestamp", Timestamp.now()
                ).addOnSuccessListener(aVoid -> Log.d(TAG, "Chatroom last message updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating chatroom last message", e));
    }






    // 번역된 메시지를 저장하는 메서드
    public static void saveTranslatedMessage(String chatroomId, String message, String translatedMessage, String senderId) {
        // 메시지 데이터 구성
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("message", message);
        messageData.put("translatedMessage", translatedMessage);
        messageData.put("senderId", senderId);
        messageData.put("timestamp", FieldValue.serverTimestamp()); // Firestore Timestamp 사용

        // 채팅방 메시지 컬렉션 레퍼런스 가져오기
        CollectionReference chatroomMessageRef = getChatroomMessageReference(chatroomId);

        // 메시지를 채팅방에 추가
        chatroomMessageRef.add(messageData)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Translated message successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing translated message", e));
    }

    public static void saveAndSendTranslatedMessage(String chatroomId, String message, String translatedMessage, String senderId, String receiverId) {
        // 메시지 데이터 생성
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("message", message);
        messageData.put("translatedMessage", translatedMessage);
        messageData.put("senderId", senderId);
        messageData.put("receiverId", receiverId); // 추가: 수신자 ID
        // Firestore 서버 타임스탬프로 설정
        messageData.put("timestamp", FieldValue.serverTimestamp());

        // 채팅 메시지 저장 및 전송
        getChatroomMessageReference(chatroomId).add(messageData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Translated message successfully written!");

                    // 성공적으로 메시지를 저장한 후, 수신자에게 메시지 전송
                    // 예시: Firebase Cloud Messaging 또는 Realtime Database의 트리거를 통한 실시간 업데이트 등을 활용하여 수신자에게 메시지 전송
                    sendMessageToReceiver(chatroomId, documentReference.getId(), messageData);
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error writing translated message", e));
    }





    // 수신자에게 메시지를 전송하는 메서드
    public static void sendMessageToReceiver(String chatroomId, String messageId, Map<String, Object> messageData) {
        // 수신자 ID를 messageData에서 가져옵니다.
        String receiverId = (String) messageData.get("receiverId");

        // messageData에서 번역된 메시지를 가져옵니다.
        String translatedMessage = (String) messageData.get("translatedMessage");

        // 수신자에게 전송할 메시지 데이터를 생성합니다.
        Map<String, Object> receiverMessageData = new HashMap<>();
        receiverMessageData.put("message", translatedMessage); // 번역된 메시지 전송
        receiverMessageData.put("senderId", messageData.get("senderId")); // 송신자 ID 전송

        // 타임스탬프 값을 현재 시간으로 설정합니다.
        receiverMessageData.put("timestamp", System.currentTimeMillis()); // 타임스탬프 전송

        // 채팅방의 messages 하위에 새로운 메시지를 추가합니다.
        DatabaseReference chatroomRef = FirebaseDatabase.getInstance().getReference("chatrooms").child(chatroomId).child("messages");
        chatroomRef.child(messageId).setValue(receiverMessageData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Message successfully sent to receiver"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to send message to receiver", e));
    }





    //현재 사용자의 FCM 토큰을 가져오는 메서드
    public static void getFCMToken(FcmTokenCallback callback) {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String token) {
                        Log.d(TAG, "FCM token retrieved: " + token);
                        callback.onFcmTokenReceived(token);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to retrieve FCM token", e);
                        callback.onFcmTokenReceived(null);
                    }
                });
    }

    public interface FcmTokenCallback {
        void onFcmTokenReceived(String token);

        void onTokenReceived(String fcmToken);
    }

    // 사용자의 국가 코드에 따라 적절한 채팅방에 메시지를 전송하는 메서드
    public static void sendMessageToUser(String senderId, String receiverId, String message, String translatedMessage) {
        // 사용자의 언어 또는 국가 코드를 가져와서 적절한 채팅방 ID를 가져오는 메서드 호출
        getUserCountryCode(senderId, senderCountryCode -> {
            if (senderCountryCode != null) {
                // CountryChatroomMapper를 사용하여 적절한 채팅방 ID를 가져옴
                String chatroomId = CountryChatroomMapper.getChatroomIdForCountry(senderCountryCode);

                if (chatroomId != null) {
                    // 메시지 전송
                    sendMessageToUser(senderId, receiverId, message, translatedMessage, chatroomId);
                } else {
                    Log.e(TAG, "Failed to get chatroom ID for sender's country code: " + senderCountryCode);
                }
            } else {
                Log.e(TAG, "Failed to get sender's country code");
            }
        });
    }



    // 메시지를 채팅방에 전송하는 메서드
    private static void sendMessageToUser(String senderId, String receiverId, String message, String translatedMessage, String chatroomId) {
        // 메시지 모델 생성
        Message chatMessage = new Message(message, translatedMessage, Timestamp.now(), senderId);
        chatMessage.setTranslatedMessage(translatedMessage); // 번역된 메시지 설정

        // 채팅방의 메시지 컬렉션 레퍼런스 가져오기
        CollectionReference chatroomMessageRef = getChatroomMessageReference(chatroomId);

        // 메시지를 채팅방에 추가
        chatroomMessageRef.add(chatMessage)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Message sent successfully!"))
                .addOnFailureListener(e -> Log.e(TAG, "Error sending message: " + e.getMessage()));
    }

    public static void getOrCreateChatroomModel(String chatroomId, String otherUserId, OnChatroomModelReceived callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference chatroomRef = db.collection("chatrooms").document(chatroomId);

        chatroomRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                // 이미 존재하는 채팅방 모델을 가져옵니다
                ChatroomModel chatroomModel = task.getResult().toObject(ChatroomModel.class);
                callback.onReceived(chatroomModel);
            } else {
                // 새로운 채팅방 모델을 생성합니다
                List<String> userIds = Arrays.asList(FirebaseUtil.currentUserId(), otherUserId);
                ChatroomModel newChatroomModel = new ChatroomModel(chatroomId, userIds, null, null, null);
                chatroomRef.set(newChatroomModel).addOnCompleteListener(createTask -> {
                    if (createTask.isSuccessful()) {
                        callback.onReceived(newChatroomModel);
                    } else {
                        callback.onReceived(null);
                    }
                });
            }
        });
    }

    public static void updateChatroomModel(String chatroomId, ChatMessageModel chatMessage) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference chatroomRef = db.collection("chatrooms").document(chatroomId);

        chatroomRef.update("lastMessage", chatMessage.getMessage(),
                        "lastMessageSenderId", chatMessage.getSenderId(),
                        "lastMessageTimestamp", FieldValue.serverTimestamp())
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("FirebaseUtil", "채팅방 모델 업데이트에 실패했습니다: " + chatroomId);
                    }
                });
    }




    // 채팅방 모델을 받아오는 콜백 인터페이스
    public interface OnChatroomModelReceived {
        void onReceived(ChatroomModel chatroomModel);
    }



}
