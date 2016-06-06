package me.wizos.billing.bean;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import me.wizos.billing.ActivityCollector;
import me.wizos.billing.Global;
import me.wizos.billing.utils.ResourceIdUtils;

/**
 * 账单
 * Created by xdsjs on 2015/10/14.
 */
public class Bill implements Parcelable {
    public String name;
    public int age;

    // 必须要创建一个名叫CREATOR的常量。
    public static final Parcelable.Creator<Bill> CREATOR = new Parcelable.Creator<Bill>() {
        @Override
        public Bill createFromParcel(Parcel source) { //重写createFromParcel方法，创建并返回一个获得了数据的user对象
            return new Bill(source);
        }
        @Override
        public Bill[] newArray(int size) {
            return new Bill[size];
        }
    };
    public Bill() {}// 无参数构造器方法，供外界创建类的实例时调用

    private Bill(Parcel source) { // 带参构造器方法私用化，本构造器仅供类的方法createFromParcel调用
        time = source.readString();
        money = source.readString();
        typeId = source.readString();
        accountId = source.readString();
        remark = source.readString();
        upload = source.readInt();
        System.out.println("【Parcel】"+typeId+"--"+remark+"=="+time+"=="+upload);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // 将对象中的属性保存至目标对象dest中
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time);
        dest.writeString(money);
        dest.writeString(typeId);
        dest.writeString(accountId);
        dest.writeString(remark);
        dest.writeInt(upload);
    }


    private String time;
    private String money;
    private String typeId;
    private String accountId;
    private String remark;
    private int upload = 0;//0表示未更新，1表示已更新
    private String typeName;//记账种类的名称
    private Drawable typeDrawable;//记账种类对应的图像


    public Bill( String time, String money, String typeId, String accountId, String remark, int upload) {
        this.time = time;
        this.money = money;
        this.typeId = typeId;
        this.accountId = accountId;
        this.remark = remark;
        this.upload = upload;
    }


    public void setTime(String time) {
        this.time = time;
    }
    public String getTime() {
        return time;
    }

    public void setMoney(String money) {
        this.money = money;
    }
    public String getMoney() {
        return money;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
    public String getTypeId() {
        return typeId;
    }

    public void setAccountId(String account){this.accountId = account;}
    public String getAccountId(){return accountId;}

    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getRemark() {
        return remark;
    }

    public void setUpload(int upload) {
        this.upload = upload;
    }
    public int getUpload() {
        return upload;
    }


    public String getTypeName() {
        String string = this.getTypeId();
        String[] strings = string.split("_");
        String type = strings[0];
//        System.out.println("【getTypeName】" + type + "====" + strings + "====" + strings[1]);
        if(type.equals("payout")){
//            System.out.println("【getTypeName】1");
            return Global.typesPayout[Integer.valueOf(strings[1])];
        }else if(type.equals( "income")){
//            System.out.println("【getTypeName】2");
            return Global.typesIncome[Integer.valueOf(strings[1])];
        }
//        UToast.showToast("在getTypeName错误");
//        System.out.println("【getTypeName】3");
        return null;
    }

    public Drawable getTypeDrawable(String iconName) {
        int resId = ResourceIdUtils.getIdOfResource(iconName, "drawable");
//        System.out.println("【iconName】" + "ttt" + iconName + resId);
        return ActivityCollector.getContext().getResources().getDrawable(resId,null);
    }


    @Override //"\n" +
    public String toString() {
        return "【typeId:--->" + this.getTypeId() + "price:--->" + this.getMoney()+"】";
    }
}
