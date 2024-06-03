package com.example.easychat;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.easychat.model.UserModel;
import com.example.easychat.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginUsernameActivity extends AppCompatActivity {

    EditText usernameInput;
    Button letMeInBtn;
    ProgressBar progressBar;
    String phoneNumber;
    UserModel userModel;
    String countryCode; // 추가: 국가 코드 저장 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_username);

        usernameInput = findViewById(R.id.login_username);
        letMeInBtn = findViewById(R.id.login_let_me_in_btn);
        progressBar = findViewById(R.id.login_progress_bar);

        phoneNumber = getIntent().getExtras().getString("phone");
        countryCode = getIntent().getStringExtra("countryCode"); // 추가: Intent로부터 countryCode 가져오기
        getUsername();

        letMeInBtn.setOnClickListener((v -> {
            setUsername();
        }));
    }

    void setUsername() {
        String username = usernameInput.getText().toString();
        if (username.isEmpty() || username.length() < 3) {
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }
        setInProgress(true);
        if (userModel != null) {
            userModel.setUsername(username);
            userModel.setCountryCode(countryCode); // userModel에 countryCode가 null값으로 입력되어 마지막에 추가로 입력하는 과정
            updateUserModel(userModel);
        } else {
            // FCM 토큰 가져오기
            FirebaseUtil.getUserModel(FirebaseUtil.currentUserId(), new FirebaseUtil.UserModelCallback() {
                @Override
                public void onUserModelReceived(UserModel user) {
                    if (user != null) {
                        userModel = user;
                        userModel.setUsername(username);
                        userModel.setCountryCode(countryCode);
                        updateUserModel(userModel);
                    } else {
                        // FCM 토큰을 받은 후 사용자 모델 생성 및 저장
                        FirebaseUtil.getFCMToken(new FirebaseUtil.FcmTokenCallback() {
                            @Override
                            public void onFcmTokenReceived(String token) {
                                if (token != null) {
                                    // FCM 토큰을 받은 후 사용자 모델 생성 및 저장
                                    userModel = new UserModel(username, phoneNumber, FirebaseUtil.currentUserId(), token, countryCode, Timestamp.now());
                                    updateUserModel(userModel);
                                } else {
                                    // FCM 토큰을 받지 못한 경우 에러 처리
                                    Log.e(TAG, "Failed to retrieve FCM token");
                                }
                            }

                            @Override
                            public void onTokenReceived(String fcmToken) {
                                // Not used in this context
                            }
                        });
                    }
                }
            });
        }
    }



    // 사용자 모델을 Firebase에 업데이트하는 메서드
    private void updateUserModel(UserModel userModel) {
        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginUsernameActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }


    void getUsername() {
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    userModel = task.getResult().toObject(UserModel.class);
                    if (userModel != null) {
                        usernameInput.setText(userModel.getUsername());
                    }
                }
            }
        });
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            letMeInBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            letMeInBtn.setVisibility(View.VISIBLE);
        }
    }
}
