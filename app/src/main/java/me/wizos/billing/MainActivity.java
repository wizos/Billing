package me.wizos.billing;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;
import me.wizos.billing.adapter.ListAdapter;
import me.wizos.billing.bean.Bill;
import me.wizos.billing.bean.KeyValue;
import me.wizos.billing.db.BillTableDao;
import me.wizos.billing.db.DBManager;
import me.wizos.billing.pickerview.TimePickerView;
import me.wizos.billing.sdlv.Menu;
import me.wizos.billing.sdlv.MenuItem;
import me.wizos.billing.sdlv.SlideAndDragListView;
import me.wizos.billing.utils.UDensity;
import me.wizos.billing.utils.UTime;
import me.wizos.billing.utils.UToast;


public class MainActivity extends AppCompatActivity  implements SlideAndDragListView.OnListItemLongClickListener,
        SlideAndDragListView.OnDragListener, SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnListItemClickListener, SlideAndDragListView.OnMenuItemClickListener,
        SlideAndDragListView.OnItemDeleteListener,OnItemClickListener {


    protected String s_list = BillTableDao.COLUMN_NAME_TIME;
//    protected String s_time = UTime.getDate(0);
    protected String s_outin = "payout";

    protected String today = UTime.getDate(0);
    protected Button timeTitle,moneyOuntin,moneyCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_bill);// setContentView(R.layout.activity_main);
        Context context = ActivityCollector.getContext();
        DBManager.getInstance().onInit(context);
        // 初始化组件（这 3 个顺序不要改）；感悟：在 onCreate 里初始化控件，在 onResume 里初始化数据。这样在重新进入界面后，方便重新加载新的数据
        init();
        initMenu(); // 初始化 listView 项的左右滑动菜单
        initPickerView();
        initBillListListener();
        billlist();
    }


    @Override
    public void onResume(){
        super.onResume();
        if(haveStop) {
            refreshList();
            Global.isEditing = "";
            haveStop = false;
            System.out.println("【onResume：haveStop】");
        }
        System.out.println("【onResume】");
    }
    protected boolean haveStop = false;
    @Override
    public void onStop(){
        super.onStop();
        haveStop = true;
    }

    protected void init(){
        timeTitle = (Button)findViewById(R.id.date_time);
        moneyOuntin = (Button)findViewById(R.id.money_ountin);
        moneyCount = (Button)findViewById(R.id.money_count);
    }

    private LineChartView lineChart;
    protected void initLineChartView(){
        lineChart = (LineChartView) findViewById(R.id.linechart);
        if( lineChart.getVisibility() == View.INVISIBLE){ lineChart.setVisibility(View.VISIBLE);}
        LineChartData lineData;
        List<Line> lines = new ArrayList<Line>();
        List<PointValue> values = new ArrayList<PointValue>();
        int num = 6;
        for(int i=0; i<=num ; ++i){
            values.add(new PointValue(i, count(getDB(BillTableDao.COLUMN_NAME_TIME, UTime.getDate(i - num)), s_outin)));
        }
        Line line = new Line(values);
        line.setColor(Color.WHITE);
        line.setShape(ValueShape.CIRCLE); // 形状
        line.setPointRadius(UDensity.dpToPx(2));// line.setStrokeWidth(UDensity.dpToPx(2)); //设置折线宽度
        line.setHasLabels(true); // 设置数目点的标签
        line.setHasLabelsOnlyForSelected(false);
        line.setHasLines(true);
        line.setHasPoints(true);
        line.setPointColor(UDensity.getColor(R.color.bluewhite));
        lines.add(line);

        lineData = new LineChartData(lines);
        lineData.setBaseValue(Float.NEGATIVE_INFINITY);
        lineChart.setLineChartData(lineData);
        System.out.println("【4】initLineChartView" +  lineChart.getVisibility());
    }



    TimePickerView pvTime;
    protected void initPickerView(){
        //时间选择器
        pvTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        // 控制时间范围
        Calendar calendar = Calendar.getInstance();
        pvTime.setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR));
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                Global.chooseDate = UTime.getFormatDate(date);
                loadBilllist(Global.chooseDate);
            }
        });
        //弹出时间选择器
        timeTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !s_list .equals( BillTableDao.COLUMN_NAME_TIME )){return;}
                pvTime.show();
            }
        });
    }

    // 初始化（每一个列表项左右滑动时出现的）菜单
    protected Menu mMenu;
    public void initMenu() {
        mMenu = new Menu(new ColorDrawable(Color.WHITE), true, 0);//第2个参数表示滑动item是否能滑的过量(true表示过量，就像Gif中显示的那样；false表示不过量，就像QQ中的那样)
        mMenu.addItem(new MenuItem.Builder().setWidth(UDensity.get2Px(this, R.dimen.slv_menu_left_width))
                .setBackground(new ColorDrawable(getResources().getColor(R.color.white1)))
//                .setIcon(getResources().getDrawable(R.drawable.ic_launcher)) // 插入图片
                .setText("删除")
                .setTextColor(UDensity.getColor(R.color.crimson))
                .setTextSize((int) getResources().getDimension(R.dimen.txt_size))
                .build());
        mMenu.addItem(new MenuItem.Builder().setWidth(UDensity.get2Px(this, R.dimen.slv_menu_right_width))
                .setBackground(new ColorDrawable(getResources().getColor(R.color.white1)))
                .setDirection(MenuItem.DIRECTION_RIGHT) // 设置是左或右
                .setTextColor(R.color.white)
                .setText("编辑")
                .setTextSize(UDensity.getDimen(this, R.dimen.slv_menu_txt))
                .build());
    }

    protected SlideAndDragListView billList;
    protected BillWrap billWrap;
    public void initBillListListener() {
        billList = (SlideAndDragListView)findViewById(R.id.billlist);
        billList.setMenu(mMenu);
        billList.addHeader(this);
        billList.setOnListItemClickListener(this);
        billList.setOnSlideListener(this);
        billList.setOnMenuItemClickListener(this);
        billList.setOnItemDeleteListener(this);
        billWrap = (BillWrap)findViewById(R.id.billwrap);
        billWrap.init();
//        billList.getHeaderViewsCount();
//        System.out.println("【bills2】" + bills.size() + bills);
//        billList.setOnListItemLongClickListener(this);
//        billList.setOnDragListener(this, mAppList);
    }

