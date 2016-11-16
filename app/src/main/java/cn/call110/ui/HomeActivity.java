package cn.call110.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.TextView;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.List;

import cn.call110.R;
import cn.call110.adapter.HomeAdapter;
import cn.call110.model.HomeMenuItem;
import cn.call110.utils.IntentUtils;


public class HomeActivity extends AppCompatActivity {
    private GridView mGridView;
    private HomeAdapter mAdapter;
    private List<HomeMenuItem> list;
    private int[] colors = {R.color.tel, R.color.msm, R.color.query, R.color.advice};
    private int[] icons = {R.mipmap.icon_tel, R.mipmap.icon_msm, R.mipmap.icon_query, R.mipmap.icon_advice};
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
