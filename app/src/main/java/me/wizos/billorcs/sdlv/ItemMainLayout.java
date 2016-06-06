package me.wizos.billorcs.sdlv;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.Scroller;

import me.wizos.billorcs.Global;

/**
 * Created by yuyidong on 15/9/24.
 */
class ItemMainLayout extends FrameLayout {
    private static final int INTENTION_LEFT_OPEN = 1;
    private static final int INTENTION_LEFT_CLOSE = 2;
    private static final int INTENTION_LEFT_ALREADY_OPEN = 3;
    private static final int INTENTION_RIGHT_OPEN = -1;
    private static final int INTENTION_RIGHT_CLOSE = -2;
    private static final int INTENTION_RIGHT_ALREADY_OPEN = -3;
    private static final int INTENTION_SCROLL_BACK = -4;
    private static final int INTENTION_ZERO = 0;
    private int mIntention = INTENTION_ZERO;

    /* 判断当前是否滑出，若为滑出，则是SCROLL_STATE_OPEN，则过度的滑动都不会去触发slideOpen接口，同理SCROLL_STATE_CLOSE */
    private static final int SCROLL_STATE_OPEN = 1;
    private static final int SCROLL_STATE_CLOSE = 0;
    private int mScrollState = SCROLL_STATE_CLOSE;
    /* 时间 */
    private static final int SCROLL_TIME = 500;//500ms
    private static final int SCROLL_BACK = 250;//250MS
    private static final int SCROLL_DELETE_TIME = 300;//300ms
    /* 控件高度 */
    private int mHeight;
    /* 删除的时候高度的变换 */
    private int mDeleteHeight = DEFAULT_DELETE_HEIGHT;
    private static final int DEFAULT_DELETE_HEIGHT = -4399;
    /* 子控件中button的总宽度 */
    private int mBtnLeftTotalWidth;
    private int mBtnRightTotalWidth;
    /* 子view */
    private ItemBackGroundLayout mItemLeftBackGroundLayout;
    private ItemBackGroundLayout mItemRightBackGroundLayout;
    private ItemCustomLayout mItemCustomLayout;
    /* Scroller */
    private Scroller mScroller;
    /* 控件是否滑动 */
    private boolean mIsMoving = false;
    /* 是不是要滑过(over) */
    private boolean mWannaOver = true;
    /* 坐标 */
    private float mXDown;
    private float mYDown;
    /* 最小滑动距离，超过了，才认为开始滑动 */
    private int mTouchSlop = 0;
    /* X方向滑动距离 */
    private float mLeftDistance;
    /* 滑动的监听器 */
    private OnItemSlideListenerProxy mOnItemSlideListenerProxy;

    public ItemMainLayout(Context context) {
        this(context, null);
    }

    public ItemMainLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    //【
    private Context context;
    //】
    public ItemMainLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
        mItemRightBackGroundLayout = new ItemBackGroundLayout(context);
        addView(mItemRightBackGroundLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mItemLeftBackGroundLayout = new ItemBackGroundLayout(context);
        addView(mItemLeftBackGroundLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mItemCustomLayout = new ItemCustomLayout(context);
        addView(mItemCustomLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        // getScaledTouchSlop是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件。如果小于这个距离就不触发移动控件，如viewpager就是用这个距离来判断用户是否翻页
    }

    /**
     * 得到CustomView
     *
     * @return
     */
    public ItemCustomLayout getItemCustomLayout() {
        return mItemCustomLayout;
    }

    /**
     * 得到左边的背景View
     *
     * @return
     */
    public ItemBackGroundLayout getItemLeftBackGroundLayout() {
        return mItemLeftBackGroundLayout;
    }

    /**
     * 得到右边的背景View
     *
     * @return
     */
    public ItemBackGroundLayout getItemRightBackGroundLayout() {
        return mItemRightBackGroundLayout;
    }

    /**
     * @param btnLeftTotalWidth
     * @param btnRightTotalWidth
     * @param wannaOver
     */
    public void setParams(int btnLeftTotalWidth, int btnRightTotalWidth, boolean wannaOver) {
        requestLayout();
        mBtnLeftTotalWidth = btnLeftTotalWidth;
        mBtnRightTotalWidth = btnRightTotalWidth;
        mWannaOver = wannaOver;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mDeleteHeight == DEFAULT_DELETE_HEIGHT || mDeleteHeight < 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            mHeight = getMeasuredHeight();
            for (int i = 0; i < getChildCount(); i++) {
                measureChild(getChildAt(i), widthMeasureSpec, MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY));
            }
        } else if (mDeleteHeight >= 0) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mDeleteHeight);
            for (int i = 0; i < getChildCount(); i++) {
                measureChild(getChildAt(i), widthMeasureSpec, MeasureSpec.makeMeasureSpec(mDeleteHeight, MeasureSpec.EXACTLY));
            }
        }

    }
    //【
    protected int x;
     //】

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(false);
		/*【】
        // ACTION_DOWN=0 和 ACTION_UP=1 就是单点触摸屏幕，按下去和放开的操作
        // ACTION_POINTER_DOWN和ACTION_POINTER_UP就是多点触摸屏幕，当有一只手指按下去的时候，另一只手指按下和放开的动作捕捉；
        // ACTION_MOVE=2 就是手指在屏幕上移动的操作；
		*/
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                System.out.println("【】+++  ACTION_DOWN" + ev.getAction());
                mXDown = ev.getX();
                mYDown = ev.getY();
                //控件初始距离
                /* X方向滑动距离 */
                mLeftDistance = mItemCustomLayout.getLeft(); // 获取view的左坐标
                //是否有要scroll的动向，目前没有
                mIsMoving = false;