//    public ViewPager billViewPager;
//    protected View activity_BillList;
//    protected ExpressionPagerAdapter billViewPagerAdapter;
////    protected void initBillPager(){
////        billViewPager = (ViewPager)findViewById(R.id.billPager);
////        billViewPager.addOnPageChangeListener(getOnPagerChangeListener());// 设置页面滑动监听
////        addNewBillListPageToBillListArray(); // 创建了 viewPager 的一个页面
////        billViewPagerAdapter = new ExpressionPagerAdapter(billListArray);
////        billViewPager.setAdapter(billViewPagerAdapter);
//////        billViewPager.setCurrentItem(6, false);
////    }
//    protected List<View> billListArray;
////    private void addNewBillListPageToBillListArray(){
////        activity_BillList = View.inflate(this, R.layout.activity_billlist, null);
////        billList = (SlideAndDragListView)activity_BillList.findViewById(R.id.billlist);
////        initBillListListener();
////        if (billListArray == null){
////            billListArray = new ArrayList<>();
////            System.out.println("【是的，billListArray为空】");}
////        System.out.println("【是1】" + "="+"=" + activity_BillList);
////        billListArray.add(activity_BillList);
////    }

    protected float count(List<Bill> bills , String ontinType){
        float money = 0f;
        if (bills.size()==0){ System.out.println("【bills】"+ money); return money;}
        for(Bill bill:bills){
            if(bill.getTypeId().contains(ontinType)){
                money = Float.valueOf(bill.getMoney()) + money ;
            }
        }
        return money;
    }
    protected String getMoneyCount(List<Bill> bills,String type){
        return String.valueOf(count(bills,type));
    }

    protected int n=0;
    public void money_ountin_Clicked(View view){
        money_count_Clicked(view);
    }
    public void money_count_Clicked(View view){
//        if( s_outin.equals("all") ){ return;}
        if( s_outin.equals("payout") ){
            s_outin = "income";
            moneyCount.setTextColor(getResources().getColor(R.color.rise_number_text_color_red));
        }else {
            s_outin = "payout";
            moneyCount.setTextColor(getResources().getColor(R.color.white));
        }

        if( s_list.equals(BillTableDao.COLUMN_NAME_TIME)){ billlist();}
        else if ( s_list.equals(BillTableDao.COLUMN_NAME_ACCOUNT) ){
            n = n+1;
            if( n % 3==0){
                s_outin = "all";
                moneyCount.setTextColor(getResources().getColor(R.color.white));
            }
            loadAccountlist(null);
        }
        else if ( s_list.equals(BillTableDao.COLUMN_NAME_TYPE) ){ loadTypelist(null); }
    }

    protected List<KeyValue> l_TypeList= null;
    protected void loadTypelist(String xxx){
        billList = (SlideAndDragListView)findViewById(R.id.billlist);
        if( lineChart.getVisibility() == View.VISIBLE){lineChart.setVisibility(View.INVISIBLE);}
        s_list = BillTableDao.COLUMN_NAME_TYPE;
        List<Bill> bills = getDB(BillTableDao.COLUMN_NAME_TYPE, xxx);
        int footerCount = billList.getFooterViewsCount();
        List<KeyValue> l_KeyValue = new ArrayList<>();
        switch (s_outin){
            case "all":
                l_KeyValue = getKeyvalueFromBills(bills, "Type", "all");
                break;
            case "payout":
                l_KeyValue = getKeyvalueFromBills(bills, "Type", "payout");
                break;
            case "income":
                l_KeyValue = getKeyvalueFromBills(bills, "Type", "income");
                break;
        }
        if( l_KeyValue != null){
            l_TypeList = l_KeyValue;
            if( footerCount != 0 ){
                billList.removeFooter(); }
        }else if( footerCount == 0 ){
            billList.addFooter(this);
        }
        billList.setAdapter(new ListAdapter(this, 1, l_KeyValue, 0));
        moneyOuntin.setText(R.string.payout);
        moneyCount.setText(getCount(l_KeyValue));
        timeTitle.setText(R.string.title_type_count);
        closeBackTodayIcon();
        Global.canSdlvMenu = false; // 这里该不该有一个限制左右滑动的设置呢
        Global.canSdlvPull = false;
        System.out.println("【】"+l_KeyValue + s_outin);
    }


