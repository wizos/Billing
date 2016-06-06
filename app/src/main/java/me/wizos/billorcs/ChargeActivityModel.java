//package me.wizos.scrollid;
//
//
//import android.support.v4.view.ViewPager;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import java.util.List;
//
//import me.wizos.billorcs.bean.Bill;
//import me.wizos.billorcs.bean.BillType;
//import me.wizos.billorcs.db.BillTableDao;
//import me.wizos.billorcs.db.DBManager;
//import me.wizos.billorcs.utils.UTime;
//import me.wizos.billorcs.utils.UToast;
//
///**
// * Created by Wizos on 2015/12/31.
// */
//public class ChargeActivityModel extends AppCompatActivity{
//
////    下方是为了实现 keyboard 中 与 其 布局文件中 android:onClick()  关联。因为其关联方法必须在关联的Activity上
//    protected StringBuffer stringBuffer = new StringBuffer();
//    protected String moneySum = "";
//    protected String recordTime;
//    protected TextView MoneyView;
//    protected List<BillType> billTypesPayout;
//    protected List<BillType> billTypesIncome;
//    protected ViewPager viewPager;
//    protected View background,keyboard;
//    protected EditText editText;
//    public Bill billing = new Bill();
////    private Intent intent;
//
//    public void binding(){
//        billTypesPayout = DBManager.getInstance().getBillTypes(-1);
//        billTypesIncome = DBManager.getInstance().getBillTypes(1);
//        MoneyView = (TextView)findViewById(R.id.moneyShow_text);
//        keyboard = findViewById(R.id.keyboard);
//        viewPager = (ViewPager) findViewById(R.id.viewPager);
//        background = findViewById(R.id.background);
//        recordTime = UTime.getCurrentDate();
//    }
//
//
//
//
//    public void changeMoneyView(String s){
//        MoneyView.setText(s);
////        horizontalScrollView.fullScroll(horizontalScrollView.FOCUS_RIGHT);
//    }
//    private String calculate(StringBuffer stringBuffer) {
//        if (!stringBuffer.toString().contains("+")){
//            return stringBuffer.toString();
//        }
//        checkKey();
//        String num = new String(stringBuffer);
//        String[] nums = num.split("\\+");
//        float total = 0f;
//        for (int i = 0; i < nums.length; i++) {
//            total += Float.valueOf(nums[i]);
//        }
//        return String.valueOf(total);
//    }
//
//
//    private Boolean newString = false;
//    private Boolean newPoint = true;
//
//    public void onNumKeyClicked(View view) {
//        String tag = view.getTag().toString();
//        // 判断处理首次输入 为 0，.，+ 的情况
//        if ( (tag.equals(".") || tag.equals("+")) && stringBuffer.length() == 0){
//            return;}
//        // 判断处理非首次输入时，输入 .或+ 的情况
//        if (stringBuffer.length() != 0) {
//            char lastString = stringBuffer.charAt(stringBuffer.length() - 1);
//            if (stringBuffer.length() == 0 && lastString == '0'&& !tag.equals(".") ){
//                newString = false;
//            }
//            if( (lastString == '+' || lastString == '.') && ( tag.equals("+") || tag.equals(".")) ){
//                return;
//            } else if (tag.equals("+")){
//                newString = false;
//                newPoint = true;
//                chargeKey(1);
//            } else if (tag.equals(".")){
//                if (newPoint){
//                    newString = false;
//                    newPoint = false;
//                }else {
//                    return;
//                }
//            }
//        }
//        if(newString){
//            stringBuffer = new StringBuffer();
//            newString = false;
//        }
//        stringBuffer.append(tag);
//        moneySum = stringBuffer.toString();
//        changeMoneyView(moneySum);
//
//        // 判断已有的金额后面是不是有 + 号
////        if (stringBuffer.length() != 0 && tag.equals("+")) {
////            if (stringBuffer.charAt(stringBuffer.length() - 1) == '+') {
////                return;
////            } else {
////                newString = false;
////                newPoint = true;
////                chargeKey(1);
////            }
////        }
////        if (stringBuffer.length() != 0 && tag.equals(".")) {
////            if (stringBuffer.charAt(stringBuffer.length() - 1) == '.' || !newPoint) {
////                return;
////            }else {newString = false;newPoint = false;}
////        }
////        if(newString){
////            stringBuffer = new StringBuffer();
////            newString = false;
////        }
////        stringBuffer.append(tag);
////        moneySum = stringBuffer.toString();
////        changeMoneyView(moneySum);
//    }
////    private void upDateString(String tag){
////        stringBuffer.append(tag);
////        moneySum = stringBuffer.toString();
////        changeMoneyView(moneySum);
////    }
//
//
//    public void onDeleteKeyClicked(View view) {
//        if(newString){newString = false;}
//        if (stringBuffer.length() == 0)
//            return;
//        if (stringBuffer.length() != 0) {
//            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
//            moneySum = stringBuffer.toString();
//        }
//        checkKey();
//        changeMoneyView(moneySum);
//    }
//
//    public void checkPoint(){
//        if (moneySum.contains(".")){
//            newPoint = false;
//        }
//    }
//    public void checkKey(){
//        if(stringBuffer.toString().contains("+")){
//            chargeKey(1);
//        }else{
//            chargeKey(0);
//        }
//    }
//    private void chargeKey(int i){
//        Button calculate = (Button)findViewById(R.id.calculate);
//        Button submit = (Button)findViewById(R.id.submit);
//        switch (i){
//            case 0:
//                calculate.setVisibility(View.GONE);
//                submit.setVisibility(View.VISIBLE);
//                break;
//            case 1:
//                calculate.setVisibility(View.VISIBLE);
//                submit.setVisibility(View.GONE);
//                break;
//            case 2:
//
//                break;
//        }
//    }
//
//
//    public void onClearKeyClicked(View view) {
//        stringBuffer = new StringBuffer();
//        moneySum = "";
//        changeMoneyView(stringBuffer.toString());
//    }
//
//    public void onEqualKeyClicked(View view){
//        moneySum = calculate(stringBuffer);
//        stringBuffer = new StringBuffer();
//        stringBuffer.append(moneySum);
//        // 去除 .0
//        if (stringBuffer.charAt(stringBuffer.length()-1) == '0'){
//            if(stringBuffer.charAt(stringBuffer.length()-2) == '.'){
//                stringBuffer.deleteCharAt(stringBuffer.length()-1);
//                stringBuffer.deleteCharAt(stringBuffer.length()-1);
//            }
//        }
//        moneySum = stringBuffer.toString();
//        changeMoneyView(moneySum);
//        checkKey();
//        checkPoint();
//        newString = true; // 为了在：计算过后的金额，再输入新的数字时，可以覆盖。而不是在后面添加
//    }
//
//    public void onSubmitKeyClicked(View view) {
//        if(!checkMoneySum()){return;}
//        EditText editText = (EditText)findViewById(R.id.editText);
//        String remark = editText.getText().toString();
//        if (remark == "备注一下"){remark="";}
//        BillTableDao billTableDao = new BillTableDao();
//        if(!Global.isEditing.equals("")){
//            billTableDao.editBill(new Bill(getChooseTypeId() + "", moneySum, remark, recordTime, 1));
//            UToast.showToast("修改成功");
//            Global.isEditing = "";
//            Global.bill = null;
//        }else {
//            billTableDao.addBill(new Bill(getChooseTypeId() + "", moneySum, remark, recordTime, 0));
//            UToast.showToast("记账成功");
//        }
////        System.out.println("【记账成功】" + getChooseTypeId() + "====" + UTime.getToday() + "====" + moneySum);
//        Global.isBilled = true;
//        ActivityCollector.switchActivity(this, "MainActivity", null);
//        System.out.println("【轻触成功】");
//    }
//
//
//
//    private Boolean checkMoneySum(){
//        if (moneySum.equals("")) {
//            UToast.showToast("请输入价格");
//            return false;
//        }
//        String[] nums = moneySum.split("\\.");
//        if (nums.length > 2 ){
//            UToast.showToast("你怎么输那么多小数点啊，输错了吧");
//            return false;
//        }else if ( nums.length == 1 && moneySum.indexOf("0") == 0 ){
//            stringBuffer = new StringBuffer();
//            stringBuffer.append(moneySum);
//            stringBuffer.deleteCharAt(0);
//            moneySum = stringBuffer.toString();
//        }
//        if( moneySum.indexOf(".") == moneySum.length()-1 ){
//            stringBuffer = new StringBuffer();
//            stringBuffer.append(moneySum);
//            stringBuffer.deleteCharAt(moneySum.length()-1);
//            moneySum = stringBuffer.toString();
//        }
//        return true;
//    }
//
//    protected String chooseTypeId = "payout_0";
//
//
//    public String getChooseTypeId(){
//        return chooseTypeId;
//    }
//}
