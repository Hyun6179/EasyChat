package com.example.easychat.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.List;

public class ChatroomModel {
    String chatroomId;
    List<String> userIds;
    Object lastMessageTimestamp;
    String lastMessageSenderId;
    String lastMessage;

    public ChatroomModel() {
    }

    public ChatroomModel(String chatroomId, List<String> userIds, Timestamp lastMessageTimestamp, String lastMessageSenderId, String lastMessage) {
        this.chatroomId = chatroomId;
        this.userIds = userIds;
        // Firestore의 서버 타임스탬프 사용
        this.lastMessageTimestamp = lastMessageTimestamp != null ? lastMessageTimestamp : FieldValue.serverTimestamp();
        this.lastMessageSenderId = lastMessageSenderId != null ? lastMessageSenderId : "";
        this.lastMessage = lastMessage != null ? lastMessage : "";
    }



    // getter, setter 메서드 추가
    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public Object getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