//    protected void accountList(String xxx){
//        billList = (SlideAndDragListView)findViewById(R.id.billlist);
//        s_list = BillTableDao.COLUMN_NAME_ACCOUNT;
//        Global.canSdlvMenu = false;
//        if( lineChart.getVisibility() == View.VISIBLE){lineChart.setVisibility(View.INVISIBLE);}
//        loadAccountlist(xxx);
//        chargeTitle(Global.chooseDate);
//    }



    protected List<KeyValue> l_AccountList= null;
    protected void loadAccountlist(String xxx){
        billList = (SlideAndDragListView)findViewById(R.id.billlist);
        if( lineChart.getVisibility() == View.VISIBLE){lineChart.setVisibility(View.INVISIBLE);}
        s_list = BillTableDao.COLUMN_NAME_ACCOUNT;
        List<Bill> bills = getDB(BillTableDao.COLUMN_NAME_ACCOUNT, xxx);
        int footerCount = billList.getFooterViewsCount();
        List<KeyValue> l_KeyValue = new ArrayList<>();
        switch (s_outin){
            case "all":
                l_KeyValue = getKeyvalueFromBills(bills, "Account", "all");
                break;
            case "payout":
                l_KeyValue = getKeyvalueFromBills(bills, "Account", "payout");
                break;
            case "income":
                l_KeyValue = getKeyvalueFromBills(bills, "Account", "income");
                break;
        }
        if( l_KeyValue != null){
            l_AccountList = l_KeyValue;
            if( footerCount != 0 ){
                billList.removeFooter(); }
        }else if( footerCount == 0 ){
            billList.addFooter(this);
        }
        billList.setAdapter(new ListAdapter(this, 1, l_KeyValue, 0));
        moneyOuntin.setText(R.string.balace);
        moneyCount.setText(getCount(l_KeyValue));
        timeTitle.setText(R.string.title_account_count);
        closeBackTodayIcon();
        Global.canSdlvMenu = false; // 这里该不该有一个限制左右滑动的设置呢
        Global.canSdlvPull = false;
        System.out.println("【】"+l_KeyValue + s_outin);
    }
