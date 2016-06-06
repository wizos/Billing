/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.wizos.billorcs;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import java.util.List;

public class ExpressionPagerAdapter extends PagerAdapter {

    private List<View> views;
    private int size;// 页数

    public ExpressionPagerAdapter(List<View> views) {
        this.views = views;
        size = views == null ? 0 : views.size();
    }
    public void setListViews(List<View> views) {// 自己写的一个方法用来添加数据  这个可是重点啊
        this.views = views;
        size = views == null ? 0 : views.size();
    }

    @Override
    public int getCount() {
        return views.size();
    }

    /**
     * 判断出去的view是否等于进来的view 如果为true直接复用
     */
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    /**
     * 创建一个view
     */
//    @Override
//    public Object instantiateItem(View arg0, int arg1) {
//        ((ViewPager) arg0).addView(views.get(arg1));
//        return views.get(arg1);
//    }
    @Override
    public Object instantiateItem(View arg0, int arg1) {// 返回view对象
        try {
            ((ViewPager) arg0).addView(views.get(arg1 % size), 0);
        } catch (Exception e) {
            Log.e("zhou", "exception：" + e.getMessage());
        }
        return views.get(arg1 % size);
    }

    /**
     * 销毁预加载以外的view对象, 会把需要销毁的对象的索引位置传进来就是position
     */
    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView(views.get(arg1 % size));
    }

}
