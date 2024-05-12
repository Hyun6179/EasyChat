package com.example.easychat.papago_trans;

import okhttp3.*;

public class PapagoTranslator {

    private static final String PAPAGO_API_URL = "https://naveropenapi.apigw.ntruss.com/nmt/v1/translation";
    private static final String CLIENT_ID = "zvii9x0xba";
    private static final String CLIENT_SECRET = "nSShvwgFXbPuGbJZDmZsTsCKrUcqi74s1u2NvFwX";

    public static void translate(SelectLanguage language, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = new FormBody.Builder()
                .add("source", language.getSourceLang().getCode())
                .add("target", language.getTargetLang().getCode())
                .add("text", language.getMessage())
                .build();

        Request request = new Request.Builder()
                .url(PAPAGO_API_URL)
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("X-NCP-APIGW-API-KEY-ID", CLIENT_ID)
                .addHeader("X-NCP-APIGW-API-KEY", CLIENT_SECRET)
                .build();

        client.newCall(request).enqueue(callback);
    }

}
