package me.wizos.billing.bean;

import android.graphics.drawable.Drawable;

import me.wizos.billing.Global;

/**
 * 表示每一种记账种类
 * Created by xdsjs on 2015/10/20.
 */
public class BillType {
//    private int typeId;//记账种类对应的Id
    private String typeId;
    private int typeFrequency;//对应次数
//    private int time;//对应次数
    private int typeNums; // 记账种类的数目
    private String typeName;//类型名称

    private Drawable typeDrawable;

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public int getTime() {
        return typeFrequency;
    }

    public void setTime(int frequency) {
        this.typeFrequency = frequency;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    public int getTypeNums(int typeOutIn){
        this.typeNums = typeOutIn;
        typeNums = Global.typesPayout.length;
        return typeNums;
    }

//    public  Drawable getTypeDrawable() {
//        int resId = ResourceIdUtils.getIdOfResource(this.getTypeId(), "drawable");
//        return ActivityCollector.getContext().getResources().getDrawable(resId,null);
//    }

    @Override
    public String toString() {
        return "name:" + this.getTypeName() + "typeId:" + this.getTypeId() + "time:" + this.getTime();
    }
}
