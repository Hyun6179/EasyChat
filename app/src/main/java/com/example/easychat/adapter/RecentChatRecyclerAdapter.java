package com.example.easychat.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easychat.ChatActivity;
import com.example.easychat.R;
import com.example.easychat.model.ChatroomModel;
import com.example.easychat.model.UserModel;
import com.example.easychat.utils.AndroidUtil;
import com.example.easychat.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());

                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);

                        if (otherUserModel != null) {
                            FirebaseUtil.getOtherProfilePicStorageRef(otherUserModel.getUserId()).getDownloadUrl()
                                    .addOnCompleteListener(t -> {
                                        if (t.isSuccessful()) {
                                            Uri uri = t.getResult();
                                            AndroidUtil.setProfilePic(context, uri, holder.profilePic);
                                        } else {
                                            // 프로필 사진이 없는 경우 기본 이미지를 설정
                                            holder.profilePic.setImageResource(R.drawable.ls_icon);
                                            Log.e("StorageException", "프로필 사진을 찾을 수 없습니다. 기본 이미지를 설정합니다.");
                                        }
                                    }).addOnFailureListener(e -> {
                                        // 다운로드 URL을 가져오는 도중 오류가 발생한 경우 처리
                                        holder.profilePic.setImageResource(R.drawable.ls_icon);
                                        Log.e("StorageException", "프로필 사진을 가져오는 도중 오류가 발생했습니다.", e);
                                    });

                            holder.usernameText.setText(otherUserModel.getUsername());
                            if (lastMessageSentByMe)
                                holder.lastMessageText.setText("You: " + model.getLastMessage());
                            else
                                holder.lastMessageText.setText(model.getLastMessage());
                            holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                            holder.itemView.setOnClickListener(v -> {
                                // 채팅 활동으로 이동
                                Intent intent = new Intent(context, ChatActivity.class);
                                AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            });
                        }
                    } else {
                        Log.e("FirestoreException", "다른 사용자 정보를 가져오는 도중 오류가 발생했습니다.", task.getException());
                    }
                }).addOnFailureListener(e -> {
                    Log.e("FirestoreException", "채팅방에서 다른 사용자를 가져오는 도중 오류가 발생했습니다.", e);
                });
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
