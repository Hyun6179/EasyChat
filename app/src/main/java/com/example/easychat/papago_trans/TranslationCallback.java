package com.example.easychat.papago_trans;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TranslationCallback implements Callback {

    private String filePath;

    public TranslationCallback(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        e.printStackTrace();
        // 실패 시 처리 로직을 작성합니다.
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (response.isSuccessful()) {
            String responseData = response.body().string();
            saveToFile(responseData, filePath);
        } else {
            // 응답이 실패했을 때의 처리 로직을 작성합니다.
        }
    }

    private void saveToFile(String data, String filePath) {
        FileWriter fileWriter = null;
        try {
            File file = new File(filePath);
            fileWriter = new FileWriter(file);
            fileWriter.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
