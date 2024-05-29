package com.example.easychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.easychat.model.UserModel;
import com.example.easychat.utils.AndroidUtil;
import com.example.easychat.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (getIntent().getExtras() != null) {
            // From notification
            String userId = getIntent().getExtras().getString("userId");
            FirebaseUtil.allUserCollectionReference().document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                UserModel model = document.toObject(UserModel.class);

                                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(mainIntent);

                                Intent intent = new Intent(SplashActivity.this, ChatActivity.class);
                                AndroidUtil.passUserModelAsIntent(intent, model);
                                startActivity(intent);
                            } else {
                                Log.e(TAG, "No such document");
                            }
                        } else {
                            Log.e(TAG, "get failed with ", task.getException());
                        }
                        finish();
                    });

        } else {
            new Handler().postDelayed(() -> {
                if (FirebaseUtil.isLoggedIn()) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginPhoneNumberActivity.class));
                }
                finish();
            }, 1000);
        }
    }
}
