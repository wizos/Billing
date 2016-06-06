package me.wizos.billorcs;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.wizos.billorcs.bean.Bill;
import me.wizos.billorcs.bean.BillType;
import me.wizos.billorcs.db.DBManager;
import me.wizos.billorcs.pickerview.TimePickerView;
import me.wizos.billorcs.utils.UDensity;
import me.wizos.billorcs.utils.UTime;
import me.wizos.billorcs.utils.UTool;
import me.wizos.billorcs.view.ExpandGridView;

public class ChargeActivity extends ChargeActivityKeyboard {
//    private List<BillType> billTypes;
//    public static Activity instances = null;
protected Context context;

    @Override  // @Override是伪代码,表示重写(复写)：1、可当注方便阅读；2、方便编译器验证“复写”这个方法名是否在父类中所有的
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_charge);
        context = ActivityCollector.getContext();
        DBManager.getInstance().onInit(context);
        binding();
        initType();
        initPickerView();
        initAccount();
        initRemarkView();
    }
    @Override
    public void onResume(){
        super.onResume();
        if(!Global.isEditing.equals("")) { // 其实直接用那个 bill是否存在 来判断 是否在编辑模式下 也可以
//            System.out.println("【initViewData(true)】");
            giveViewData();// 补充界面的数据  //  checkViewData(); // 因为传入的数据是规范的，所以不用检查？
        }
//        else {
//            clearViewData();
//        }
    }

    protected void giveViewData(){
        Bill bill = Global.bill;
        recordTime = bill.getTime();
        moneySum = bill.getMoney();
        moneyView.setText(moneySum);
        stringBuffer = new StringBuffer(moneySum);
        account = bill.getAccountId();
        accountPosition = UTool.findPosition(Global.account, account);
        spinner.setSelection(accountPosition,true);
        System.out.println("【2】" + accountPosition + stringBuffer + "==="+ bill.getMoney() + bill.getRemark() + bill.getTime() +bill.getTypeId());
        remarkView.setText(bill.getRemark());
        chooseType = bill.getTypeId();
        chooseAccount = bill.getAccountId();
        drawChooseTypeIcon(chooseType);
        checkMoneySum();
    }
