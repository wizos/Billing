package me.wizos.billing.utils;

import android.widget.Toast;

import me.wizos.billing.ActivityCollector;

/**
 * Created by xdsjs on 2015/11/27.
 */
public class UToast {
    public static void showToast(String msg) {
        Toast toast = Toast.makeText(ActivityCollector.getContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }
    public static void showShort(String msg) {
        Toast toast = Toast.makeText(ActivityCollector.getContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}
