package me.wizos.billing.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by OS on 2016/1/7.
 * 改自 DbOpenHelper
 */
public class DBOpener extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;
    private static DBOpener instance;
    //
    private DBOpener dbOpenHelper;
    private static Context context;
    //

    // 创建账单表
    // DAO(Data Access Object)是一个数据访问接口
    private static final String BILL_TABLE_CREATE = "CREATE TABLE "
            + BillTableDao.TABLE_NAME + " ("
            + BillTableDao.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BillTableDao.COLUMN_NAME_TIME + " TEXT, "
            + BillTableDao.COLUMN_NAME_MONEY + " TEXT, "
            + BillTableDao.COLUMN_NAME_TYPE + " TEXT, "
            + BillTableDao.COLUMN_NAME_ACCOUNT + " TEXT, "
            + BillTableDao.COLUMN_NAME_REMARK + " TEXT, "
            + BillTableDao.COLUMN_NAME_UPLOAD + " INTEGER); ";
    //创建次数表
    /*
    CREATE TABLE 表名称
    (
    列名称1 数据类型,
    列名称2 数据类型,
    列名称3 数据类型,
    )
    varchar(size) 容纳可变长度的字符串（可容纳字母、数字以及特殊的字符）。括号中规定字符串的最大长度。
    integer(size) 仅容纳整数。在括号内规定数字的最大位数。
    DEFAULT 约束用于向列中插入默认值。如果没有规定其他的值，那么会将默认值添加到所有的新记录。
    */
    private static final String TIME_TABLE_CREATE = "CREATE TABLE " + TypeFrequencyDao.TABLE_TYPE_FREQUENCY + " (" +
            "time varchar(10) NOT NULL," +
            "type_0 integer default 10," +
            "type_1 integer default 10," +
            "type_2 integer default 10," +
            "type_3 integer default 10," +
            "type_4 integer default 10," +
            "type_5 integer default 10," +
            "type_6 integer default 10," +
            "type_7 integer default 10," +
            "type_8 integer default 10," +
            "type_9 integer default 10," +
            "type_10 integer default 10," +
            "type_11 integer default 10," +
            "type_12 integer default 10," +
            "type_13 integer default 10," +
            "type_14 integer default 10," +
            "type_15 integer default 10," +
            "type_16 integer default 10," +
            "type_17 integer default 10," +
            "type_18 integer default 10," +
            "type_19 integer default 10," +
            "type_20 integer default 10," +
            "type_21 integer default 10," +
            "type_22 integer default 10," +
            "type_23 integer default 10," +
            "type_24 integer default 10," +
            "type_25 integer default 10);";



    private DBOpener(Context context){
        super(context, getUserDatabaseName(), null, DATABASE_VERSION);
    }
    public static DBOpener getInstance(Context context) {
        if (instance == null) {
            instance = new DBOpener(context);
            System.out.println("这个 DBOpener 实例为空" + instance);
        }
        return instance;
    }


    private static String getUserDatabaseName() {
        return "_bill.db";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BILL_TABLE_CREATE);
        db.execSQL(TIME_TABLE_CREATE);
        db.execSQL("insert into frequency (time) values(\"0\")");
        db.execSQL("insert into frequency (time) values(\"1\")");
        db.execSQL("insert into frequency (time) values(\"2\")");
        db.execSQL("insert into frequency (time) values(\"3\")");
        db.execSQL("insert into frequency (time) values(\"4\")");
        db.execSQL("insert into frequency (time) values(\"5\")");
        db.execSQL("insert into frequency (time) values(\"6\")");
        db.execSQL("insert into frequency (time) values(\"7\")");
        db.execSQL("insert into frequency (time) values(\"8\")");
        db.execSQL("insert into frequency (time) values(\"9\")");
        db.execSQL("insert into frequency (time) values(\"10\")");
        db.execSQL("insert into frequency (time) values(\"11\")");
        db.execSQL("insert into frequency (time) values(\"12\")");
        // insert into person(name, age) values('测试数据', 4)
        // 往 frequency 表中添加进一条 列为 time 值为 X 的记录
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
        }
        if (oldVersion < 3) {
        }
        if (oldVersion < 4) {
        }
    }

    public void closeDB() {
        if (instance != null) {
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance = null;
        }
    }
}
