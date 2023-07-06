package com.zrh.file.picker;

/**
 * @author zrh
 * @date 2023/7/6
 */
public class FilePickOptions {
    private String mimeType = "*/*";
    private boolean allowMultiple = true;

    public boolean isAllowMultiple() {
        return allowMultiple;
    }

    public void setAllowMultiple(boolean allowMultiple) {
        this.allowMultiple = allowMultiple;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
