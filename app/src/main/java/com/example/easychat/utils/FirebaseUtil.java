package com.example.easychat.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

public class FirebaseUtil {

    // 현재 사용자의 ID를 가져오는 메서드
    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    // 사용자가 로그인되어 있는지 확인하는 메서드
    public static boolean isLoggedIn() {
        if (currentUserId() != null) {
            return true;
        }
        return false;
    }

    // 현재 사용자의 정보를 가져오는 메서드
    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    // 모든 사용자의 컬렉션 레퍼런스를 가져오는 메서드
    public static CollectionReference allUserCollectionReference() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    // 특정 채팅방의 레퍼런스를 가져오는 메서드
    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    // 특정 채팅방의 메시지 레퍼런스를 가져오는 메서드
    public static CollectionReference getChatroomMessageReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    // 두 사용자의 ID를 사용하여 채팅방 ID를 생성하는 메서드
    public static String getChatroomId(String userId1, String userId2) {
        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
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
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
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

    // 사용자의 국가 코드를 업데이트하는 메서드
    public static void updateUserCountryCode(String userId, String countryCode) {
        DocumentReference userRef = getUserReference(userId);
        userRef.update("countryCode", countryCode)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firestore", "Country code updated successfully");
                    } else {
                        Log.e("Firestore", "Failed to update country code", task.getException());
                    }
                });
    }

    // 사용자의 국가 코드를 저장하는 메서드
    public static void saveUserCountryCode(String userId, String countryCode) {
        firestore.collection("users").document(userId)
                .update("countryCode", countryCode)
                .addOnSuccessListener(aVoid -> Log.d("FirestoreHelper", "CountryCode successfully written!"))
                .addOnFailureListener(e -> Log.w("FirestoreHelper", "Error writing document", e));
    }

    // 사용자의 국가 코드를 가져오는 메서드
    public static void getUserCountryCode(String userId, CountryCodeCallback callback) {
        firestore.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String countryCode = document.getString("countryCode");
                            callback.onCallback(countryCode);
                        } else {
                            Log.d("FirestoreHelper", "No such document");
                        }
                    } else {
                        Log.d("FirestoreHelper", "get failed with ", task.getException());
                    }
                });
    }

    // 콜백 인터페이스 정의
    public interface CountryCodeCallback {
        void onCallback(String countryCode);
    }
}
