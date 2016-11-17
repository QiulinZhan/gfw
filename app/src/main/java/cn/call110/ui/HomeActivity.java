package cn.call110.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.daimajia.swipe.util.Attributes;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.call110.R;
import cn.call110.adapter.HomeAdapter;
import cn.call110.adapter.RecyclerViewAdapter;
import cn.call110.model.Banner;
import cn.call110.model.HomeMenuItem;
import cn.call110.utils.IntentUtils;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;


public class HomeActivity extends AppCompatActivity {
    private GridView mGridView;
    private HomeAdapter mAdapter;
    private List<HomeMenuItem> list;
    private String[] colors = {"#ffae42", "#1fb991", "#fb6d50", "#009ff2"};
    private int[] icons = {R.mipmap.icon_tel, R.mipmap.icon_msm, R.mipmap.icon_query, R.mipmap.icon_advice};
    private ConvenientBanner banner;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter rAdapter;
    private ArrayList<String> mDataSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.title);
        toolbar.setTitle("");
        title.setText("天网");
        setSupportActionBar(toolbar);
        mGridView = (GridView) findViewById(R.id.grid);
        list = new ArrayList();
        String[] menuTitles = {getString(R.string.home_menu1), getString(R.string.home_menu2), getString(R.string.home_menu3), getString(R.string.home_menu6)};
        for(int i = 0; i < menuTitles.length; i ++) {
            list.add(new HomeMenuItem().setIcon(icons[i]).setTitle(menuTitles[i]).setBackgroundColor(colors[i]));
        }
        mAdapter = new HomeAdapter(this, list);
        mGridView.setAdapter(mAdapter);
        toolbar.setOnMenuItemClickListener(e -> {
            if(R.id.setting == e.getItemId()){
                IntentUtils.launch(this, SettingActivity.class);
                return true;
            }
            return false;
        });

        banner = (ConvenientBanner) findViewById(R.id.banner);
        //自定义你的Holder，实现更多复杂的界面，不一定是图片翻页，其他任何控件翻页亦可。
        banner.setPages(
                new CBViewHolderCreator<LocalImageHolderView>() {
                    @Override
                    public LocalImageHolderView createHolder() {
                        return new LocalImageHolderView();
                    }
                }, Arrays.asList(
                        new Banner().setImageId(R.mipmap.banner_1_1).setText("吉林省政府副省长、省委政法委副书记胡家福对反电信诈骗\n发表重要讲话"),
                        new Banner().setImageId(R.mipmap.banner_1_1).setText("fsdfefsf"),
                        new Banner().setImageId(R.mipmap.banner_1_1).setText("12312dssfe")))
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused})
                //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
        //设置翻页的效果，不需要翻页效果可用不设
        //.setPageTransformer(Transformer.DefaultTransformer);    集成特效之后会有白屏现象，新版已经分离，如果要集成特效的例子可以看Demo的点击响应。
//        convenientBanner.setManualPageable(false);//设置不能手动影响
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // Layout Managers:
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Item Decorator:
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setItemAnimator(new FadeInLeftAnimator());
        String[] adapterData = new String[]{"Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"};
        mDataSet = new ArrayList<String>(Arrays.asList(adapterData));
        rAdapter = new RecyclerViewAdapter(this, mDataSet);
        ((RecyclerViewAdapter) rAdapter).setMode(Attributes.Mode.Single);
        recyclerView.setAdapter(rAdapter);

        /* Listeners */
//        recyclerView.setOnScrollListener(onScrollListener);
    }

    public class LocalImageHolderView implements Holder<Banner> {
        private ImageView imageView;
        private TextView tv;
        @Override
        public View createView(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.banner1, null);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            tv = (TextView) view.findViewById(R.id.textView);
            return view;
        }

        @Override
        public void UpdateUI(Context context, final int position, Banner data) {
            imageView.setImageResource(data.getImageId());
            tv.setText(data.getText());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.home_toolbar_menu, menu);
        MenuItem item =  (MenuItem) menu.findItem(R.id.setting);
        item.setIcon(new IconicsDrawable(this).icon(FontAwesome.Icon.faw_cog).color(ContextCompat.getColor(this, R.color.theme_window_background)).sizeDp(15));
        return true;
    }

    public static void launch(Activity activity) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeCustomAnimation(activity,
                        R.anim.head_in, R.anim.head_out);
        Intent intent = new Intent(activity, SettingActivity.class);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        final float scale = getResources().getDisplayMetrics().density;

        float i = (banner.getWidth() / scale + 0.5f);
        // 过滤按键动作
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }
    //    138.128.204.17:8081/compass/p/blackphone?username=admin&password=123321

}
