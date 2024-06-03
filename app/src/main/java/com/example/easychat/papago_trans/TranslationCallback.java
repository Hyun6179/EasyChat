package com.example.easychat.papago_trans;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class TranslationCallback implements Callback {

    @Override
    public void onFailure(Call call, IOException e) {
        e.printStackTrace();
        // 실패 시 처리 로직을 호출합니다.
        onTranslationFailure(e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (response.isSuccessful()) {
            String responseData = response.body().string();
            // 성공 시 번역된 결과를 처리합니다.
            onTranslationSuccess(responseData);
        } else {
            // 응답이 실패했을 때의 처리 로직을 호출합니다.
            onTranslationFailure(new Exception("Response was not successful"));
        }
    }

    public abstract void onTranslationSuccess(String translatedMessage);

    public abstract void onTranslationFailure(Exception e);
}
