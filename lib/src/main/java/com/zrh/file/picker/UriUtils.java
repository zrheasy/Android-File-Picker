package com.zrh.file.picker;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import androidx.annotation.WorkerThread;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zrh
 * @date 2023/7/6
 */
public class UriUtils {
    @WorkerThread
    public static File getFileFromUri(Context context, Uri uri, File outputDir) throws IOException {
        String md5 = MD5Utils.md5(uri.toString());
        outputDir = new File(outputDir, md5);
        if (!outputDir.exists()) outputDir.mkdirs();

        Map<String, Object> fields = getMetaInfo(context, uri);
        File file = new File(outputDir, getName(fields));
        if (file.exists()) return file;

        InputStream inputStream = null;
        OutputStream fileOs = null;
        try {
            fileOs = new FileOutputStream(file);
            inputStream = context.getContentResolver().openInputStream(uri);

            int len;
            byte[] buffer = new byte[1024];
            while ((len = inputStream.read(buffer)) != -1) {
                fileOs.write(buffer, 0, len);
            }
            fileOs.flush();
        } finally {
            close(inputStream);
            close(fileOs);
        }

        return file;
    }

    public static String getName(Map<String, Object> fields) {
        Object field = fields.get(OpenableColumns.DISPLAY_NAME);
        if (field != null) {
            return field.toString();
        }
        return "";
    }

    @WorkerThread
    public static Map<String, Object> getMetaInfo(Context context, Uri uri) {
        Map<String, Object> result = new HashMap<>();
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) return result;
        cursor.moveToFirst();
        String[] columnNames = cursor.getColumnNames();
        for (String columnName : columnNames) {
            Object field = getFiled(cursor, columnName);
            if (field != null) {
                result.put(columnName, field);
            }
        }
        cursor.close();
        return result;
    }

    private static Object getFiled(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index == -1) return null;
        int type = cursor.getType(index);
        switch (type) {
            case Cursor.FIELD_TYPE_BLOB: {
                return cursor.getBlob(index);
            }
            case Cursor.FIELD_TYPE_FLOAT: {
                return cursor.getFloat(index);
            }
            case Cursor.FIELD_TYPE_INTEGER: {
                return cursor.getInt(index);
            }
            case Cursor.FIELD_TYPE_STRING: {
                return cursor.getString(index);
            }
            case Cursor.FIELD_TYPE_NULL: {
                return null;
            }
        }
        return null;
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {}
        }
    }
}
