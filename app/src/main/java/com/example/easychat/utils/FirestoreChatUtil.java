package com.example.easychat.utils;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FirestoreChatUtil {
    private static final String TAG = "FirestoreChatUtil";

    private FirebaseFirestore db;

    public FirestoreChatUtil() {
        db = FirebaseFirestore.getInstance();
    }

    public void sendMessage(String chatroomId, String senderId, String message) {
        Map<String, Object> chatMessage = new HashMap<>();
        chatMessage.put("senderId", senderId);
        chatMessage.put("message", message);

        db.collection("chatrooms")
                .document(chatroomId)
                .collection("messages")
                .add(chatMessage)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Message sent with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Error sending message", e));
    }

    public void listenForNewMessages(String chatroomId, OnNewMessageListener listener) {
        db.collection("chatrooms")
                .document(chatroomId)
                .collection("messages")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Listen failed", error);
                        return;
                    }

                    if (value != null) {
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            String senderId = doc.getString("senderId");
                            String message = doc.getString("message");
                            listener.onNewMessage(senderId, message);
                        }
                    }
                });
    }

    public interface OnNewMessageListener {
        void onNewMessage(String senderId, String message);
    }
}