//    protected void clearViewData(){ // 初始化界面的数据
//        moneyView.setText(R.string.moneyHint);
//        remarkView.setHint(R.string.remark);
//        moneySum ="";
//        stringBuffer = new StringBuffer();
//        chooseTypeId = "payout_0";
//        drawChooseTypeIcon("payout_0");
//        account = "现金";
//        floatCount = 0;
//        newString = false;
//        canNewPoint = true;
//        haveCharge =false;
//        recordTime = Global.recordTime;
//        chargeKeyView(0);
//    }


    private float mXDown;
    private float mYDown;
    /*
    监测 手势滑动返回状态
     */
    private int checkMotion(MotionEvent ev) {
        int motionState;
        if( ev.getY() - mYDown > 100 && ExpandViewPaper){ // 下滑 && ViewPager展开
            motionState = 0;
        } else if( mYDown - ev.getY() > 100  && !ExpandViewPaper){  // 上滑 && ViewPager没有展开
            motionState = 1;
        } else {  motionState = -1; }
        return (motionState);
    }
    /*
    根据 手势滑动的返回状态 改变 ViewPager
     */
    public static boolean ExpandViewPaper = false;
    public void expandViewPaper(int state){
        if ( state == 0 ){
            RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) viewPager.getLayoutParams(); // 取控件aaa当前的布局参数
            linearParams.height = getGridViewHeight(0);       //getGridViewHeight(0) 当控件的高强制设成 getGridViewHeight(0)
            viewPager.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件aaa
            ExpandViewPaper = false;
        } else if ( state == 1 ){
            RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams)viewPager.getLayoutParams(); // 取控件aaa当前的布局参数
            linearParams.height = getGridViewHeight(1);        // 当控件的高强制设成 ViewPaperHeight 的象素
            viewPager.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件aaa
            ExpandViewPaper = true;
        }
        System.out.println("【】获得高度：" + getGridViewHeight(0) +"【】获得高度：" + UDensity.dip2px(this, 125) + UDensity.px2dip(this, getGridViewHeight(0)));
    }
    protected void checkViewPaper(){
        if(ExpandViewPaper){expandViewPaper(0);}
    }

    protected void changeKeyboard(int state){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        switch (state){
            case 0:  // 关闭系统键盘，显示计算
                remarkView.setInputType(InputType.TYPE_NULL);
                keyboard.setVisibility(View.VISIBLE);
                break;
            case 1:  // 打开系统键盘，关闭计算
                keyboard.setVisibility(View.GONE);
                imm.showSoftInput(remarkView, InputMethodManager.SHOW_FORCED);
                break;
        }
    }
    protected void initRemarkView() {
        remarkView = (EditText)findViewById(R.id.remark);
        remarkView.setOnFocusChangeListener(onFocusAutoClearHintListener);
    }
    public View.OnFocusChangeListener onFocusAutoClearHintListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText textView = (EditText) v;
            if (hasFocus) {
                textView.setTag(textView.getHint().toString());
                textView.setHint("");
                checkViewPaper();
                changeKeyboard(1);
            } else {
                textView.setHint(textView.getTag().toString());
                checkViewPaper();
                changeKeyboard(0);
            }
        }
    };

    public void onClickedHideIME(View view){
        view.requestFocus();
    }
    protected int viewPagerCurrentItem = 0;
    // 初始化用途类别（viewpager + gridView 九宫图）
    protected void initType() {
        final List<View> views = new ArrayList<>(); // 这句创建了一个ArrayList的对象后把它向上转型到了List。此时它是一个List对象了，有些ArrayList有但是List没有的属性和方法，它就不能再用了。
        /*
        List是一个接口，而ArrayList是一个类，它继承并实现了List。
        List list; 是正确的，list = null;  List list = new List(); 是错误的用法
        List接口的常用实现类有ArrayList和LinkedList，在使用List集合时，通常情况下声明为List类型，实例化时根据实际情况的需要，实例化为ArrayList或LinkedList，例如：
        List<String> l1 = new ArrayList<String>(); // 利用ArrayList类实例化List集合
        List<String> l2 = new LinkedList<String>();// 利用LinkedList类实例化List集合
        1．add(int index, Object obj)方法和set(int index, Object obj)方法的区别
        在使用List集合时需要注意区分add(int index, Object obj)方法和set(int index, Object obj)方法，前者是向指定索引位置添加对象，而后者是修改指定索引位置的对象
        list.set(1, b);// 将索引位置为1的对象e修改为对象b
        list.add(2, c);// 将对象c添加到索引位置为2的位置
         */
        views.add(getGridChildView(-1)); // 支出的
        views.add(getGridChildView(1)); // 收入的
        viewPager.setAdapter(new ExpressionPagerAdapter(views));
        viewPager.addOnPageChangeListener(getOnPagerChangeListener());
        System.out.println("【 viewPager 的高度】" + viewPager.getHeight());
    }

    public View gridView;
    public ExpandGridView gv;
    public View getGridChildView(final int i) {
        gridView = View.inflate(this, R.layout.gridview, null);
//        final ExpandGridView gv = (ExpandGridView) gridView.findViewById(R.id.gridview); // final成员变量表示常量，只能被赋值一次，赋值后值不再改变
        gv = (ExpandGridView) gridView.findViewById(R.id.gridview);
        final List<BillType> list = new ArrayList<BillType>();
        if (i == -1) {
            list.addAll(billTypesPayout.subList(0, billTypesPayout.size())); // subList 作用是返回一个以fromIndex为起始索引（包含），以toIndex为终止索引（不包含）的子列表（List）。但值得注意的是，返回的这个子列表的幕后其实还是原列表；也就是说，修改这个子列表，将导致原列表也发生改变；反之亦然。
        }else if (i == 1) {
            list.addAll(billTypesIncome.subList(0, billTypesIncome.size()));
        }
        final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this,1, list);
        gv.setAdapter(expressionAdapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drawChooseTypeIcon(Global.dial_OutIn + position);
                expandViewPaper(0);
                changeKeyboard(0);
                chooseType = Global.dial_OutIn + String.valueOf(position);
            }
            /*
            parent	The AdapterView where the click happened.
            view	The view within the AdapterView that was clicked (this will be a view provided by the adapter(适应))
            position	The position of the view in the adapter(适应).
            id	The row id of the item that was clicked.
            */
        });
        gv.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                boolean usedTouch = false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mXDown = event.getX();
                        mYDown = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int viewPaperState = checkMotion(event);
                        if (viewPaperState == 1) {
                            keyboard.setVisibility(View.GONE);
                        }
                        if (viewPaperState == 0) {
                            keyboard.setVisibility(View.VISIBLE);
                        }
                        expandViewPaper(viewPaperState);
                        if (viewPaperState != -1) {
                            usedTouch = true;
                        } else {
                            usedTouch = false;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    default:
                        break;
                }
                return usedTouch;
            }
        });
        return gridView;
    }

    /*
     根据 参数获得 GridViewHeight 参数 0 代表默认有 2行 ，参数 1 是最大行的高
     */
    private int getGridViewHeight(int mode){
        View gridview_genre = gridView.findViewById(R.id.gridview_genre);
        int gridGenreHeight = gridview_genre.getHeight() + UDensity.get2Px(this, R.dimen.gridview_vertical_pacing);
        int gridViewHeight = gridGenreHeight;

        switch (mode){
            case 0:
                gridViewHeight = gridGenreHeight*2;
                break;
            case 1:
                if(Global.typesPayout.length / 5 > Global.typesIncome.length / 5 ){
                    if (Global.typesPayout.length % 5 != 0){
                        gridViewHeight = ( Global.typesPayout.length / 5 + 1 ) * gridGenreHeight ;
                    } else {
                        gridViewHeight = Global.typesPayout.length / 5 * gridGenreHeight ;
                    }
                }else {
                    if (Global.typesIncome.length % 5 != 0){
                        gridViewHeight = ( Global.typesIncome.length / 5 + 1 ) * gridGenreHeight ;
                    } else {
                        gridViewHeight = Global.typesIncome.length / 5 * gridGenreHeight ;
                    }
                }
                break;
        }
        System.out.println("【高度】" + gridGenreHeight);
        return gridViewHeight - UDensity.get2Px(this, R.dimen.gridview_vertical_pacing);
    }


    protected void initAccount(){
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,Global.account);
//        上面第一个参数指上下文对象，第二个参数指定下拉框的样式，第三个参数指定TextView的id，R.id.textid 在R.layout.item中定义，第四个参数提供数据源
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPrompt("账户");   //在Spinner弹出选择对话框的时候对话框的标题
//        setDropDownHorizontalOffset(int)
//        spinnerMode=”dropdown”// 下拉的项目选择窗口在水平方向相对于Spinner窗口的偏移量。
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                account = Global.account[position];
                chooseAccount = "account_"+ position;
                account = (String) spinner.getSelectedItem();
                System.out.println("【】" + position);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

