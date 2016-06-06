package me.wizos.billorcs.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 自定义的gridview
 * 绑定在 gridview 上
 */
public class ExpandGridView extends GridView {
    public ExpandGridView(Context context) {
        super(context);
    }

    public ExpandGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

//    @Override
//    public void setOnTouchListener(OnTouchListener l){
//
//    }




////     【】
//    /* 坐标 */
//    private float mXDown;
//    private float mYDown;
//    @Override
//    public boolean onTouchEvent(MotionEvent ev){
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mXDown = ev.getX();
//                mYDown = ev.getY();
//                Log.i("界面2的ACTION_DOWN：", "ACTION_DOWN");
//                break;
//            case MotionEvent.ACTION_MOVE:
////                int viewPaperState = checkViewPaper(ev);
////                changeViewPaper(viewPaperState);
//                break;
//            case MotionEvent.ACTION_UP:
//                Log.i("界面2的ACTION_UP：", "ACTION_UP");
//                break;
//            default:
//                break;
//        }
//        return true;
//    }


//    private int getViewPaperHeight(){
//        View gridview_genre = findViewById(R.id.gridview_genre);
//        int gridGenreHeight = gridview_genre.getHeight();
//        int viewPaperHeight = Global.types.length / 5 * gridGenreHeight;
//        System.out.println("检测viewPager:" + "ttt"+ viewPaperHeight);
//        return viewPaperHeight;
//    }


}
