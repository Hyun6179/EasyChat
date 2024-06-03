package com.example.easychat;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.example.easychat.model.UserModel;
import com.example.easychat.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;

    ChatFragment chatFragment;
    ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        searchButton = findViewById(R.id.main_search_btn);

        searchButton.setOnClickListener((v) -> {
            startActivity(new Intent(MainActivity.this, SearchUserActivity.class));
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_chat) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, chatFragment).commit();
                }
                if (item.getItemId() == R.id.menu_profile) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, profileFragment).commit();
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_chat);

        // ChatRoomManager를 시작합니다.
        ChatRoomManager.start();

        // FCM 토큰 가져오기
        getFCMToken();
    }

    void getFCMToken() {
        FirebaseUtil.getUserModel(FirebaseUtil.currentUserId(), new FirebaseUtil.UserModelCallback() {
            @Override
            public void onUserModelReceived(UserModel userModel) {
                if (userModel != null) {
                    String fcmToken = userModel.getFcmToken();
                    if (fcmToken != null) {
                        // 성공적으로 토큰을 가져왔을 때의 처리
                        Log.d(TAG, "FCM token retrieved: " + fcmToken);
                        // Firebase Firestore에 업데이트 또는 다른 작업 수행
                        FirebaseUtil.currentUserDetails().update("fcmToken", fcmToken)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "FCM token updated in Firestore"))
                                .addOnFailureListener(e -> Log.e(TAG, "Failed to update FCM token in Firestore", e));
                    } else {
                        // 토큰을 가져오지 못했을 때의 처리
                        Log.e(TAG, "FCM token is null");
                    }
                } else {
                    // UserModel을 가져오지 못했을 때의 처리
                    Log.e(TAG, "Failed to retrieve UserModel");
                }
            }
        });
    }

}
