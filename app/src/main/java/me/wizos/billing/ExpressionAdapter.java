package me.wizos.billing;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.wizos.billing.bean.BillType;

public class ExpressionAdapter extends ArrayAdapter<BillType> {

    protected static Context context;
    public ExpressionAdapter(Context context, int textViewResourceId, List<BillType> billTypes) {
        super(context, textViewResourceId, billTypes);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(getContext(), R.layout.gridview_genre, null);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.ivTypeImage = (ImageView) convertView.findViewById(R.id.iv_type_avatar);
        viewHolder.tvTypeName = (TextView) convertView.findViewById(R.id.tv_type_name);
        int resId = getIdOfResource(getItem(position).getTypeId(), "drawable");
        viewHolder.ivTypeImage.setImageResource(resId);
        viewHolder.tvTypeName.setText(getItem(position).getTypeName());
        return convertView;
    }
    public static int getIdOfResource(String name, String type){
        return context.getResources().getIdentifier(name, type, context.getPackageName());
    }

    public class ViewHolder {
        public ImageView ivTypeImage;
        public TextView tvTypeName;
    }

}
