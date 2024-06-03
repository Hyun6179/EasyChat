package com.example.easychat.utils;

import android.content.Context;
import android.util.Log;

import com.google.firebase.storage.StorageException;

public class FirebaseStorageErrorHandler {

    private static final String TAG = "FirebaseStorageError";

    public static void handleStorageError(Context context, StorageException storageException) {
        if (storageException != null) {
            int errorCode = storageException.getErrorCode();
            switch (errorCode) {
                case StorageException.ERROR_OBJECT_NOT_FOUND:
                    // "Object not found" 에러 처리
                    Log.e(TAG, "오브젝트를 찾을 수 없는 에러가 발생했습니다.");
                    // 여기에는 기본 이미지 표시 등의 로직을 추가할 수 있습니다.
                    break;
                case StorageException.ERROR_QUOTA_EXCEEDED:
                    // "Quota exceeded" 에러 처리
                    Log.e(TAG, "저장 공간 할당량 초과 에러가 발생했습니다.");
                    // 여기에는 다른 로직을 추가할 수 있습니다.
                    break;
                // 필요한 경우 추가적인 케이스를 여기에 추가할 수 있습니다.
                default:
                    // 기타 에러 처리
                    Log.e(TAG, "예상치 못한 에러가 발생했습니다: " + storageException.getMessage());
                    // 여기에는 다른 로직을 추가할 수 있습니다.
                    break;
            }
        }
    }
}
