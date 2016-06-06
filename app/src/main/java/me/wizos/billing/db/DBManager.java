package me.wizos.billing.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import me.wizos.billing.Global;
import me.wizos.billing.bean.Bill;
import me.wizos.billing.bean.BillType;
import me.wizos.billing.utils.UTime;

/**
 * Created by Wizos on 2016/1/15.
 */
public class DBManager {
    static private DBManager dbMgr = new DBManager();
    private DBOpener dbHelper;

    public void onInit(Context context) {
        dbHelper = DBOpener.getInstance(context);
    }

    public static synchronized DBManager getInstance() {
        return dbMgr;
    }


    synchronized public void closeDB() {
        if (dbHelper != null) {
            dbHelper.closeDB();
        }
    }



    // 更新账单list
    synchronized void updateBillList(List<Bill> bills) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            for (Bill bill : bills) {
                ContentValues cv = new ContentValues();
                cv.put(BillTableDao.COLUMN_NAME_UPLOAD, 1);
                db.update(BillTableDao.TABLE_NAME, cv, "time = ?", new String[]{bill.getTime() + ""});
            }
        }
    }
    //保存账单list
    synchronized void saveBillList(List<Bill> bills) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(BillTableDao.TABLE_NAME, null, null);
            for (Bill bill : bills) {
                ContentValues cv = new ContentValues();
                cv.put(BillTableDao.COLUMN_NAME_TIME, bill.getTime());
                cv.put(BillTableDao.COLUMN_NAME_MONEY, bill.getMoney());
                cv.put(BillTableDao.COLUMN_NAME_TYPE, bill.getTypeId());
                cv.put(BillTableDao.COLUMN_NAME_ACCOUNT, bill.getAccountId());
                cv.put(BillTableDao.COLUMN_NAME_REMARK, bill.getRemark());
                cv.put(BillTableDao.COLUMN_NAME_UPLOAD, bill.getUpload());
                db.replace(BillTableDao.TABLE_NAME, null, cv);
            }
        }
    }
    //保存单个账单
    public synchronized void addBill(Bill bill) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues cv = new ContentValues();
            // SQLiteDatabase中提供了一个 insert()方法，专门用于添加数据的。它接收三个参数，1是表名，向哪张表里添加数据。2 参数用于在未指定添加数据的情况下给某些可为空的列自动赋值 NULL，一般直接传入null即可。3 是一个 ContentValues对象
            // ContentValues对象的 put() 方法用于向 ContentValues 中添加数据，只需要将表中的每个列名以及相应的待添加数据传入即可
            cv.put(BillTableDao.COLUMN_NAME_TIME, bill.getTime());
            cv.put(BillTableDao.COLUMN_NAME_MONEY, bill.getMoney());
            cv.put(BillTableDao.COLUMN_NAME_TYPE, bill.getTypeId());
            cv.put(BillTableDao.COLUMN_NAME_ACCOUNT, bill.getAccountId());
            cv.put(BillTableDao.COLUMN_NAME_REMARK, bill.getRemark());
            cv.put(BillTableDao.COLUMN_NAME_UPLOAD, bill.getUpload());
            db.replace(BillTableDao.TABLE_NAME, null, cv);
        }
    }
    //保存单个账单
    public synchronized void editBill(Bill bill) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues cv = new ContentValues();
            cv.put(BillTableDao.COLUMN_NAME_TIME, bill.getTime());
            cv.put(BillTableDao.COLUMN_NAME_MONEY, bill.getMoney());
            cv.put(BillTableDao.COLUMN_NAME_TYPE, bill.getTypeId());
            cv.put(BillTableDao.COLUMN_NAME_ACCOUNT, bill.getAccountId());
            cv.put(BillTableDao.COLUMN_NAME_REMARK, bill.getRemark());
            cv.put(BillTableDao.COLUMN_NAME_UPLOAD, bill.getUpload());
            System.out.println("【editBill】" + bill.getTime());
            db.update(BillTableDao.TABLE_NAME, cv, "time = ?", new String[]{bill.getTime() + ""});
        }
    }
    public synchronized void deleteBill(String timeAsID){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(BillTableDao.TABLE_NAME, BillTableDao.COLUMN_NAME_TIME + "=?", new String[]{timeAsID});
        }

    }

    //根据日期获取账单
    public synchronized List<Bill> getBills(String time) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Bill> bills = new ArrayList<>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + BillTableDao.TABLE_NAME + " where " + BillTableDao.COLUMN_NAME_TIME + " like ?", new String[]{time + "%"});
            Bill bill;
