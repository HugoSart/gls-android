package com.hugovs.gls.util;

import android.app.Activity;
import android.widget.Toast;

/**
 * Class that encapsulates the utilities for toast creation.
 */
public class ToastUtil {

    private ToastUtil() {
        //no instance
    }

    /**
     * Show a toast.
     * @param activity the activity to be served as context.
     * @param toast the toast text.
     * @param length the toast duration.
     */
    public static void showToast(final Activity activity, final String toast, final int length) {
        activity.runOnUiThread(() -> Toast.makeText(activity, toast, length).show());
    }

}
