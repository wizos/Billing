package me.wizos.billorcs.db;

import android.content.Context;

/**
 * 次数表
 * Created by xdsjs on 2015/10/20.
 */
public class TypeFrequencyDao {
    public static final String TABLE_TYPE_FREQUENCY = "frequency";

    public TypeFrequencyDao(Context context) {
        DBManager.getInstance();
    }

//    //根据当前的时间段更新次数表
//    public void updateTime(BillType billType) {
//        DBOpener.getInstance().updateTime(billType);
//    }
//
    // 被集成到 DBOpener 文件当中
    // 根据当前的时间段获取排好序的次数表
//    public List<BillType> getBillTypeList() {
//        return DBOpener.getInstance().getBillTypes();
//    }
}
