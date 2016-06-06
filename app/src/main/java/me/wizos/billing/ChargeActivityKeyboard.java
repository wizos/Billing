package me.wizos.billing;


import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import me.wizos.billing.bean.Bill;
import me.wizos.billing.bean.BillType;
import me.wizos.billing.db.BillTableDao;
import me.wizos.billing.db.DBManager;
import me.wizos.billing.utils.UTime;
import me.wizos.billing.utils.UToast;

/**
 * Created by Wizos on 2015/12/31.
 */
public class ChargeActivityKeyboard extends AppCompatActivity{

//    下方是为了实现 keyboard 中 与 其 布局文件中 android:onClick()  关联。因为其关联方法必须在关联的Activity上
    protected String recordTime = "";
    protected String moneySum = "";
    protected StringBuffer stringBuffer = new StringBuffer();
    protected String chooseType = "payout_0";
    protected String chooseAccount = "account_0";
    protected String account = "现金";
    protected int accountPosition;
    protected Boolean newString = false;
    protected Boolean canNewPoint = true;
    protected int floatCount = 0; // 小数点后几位
    protected boolean haveCharge = false;

    protected TextView moneyView;
    protected EditText remarkView;
    protected Spinner spinner;
    protected List<BillType> billTypesPayout;
    protected List<BillType> billTypesIncome;
    protected ViewPager viewPager;
    protected View background,keyboard;
    public Bill billing = new Bill();

    public void binding(){
        billTypesPayout = DBManager.getInstance().getBillTypes(-1);
        billTypesIncome = DBManager.getInstance().getBillTypes(1);
        moneyView = (TextView)findViewById(R.id.moneyShow_text);
        keyboard = findViewById(R.id.keyboard);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        background = findViewById(R.id.background);
//        recordTime = Global.recordTime;
    }


    /**
     * 【设计思路】每次有：输入/删除/计算时，要1根据已有数据检查输入/已有数据是否合格，2变动已有数据，3变动UI
     *
     */
    protected float[] nums;
    protected boolean checkMoneySum(){
        // 在无输入的情况下，检查已有数据
        if (moneySum.equals("")) {
            UToast.showToast("请输入价格");
            chargeKeyView(0);
            return false;}
            String[] nums = moneySum.split("\\+");
            if (nums.length > 1){
                chargeKeyView(2);
            }else {chargeKeyView(1);}
            nums = nums[nums.length-1].split("\\.");
            if (nums.length > 1){
                floatCount = nums[1].length();
                canNewPoint = false;
            }
        System.out.println("【121】" + floatCount + canNewPoint + "==="+  moneySum + stringBuffer );
        return true;
    }


    protected void decimalNums(String num){
        String[] nums = num.split("\\.");
        floatCount = nums[1].length();
    }
    protected void reviseMoneySum(){
        StringBuffer string = new StringBuffer(moneySum);
        while (string.charAt(0) == '0'){
            string.deleteCharAt(0);
        }
        if( string.charAt(string.length() - 1) == '.' || string.charAt(string.length() - 1) == '+'){
            string.deleteCharAt(string.length()-1);
        }
        if (string.charAt(string.length()-1) == '0' && string.charAt(string.length()-2) == '.'){
            string.deleteCharAt(string.length()-1);
            string.deleteCharAt(string.length()-1);
        }
        moneySum = string.toString();
        if( moneySum.contains("+") ){
            string = new StringBuffer(moneySum);
            moneySum = calculate(string);
        }
        moneySum = string.toString();
        System.out.println("【122】" + floatCount + canNewPoint + "==="+  moneySum + stringBuffer );
    }
    protected int keyViewState = 0;
    protected void chargeKeyView(int i){
        if(i == keyViewState){return;}
        Button equal = (Button)findViewById(R.id.equal);
        Button submit = (Button)findViewById(R.id.submit);
        Button close = (Button)findViewById(R.id.close);
        switch (i){
//            case -1:
//                if(!haveCharge && !moneySum.equals("")){
//                    chargeKeyView(1);
//                }else if( haveCharge && moneySum.equals("")){chargeKeyView(0);}
//                break;
            case 0:
                submit.setVisibility(View.GONE);
                equal.setVisibility(View.GONE);
                close.setVisibility(View.VISIBLE);
                keyViewState = 0;
                break;
            case 1:
                equal.setVisibility(View.GONE);
                close.setVisibility(View.GONE);
                submit.setVisibility(View.VISIBLE);
                keyViewState = 1;
                break;
            case 2:
                submit.setVisibility(View.GONE);
                close.setVisibility(View.GONE);
                equal.setVisibility(View.VISIBLE);
                keyViewState = 2;
                break;
        }
    }
//    public void changeMoneyView(String s){
//        moneyView.setText(s);
//    }
//    public void onPlusKeyClicked(View view){
//    }
//    public void onPointKeyClicked(View view){
//    }


