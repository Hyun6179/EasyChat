package com.example.easychat.papago_trans;

import android.util.Log;
import okhttp3.*;

public class PapagoTranslator {
    private static final String TAG = "PapagoTranslator";
    private static final String PAPAGO_API_URL = "https://naveropenapi.apigw.ntruss.com/nmt/v1/translation";
    private static final String CLIENT_ID = "zvii9x0xba";
    private static final String CLIENT_SECRET = "nSShvwgFXbPuGbJZDmZsTsCKrUcqi74s1u2NvFwX";

    public static void translate(SelectLanguage language, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        // 언어 소스 및 대상 가져오기
        String sourceLang = language.getSourceLang();
        String targetLang = language.getTargetLang();

        // 언어 소스 및 대상이 null이 아닌지 확인
        if (sourceLang == null || targetLang == null) {
            // 언어 소스 또는 대상이 null이면 예외를 던짐
            throw new IllegalArgumentException("Language source or target cannot be null");
        }

        // 로그에 언어 정보 출력
        Log.d(TAG, "Source Language: " + sourceLang);
        Log.d(TAG, "Target Language: " + targetLang);
        Log.d(TAG, "Message: " + language.getMessage());

        // HTTP 요청 본문 만들기
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = new FormBody.Builder()
                .add("source", sourceLang)
                .add("target", targetLang)
                .add("text", language.getMessage())
                .build();

        // HTTP 요청 만들기
        Request request = new Request.Builder()
                .url(PAPAGO_API_URL)
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("X-NCP-APIGW-API-KEY-ID", CLIENT_ID)
                .addHeader("X-NCP-APIGW-API-KEY", CLIENT_SECRET)
                .build();

        // HTTP 요청 보내기
        client.newCall(request).enqueue(callback);
    }
}
