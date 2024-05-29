package com.example.easychat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;


import com.example.easychat.model.UserModel;
import com.example.easychat.papago_trans.SelectLanguage;
import com.hbb20.CountryCodePicker;

public class LoginPhoneNumberActivity extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    Button sendOtpBtn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);

        countryCodePicker = findViewById(R.id.login_countrycode);
        phoneInput = findViewById(R.id.login_mobile_number);
        sendOtpBtn = findViewById(R.id.send_otp_btn);
        progressBar = findViewById(R.id.login_progress_bar);

        progressBar.setVisibility(View.GONE);

        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        sendOtpBtn.setOnClickListener((v) -> {
            if (!countryCodePicker.isValidFullNumber()) {
                phoneInput.setError("Phone number not valid");
                return;
            }

            // countryCodePicker에서 국가 코드 가져오기
            String countryCode = countryCodePicker.getSelectedCountryCode();
            Log.d("countryCodePicker", countryCode);

            SelectLanguage selectLanguage = new SelectLanguage(countryCode);
            String languageCode = selectLanguage.getLanguageCodeFromCountryCode(countryCode);
            Log.d("language", languageCode);

            Intent intent = new Intent(LoginPhoneNumberActivity.this, LoginOtpActivity.class);
            intent.putExtra("phone", countryCodePicker.getFullNumberWithPlus());

            // Intent에 countryCode도 추가
            intent.putExtra("countryCode", languageCode);


            startActivity(intent);
        });

    }

}