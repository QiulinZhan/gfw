package cn.call110.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.shapes.Shape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import cn.call110.R;
import cn.call110.model.HomeMenuItem;

/**
 * Created by zhan on 2016/11/7.
 */

public class HomeAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<HomeMenuItem> list;
    private Context context;
    public HomeAdapter(Context context, List<HomeMenuItem> list) {
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
        View view;
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.home_item, parent, false);
        } else {
            view = convertView;
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        TextView textView = (TextView) view.findViewById(R.id.title);
        HomeMenuItem item = list.get(position);
        view.setTag(item);
        Picasso.with(context).load(item.getIcon()).into(imageView);
        textView.setText(item.getTitle());
        GradientDrawable gd = (GradientDrawable) view.getBackground();
        gd.setColor(Color.parseColor(item.getBackgroundColor()));
        return view;
    }
}
