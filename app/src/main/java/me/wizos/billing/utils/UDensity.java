package me.wizos.billing.utils;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.util.DisplayMetrics;

import me.wizos.billing.ActivityCollector;

/**
 * Created by Wizos on 2016/2/13.
 */
public class UDensity {
    /**
     * 从 R.dimen 文件中获取到数值，再根据手机的分辨率转成为 px(像素)
     */
    public static int get2Px(Context context,@DimenRes int id) {
        final float scale = context.getResources().getDisplayMetrics().density;
        final float dpValue = (int)context.getResources().getDimension(id);
        return (int) (dpValue * scale + 0.5f);
    }
    public static int getDimen(Context context,@DimenRes int id) {
//        final int dimen = (int)context.getResources().getDimension(id);
        return (int)context.getResources().getDimension(id);
    }
    public static int getColor(@ColorRes int id) {
//        final int dimen = (int)context.getResources().getDimension(id);
        return (int)ActivityCollector.getContext().getResources().getColor(id);
    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = ActivityCollector.getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }



    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }



}