/*
确定点的状态
 */
//    protected String dial_OutIn;
    public void setCurDial(int position) {
        ImageView dial_Payout = (ImageView)findViewById(R.id.dial_Payout_img);
        ImageView dial_Income = (ImageView)findViewById(R.id.dial_Income_img);
        switch (position) {
            case 0:
                dial_Payout.setImageResource(R.drawable.dial_state_1);
                dial_Income.setImageResource(R.drawable.dial_state_0);
//                dial_OutIn = "payout_";
                Global.dial_OutIn = "payout_";
                break;
            case 1:
                dial_Payout.setImageResource(R.drawable.dial_state_0);
                dial_Income.setImageResource(R.drawable.dial_state_1);
                Global.dial_OutIn = "income_";
                break;
        }
    }


    /*
    添加viewPager监听器
     */
    public ViewPager.OnPageChangeListener getOnPagerChangeListener() {
        return new ViewPager.OnPageChangeListener() {
//            当页面在滑动的时候会调用此方法，在滑动被停止之前，此方法回一直得到调用。其中三个参数的含义分别为：
//            position:当前页面，及你点击滑动的页面；positionOffset:当前页面偏移的百分比；positionOffsetPixels:当前页面偏移的像素位置
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if( !ExpandViewPaper && positionOffsetPixels>0){ expandViewPaper(0);}
            }
            // 此方法是页面跳转完后得到调用，position 是你当前选中的页面的位置编号
            @Override
            public void onPageSelected(int position) {
                setCurDial(position);
                if (position == viewPagerCurrentItem) {
                    return;
                }
                Button payoutTitle = (Button) findViewById(R.id.payout);
                Button incomeTitle = (Button) findViewById(R.id.income);
                switch (position) {
                    case 0:
                        incomeTitle.setVisibility(View.GONE);
                        payoutTitle.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        payoutTitle.setVisibility(View.GONE);
                        incomeTitle.setVisibility(View.VISIBLE);
                        break;
                }
                viewPagerCurrentItem = position;
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }
    protected void drawChooseTypeIcon(String position){
        ImageView imageView = (ImageView) findViewById(R.id.moneyShow_Icon);
        Drawable drawable = billing.getTypeDrawable(position); //Global.dial_OutIn +String.valueOf(position)
        imageView.setImageDrawable(drawable);
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
                recordTime = UTime.getFormatDate(date) + " " + UTime.getNow() ;
            }
        });
    }
    public void show_pickerview(View view){
        pvTime.show();
    }

}