//                Log.i("onTouchEvent事件：", "ACTION_DOWN。X = " + x);
                break;
            case MotionEvent.ACTION_MOVE:
			     // 【
                x = x - 1; 
				// 】
                // && 条件与，两个都必须为 true 才返回 true
                if (fingerNotMove(ev) && !mIsMoving) { //手指的范围在50以内，算作没有移动
                    //执行ListView的手势操作
                    getParent().requestDisallowInterceptTouchEvent(false);
					/*【】
					//getParent().requestDisallowInterceptTouchEvent(true);方法。一旦底层View收到touch的action后调用这个方法那么父层View就不会再调用onInterceptTouchEvent了，也无法截获以后的action。
                    // 在系统发出的一个完整手势通知中的每一次变化通知(按下，移动，离开)，都会从 父层向子层 传递，中间可以被 viewGroup 拦截。
                    // getParent().requestDisallowInterceptTouchEvent(true) 后，在之后的手势变化通知中其父层就无法拦截了。
					*/
                } else if (  Global.canSdlvMenu && fingerLeftAndRightMove(ev) || mIsMoving) { //上下范围在50，主要检测左右滑动
                    //是否有要scroll的动向
                    mIsMoving = true;
                    //执行控件的手势操作
                    getParent().requestDisallowInterceptTouchEvent(true);
                    float moveDistance = ev.getX() - mXDown;//这个往右是正，往左是负
                    //判断意图
                    if (moveDistance > 0) {//往右
                        if (mLeftDistance == 0) {//关闭状态
                            mIntention = INTENTION_LEFT_OPEN;
                            setBackGroundVisible(true, false);
                        } else if (mLeftDistance < 0) {//右边的btn显示出来的
                            mIntention = INTENTION_RIGHT_CLOSE;
                        } else if (mLeftDistance > 0) {//左边的btn显示出来的
                            mIntention = INTENTION_LEFT_ALREADY_OPEN;
                        }
                    } else if (moveDistance < 0) {//往左
                        if (mLeftDistance == 0) {//关闭状态
                            mIntention = INTENTION_RIGHT_OPEN;
                            setBackGroundVisible(false, true);
                        } else if (mLeftDistance < 0) {//右边的btn显示出来的
                            mIntention = INTENTION_RIGHT_ALREADY_OPEN;
                        } else if (mLeftDistance > 0) {//左边的btn显示出来的
                            mIntention = INTENTION_LEFT_CLOSE;
                        }
                    }
                    //计算出距离
                    switch (mIntention) {
                        case INTENTION_LEFT_OPEN:
                        case INTENTION_LEFT_ALREADY_OPEN:
                            //此时moveDistance为正数，mLeftDistance为0
                            float distanceLeftOpen = mLeftDistance + moveDistance;
                            if (!mWannaOver) {
                                distanceLeftOpen = distanceLeftOpen > mBtnLeftTotalWidth ? mBtnLeftTotalWidth : distanceLeftOpen;
                            }
                            //滑动
                            mItemCustomLayout.layout((int) distanceLeftOpen, mItemCustomLayout.getTop(),
                                    mItemCustomLayout.getWidth() + (int) distanceLeftOpen, mItemCustomLayout.getBottom());
                            break;
                        case INTENTION_LEFT_CLOSE:
                            //此时moveDistance为负数，mLeftDistance为正数
                            float distanceLeftClose = mLeftDistance + moveDistance < 0 ? 0 : mLeftDistance + moveDistance;
                            //滑动
                            mItemCustomLayout.layout((int) distanceLeftClose, mItemCustomLayout.getTop(),
                                    mItemCustomLayout.getWidth() + (int) distanceLeftClose, mItemCustomLayout.getBottom());
                            break;
                        case INTENTION_RIGHT_OPEN:
                        case INTENTION_RIGHT_ALREADY_OPEN:
                            //此时moveDistance为负数，mLeftDistance为0
                            float distanceRightOpen = mLeftDistance + moveDistance;
                            //distanceRightOpen为正数
                            if (!mWannaOver) {
                                distanceRightOpen = -distanceRightOpen > mBtnRightTotalWidth ? -mBtnRightTotalWidth : distanceRightOpen;
                            }
                            //滑动
                            mItemCustomLayout.layout((int) distanceRightOpen, mItemCustomLayout.getTop(),
                                    mItemCustomLayout.getWidth() + (int) distanceRightOpen, mItemCustomLayout.getBottom());
                            break;
                        case INTENTION_RIGHT_CLOSE:
                            //此时moveDistance为正数，mLeftDistance为负数
                            float distanceRightClose = mLeftDistance + moveDistance > 0 ? 0 : mLeftDistance + moveDistance;
                            //滑动
                            mItemCustomLayout.layout((int) distanceRightClose, mItemCustomLayout.getTop(),
                                    mItemCustomLayout.getWidth() + (int) distanceRightClose, mItemCustomLayout.getBottom());

                            break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 下面这段是让菜单项在滑出的距离比较小的时候弹回的效果，上面 ACTION_UP 不能加 break; 不然这段失效
                System.out.println("【----ACTION_CANCEL】");
                switch (mIntention) {
                    case INTENTION_LEFT_CLOSE:
                    case INTENTION_LEFT_OPEN:
                    case INTENTION_LEFT_ALREADY_OPEN:
                        //如果滑出的话，那么就滑到固定位置(只要滑出了 mBtnLeftTotalWidth / 2 ，就算滑出去了)
                        if (Math.abs(mItemCustomLayout.getLeft()) > mBtnLeftTotalWidth / 2) {
                            //滑出
                            mIntention = INTENTION_LEFT_OPEN;
                            int delta = mBtnLeftTotalWidth - Math.abs(mItemCustomLayout.getLeft());
                            mScroller.startScroll(mItemCustomLayout.getLeft(), 0, delta, 0, SCROLL_TIME);
                            if (mOnItemSlideListenerProxy != null && mScrollState != SCROLL_STATE_OPEN) {
                                mOnItemSlideListenerProxy.onSlideOpen(this, MenuItem.DIRECTION_LEFT);
                            }
                            mScrollState = SCROLL_STATE_OPEN;
                        } else {
                            mIntention = INTENTION_LEFT_CLOSE;
                            //滑回去,归位
                            mScroller.startScroll(mItemCustomLayout.getLeft(), 0, -mItemCustomLayout.getLeft(), 0, SCROLL_TIME);
                            if (mOnItemSlideListenerProxy != null && mScrollState != SCROLL_STATE_CLOSE) {
                                mOnItemSlideListenerProxy.onSlideClose(this, MenuItem.DIRECTION_LEFT);
                            }
                            mScrollState = SCROLL_STATE_CLOSE;
                        }
                        break;
                    case INTENTION_RIGHT_CLOSE:
                    case INTENTION_RIGHT_OPEN:
                    case INTENTION_RIGHT_ALREADY_OPEN:
                        if (Math.abs(mItemCustomLayout.getLeft()) > mBtnRightTotalWidth / 2) {
                            //滑出
                            mIntention = INTENTION_RIGHT_OPEN;
                            int delta = mBtnRightTotalWidth - Math.abs(mItemCustomLayout.getLeft());
                            mScroller.startScroll(mItemCustomLayout.getLeft(), 0, -delta, 0, SCROLL_TIME);
                            if (mOnItemSlideListenerProxy != null && mScrollState != SCROLL_STATE_OPEN) {
                                mOnItemSlideListenerProxy.onSlideOpen(this, MenuItem.DIRECTION_RIGHT);
                            }
                            mScrollState = SCROLL_STATE_OPEN;
                        } else {
                            mIntention = INTENTION_RIGHT_CLOSE;
                            mScroller.startScroll(mItemCustomLayout.getLeft(), 0, -mItemCustomLayout.getLeft(), 0, SCROLL_TIME);
                            //滑回去,归位
                            if (mOnItemSlideListenerProxy != null && mScrollState != SCROLL_STATE_CLOSE) {
                                mOnItemSlideListenerProxy.onSlideClose(this, MenuItem.DIRECTION_RIGHT);
                            }
                            mScrollState = SCROLL_STATE_CLOSE;
                        }
                        break;
                }
                mIntention = INTENTION_ZERO;
                postInvalidate();
                mIsMoving = false;
                break;
            default:
                break;
        }
//        System.out.println("【】+++" + ev.getAction());
        return true;
    }

    /**
     * 设置哪边显示哪边不显示
     *
     * @param leftVisible
     * @param rightVisible
     */
    private void setBackGroundVisible(boolean leftVisible, boolean rightVisible) {
        if (leftVisible) {
            if (mItemLeftBackGroundLayout.getVisibility() != VISIBLE) {
                mItemLeftBackGroundLayout.setVisibility(VISIBLE);
            }
        } else {
            if (mItemLeftBackGroundLayout.getVisibility() == VISIBLE) {
                mItemLeftBackGroundLayout.setVisibility(GONE);
            }
        }
        if (rightVisible) {
            if (mItemRightBackGroundLayout.getVisibility() != VISIBLE) {
                mItemRightBackGroundLayout.setVisibility(VISIBLE);
            }
        } else {
            if (mItemRightBackGroundLayout.getVisibility() == VISIBLE) {
                mItemRightBackGroundLayout.setVisibility(GONE);
            }
        }
    }

    /**
     * 上下左右不能超出50
     *
     * @param ev
     * @return
     */
    private boolean fingerNotMove(MotionEvent ev) {
        return (mXDown - ev.getX() < mTouchSlop && mXDown - ev.getX() > -mTouchSlop &&
                mYDown - ev.getY() < mTouchSlop && mYDown - ev.getY() > -mTouchSlop);
    }

    /**
     * 左右得超出50，上下不能超出50
     *
     * @param ev
     * @return
     */
    private boolean fingerLeftAndRightMove(MotionEvent ev) {
        return ((ev.getX() - mXDown > mTouchSlop || ev.getX() - mXDown < -mTouchSlop) &&
                ev.getY() - mYDown < mTouchSlop && ev.getY() - mYDown > -mTouchSlop);
    }

    // 【
    private boolean fingerUpAndDownMove(MotionEvent ev) {
        return ((mXDown - ev.getX() < mTouchSlop && mXDown - ev.getX() > -mTouchSlop) &&
                ev.getY() - mYDown > mTouchSlop && ev.getY() - mYDown < -mTouchSlop);
    }
    //】

    /**
     * 删除Item
     */
    public void deleteItem(final OnItemDeleteListenerProxy onItemDeleteListenerProxy) {
        scrollBack();
        mDeleteHeight = mHeight;
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDeleteHeight = DEFAULT_DELETE_HEIGHT;
                ItemMainLayout.this.requestLayout();
                ItemMainLayout.this.getItemCustomLayout().refreshBackground();
                if (onItemDeleteListenerProxy != null) {
                    onItemDeleteListenerProxy.onDelete(ItemMainLayout.this);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1.0f) {
                    mDeleteHeight = mHeight;
                } else {
                    mDeleteHeight = mHeight - (int) (mHeight * interpolatedTime);
                }
                ItemMainLayout.this.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setAnimationListener(animationListener);
        animation.setDuration(SCROLL_DELETE_TIME);
        startAnimation(animation);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mItemCustomLayout.layout(mScroller.getCurrX(), mItemCustomLayout.getTop(),
                    mScroller.getCurrX() + mItemCustomLayout.getWidth(), mItemCustomLayout.getBottom());
            postInvalidate();
        }
        super.computeScroll();
    }

    /**
     * 归位
     */
    protected void scrollBack() {
        mIntention = INTENTION_SCROLL_BACK;
        mScroller.startScroll(mItemCustomLayout.getLeft(), 0, -mItemCustomLayout.getLeft(), 0, SCROLL_BACK);
        postInvalidate();
        mScrollState = SCROLL_STATE_CLOSE;
    }

    /**
     * @param x
     * @return 是不是滑动了
     */
    protected boolean scrollBack(float x) {
        if (mItemCustomLayout.getLeft() > 0) {
            //已经向右滑动了
            if (x > mItemCustomLayout.getLeft()) {
                //没有点击到menu的button
                scrollBack();
                mScrollState = SCROLL_STATE_CLOSE;
                return true;
            }
        } else if (mItemCustomLayout.getLeft() < 0) {
            //已经向左滑动了
            if (x < mItemCustomLayout.getRight()) {
                //没有点击到menu的button
                scrollBack();
                mScrollState = SCROLL_STATE_CLOSE;
                return true;
            }
        }
        return false;
    }

    /**
     * 设置item滑动的监听器
     *
     * @param onItemSlideListenerProxy
     */
    protected void setOnItemSlideListenerProxy(OnItemSlideListenerProxy onItemSlideListenerProxy) {
        mOnItemSlideListenerProxy = onItemSlideListenerProxy;
    }

    protected interface OnItemSlideListenerProxy {
        void onSlideOpen(View view, int direction);

        void onSlideClose(View view, int direction);
    }

    protected interface OnItemDeleteListenerProxy {
        void onDelete(View view);
    }
}