//
//    protected void loadKeyValuelist(String column,String xxx){
//        List<Bill> bills = getDB(column, xxx);
//        int footerCount = billList.getFooterViewsCount();
//
//        List<KeyValue> l_KeyValue = new ArrayList<>();
//        switch (s_outin){
//            case "all":
//                l_KeyValue = getAccountsFromBills(bills);
//                System.out.println("【】"+l_KeyValue);
//                break;
//            case "payout":
//                l_KeyValue = getKeyValueFromBills(bills, "Account", "payout");
//                break;
//            case "income":
//                l_KeyValue = getKeyValueFromBills(bills, "Account", "income");
//                break;
//        }
//        if( l_KeyValue != null){
//            if( footerCount != 0 ){
//                l_AccountList = l_KeyValue;
//                billList.removeFooter(); }
//        }else if( footerCount == 0 ){
//            billList.addFooter(this);
//        }
//        billList.setAdapter(new ListAdapter(this, 1, l_KeyValue, 0));
//        moneyOuntin.setText(R.string.balace);
//        moneyCount.setText(getCount(l_KeyValue));
//    }

    // 当前所在的 ViewPager 页面位置
    // protected int pagePosition = 6;
    // 把 bills 适配进 billList中，把 billLists 适配进 ViewPager 中
    protected void loadBilllist(String column,String date){
        bills = getDB(column, date); // bills = getBillsFromDB(date);
        int footerCount = billList.getFooterViewsCount();
        if( bills.size() != 0 || Global.isBilled ){
            if( footerCount != 0 ){
                billList.removeFooter();
                System.out.println("【getbills】不为零");
            }
            Global.isBilled = false;
        }else if( footerCount == 0 ){
            billList.addFooter(this);
        }
        billList.setAdapter(billsAdapter);
        moneyCount.setText(getMoneyCount(bills, s_outin));
    }

    protected void loadBilllist(String date){
        loadBilllist(BillTableDao.COLUMN_NAME_TIME, date);
    }


    protected void billlist(){
        billList = (SlideAndDragListView)findViewById(R.id.billlist);
        s_list = BillTableDao.COLUMN_NAME_TIME;
        Global.canSdlvMenu = true;
        Global.canSdlvPull = true;
        loadBilllist(Global.chooseDate);
        initLineChartView();
        closeBackTodayIcon();
        chargeTitle(Global.chooseDate);
    }
    protected void chargeTitle(String date){
        ImageView backToday = (ImageView)findViewById(R.id.back_today);
        if(date.equals(today)){
            backToday.setVisibility(View.GONE);
            timeTitle.setText(R.string.title_today);
        }else {
            backToday.setVisibility(View.VISIBLE);
            timeTitle.setText(date);
        }
    }
    protected void accountItemlist(String xxx){
        i_Layer = 1;
        s_list = "AccountItem";
        Global.canSdlvMenu = true;
        Global.canSdlvPull = false;
        billList = (SlideAndDragListView)findViewById(R.id.billlist);
        loadBilllist(BillTableDao.COLUMN_NAME_ACCOUNT,xxx);
        moneyCount.setText(getMoneyCount(bills, s_outin));
        lineChart.setVisibility(View.INVISIBLE);
        showBackTodayIcon();
        chargeTitle(Global.getIDName(xxx));
    }
    protected void typeItemList(String xxx){
        i_Layer = 1;
        s_list = "TypeItem";

        Global.canSdlvMenu = true;
        Global.canSdlvPull = false;
        billList = (SlideAndDragListView)findViewById(R.id.billlist);
        loadBilllist(BillTableDao.COLUMN_NAME_TYPE, xxx);
        moneyCount.setText(getMoneyCount(bills, s_outin));
        lineChart.setVisibility(View.INVISIBLE);
        showBackTodayIcon();
        chargeTitle(Global.getIDName(xxx));
    }
    protected void showBackTodayIcon(){
        ImageView backToday = (ImageView)findViewById(R.id.back_today);
        if(backToday.getVisibility() == View.GONE){backToday.setVisibility(View.VISIBLE);}
    }
    protected void closeBackTodayIcon(){
        ImageView backToday = (ImageView)findViewById(R.id.back_today);
        if(backToday.getVisibility() == View.VISIBLE){backToday.setVisibility(View.GONE);}
    }

    public void backToList(View view){
        if(s_list.equals("AccountItem")){
            loadAccountlist(null);
        }else if(s_list.equals("TypeItem")){
            loadTypelist(null);
        }else{
            s_outin = "payout";
            Global.chooseDate = today;
            billlist();
        }
    }


    protected List<Bill> bills;
    public List<Bill> getDB(String column,String key) {
        BillTableDao billTableDao = new BillTableDao(this);
        List<Bill> billArray;
        billArray = billTableDao.getDB(column, key);
        System.out.println("【getDB】" + billArray + column + key);
        Collections.reverse(billArray); // 根据元素的自然顺序 对指定列表按降序进行排序
        return billArray;
    }

