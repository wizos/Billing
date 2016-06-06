package me.wizos.billing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.wizos.billing.Global;
import me.wizos.billing.R;
import me.wizos.billing.bean.KeyValue;

/**
 * Created by Wizos on 2016/2/12.
 */
public class ListAdapter extends ArrayAdapter<KeyValue> {
    public ListAdapter(Context context, int textViewResourceId, List<KeyValue> keyvalues, int listMode){
        super(context, textViewResourceId, keyvalues);
        this.keyvalues = keyvalues;
        this.context = context;
        this.listMode = listMode;
    }

    List<KeyValue> keyvalues;
    Context context;
    int listMode;

    @Override
    public int getCount() {
        return keyvalues.size();
    }
    @Override
    public KeyValue getItem(int position) {
        return keyvalues.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomViewHolder cvh;
        if (convertView == null) {
            cvh = new CustomViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_billlist_item, null);
            cvh.imgIcon = (ImageView) convertView.findViewById(R.id.billIcon);
            cvh.txtName = (TextView) convertView.findViewById(R.id.billName);
            cvh.txtMoney = (TextView) convertView.findViewById(R.id.billMoney);
            convertView.setTag(cvh);
        } else {
            cvh = (CustomViewHolder) convertView.getTag();
        }
        KeyValue keyvalue = this.getItem(position);
        cvh.txtMoney.setText(String.valueOf(keyvalue.getValue()));

//        System.out.println("【】"+keyvalue.getKey());
        cvh.imgIcon.setImageDrawable(keyvalue.getDrawable(keyvalue.getKey()));
        String[] num = keyvalue.getKey().split("_");
        String num0 = num[0];
        int num1 = Integer.valueOf(num[1]);
        if (num0.equals("account")){ cvh.txtName.setText(Global.account[num1]);
        }else if (num0.equals("payout")){cvh.txtName.setText(Global.typesPayout[num1]);
        }else if (num0.equals("income")){cvh.txtName.setText(Global.typesIncome[num1]);}
        return convertView;
    }
    class CustomViewHolder {
        public ImageView imgIcon;
        public TextView txtName;
        public TextView txtMoney;
    }

}
