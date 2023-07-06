package com.zrh.file.picker;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

/**
 * @author zrh
 * @date 2023/7/6
 */
public class FilePicker {
    private static final String TAG = "FilePicker";

    public static void pick(FragmentActivity activity, FilePickOptions options, FilePickCallback callback) {
        pick(activity, activity.getSupportFragmentManager(), options, callback);
    }

    public static void pick(Fragment fragment, FilePickOptions options, FilePickCallback callback) {
        pick(fragment.requireContext(), fragment.getChildFragmentManager(), options, callback);
    }

    private static void pick(Context context,
                             FragmentManager fragmentManager,
                             FilePickOptions options,
                             FilePickCallback callback) {
        FilePickFragment filePickFragment = getFilePickFragment(fragmentManager);

        filePickFragment.setCallback(callback);
        filePickFragment.pick(options);
    }

    private static FilePickFragment getFilePickFragment(FragmentManager fragmentManager) {
        Fragment fragment = fragmentManager.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new FilePickFragment();
            fragmentManager.beginTransaction().add(fragment, TAG).commitNow();
        }
        return (FilePickFragment) fragment;
    }

}