//
//    protected List<KeyValue> getAllFromBills(List<Bill> billArray){
//        if(billArray == null){ return null;}
//        float money = 0 ,fPayoutMoney = 0 , fIncomeMoney = 0;
//        int i = 0 ;
//        HashMap<String, Float> mMap = new HashMap<>(),hPayoutMap = new HashMap<>(),hIncomeMap = new HashMap<>();
//        List<KeyValue> mKeyValueList = new ArrayList<>();
//        String[] AccountSArray = new String[billArray.size()];
//
//        for(Bill bill:billArray){
//            AccountSArray[i] = bill.getAccountId();
//            if(bill.getTypeId().contains("payout")){
//                money = - Float.valueOf(bill.getMoney());
//            }else if(bill.getTypeId().contains("income")){
//                money = Float.valueOf(bill.getMoney());
//            }
//            if( !mMap.containsKey(AccountSArray[i]) ){
//                mMap.put(AccountSArray[i],money );
//                i = i + 1;
//            }else {
//                mMap.put(AccountSArray[i],money + mMap.get(AccountSArray[i]));
//            }
//        }
//        // 把数据装入 KeyValueList
//        KeyValue mKeyValue;
//        for(int x=0; x<i ;x++){
//            mKeyValue = new KeyValue();
//            mKeyValue.setKey(AccountSArray[x]);
//            mKeyValue.setValue(mMap.get(AccountSArray[x]));
//            mKeyValueList.add(mKeyValue);
//        }
//        return mKeyValueList;
//    }
//
//
//    protected List<KeyValue> getOutinFromBills(List<Bill> billArray,String column,String outin){
//        if(billArray == null){return null;}
//        float money = 0;
//        int i = 0;
//        HashMap<String, Float> mMap = new HashMap<>();
//        List<KeyValue> mKeyValueList = new ArrayList<>();
//        String[] mKeyValueArray = new String[billArray.size()];
//
//        if (column.equals("Account")){
//            for(Bill bill:billArray){
//                if (bill.getTypeId().contains(outin)){
//                    mKeyValueArray[i] = bill.getAccountId(); //
//                    money = Float.valueOf(bill.getMoney());
//                    if( !mMap.containsKey(mKeyValueArray[i]) ){
//                        mMap.put(mKeyValueArray[i],money);
//                        i = i + 1;
//                    }else {
//                        mMap.put(mKeyValueArray[i],money + mMap.get(mKeyValueArray[i]));
//                    }
//                }
//            }
//        }else if (column.equals("Type")){
//            for(Bill bill:billArray){
//                if (bill.getTypeId().contains(outin)){
//                    mKeyValueArray[i] = bill.getTypeId(); //
//                    money = Float.valueOf(bill.getMoney());
//                    if( !mMap.containsKey(mKeyValueArray[i]) ){
//                        mMap.put(mKeyValueArray[i],money);
//                        i = i + 1;
//                    }else {
//                        mMap.put(mKeyValueArray[i],money + mMap.get(mKeyValueArray[i]));
//                    }
//                }
//            }
//        }
//        KeyValue mKeyValue;
//        for(int x=0; x<i ;x++){
//            mKeyValue = new KeyValue();
//            mKeyValue.setKey(mKeyValueArray[x]);
//            mKeyValue.setValue(mMap.get(mKeyValueArray[x]));
//            mKeyValueList.add(mKeyValue);
//        }
//        return mKeyValueList;
//    }

    protected List<KeyValue> getKeyvalueFromBills(List<Bill> billArray,String column,String outin){
        if(billArray == null){return null;}
        float money = 0;
        int i = 0;
        HashMap<String, Float> mMap = new HashMap<>();
        List<KeyValue> mKeyValueList = new ArrayList<>();
        String[] mKeyValueArray = new String[billArray.size()];

        if (column.equals("Account")){
            if ( outin.equals("all") ){
                for(Bill bill:billArray) {
                    mKeyValueArray[i] = bill.getAccountId();
                    if (bill.getTypeId().contains("payout")) {
                        money = -Float.valueOf(bill.getMoney());
                    } else if (bill.getTypeId().contains("income")) {
                        money = Float.valueOf(bill.getMoney());
                    }
                    if (!mMap.containsKey(mKeyValueArray[i])) {
                        mMap.put(mKeyValueArray[i], money);
                        i = i + 1;
                    } else {
                        mMap.put(mKeyValueArray[i], money + mMap.get(mKeyValueArray[i]));
                    }
                }
            }else {
                for(Bill bill:billArray){
                    if (bill.getTypeId().contains(outin)){
                        mKeyValueArray[i] = bill.getAccountId(); //
                        money = Float.valueOf(bill.getMoney());
                        if( !mMap.containsKey(mKeyValueArray[i]) ){
                            mMap.put(mKeyValueArray[i],money);
                            i = i + 1;
                        }else {
                            mMap.put(mKeyValueArray[i],money + mMap.get(mKeyValueArray[i]));
                        }
                    }
                }
            }
        }else if (column.equals("Type")){
            if ( outin.equals("all") ){
                for(Bill bill:billArray) {
                    mKeyValueArray[i] = bill.getTypeId();
                    if (bill.getTypeId().contains("payout")) {
                        money = -Float.valueOf(bill.getMoney());
                    } else if (bill.getTypeId().contains("income")) {
                        money = Float.valueOf(bill.getMoney());
                    }
                    if (!mMap.containsKey(mKeyValueArray[i])) {
                        mMap.put(mKeyValueArray[i], money);
                        i = i + 1;
                    } else {
                        mMap.put(mKeyValueArray[i], money + mMap.get(mKeyValueArray[i]));
                    }
                }
            }else {
                for(Bill bill:billArray){
                    if (bill.getTypeId().contains(outin)){
                        mKeyValueArray[i] = bill.getTypeId(); //
                        money = Float.valueOf(bill.getMoney());
                        if( !mMap.containsKey(mKeyValueArray[i]) ){
                            mMap.put(mKeyValueArray[i],money);
                            i = i + 1;
                        }else {
                            mMap.put(mKeyValueArray[i],money + mMap.get(mKeyValueArray[i]));
                        }
                    }
                }
            }
        }
        KeyValue mKeyValue;
        for(int x=0; x<i ;x++){
            mKeyValue = new KeyValue();
            mKeyValue.setKey(mKeyValueArray[x]);
            mKeyValue.setValue(mMap.get(mKeyValueArray[x]));
            mKeyValueList.add(mKeyValue);
        }
        return mKeyValueList;
    }





    protected String getCount(List<KeyValue> array){
        if(array==null){return "";}
        float money = 0;
        for(KeyValue bill:array){
            money = bill.getValue() + money ;
        }
        return String.valueOf(money);
    }

    /*
    添加viewPager监听器
     */
    protected String nextLoadDate;
    protected String lastLoadDate;
    protected int absolutePosition = 0;
    protected boolean isScrolling;
    private int lastValue = 0;
    protected int direction;
    protected boolean NoReLoading;