    public void onNumKeyClicked(View view) { // 因为输入的入口只有这一个，就把就把检查输入的放到这里
        String tag = view.getTag().toString();
//        if(!checkTagData(tag)){return;}
        // 在有输入的情况下，检查已有数据和输入数据
        if (stringBuffer.length() == 0 && (tag.equals(".") || tag.equals("+"))) { return; }
        // 判断处理非首次输入时，输入 .或+ 的情况
        if(stringBuffer.length() != 0){
            char lastString = stringBuffer.charAt(stringBuffer.length() - 1);
            if ((lastString == '+' || lastString == '.') && (tag.equals("+") || tag.equals("."))) { return;}
            if (!canNewPoint){
                if ( (floatCount >= 2 && (!tag.equals("+")) || tag.equals(".")) ){ return;}
                floatCount = floatCount + 1;
            }
        }
        if (tag.equals("+")) {
            canNewPoint = true;
            floatCount = 0;
            chargeKeyView(2);
        } else if (tag.equals(".") && canNewPoint) { canNewPoint = false;}
        if(!moneySum.contains("+")){chargeKeyView(1);}
        if (newString) {
            stringBuffer = new StringBuffer();
            newString = false;
        }
        System.out.println("【121】" + floatCount + canNewPoint + "==="+  moneySum + stringBuffer );
        stringBuffer.append(tag);
        moneySum = stringBuffer.toString();
        moneyView.setText(moneySum);
    }
    public void onDeleteKeyClicked(View view) {
        if (stringBuffer.length() == 0){System.out.println("【stringBuffer为空】"); return;}
        newString = false;
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        moneySum = stringBuffer.toString();
        checkMoneySum();
//        chargeKeyView(-1);
        moneyView.setText(moneySum);
        System.out.println("修改" + moneySum);
    }
    public void onClearKeyClicked(View view) {
        stringBuffer = new StringBuffer();
        moneySum = "";
        newString = false;
        canNewPoint = true;
        chargeKeyView(0);
        moneyView.setText(moneySum);
    }
    public void onEqualKeyClicked(View view){
        moneySum = calculate(stringBuffer);

        moneyView.setText(moneySum);
        checkMoneySum();
        newString = true; // 为了在：计算过后的金额，再输入新的数字时，可以覆盖。而不是在后面添加
    }
    public void onSubmitKeyClicked(View view) {
        if(!checkMoneySum()){return;}
        reviseMoneySum();
        System.out.println("修改"+  moneySum);
        remarkView = (EditText)findViewById(R.id.remark);
        String remark = remarkView.getText().toString();
        if (remark.equals(getString(R.string.remark))){remark="";}
        BillTableDao billTableDao = new BillTableDao();
        if(!Global.isEditing.equals("")){
            billTableDao.editBill(new Bill( getRecordTime(), moneySum, chooseType + "", chooseAccount, remark, 1));
            UToast.showToast("修改成功");
            Global.isEditing = "";
            Global.bill = null;
        }else {
            billTableDao.addBill(new Bill( getRecordTime(), moneySum, chooseType + "", chooseAccount, remark, 0));
            UToast.showToast("记账成功");
        }
        System.out.println("【记账成功】" +  getRecordTime() + moneySum + chooseType + ""+ chooseAccount +remark);
        Global.isBilled = true;
        ActivityCollector.switchActivity(this, "MainActivity", null);
        System.out.println("【轻触成功】");
    }

    private String calculate(StringBuffer stringBuffer) {
        if (!stringBuffer.toString().contains("+")){
            return stringBuffer.toString();
        }
        String num = new String(stringBuffer);
        String[] nums = num.split("\\+");
        float total = 0f;
        for (int i = 0; i < nums.length; i++) {
            total += Float.valueOf(nums[i]);
            System.out.println("【】"+stringBuffer+"【】"+stringBuffer+"【】"+moneySum);
            System.out.println("【】"+i+"【】"+nums[i]+"【】"+total);
        }
        return String.valueOf(total);
    }


    public void onCloseKeyClicked(View view){
        ActivityCollector.switchActivity(this, "MainActivity", null);
    }
    public String getChooseType(){
        return chooseType;
    }
    public String getChooseAccount(){
        return chooseAccount;
    }
    protected String getRecordTime(){
        if (recordTime.equals("")){
            recordTime = Global.chooseDate + " " + UTime.getNow() ;
            System.out.println("传递 recordTime 失败");
        } else if( Global.chooseDate.contains(" ")){
            Global.chooseDate = Global.chooseDate  + " " + UTime.getNow() ;
        }
//        if( Global.chooseDate.equals("") ){Global.chooseDate = UTime.getDate(0)  + " " + UTime.getNow() ;; System.out.println("传递 recordTime 失败");}

        return recordTime;
    }

}
