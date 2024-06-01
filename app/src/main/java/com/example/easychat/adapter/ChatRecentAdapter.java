package com.example.easychat.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easychat.model.ChatMessageModel;
import com.example.easychat.papago_trans.PapagoTranslator;
import com.example.easychat.papago_trans.SelectLanguage;
import com.example.easychat.utils.FirebaseUtil;
import com.example.easychat.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class ChatRecentAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecentAdapter.ChatModelViewHolder> {
    private Context context;

    public ChatRecentAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_recycler_row, parent, false);
        return new ChatModelViewHolder(view);
    }
    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        // 사용자가 오른쪽에 보낸 메시지인 경우
        if (model.getSenderId().equals(FirebaseUtil.currentUserId())) {
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextview.setText(model.getMessage());
        } else { // 사용자가 왼쪽에 보낸 메시지인 경우
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatTextview.setText(model.getMessage());
        }
    }




    @Override
    public int getItemCount() {
        return getSnapshots().size();
    }

    public static class ChatModelViewHolder extends RecyclerView.ViewHolder {
        TextView leftChatTextview;
        TextView rightChatTextview;
        LinearLayout leftChatLayout, rightChatLayout;
        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
        }

    }

    private void translateMessage(String message, String sourceLang, String targetLang, TranslateCallback callback, Context context) {
        PapagoTranslator.translate(new SelectLanguage(sourceLang, targetLang, message), new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                callback.onTranslate(message);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    String translatedMessage = jsonObject.getJSONObject("message").getJSONObject("result").getString("translatedText");

                    new Handler(Looper.getMainLooper()).post(() -> {
                        callback.onTranslate(translatedMessage);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onTranslate(message);
                }
            }
        });
    }

    public interface TranslateCallback {
        void onTranslate(String translatedText);
    }
}