//
//    public ViewPager.OnPageChangeListener getOnPagerChangeListener() {
//        return new ViewPager.OnPageChangeListener() {
////            当页面在滑动的时候会调用此方法，在滑动被停止之前，此方法回一直得到调用。其中三个参数的含义分别为：
////            position:当前页面，及你点击滑动的页面；positionOffset:当前页面偏移的百分比；positionOffsetPixels:当前页面偏移的像素位置
//            @Override
//            public void onPageSelected(int position) {// 页面选择响应函数
//                // 如果需要实现页面滑动时动态添加，请在此判断 position 的值
//                // 当然此方式在必须在初始化 ViewPager 的时候给的页数必须 >2
//                // 因为给1页的话 ViewPager 是响应不了此函数的
//                // 例：
//                if(NoReLoading){return;}
//
//                absolutePosition = absolutePosition + direction;
//                Global.chooseDate = UTime.getDate(absolutePosition);
//                if(Global.chooseDate.equals(today)){
//                    NoReLoading = true;
//                    billViewPager.setCurrentItem(6,false);
//                    pagePosition = 6;
//                }else {
//                    if(position == 0){
//                        NoReLoading = true;
//                        billViewPager.setCurrentItem(4,false);
//                        pagePosition = 4;
//                    }else if(position == 5){
//                        NoReLoading = true;
//                        billViewPager.setCurrentItem(1,false);
//                        pagePosition = 1;
//                    }else{
//                        pagePosition = position;
//                    }
//            }
//                loadBilllist(Global.chooseDate);
//                NoReLoading = false;
//            }
//
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {// 滑动中。。。
//                if (isScrolling) {
//                    if ( lastValue > positionOffsetPixels) { //  direction != -1 &&
//                        direction = -1; // 界面向左滚动
////                        absolutePosition = absolutePosition + direction;
//                        Log.i("【方向1】","direction = " + direction + "==" + absolutePosition );
////                        loadBilllist(lastLoadDate);
//                    } else if ( lastValue < positionOffsetPixels ) {
//                        direction = 1;// 界面向右滚动，向右侧滑动
////                        absolutePosition = absolutePosition + direction;
////                        loadBilllist(nextLoadDate);
//                        Log.i("【方向2】","direction = " + direction + "==" + absolutePosition );
//                    } else if ( lastValue == positionOffsetPixels) {
//                        direction = 0;
//                        Log.i("【方向3】","direction = " + direction + "==" + absolutePosition + getChooseDate());
//                    }
//                }
////                Log.i("【方向】", direction + "==" + position + getChooseDate());
//                lastValue = positionOffsetPixels;
//            }
//            public void onPageScrollStateChanged(int scrollState) {// 滑动状态改变
//                if (scrollState == 1) {
//                    isScrolling = true;
//                } else {
//                    isScrolling = false;
//                }
//            }
//        };
//    }

//
////    public static int headerPaddingLeftRight,headerPaddingBottom,headerHeight;
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus){
////
////        MainActivity.headerHeight = header.getMeasuredHeight();
////        MainActivity.headerPaddingBottom = header.getPaddingBottom();
////        MainActivity.headerPaddingLeftRight = getPaddingLeft();
//    }

    @Override
    public void onListItemLongClick(View view, int position) {
    }
    @Override
    public void onDragViewStart(int position) {
    }
    @Override
    public void onDragViewMoving(int position) {
    }
    @Override
    public void onDragViewDown(int position) {
    }


    private String itemListTitle;
    private int i_Layer = 0;
    @Override
    public void onListItemClick(View v, int position) {
        if ( s_list.equals(BillTableDao.COLUMN_NAME_TIME) || s_list.equals("AccountItem") || s_list.equals("TypeItem")){return;}
        System.out.println("【List被点击1】" +position);
        if ( s_list.equals(BillTableDao.COLUMN_NAME_ACCOUNT)){
            float value = l_AccountList.get(position-1).getValue();
            itemListTitle = l_AccountList.get(position-1).getKey();
            System.out.println("【List被点击11】" + itemListTitle);
            accountItemlist(itemListTitle);
        }else if(s_list.equals(BillTableDao.COLUMN_NAME_TYPE)){
            float value = l_TypeList.get(position-1).getValue();
            itemListTitle = l_TypeList.get(position-1).getKey();
            typeItemList(itemListTitle);
        }
        System.out.println("【List被点击2】" + v + position);

    }
    @Override
    public void onSlideOpen(View view, View parentView, int position, int direction) {
    }
    @Override
    public void onSlideClose(View view, View parentView, int position, int direction) {
    }

    /*
    ITEM_NOTHING = 0;
    ITEM_SCROLL_BACK = 1;
    ITEM_DELETE_FROM_BOTTOM_TO_TOP = 2;
    左侧的按钮序号为正序( 0,1)。右侧为逆序 (1,0)
     */
    protected Bill billsItem;
    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        switch (direction) {
            case MenuItem.DIRECTION_LEFT:
                switch (buttonPosition) {
                    case 0:
                        return Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP;
                }
                break;
            case MenuItem.DIRECTION_RIGHT:
                switch (buttonPosition) {
                    case 0:
//                        System.out.println("【】"+ v +"=="+itemPosition+"="+buttonPosition+"="+direction+"="+bills);
                        billsItem = bills.get(itemPosition-1);
                        Global.isEditing = billsItem.getTime();
                        Global.bill = billsItem;
                        System.out.println("【itemPosition】" + itemPosition + Global.bill );
                        ActivityCollector.switchActivity(this, "ChargeActivity", null);
                        return Menu.ITEM_EDIT;
                }
        }
        return Menu.ITEM_NOTHING;
    }


    @Override
    public void onItemDelete(View view, int position) {
        BillTableDao billTableDao = new BillTableDao();
        billsItem = bills.get(position- billList.getHeaderViewsCount());
        billTableDao.deleteBill(billsItem.getTime());
        bills.remove(position - billList.getHeaderViewsCount());
        billsAdapter.notifyDataSetChanged();
        refreshList();
    }

    protected void  refreshList(){
        System.out.println("【refreshList】" + s_list);
        if(s_list.equals(BillTableDao.COLUMN_NAME_TIME)){
            billlist();
        }else if(s_list.equals(BillTableDao.COLUMN_NAME_ACCOUNT)){
            loadAccountlist(null);
        }else if(s_list.equals(BillTableDao.COLUMN_NAME_TYPE)){
            loadTypelist(null);
        }else if(s_list.equals("AccountItem")){
            accountItemlist(itemListTitle);
        }else if(s_list.equals("TypeItem")){
            typeItemList(itemListTitle);
        }
    }



    /**
     * 账单适配器
     */
    protected BaseAdapter billsAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
