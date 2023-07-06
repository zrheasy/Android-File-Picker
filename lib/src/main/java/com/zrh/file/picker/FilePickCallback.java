package com.zrh.file.picker;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * @author zrh
 * @date 2023/7/6
 */
public interface FilePickCallback {
    void onResult(@NonNull List<Uri> data);

    void onError(int code, @NonNull String msg);
}
