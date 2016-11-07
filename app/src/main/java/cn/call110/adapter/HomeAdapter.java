package cn.call110.adapter;

import android.widget.Adapter;

/**
 * Created by zhan on 2016/11/7.
 */

public class HomeAdapter extends Adapter {
    private LayoutInflater layoutInflater;
    private List<VideoViewItem> list;
    public VideoListAdapter(Context context, List<VideoViewItem> list) {
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return this.list != null ? this.list.size(): 0 ;
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.video_item, parent, false);
        } else {
            view = convertView;
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.countryImage);
//        TextView textView = (TextView) view.findViewById(R.id.countryName);
        VideoViewItem item = list.get(position);
        view.setTag(item.getImageId());
        //获取自定义的类实例
        Picasso.with(imageView.getContext()).load(item.getImageId()).into(imageView);
        //textView.setText(item.getName());
        return view;
    }
}
