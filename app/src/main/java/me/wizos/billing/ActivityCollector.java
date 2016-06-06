package me.wizos.billing;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import me.wizos.billing.bean.Bill;

/**
 * Created by Wizos on 2015/12/24.
 * 该类为活动管理器，每个活动创建时都添加到该 list （销毁时便移除），可以实时收集到目前存在的 活动 ，方便要退出该应用时调用 finishAll() 来一次性关闭所有活动
 */
public class ActivityCollector extends Application{
    public static List<Activity> activities = new ArrayList<Activity>();
    private static Context context;
    //    private static ActivityCollector instance;   extends Application


    public static void addActivity(Activity activity){
        context = getContext();
        activities.add(activity);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        ActivityCollector.context = getApplicationContext();
    }
    public static Context getContext(){return context;}

    public static void switchActivity(Context context,String activity,Bill bill){
        if (activity.equals( "ChargeActivity")){
            Intent intent = new Intent(context, ChargeActivity.class);
//            if( Global.chooseDate.equals("") ){Global.chooseDate = UTime.getDate(0)  + " " + UTime.getNow() ;; System.out.println("传递 recordTime 失败");}
//            else if( Global.chooseDate.contains(" ")){ Global.chooseDate = Global.chooseDate  + " " + UTime.getNow() ;   }
//            Global.recordTime = Global.chooseDate +" "+ UTime.getNow();
//            if (!Global.bill.equals(null)){
//                intent.putExtra(Global.isEditing,bill);
//                System.out.println("【editBill(false)】" + Global.isEditing + bill );
//            }
            context.startActivity(intent);
        }
        if (activity.equals( "MainActivity")){
            if(Global.isBilled){removeContext(context);}
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
//            System.out.println("【534534】");
        }
    }

    public static void removeActivity(Activity activity){
        activities.remove(activity);
        activity.finish();
    }
    public static void removeContext(Context context){
        Activity activity = (Activity) context;
        activities.remove(activity);
        activity.finish();
    }


    public static void finishAll(){
        for (Activity activity : activities){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }

}
