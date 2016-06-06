package me.wizos.billing.bean;

import android.graphics.drawable.Drawable;

import me.wizos.billing.ActivityCollector;
import me.wizos.billing.utils.ResourceIdUtils;

/**
 * Created by Wizos on 2016/2/16.
 */
public class Account {
    private String account;
    private float count;

    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }

    public float getCount() {
        return count;
    }
    public void setCount(float count) {
        this.count = count;
    }

    public Drawable getAccountDrawable(String iconName) {
        int resId = ResourceIdUtils.getIdOfResource(iconName, "drawable");
//        System.out.println("【iconName】" + "ttt" + iconName + resId);
        return ActivityCollector.getContext().getResources().getDrawable(resId,null);
    }
}
