package me.wizos.billing.bean;

import android.graphics.drawable.Drawable;

import me.wizos.billing.ActivityCollector;
import me.wizos.billing.utils.ResourceIdUtils;

/**
 * Created by Wizos on 2016/2/21.
 */
public class KeyValue {
private String key;
private float value;

        public String getKey() {
            return key;
        }
        public void setKey(String key) {
            this.key = key;
        }

        public float getValue() {
            return value;
        }
        public void setValue(float value) {
            this.value = value;
        }

        public Drawable getDrawable(String iconName) {
            int resId = ResourceIdUtils.getIdOfResource(iconName, "drawable");
            return ActivityCollector.getContext().getResources().getDrawable(resId,null);
        }
}