//            System.out.println("【-bill-】3" + bills.size());
            return bills.size();
        }
        @Override
        public Object getItem(int position) {
//            System.out.println("【-bill-】2" + bills.get(position));
            return bills.get(position);
        }
        @Override
        public long getItemId(int position) {
//            System.out.println("【-bill-】4" + position);
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            System.out.println("【-bill-】1");
            CustomViewHolder cvh;
            if (convertView == null) {
                cvh = new CustomViewHolder();
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_billlist_item, null);
                cvh.imgIcon = (ImageView) convertView.findViewById(R.id.billIcon);
                cvh.txtType = (TextView) convertView.findViewById(R.id.billName);
                cvh.txtMoney = (TextView) convertView.findViewById(R.id.billMoney);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }
            Bill bill = (Bill)this.getItem(position);
//            System.out.println("【-bill-】" + bill + "=="+ bill.getTypeId() + bill.getMoney() + bill.getRemark());
            cvh.imgIcon.setImageDrawable(bill.getTypeDrawable(bill.getTypeId()));
            cvh.txtMoney.setText(bill.getMoney());
            if(bill.getRemark().equals("")){ cvh.txtType.setText(bill.getTypeName()); }else { cvh.txtType.setText(bill.getRemark());}
            return convertView;
        }
        class CustomViewHolder {
            public ImageView imgIcon;
            public TextView txtType;
            public TextView txtMoney;
        }
    };

    /**
     * 监听返回键，弹出提示退出对话框
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(s_list.equals(BillTableDao.COLUMN_NAME_ACCOUNT) || s_list.equals(BillTableDao.COLUMN_NAME_TYPE)){
//            s_outin = "payout";
            billlist();
            return true;
        }else if(s_list.equals("AccountItem")){
//            s_outin = "payout";
            loadAccountlist(null);
            return true;
        }else if(s_list.equals("TypeItem")){
            loadTypelist(null);
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) { //重复次数。 点后退键的时候,为了防止点得过快,触发两次后退事件
            createDialog();// 创建弹出的Dialog
            return true;//返回真表示返回键被屏蔽掉
        }
        return super.onKeyDown(keyCode, event);
    }
    private void createDialog() {
        new AlertDialog.Builder(this)
                .setMessage("确定退出app?")
                .setPositiveButton("好滴 ^_^",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCollector.finishAll();
                    }
                        })
                .setNegativeButton("不！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }


    public void billMenu_Clicked(View view){
        new AlertView( null, null, "取消",null, new String[]{"今日账单","总账户", "总账目"}, this, AlertView.Style.ActionSheet, this).show();
    }
    @Override
    public void onItemClick(Object o,int position) {
        System.out.println("对象是" + o + "位置时" + position);
        //判断是否是拓展窗口View，而且点击的是非取消按钮
        if(position != AlertView.CANCELPOSITION){
            switch (position){
                case 0:
                    s_outin = "payout";
                    billlist();
                    break;
                case 1:
                    System.out.println("对象是23123" + o + "位置时" + position);
                    s_outin = "all";
                    loadAccountlist(null);
                    break;
                case 2:
                    s_outin = "payout";
                    loadTypelist(null);
                    break;
            }
            UToast.showToast("点击了第" + position + "个");
        }
    }


//    protected void initSSS(){
//        UToast.showToast("等待更新了数据库字段");
//        if (!check()){return;}
//        List<Bill> bills = getDB(BillTableDao.COLUMN_NAME_TYPE, "");
//        BillTableDao billTableDao = new BillTableDao();
//        String account;
//        for (Bill bill:bills){
//            account = bill.getAccountId();
//            for (int i=0;i<4;i++){
//                if(account.equals(Global.account[i])){
//                    account = "account_"+ i;
//                }
//            }
//            billTableDao.editBill(new Bill( bill.getTime() , bill.getMoney(), bill.getTypeId() + "", account ,bill.getRemark(), 1));
//        }
//        save();
//        UToast.showToast("更新了数据库字段 Account ");
//    }
//
//    public void save() {
//        SharedPreferences preferences = getSharedPreferences("have", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putBoolean("have", true);
//        editor.apply();
//    }
//    public boolean check(){
//        SharedPreferences preferences = getSharedPreferences("have", Context.MODE_PRIVATE);
//        return preferences.getBoolean("have", false);
//    }
}
