package com.hugovs.gunfirelocatorstreamer.util;

import android.app.Activity;
import android.widget.Toast;

public class ToastUtil {

    private ToastUtil() {}

    public static void showToast(final Activity activity, final String toast, final int length) {
        activity.runOnUiThread(() -> Toast.makeText(activity, toast, length).show());
    }

}
