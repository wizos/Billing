//package me.wizos.billorcs.view;
//
//import android.content.Context;
//import android.support.v4.view.ViewPager;
//import android.util.AttributeSet;
//
///**
// * Created by Wizos on 2016/2/11.
// */
//public class BillViewPaper extends ViewPager {
//    public BillViewPaper(Context context){
//        super(context);
//    }
//    public BillViewPaper(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//    @Override
//    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
//        super.onMeasure(widthMeasureSpec, expandSpec);
//
//    }
////    /*
////    添加viewPager监听器
////     */
////    public ViewPager.OnPageChangeListener getOnPagerChangeListener() {
////        return new ViewPager.OnPageChangeListener() {
////            //            当页面在滑动的时候会调用此方法，在滑动被停止之前，此方法回一直得到调用。其中三个参数的含义分别为：
//////            position:当前页面，及你点击滑动的页面；positionOffset:当前页面偏移的百分比；positionOffsetPixels:当前页面偏移的像素位置
////            @Override
////            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
////                if( !ExpandViewPaper && positionOffsetPixels>0){ expandViewPaper(0);}
////            }
////            // 此方法是页面跳转完后得到调用，position 是你当前选中的页面的位置编号
////            @Override
////            public void onPageSelected(int position) {
////                setCurDial(position);
////                if (position == viewPagerCurrentItem) {
////                    return;
////                }
////                Button payoutTitle = (Button) findViewById(R.id.payout);
////                Button incomeTitle = (Button) findViewById(R.id.income);
////                switch (position) {
////                    case 0:
////                        incomeTitle.setVisibility(View.GONE);
////                        payoutTitle.setVisibility(View.VISIBLE);
////                        break;
////                    case 1:
////                        payoutTitle.setVisibility(View.GONE);
////                        incomeTitle.setVisibility(View.VISIBLE);
////                        break;
////                }
////                viewPagerCurrentItem = position;
////            }
////            @Override
////            public void onPageScrollStateChanged(int state) {
////
////            }
////        };
////    }
//}
