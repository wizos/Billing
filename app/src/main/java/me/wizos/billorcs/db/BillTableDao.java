package me.wizos.billorcs.db;

import android.content.Context;

import java.util.List;

import me.wizos.billorcs.bean.Bill;

/**
 * 账单表
 * Created by xdsjs on 2015/10/14.
 */
public class BillTableDao {
    public static final String TABLE_NAME = "bill_table";
    public static final String COLUMN_NAME_ID = "id"; //主键
    public static final String COLUMN_NAME_TIME = "time"; //记账时间
    public static final String COLUMN_NAME_MONEY = "money"; //记账金额
    public static final String COLUMN_NAME_TYPE = "type"; //记账类型
    public static final String COLUMN_NAME_ACCOUNT = "account"; //记账账户
    public static final String COLUMN_NAME_REMARK = "remark"; //记账备注
    public static final String COLUMN_NAME_UPLOAD = "upload";//标记是否已经备份,0未备份,1已备份
//    public static final String COLUMN_NAME_OUTIN = "outin"; //记账的支出或收入

    public BillTableDao() {
        DBManager.getInstance();
    }
    public BillTableDao(Context context) {
        DBManager.getInstance();
    }

    //保存账单list
    public void saveBillList(List<Bill> bills) {
//        DBOpener.getInstance().saveBillList(bills);
    }

    //保存单个记录
    public void addBill(Bill bill) {
        DBManager.getInstance().addBill(bill);
    }
    //保存单个记录
    public void editBill(Bill bill) {
        DBManager.getInstance().editBill(bill);
    }
    //保存单个记录
    public void deleteBill(String timeAsID) {
        DBManager.getInstance().deleteBill(timeAsID);
    }
    //根据日期获取账单List
    public List<Bill> getBillList(String time) {
        return DBManager.getInstance().getBills(time);
    }
//    public List<Bill> getBillAll() {
//        return DBManager.getInstance().getBillData();
//    }
    //根据 列和键 获取账单
    public List<Bill> getDB(String column,String key) {
        return DBManager.getInstance().getDB(column, key);
    }
//
//    //更新账单list
//    public void updateBillList(List<Bill> bills) {
//        DBOpener.getInstance().updateBillList(bills);
//    }
}