//            System.out.println("【rawQuery】"+ BillTableDao.COLUMN_NAME_TIME + " = " + time + "-=-"+ cursor);
            while (cursor.moveToNext()) {
                bill = new Bill();
                bill.setTime(cursor.getString(cursor.getColumnIndex(BillTableDao.COLUMN_NAME_TIME)));
                bill.setMoney(cursor.getString(cursor.getColumnIndex(BillTableDao.COLUMN_NAME_MONEY)));
                bill.setTypeId(cursor.getString(cursor.getColumnIndex(BillTableDao.COLUMN_NAME_TYPE)));
                bill.setAccountId(cursor.getString(cursor.getColumnIndex(BillTableDao.COLUMN_NAME_ACCOUNT)));
                bill.setRemark(cursor.getString(cursor.getColumnIndex(BillTableDao.COLUMN_NAME_REMARK)));
                bill.setUpload(cursor.getInt(cursor.getColumnIndex(BillTableDao.COLUMN_NAME_UPLOAD)));
//                System.out.println("【3】" + cursor.getString(cursor.getColumnIndex(BillTableDao.COLUMN_NAME_TYPE)) + "---" + cursor.getString(cursor.getColumnIndex(BillTableDao.COLUMN_NAME_MONEY)) );
                bills.add(bill);
            }
            cursor.close();
        }
        return bills;
    }

    // 【根据条件获取账单】  select * from TABLE_NAME where COLUMN_NAME and type='U'【】
    public synchronized List<Bill> getDB(String column,String key) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Bill> bills = new ArrayList<>();
        if (db.isOpen()) {
            Cursor cursor;
            if(key==null){
                key ="";
                cursor = db.rawQuery("select * from " + BillTableDao.TABLE_NAME + " where " + column + " > ? ", new String[]{key + ""});
            }else if(key.contains("-")){
                cursor = db.rawQuery("select * from " + BillTableDao.TABLE_NAME + " where " + column + " like ?", new String[]{key + "%"});
            }else {
                cursor = db.rawQuery("select * from " + BillTableDao.TABLE_NAME + " where " + column + " = ? ", new String[]{key + ""});
            }

            Bill bill;
            while (cursor.moveToNext()) {
                bill = new Bill();
                bill.setTime(cursor.getString(cursor.getColumnIndex(BillTableDao.COLUMN_NAME_TIME)));
                bill.setMoney(cursor.getString(cursor.getColumnIndex(BillTableDao.COLUMN_NAME_MONEY)));
                bill.setTypeId(cursor.getString(cursor.getColumnIndex(BillTableDao.COLUMN_NAME_TYPE)));
                bill.setAccountId(cursor.getString(cursor.getColumnIndex(BillTableDao.COLUMN_NAME_ACCOUNT)));
                bill.setRemark(cursor.getString(cursor.getColumnIndex(BillTableDao.COLUMN_NAME_REMARK)));
                bill.setUpload(cursor.getInt(cursor.getColumnIndex(BillTableDao.COLUMN_NAME_UPLOAD)));
                bills.add(bill);
            }
            cursor.close();
        }
        return bills;
    }



    //获取未更新到服务器的账单
    public synchronized List<Bill> getUnUploadBillList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Bill> bills = new ArrayList<>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + BillTableDao.TABLE_NAME + " where " + BillTableDao.COLUMN_NAME_UPLOAD + " = ?", new String[]{"0"});
            Bill bill;
            while (cursor.moveToNext()) {
                bill = new Bill();
                bill.setTime(cursor.getString(cursor.getColumnIndex(BillTableDao.COLUMN_NAME_TIME)));
                bill.setMoney(cursor.getString(cursor.getColumnIndex(BillTableDao.COLUMN_NAME_MONEY)));
                bill.setTypeId(cursor.getString(cursor.getColumnIndex(BillTableDao.COLUMN_NAME_TYPE)));
                bill.setAccountId(cursor.getString(cursor.getColumnIndex(BillTableDao.COLUMN_NAME_ACCOUNT)));
                bill.setRemark(cursor.getString(cursor.getColumnIndex(BillTableDao.COLUMN_NAME_REMARK)));
                bill.setUpload(cursor.getInt(cursor.getColumnIndex(BillTableDao.COLUMN_NAME_UPLOAD)));
                bills.add(bill);
            }
            cursor.close();
        }
        return bills;
    }

    //更新次数表
    synchronized void updateTime(BillType billType) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues cv = new ContentValues();
            cv.put("type_" + billType.getTypeId(), billType.getTime());
            db.update(TypeFrequencyDao.TABLE_TYPE_FREQUENCY, cv, "time = ?", new String[]{UTime.getCurrentTime() + ""});
        }
    }

    //获取所有记账类型
    public synchronized List<BillType> getBillTypes(int typeOutIn) {
        System.out.println("获取记账类型：" + dbHelper);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<BillType> billTypes = new ArrayList<>();
        int typeNums = 0;
        String[] types = null;
        String outin = null;

        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + TypeFrequencyDao.TABLE_TYPE_FREQUENCY + " where time = ?", new String[]{UTime.getCurrentTime() + ""});
            // rawQuery()方法的第一个参数为select语句；第二个参数为select语句中占位符参数的值，如果select语句没有使用占位符，该参数可以设置为null
            // SELECT 列名称 FROM 表名称 WHERE 列 运算符 值
            // SQL 使用单引号来环绕文本值（大部分数据库系统也接受双引号）。如果是数值，请不要使用引号。
            // 文本值：正确的：SELECT * FROM Persons WHERE FirstName='Bush'
            // 数值：正确的：SELECT * FROM Persons WHERE Year>1965
            // SQL 语句对大小写不敏感
            BillType billType;
            while (cursor.moveToNext()) {
                if( typeOutIn == -1 ){
                    typeNums = Global.typesPayout.length;
                    types = Global.typesPayout;
                    outin = "payout_";
                }else if (typeOutIn == 1){
                    typeNums = Global.typesIncome.length;
                    types = Global.typesIncome;
                    outin = "income_";
//                    System.out.println(typeNums +"【】--++"+ outin);
                }

                for (int i = 0; i < typeNums; i++) {
                    billType = new BillType();
//                    billType.setTime(cursor.getInt(cursor.getColumnIndex("type_" + i))); // getColumnIndex(String columnName) 根据 列名称 获得它的列索引，索引从0开始，没有返回-1
                    int ff,f;
                    ff = cursor.getColumnIndex("type_" + i);
//                    System.out.println("【输出1】" + ff + "=="+ i + "=="+ outin);
                    f = cursor.getInt(ff);
//                    System.out.println("输出：" + f + "2");
                    billType.setTime(f);
//                    System.out.println("输出：" + ff + "---" + f);
                    billType.setTypeId(outin + i);
                    billType.setTypeName(types[i]);
                    billTypes.add(billType);
//                    System.out.println("输出类型" + types[i] + i+"cursor数量");
                }

            }
            cursor.close();
        }
        return billTypes;
    }
}
