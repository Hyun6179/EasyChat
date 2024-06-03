package com.example.easychat;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.easychat.adapter.ChatRecentAdapter;
import com.example.easychat.model.ChatMessageModel;
import com.example.easychat.model.ChatroomModel;
import com.example.easychat.model.UserModel;
import com.example.easychat.papago_trans.ChatMessageProcessor;
import com.example.easychat.utils.AndroidUtil;
import com.example.easychat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    String countryCode;
    UserModel otherUser;
    String chatroomId;
    ChatroomModel chatroomModel;
    ChatRecentAdapter adapter;

    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    ImageView imageView;
    ListenerRegistration messageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat1);

        // 인텐트에서 다른 사용자를 가져옵니다
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());

        // 뷰를 초기화합니다
        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        imageView = findViewById(R.id.profile_pic_image_view);

        // 뒤로 가기 버튼과 다른 사용자의 사용자 이름을 설정합니다
        backBtn.setOnClickListener(v -> onBackPressed());
        otherUsername.setText(otherUser.getUsername());

        // 다른 사용자의 프로필 사진을 불러옵니다
        FirebaseUtil.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(this, uri, imageView);
                    }
                });

        // 현재 사용자의 국가 코드를 가져옵니다
        FirebaseUtil.getCurrentUserCountryCode(senderCountryCode -> {
            if (senderCountryCode == null) {
                Log.e(TAG, "보내는 사람의 국가 코드를 찾을 수 없습니다");
                return;
            }

            // 다른 사용자의 국가 코드를 가져옵니다
            String receiverCountryCode = otherUser.getCountryCode();
            if (receiverCountryCode == null) {
                Log.e(TAG, "수신자의 국가 코드를 찾을 수 없습니다");
                return;
            }

            // 채팅방 ID를 생성합니다
            chatroomId = FirebaseUtil.getChatroomId(
                    FirebaseUtil.currentUserId(),
                    senderCountryCode,
                    otherUser.getUserId(),
                    receiverCountryCode
            );

            // 채팅방 모델을 가져오거나 생성합니다
            FirebaseUtil.getOrCreateChatroomModel(chatroomId, otherUser.getUserId(), chatroomModel -> {
                if (chatroomModel != null) {
                    // chatroomModel을 사용하여 필요한 업데이트를 수행합니다
                    setupChatRecyclerView();
                    setupMessageListener();
                } else {
                    Log.e(TAG, "채팅방 모델을 가져오거나 생성하는 데 실패했습니다: " + chatroomId);
                }
            });

            // 메시지 보내기 버튼 클릭 리스너
            sendMessageBtn.setOnClickListener(v -> {
                String messageText = messageInput.getText().toString().trim();
                if (!messageText.isEmpty()) {
                    String senderId = FirebaseUtil.currentUserId();
                    String receiverId = otherUser.getUserId();

                    // ChatMessageModel 객체 생성
                    ChatMessageModel chatMessage = new ChatMessageModel(messageText, senderId, receiverId, null, null);

                    // 메시지 번역 및 전송
                    ChatMessageProcessor.sendMessageWithTranslation(chatMessage, senderCountryCode, receiverCountryCode);

                    // 채팅방 정보 업데이트
                    updateChatroomInfo(senderId, receiverId, messageText, senderCountryCode, receiverCountryCode);

                    // 입력 필드 초기화
                    messageInput.setText("");
                }
            });


        });
    }

    private void updateChatroomInfo(String senderId, String receiverId, String message, String senderCountryCode, String receiverCountryCode) {
        String chatroomId = FirebaseUtil.getChatroomId(senderId, senderCountryCode, receiverId, receiverCountryCode);

        // 채팅방 모델 업데이트
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ChatroomModel chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if (chatroomModel != null) {
                    // 채팅방 모델의 필드 업데이트
                    chatroomModel.setLastMessage(message);
                    chatroomModel.setLastMessageSenderId(senderId);
                    chatroomModel.setLastMessageTimestamp(Timestamp.now());

                    // Firestore에 업데이트된 채팅방 모델 저장
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "ChatroomModel updated"))
                            .addOnFailureListener(e -> Log.e(TAG, "Failed to update ChatroomModel: " + e.getMessage()));
                } else {
                    Log.e(TAG, "ChatroomModel is null");
                }
            } else {
                Log.e(TAG, "Failed to get ChatroomModel: " + task.getException().getMessage());
            }
        });
    }



    // countryCode를 설정하는 메서드
    private void setCountryCode() {
        // FirebaseUtil이나 AndroidUtil 등을 사용하여 현재 사용자의 countryCode를 가져와 설정
        FirebaseUtil.getUserCountryCode(FirebaseUtil.currentUserId(), countryCode -> {
            if (countryCode != null) {
                // countryCode가 null이 아니라면 이를 사용하여 처리
                this.countryCode = countryCode;
            } else {
                Log.e(TAG, "Failed to get countryCode");
            }
        });
    }

    private void setupMessageListener() {
        messageListener = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    ChatMessageModel chatMessage = dc.getDocument().toObject(ChatMessageModel.class);
                                    Log.d(TAG, "New message: " + chatMessage.getMessage());

                                    String currentUserId = FirebaseUtil.currentUserId();

                                    if (chatMessage.getSenderId().equals(currentUserId)) {
                                        // 내가 보낸 메시지
                                        Log.d(TAG, "Original message for sender: " + chatMessage.getMessage());
                                        // 여기에서 원본 메시지를 UI에 표시하는 로직을 추가할 수 있습니다.
                                    } else {
                                        // 상대방이 보낸 메시지
                                        if (chatMessage.getTranslatedMessage() != null && !chatMessage.getTranslatedMessage().isEmpty()) {
                                            Log.d(TAG, "Translated message for receiver: " + chatMessage.getTranslatedMessage());
                                            // 여기에서 번역된 메시지를 UI에 표시하는 로직을 추가할 수 있습니다.
                                        } else {
                                            Log.d(TAG, "Original message for receiver: " + chatMessage.getMessage());
                                            // 여기에서 원본 메시지를 UI에 표시하는 로직을 추가할 수 있습니다.
                                        }
                                    }
                                    break;
                                case MODIFIED:
                                    // 메시지가 수정된 경우 처리
                                    break;
                                case REMOVED:
                                    // 메시지가 삭제된 경우 처리
                                    break;
                            }
                        }
                    }
                });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageListener != null) {
            messageListener.remove();
        }
    }

    private void setupChatRecyclerView() {
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new ChatRecentAdapter(options, this); // Context를 직접 전달
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        // 어댑터에 등록된 데이터가 변경될 때마다 맨 위로 스크롤
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(positionStart);
            }

        });
    }

    private void getOrCreateChatroomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if (chatroomModel == null) {
                    // 생성할 때 마지막 메시지는 초기화하지 않습니다.
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserId()),
                            Timestamp.now(),
                            FirebaseUtil.currentUserId(), // 현재 사용자가 마지막 메시지를 보냄
                            "" // 초기에 마지막 메시지는 없음
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "ChatroomModel created"))
                            .addOnFailureListener(e -> Log.e(TAG, "Failed to create ChatroomModel: " + e.getMessage()));
                }
            } else {
                Log.e(TAG, "Failed to get ChatroomModel: " + task.getException().getMessage());
            }
        });
    }



    private void sendNotification(String message) {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserModel currentUser = task.getResult().toObject(UserModel.class);
                try {
                    JSONObject jsonObject = new JSONObject();

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title", currentUser.getUsername());
                    notificationObj.put("body", message);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("userId", currentUser.getUserId());

                    jsonObject.put("notification", notificationObj);
                    jsonObject.put("data", dataObj);
                    jsonObject.put("to", otherUser.getFcmToken());

                    callApi(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void callApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer YOUR_API_KEY")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
            }
        });
    }
}
