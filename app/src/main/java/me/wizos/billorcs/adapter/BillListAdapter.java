//package me.wizos.billorcs.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import java.util.List;
//
//import me.wizos.billorcs.R;
//import me.wizos.billorcs.bean.Bill;
//
///**
// * Created by Wizos on 2016/2/12.
// */
//public class BillListAdapter extends ArrayAdapter<Bill> {
//    public BillListAdapter(Context context ,int textViewResourceId, List<Bill> bills ,int listMode){
//        super(context, textViewResourceId, bills);
//        this.bills = bills;
//        this.context = context;
//        this.listMode = listMode;
//    }
//
//    List<Bill> bills;
//    Context context;
//    int listMode;
//
//    @Override
//    public int getCount() {
//        return bills.size();
//    }
//    @Override
//    public Bill getItem(int position) {
//        return bills.get(position);
//    }
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        CustomViewHolder cvh;
//        if (convertView == null) {
//            cvh = new CustomViewHolder();
//            convertView = LayoutInflater.from(context).inflate(R.layout.activity_billlist_item, null);
//            cvh.imgIcon = (ImageView) convertView.findViewById(R.id.billIcon);
//            cvh.txtName = (TextView) convertView.findViewById(R.id.billName);
//            cvh.txtMoney = (TextView) convertView.findViewById(R.id.billMoney);
//            convertView.setTag(cvh);
//        } else {
//            cvh = (CustomViewHolder) convertView.getTag();
//        }
//        Bill bill = this.getItem(position);
//        cvh.imgIcon.setImageDrawable(bill.getTypeDrawable(bill.getTypeId()));
//        cvh.txtMoney.setText(bill.getMoney());
//
//        if(bill.getRemark().equals("")){ cvh.txtName.setText(bill.getTypeName()); }else { cvh.txtName.setText(bill.getRemark());}
//        return convertView;
//    }
//    class CustomViewHolder {
//        public ImageView imgIcon;
//        public TextView txtName;
//        public TextView txtMoney;
//    }
//
//}
