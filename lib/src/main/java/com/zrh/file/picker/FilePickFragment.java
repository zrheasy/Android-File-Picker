package com.zrh.file.picker;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zrh
 * @date 2023/7/6
 */
public class FilePickFragment extends Fragment {

    private FilePickCallback mCallback;
    private final ActivityResultLauncher<Intent> selectFileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) return;
                Intent intent = result.getData();
                List<Uri> data = new ArrayList<>();
                if (intent.getClipData() != null) {
                    ClipData clipData = intent.getClipData();
                    int count = clipData.getItemCount();
                    for (int i = 0; i < count; i++) {
                        data.add(clipData.getItemAt(i).getUri());
                    }
                } else {
                    if (intent.getData() != null) data.add(intent.getData());
                }
                onResult(data);
            });

    private void onResult(List<Uri> data) {
        notifyResult(data);
    }

    public void pick(FilePickOptions options) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(options.getMimeType());
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, options.isAllowMultiple());
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        try {
            selectFileLauncher.launch(intent);
        } catch (Exception e) {
            e.printStackTrace();
            notifyError(ErrorCode.PICKER_NOT_FOUND, "Picker not found!");
        }
    }

    public void setCallback(FilePickCallback callback) {
        mCallback = callback;
    }

    private void notifyResult(List<Uri> data) {
        if (mCallback != null) mCallback.onResult(data);
    }

    private void notifyError(int code, String msg) {
        if (mCallback != null) mCallback.onError(code, msg);
    }
}
