package cn.call110.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.call110.R;
import cn.call110.model.FraudSms;
import io.realm.RealmResults;

/**
 * Created by zhan on 2016/11/7.
 */

public class SmsAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private RealmResults<FraudSms> list;
    private Context context;
    public SmsAdapter(Context context, RealmResults<FraudSms> list) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.phone_item, parent, false);
        }
        TextView phoneNum = (TextView) convertView.findViewById(R.id.phoneNum);
        FraudSms item = list.get(position);
        convertView.setTag(item);
        phoneNum.setText(item.getAddress());
        TextView createTime = (TextView) convertView.findViewById(R.id.createTime);
        createTime.setText(item.getCreateTime());
        return convertView;
    }
}
