package me.wizos.billorcs;

import android.graphics.Color;

import me.wizos.billorcs.bean.Bill;
import me.wizos.billorcs.utils.UTime;

/**
 * 这里保存一些全局的参数
 * Created by xdsjs on 2015/10/14.
 */
public class Global {
    /**
     * 记账类型的数组
     */
    public static boolean isBilled = false;
    public static String recordTime;
    public static boolean canSdlvMenu = true;
    public static boolean canSdlvPull = true;
    public static String chooseDate = UTime.getDate(0);;
    public static String isEditing = "";
    public static Bill bill; // 其实直接用这个 bill是否存在 来判断 是否在编辑模式下 也可以
    public static String dial_OutIn = "payout_";
    public static final String[] typesPayout = new String[]{
            "杂项","三餐","零食","水果",
            "服饰","化妆","理发",
            "话费","网络","数码","爱好",
            "交通","房租贷","水电煤气","生活用品","医药",
            "运动","旅游","娱乐","学习","投资","宠物",
            "红包礼品","丢钱","还钱","平准"
    };
    public static final String[] typesIncome = new String[]{
            "工资","奖金","生活费","零花钱","外快兼职",
            "报销退款","投资收益","捡钱借钱","收红包","平准"
    };
    public static final String[] account = { "现金", "建行", "工行","支付宝", "美元" };
    public static final int[] colors = new int[]{
            Color.rgb(255, 136, 34), Color.rgb(234, 103, 68), Color.rgb(118, 136, 242),
            Color.rgb(247, 186, 91), Color.rgb(245, 113, 110), Color.rgb(102, 204, 238),
            Color.rgb(44, 216, 101), Color.rgb(255, 147, 54), Color.rgb(255, 183, 0),
            Color.rgb(255, 103, 185), Color.rgb(86, 178, 255), Color.rgb(102, 204, 238),
            Color.rgb(252, 88, 48), Color.rgb(72, 217, 207), Color.rgb(146, 141, 255),
            Color.rgb(110, 207, 239), Color.rgb(255, 105, 105), Color.rgb(255, 136, 34),
            Color.rgb(63, 171, 233), Color.rgb(88, 200, 77)
    };
    /**
     * 要缓存的设置信息
     */
    public static final String SHARE_SETTING_UPLOAD = "setting_upload";//设置是否上传
    public static final String SHARE_SETTING_BILL_PWD = "setting_bill_pwd";//设置是否需要设置安全密码
    public static final String SHARE_SETTING_BUDGET = "setting_budget";//设置预算
    /**
     * 要缓存的个人信息
     */
    public static final String SHARE_PERSONAL_AVATAR = "personal_avatar";//头像
    public static final String SHARE_PERSONAL_ACCOUNT = "personal_account";//个人账号
    public static final String SHARE_PERSONAL_PWD = "personal_pwd";//个人密码
    public static final String SHARE_PERSONAL_BILL_PWD = "personal_bill_pwd";//个人安全密码
    public static final String SHARE_PERSONAL_TOTAL_IN = "personal_total_in";//个人历史总收入
    public static final String SHARE_PERSONAL_TOTAL_OUT = "personal_total_out";//个人历史总支出
    public static final String SHARE_PERSINAL_TOKEN = "personal_token";//token

    public static final String SHARE_PERSONAL_AUTO_LOGIN = "personal_auto_login";//标记是否自动登录


    public static String getIDName(String id){
        String name = null;
        String[] num = id.split("_");
        String num0 = num[0];
        int num1 = Integer.valueOf(num[1]);
        if (num0.equals("account")){
            name = Global.account[num1];
        }else if(num0.equals("payout")){
            name = Global.typesPayout[num1];
        }else if(num0.equals("income")){
            name = Global.typesIncome[num1];
        }
        return name;
    }

//    public static void log(String msg){
//    }

}
