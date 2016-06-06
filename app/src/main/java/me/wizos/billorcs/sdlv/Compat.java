package me.wizos.billorcs.sdlv;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

/**
 * Created by yuyidong on 15/12/7.
 */
class Compat {

    public static void setBackgroundDrawable(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static boolean afterLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
