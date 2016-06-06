package me.wizos.billorcs.sdlv;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.wizos.billorcs.ActivityCollector;
import me.wizos.billorcs.Global;
import me.wizos.billorcs.R;

/**
 * Created by yuyidong on 15/9/28.
 */
public class SlideAndDragListView<T> extends DragListView<T> implements WrapperAdapter.OnAdapterSlideListenerProxy,
        WrapperAdapter.OnAdapterMenuClickListenerProxy, Handler.Callback {
    /* Handler 的 Message 信息 */
    private static final int MSG_WHAT_LONG_CLICK = 1;
    /* Handler 发送message需要延迟的时间 */
    private static final long CLICK_LONG_TRIGGER_TIME = 1000;//1s
    /* onTouch里面的状态 */
    private static final int STATE_NOTHING = -1;//抬起状态
    private static final int STATE_DOWN = 0;//按下状态
    private static final int STATE_LONG_CLICK = 1;//长点击状态
    private static final int STATE_SCROLL = 2;//SCROLL状态
    private static final int STATE_LONG_CLICK_FINISH = 3;//长点击已经触发完成
    // private static final int STATE_MORE_FINGERS = 4;//多个手指
	//【改为这个
    private static final int STATE_ROLLING = 4; 
	//】
    private int mState = STATE_NOTHING;

    private static final int RETURN_SCROLL_BACK_OWN = 1;//自己有归位操作
    private static final int RETURN_SCROLL_BACK_OTHER = 2;//其他位置有归位操作
    private static final int RETURN_SCROLL_BACK_CLICK_MENU_BUTTON = 3;//点击到了滑开的item的menuButton上
    private static final int RETURN_SCROLL_BACK_NOTHING = 0;//所以位置都没有回归操作

    /* 振动 */
    private Vibrator mVibrator;
    /* handler */
    private Handler mHandler;
    /* 是否要触发itemClick */
    private boolean mIsWannaTriggerClick = true;
    /* 手指放下的坐标 */
    private int mXDown;
    private int mYDown;
    /* Menu */
    private Map<Integer, Menu> mMenuMap;
    /* WrapperAdapter */
    private WrapperAdapter mWrapperAdapter;
    /* 手指滑动的最短距离 */
    private int mShortestDistance = 25;

    /* 监听器 */
    private OnSlideListener mOnSlideListener;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private OnListItemLongClickListener mOnListItemLongClickListener;
    private OnListItemClickListener mOnListItemClickListener;
    private OnItemDeleteListener mOnItemDeleteListener;
    //【
    View header;
    View footer;
    int footerHeight;

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
    //】

    public SlideAndDragListView(Context context) {
        this(context, null);
    }

    public SlideAndDragListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideAndDragListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
		this.context = context;
        mVibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        mHandler = new Handler(this);
//        mShortestDistance = ViewConfiguration.get(context).getScaledDoubleTapSlop();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_WHAT_LONG_CLICK:
                if (mState == STATE_LONG_CLICK) {//如果得到msg的时候state状态是Long Click的话
                    //改为long click触发完成
                    mState = STATE_LONG_CLICK_FINISH;
                    //得到长点击的位置
                    int position = msg.arg1;
                    //找到那个位置的view
                    View view = getChildAt(position - getFirstVisiblePosition());
                    //如果设置了监听器的话，就触发
                    if (mOnListItemLongClickListener != null) {
                        mVibrator.vibrate(100); // 【】我想从100改为了0
                        mOnListItemLongClickListener.onListItemLongClick(view, position);
                    }
                    boolean canDrag = scrollBackByDrag(position);
                    if (canDrag && view instanceof ItemMainLayout) {
                        setDragPosition(position);
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        System.out.println("Sdlv的：dispatchTouchEvent");
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXDown = (int) ev.getX();//获取出坐标来
                mYDown = (int) ev.getY();
                mState = STATE_DOWN; //当前state状态为按下
                //【】
                /**
                 * 对屏幕触摸的监控
                 * 先判断当前是否是在顶端。如果是在最顶端，记录下你开始滑动的Y值
                 * 然后在滑动过程中（监听到的是ACTION_MOVE)，不断地判断当前滑动的范围是否到达应该刷新的程度。
                 * (根据当前的Y-之前的startY的值 与我们的控件的高度之间关系来判断）
                 * 然后在监听到手指松开时，根据当前的状态（我们在onmove（）中计算的），做相应的操作。
                 */
                if (firstVisibleItem == 0) { isRemark = true;} else { isRemark = false;}
//                Log.i("ACTION_DOWN", "判断结束");
                break;
            case MotionEvent.ACTION_MOVE:
//                System.out.println("【firstVisibleItem】" + firstVisibleItem);
                if (fingerNotMove(ev) && mState != STATE_SCROLL) { // 手指的范围在50以内
                    sendLongClickMessage(pointToPosition(mXDown, mYDown));
                    mState = STATE_LONG_CLICK;
                } else if ( fingerLeftAndRightMove(ev) ) { // 上下范围在50，为左右滑动
                    removeLongClickMessage();
                    mState = STATE_SCROLL;
                    System.out.println("左右滑动操作");
                    //将当前想要滑动哪一个传递给 wrapperAdapter
                    int position = pointToPosition(mXDown, mYDown);
                    if (position != AdapterView.INVALID_POSITION) {
                        View view = getChildAt(position - getFirstVisiblePosition());
                        if (view instanceof ItemMainLayout) { // instanceof 指出对象是否是特定类的一个实例
                            mWrapperAdapter.setSlideItemPosition(position);
                            return super.dispatchTouchEvent(ev);  //将事件传递下去
                        } else { return true;} //消耗事件
                    } else { return true;}
                }else if ( Global.canSdlvPull && ( fingerDownMove(ev) && isRemark) ){ //【左右范围在50，主要检测向下滑动
                    removeLongClickMessage();
                    chargeHeaderHeightAndPullStateByMoveSpace(ev);
//                    System.out.println("上下滑动操作结束");
                    return true;//消耗事件
                } //】
                break;
            case MotionEvent.ACTION_UP:
                completePull();
                System.out.println("【状态】" + mState + "==" + pullState+"=="+ ev.getY() +"=="+ mYDown);
                if( pullState == PULL ){
                    mState = STATE_NOTHING;
                    pullState = NONE;
                    ActivityCollector.switchActivity(context, "ChargeActivity", null);
                }

                if (mState == STATE_DOWN || mState == STATE_LONG_CLICK) {
                    int position = pointToPosition(mXDown, mYDown);
                    //是否ScrollBack了，是的话就不去执行onListItemClick操作了
                    int scrollBackState = scrollBack(position, ev.getX());
                    if (scrollBackState == RETURN_SCROLL_BACK_NOTHING) {
                        if (mOnListItemClickListener != null && mIsWannaTriggerClick) {
                            View v = getChildAt(position - getFirstVisiblePosition());
                            mOnListItemClickListener.onListItemClick(v, position);
                        }
                    }
                }
                removeLongClickMessage();
                mState = STATE_NOTHING;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mState = STATE_NOTHING;
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 将滑开的item归位
     *
     * @param position
     * @param x        坐标
     * @return
     */
    private int scrollBack(int position, float x) {
        //是不是当前滑开的这个
        if (mWrapperAdapter.getSlideItemPosition() == position) {
            boolean isScrollBack = mWrapperAdapter.returnSlideItemPosition(x);
            if (isScrollBack) {
                return RETURN_SCROLL_BACK_OWN;
            } else {
                return RETURN_SCROLL_BACK_CLICK_MENU_BUTTON;
            }
        } else if (mWrapperAdapter.getSlideItemPosition() != -1) {
            mWrapperAdapter.returnSlideItemPosition();
            return RETURN_SCROLL_BACK_OTHER;
        }
        return RETURN_SCROLL_BACK_NOTHING;
    }

    /**
     * 用于drag的ScrollBack逻辑操作
     *
     * @param position
     * @return true--->可以drag false--->不能drag
     */
    private boolean scrollBackByDrag(int position) {
        //是不是当前滑开的这个
        if (mWrapperAdapter.getSlideItemPosition() == position) {
            return false;
        } else if (mWrapperAdapter.getSlideItemPosition() != -1) {
            mWrapperAdapter.returnSlideItemPosition();
            return true;
        }
        return true;
    }

    /**
     * remove掉message
     */
    private void removeLongClickMessage() {
        if (mHandler.hasMessages(MSG_WHAT_LONG_CLICK)) {
            mHandler.removeMessages(MSG_WHAT_LONG_CLICK);
        }
    }

    /**
     * sendMessage
     */
    private void sendLongClickMessage(int position) {
        if (!mHandler.hasMessages(MSG_WHAT_LONG_CLICK)) {
            Message message = new Message();
            message.what = MSG_WHAT_LONG_CLICK;
            message.arg1 = position;
            mHandler.sendMessageDelayed(message, CLICK_LONG_TRIGGER_TIME);
        }
    }

    /**
     * 上下左右不能超出50
     *
     * @param ev
     * @return
     */
    private boolean fingerNotMove(MotionEvent ev) {
        return (mXDown - ev.getX() < mShortestDistance && mXDown - ev.getX() > -mShortestDistance &&
                mYDown - ev.getY() < mShortestDistance && mYDown - ev.getY() > -mShortestDistance);
    }

    /**
     * 左右得超出50，上下不能超出50
     *
     * @param ev
     * @return
     */
    private boolean fingerLeftAndRightMove(MotionEvent ev) {
        return ((ev.getX() - mXDown > mShortestDistance || ev.getX() - mXDown < -mShortestDistance) &&
                ev.getY() - mYDown < mShortestDistance && ev.getY() - mYDown > -mShortestDistance);
    }

    /**
     * 设置Menu
     *
     * @param menu
     */
    public void setMenu(Menu menu) {
        if (mMenuMap != null) {
            mMenuMap.clear();
        } else {
            mMenuMap = new HashMap<>(1);
        }
        mMenuMap.put(menu.getMenuViewType(), menu);
    }

    /**
     * 设置menu
     *
     * @param list
     */
    public void setMenu(List<Menu> list) {
        if (mMenuMap != null) {
            mMenuMap.clear();
        } else {
            mMenuMap = new HashMap<>(list.size());
        }
        for (Menu menu : list) {
            mMenuMap.put(menu.getMenuViewType(), menu);
        }
    }

    /**
     * 设置Menu
     *
     * @param menus
     */
    public void setMenu(Menu... menus) {
        if (mMenuMap != null) {
            mMenuMap.clear();
        } else {
            mMenuMap = new HashMap<>(menus.length);
        }
        for (Menu menu : menus) {
            mMenuMap.put(menu.getMenuViewType(), menu);
        }
    }

    @Override
    public void setAdapter(final ListAdapter adapter) {
        if (mMenuMap == null || mMenuMap.size() == 0) {
            throw new IllegalArgumentException("先设置Menu");
        }
        mWrapperAdapter = new WrapperAdapter(getContext(), this, adapter, mMenuMap) {

            @Override
            public void onScrollStateChangedProxy(AbsListView view, int scrollState) {
                // 当屏幕停止滚动时为0；当屏幕滚动且用户使用的触碰或手指还在屏幕上时为1；
                // 由于用户的操作，屏幕产生惯性滑动时为2
                if (scrollState == WrapperAdapter.SCROLL_STATE_IDLE) {
                    mIsWannaTriggerClick = true;
                } else {
                    mIsWannaTriggerClick = false;
                }
                // 【】
                SlideAndDragListView.scrollState = scrollState;
//                System.out.println("scrollState状态：" + scrollState);
            }

            @Override
            public void onScrollProxy(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 【】	firstVisibleItem表示在现时屏幕第一个ListItem(部分显示的ListItem也算)在整个ListView的位置（下标从0开始）
                 SlideAndDragListView.firstVisibleItem = firstVisibleItem;
//                 System.out.println("onScrollProxy 的滚动状态。firstVisibleItem状态：" + firstVisibleItem);
            }

            @Override
            public void onItemDelete(View view, int position) {
                if (mOnItemDeleteListener != null) {
                    mOnItemDeleteListener.onItemDelete(view, position);
                }
            }
        };
        mWrapperAdapter.setOnAdapterSlideListenerProxy(this);
        mWrapperAdapter.setOnAdapterMenuClickListenerProxy(this);
        setRawAdapter(adapter);
        super.setAdapter(mWrapperAdapter);
    }

    /**
     * 设置item滑动监听器
     */
    public void setOnSlideListener(OnSlideListener listener) {
        mOnSlideListener = listener;
    }

    /**
     * item的滑动的监听器
     */
    public interface OnSlideListener {
        /**
         * 当滑动开的时候触发
         *
         * @param view
         * @param parentView
         * @param position
         */
        void onSlideOpen(View view, View parentView, int position, int direction);

        /**
         * 当滑动归位的时候触发
         *
         * @param view
         * @param parentView
         * @param position
         */
        void onSlideClose(View view, View parentView, int position, int direction);
    }

    @Override
    public void onSlideOpen(View view, int position, int direction) {
        if (mOnSlideListener != null) {
            mOnSlideListener.onSlideOpen(view, this, position, direction);
        }
    }

    @Override
    public void onSlideClose(View view, int position, int direction) {
        if (mOnSlideListener != null) {
            mOnSlideListener.onSlideClose(view, this, position, direction);
        }
    }

    /**
     * 设置item中的button点击事件的监听器
     *
     * @param onMenuItemClickListener
     */
    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        mOnMenuItemClickListener = onMenuItemClickListener;
    }

    /**
     * item中的button监听器
     */
    public interface OnMenuItemClickListener {
        /**
         * 点击事件
         *
         * @param v
         * @param itemPosition   第几个item
         * @param buttonPosition 第几个button
         * @param direction      方向
         * @return 参考Menu的几个常量
         */
        int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction);
    }

    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        if (mOnMenuItemClickListener != null) {
            return mOnMenuItemClickListener.onMenuItemClick(v, itemPosition, buttonPosition, direction);
        }
        return Menu.ITEM_NOTHING;
    }

    @Deprecated
    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
    }

    /**
     * 设置监听器
     * @param listener
     */
    public void setOnListItemClickListener(OnListItemClickListener listener) {
        mOnListItemClickListener = listener;
    }

    /**
     * 自己的单击事件
     */
    public interface OnListItemClickListener {
        void onListItemClick(View v, int position);
    }

    @Deprecated
    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
    }

    /**
     * 设置监听器
     */
    public void setOnListItemLongClickListener(OnListItemLongClickListener listener) {
        mOnListItemLongClickListener = listener;
    }

    /**
     * 自己写的长点击事件Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP
     */
    public interface OnListItemLongClickListener {
        void onListItemLongClick(View view, int position);
    }

    public void setOnItemDeleteListener(OnItemDeleteListener onItemDeleteListener) {
        mOnItemDeleteListener = onItemDeleteListener;
    }

    public interface OnItemDeleteListener {
        void onItemDelete(View view, int position);
    }

    /** 【以下都是更改的】
     * 上下得超出50，左右不能超出50
     */
    private boolean fingerDownMove(MotionEvent ev) {
        boolean b = ((ev.getY() - mYDown > mShortestDistance ) && (ev.getX() - mXDown <  mShortestDistance ) && (mXDown - ev.getX() <  mShortestDistance ));
        System.out.println("检测上下滑动" + b+ (ev.getY() - mYDown) +"=="+ (ev.getX() - mXDown) +"=="+ (mXDown - ev.getX()));
        return b;
//        return ((ev.getY() - mYDown > 25) && (ev.getX() - mXDown < 10) && (ev.getX() - mXDown > -10));
    }

    /*【】
     * 根据当前状态，改变界面显示
     */
    public void completePull(){
        topPadding(-headerHeight);
    }

    public static int headerPaddingLeftRight,headerPaddingBottom,headerHeight;
    private void chargeHeaderHeightAndPullStateByMoveSpace(MotionEvent ev){
        TextView tip = (TextView) findViewById(R.id.list_header_text);
        if (!isRemark) { return;}
        int spaceY = (int)ev.getY() - mYDown; // 正数代表向下拉，负数代表向上滑
        int headerTopPadding = spaceY - headerHeight + 25 ;  //达到让 Header 隐藏的效果
        int y = headerTopPadding;
        System.out.println("【spaceY】" + spaceY + "【headerHeight】" + headerHeight);
        if ( (spaceY > 0 && spaceY <= headerHeight/2)){
            pullState = NONE;
//            y = headerTopPadding;
            topPadding(headerTopPadding);
            System.out.println("【Y1】" + y);
//            if ( spaceY <= R.dimen.start_pull * 2 ){ pullState = NONE; }
        } else if ( spaceY > headerHeight/2 && spaceY <= headerHeight*3/4 ){
            pullState = PULL;
//            y = y + (headerTopPadding - y)/2;
            topPadding(headerTopPadding);
            tip.setText("记一笔吧");
            System.out.println("【Y2】" + y);
        } else if ( spaceY > headerHeight*3/4 && spaceY < headerHeight*5/4){  // && scrollState == 1
            pullState = PULL;
//            y = y + (headerTopPadding- y)/4;
            topPadding(headerTopPadding);
            tip.setText("松开可以记账");
            System.out.println("【Y3】" + y);
        }else if ( spaceY <= 0 ){
            pullState = NONE;
            isRemark = false;
        }
    }

    // 设置header布局的上边距
    private void topPadding(int topPadding) {
        header.setPadding( headerPaddingLeftRight, topPadding, headerPaddingLeftRight, headerPaddingBottom);
        header.invalidate();
    }


   // 初始化界面，添加顶部布局至ListView
    public void addHeader(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        header = inflater.inflate(R.layout.activity_billlist_header, null);
//        System.out.println("【】添加头部" + header);
        measureView(header);
        headerHeight = header.getMeasuredHeight();
        headerPaddingBottom = header.getPaddingBottom();
        headerPaddingLeftRight = getPaddingLeft();
        topPadding(-headerHeight);   //如果你注释，头部可以显示出来。padding属性为负数。跟为0是一样的。没意义。
        // 重绘一下
        header.invalidate();
        this.addHeaderView(header);
//        this.setOnScrollListener(this);
    }

    // 通知父布局 header 占用的宽，高
    private void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int width = getChildMeasureSpec(0, 0, p.width);
        int height;
        int tempHeight = p.height;
        if (tempHeight > 0) {
            height = View.MeasureSpec.makeMeasureSpec(tempHeight,
                    View.MeasureSpec.EXACTLY);
        } else {
            height = View.MeasureSpec.makeMeasureSpec(tempHeight,
                    View.MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);
    }



    public void addFooter(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        footer = inflater.inflate(R.layout.activity_billlist_footer, null);
        measureView(footer);
        System.out.println("【footer】" + footer.getMeasuredHeight() + "【header】" + headerHeight + "===" + footer.getVisibility());
        this.addFooterView(footer);
//        footerHeight = footer.getMeasuredHeight();
//        footer.setVisibility(VISIBLE);
//        this.setOnScrollListener(this);
    }

    public void removeFooter() {
        this.removeFooterView(footer);
    }

}
