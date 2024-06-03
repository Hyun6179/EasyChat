package com.example.easychat.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.easychat.model.UserModel;
import com.google.firebase.Timestamp;

public class AndroidUtil {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void passUserModelAsIntent(Intent intent, UserModel model) {
        intent.putExtra("username", model.getUsername());
        intent.putExtra("phone", model.getPhone());
        intent.putExtra("userId", model.getUserId());
        intent.putExtra("countryCode", model.getCountryCode());
        intent.putExtra("createdTimestamp", model.getCreatedTimestamp());
    }
    public static UserModel getUserModelFromIntent(Intent intent) {
        String username = intent.getStringExtra("username");
        String phoneNumber = intent.getStringExtra("phone");
        String userId = intent.getStringExtra("userId");
        String token = intent.getStringExtra("token");
        String countryCode = intent.getStringExtra("countryCode");

        // 현재 시간으로 타임스탬프를 설정합니다.
        Timestamp timestamp = Timestamp.now();

        // UserModel 객체를 생성하여 반환합니다.
        return new UserModel(username, phoneNumber, userId, token, countryCode, timestamp);
    }


    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

    public static void setProfilePic(Context context, int drawableResId, ImageView imageView) {
        Glide.with(context).load(drawableResId).into(imageView);
    }
}
