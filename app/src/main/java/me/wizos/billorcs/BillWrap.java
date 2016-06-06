package me.wizos.billorcs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import me.wizos.billorcs.sdlv.SlideAndDragListView;
import me.wizos.billorcs.utils.UDensity;

/**
 * Created by Wizos on 2016/2/19.
 */
public  class BillWrap extends LinearLayout {
    public BillWrap(Context context){
        this(context, null);
    }
    public BillWrap(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }


    private int mXDown;
    private int mYDown;
    private int mState;
    View header;
    int headerHeight;
    static int firstVisibleItem; // 当前第一个可见Item的位置
    int totalItemCount;
    int lastVisibleItem;
    static int scrollState; // 滚动状态
    boolean isRemark; // 标记第一个出现的Item
    int pullState; // 当前的状态
    final int NONE = 0; // 正常状态
    final int PULL = 1; // 下拉刷新状态
    final int RELESE = 2; // 松开释放状态
    final int REFLASHING = 3; // 正在刷新状态
    private TextView txtViewLoading = null;
    private ProgressBar prgBarLoading = null;
    private Context context;
    /* onTouch里面的状态 */
    private static final int STATE_NOTHING = -1;//抬起状态
    private static final int STATE_DOWN = 0;//按下状态
    private static final int STATE_LONG_CLICK = 1;//长点击状态
    private static final int STATE_SCROLL = 2;//SCROLL状态
    private static final int STATE_LONG_CLICK_FINISH = 3;//长点击已经触发完成
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mXDown = (int) ev.getX();//获取出坐标来  super.dispatchTouchEvent(ev)
//                mYDown = (int) ev.getY();
//                mState = STATE_DOWN;
//            case MotionEvent.ACTION_MOVE:
//                System.out.println("【ioooo】" + mState);
//                if (fingerDownMove(ev)) { //【左右范围在50，主要检测向下滑动
//                    chargeHeaderHeightAndPullStateByMoveSpace(ev);
//                    System.out.println("上下121滑动操作结束");
//                    return true;//消耗事件
//                } //】
//                break;
//            case MotionEvent.ACTION_UP:
//                completePull();
//                System.out.println("【状态121】" + mState + "==" + pullState + "==" + ev.getY() + "==" + mYDown);
//                if (pullState == PULL) {
//                    mState = STATE_NOTHING;
//                    pullState = NONE;
//                    ActivityCollector.switchActivity(context, "ChargeActivity", null);
//                }
//                break;
//            default:
//                break;
//        }
//        System.out.println("【状态5555】" + mState + "==" + pullState + "==" + ev.getY() + "==" + mYDown);
//        return true;
//    }

    public void init(){
        header = findViewById(R.id.listView_Header);
        headerHeight = header.getMeasuredHeight();
        topPadding(-headerHeight);
        System.out.println("【初始化的数据】" + headerHeight + "---"+UDensity.dpToPx(70));
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXDown = (int) ev.getX();//获取出坐标来  super.dispatchTouchEvent(ev)
                mYDown = (int) ev.getY();
                mState = STATE_DOWN;
            case MotionEvent.ACTION_MOVE:
//                System.out.println("【ioooo】" + mState);
                if ( Global.canSdlvPull && ( downing || fingerDownMove(ev)) ) { //【左右范围在50，主要检测向下滑动
                    chargeHeaderHeightAndPullStateByMoveSpace(ev);
                    System.out.println("【下滑动操作】"  + Global.canSdlvPull);
                    return true;//消耗事件
                } //】
                break;
            case MotionEvent.ACTION_UP:
                downing = false;
                completePull();
//                System.out.println("【状态121】" + mState + "==" + pullState + "==" + ev.getY() + "==" + mYDown);
                if (pullState == PULL) {
                    mState = STATE_NOTHING;
                    pullState = NONE;
                    ActivityCollector.switchActivity(context, "ChargeActivity", null);
                }
                break;
            default:
                break;
        }
//        System.out.println("【状态5555】" + mState + "==" + pullState + "==" + ev.getY() + "==" + mYDown);
        return true;
    }
    private boolean fingerNotMove(MotionEvent ev) {
        return (mXDown - ev.getX() < 25 && mXDown - ev.getX() > -25 &&
                mYDown - ev.getY() < 25 && mYDown - ev.getY() > -25);
    }
    protected boolean downing = false;
    private boolean fingerDownMove(MotionEvent ev) {
        boolean a = (ev.getY() - mYDown > 25 ) && (ev.getX() - mXDown < 25 ) && (mXDown - ev.getX() < 25);
        downing = true;
//        System.out.println("检测上下滑动"  + ev.getY()+ a+"==="+ mYDown+"==="+ mXDown +"==="+  ev.getX() );
        return a;
    }
    public void completePull(){
        topPadding(-headerHeight);
    }
    private void topPadding(int topPadding) {
        System.out.println("---------------");
        header.setPadding(SlideAndDragListView.headerPaddingLeftRight, topPadding, SlideAndDragListView.headerPaddingLeftRight, SlideAndDragListView.headerPaddingBottom);
        header.invalidate();
    }
    protected int headerTopPadding;
    private void chargeHeaderHeightAndPullStateByMoveSpace(MotionEvent ev){
        TextView tip = (TextView) findViewById(R.id.list_header_text);
//        if (!isRemark) { return;}
        int spaceY = (int)ev.getY() - mYDown; // 正数代表向下拉，负数代表向上滑

//        int y = headerTopPadding;
        System.out.println("【spaceY】" + spaceY + "【headerHeight】" + headerHeight);
        if (spaceY <= headerHeight/4){ //  (spaceY > 0 &&
            pullState = NONE;
//            headerTopPadding = spaceY - headerHeight ;  //达到让 Header 隐藏的效果
//            topPadding(headerTopPadding);
            headerTopPadding = spaceY - headerHeight;
            topPadding(headerTopPadding);
            System.out.println("【Y1】" + headerTopPadding + "【Y1】" + header.getPaddingTop() );
        } else if ( spaceY > headerHeight/4 && spaceY <= headerHeight/2 ){
            pullState = PULL;
            headerTopPadding = header.getPaddingTop();
            topPadding(headerTopPadding + (spaceY - headerTopPadding)/3);
            tip.setText("记一笔吧");
            System.out.println("【Y2】" + headerTopPadding + "【Y2】" + (spaceY - headerTopPadding)/2 );
        } else if ( spaceY > headerHeight/2 && spaceY < headerHeight){  // && scrollState == 1
            pullState = PULL;
            headerTopPadding = header.getPaddingTop();
            topPadding(headerTopPadding + (spaceY - headerTopPadding)/6);
            tip.setText("松开可以记账");
            System.out.println("【Y3】" + headerTopPadding + (spaceY - headerTopPadding)/2 );
        }

//        else if ( spaceY <= 0 ){
//            pullState = NONE;
//        }
    }
}
