package cn.call110.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.GridView;

import com.zhy.autolayout.AutoLayoutActivity;

import java.util.ArrayList;
import java.util.List;

import cn.call110.R;
import cn.call110.adapter.HomeAdapter;
import cn.call110.model.HomeMenuItem;
import cn.call110.utils.DataUtils;
import cn.call110.utils.IntentUtils;

public class HomeActivity extends AutoLayoutActivity {
    private GridView mGridView;
    private HomeAdapter mAdapter;
    private List<HomeMenuItem> list;
    private String[] colors = {"#ffae42", "#1fb991", "#fb6d50", "#009ff2"};
    private int[] icons = {R.mipmap.icon_tel, R.mipmap.icon_msm, R.mipmap.icon_query, R.mipmap.icon_advice};
    private WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mGridView = (GridView) findViewById(R.id.grid);
        list = new ArrayList();
        String[] menuTitles = {getString(R.string.home_menu1), getString(R.string.home_menu2), getString(R.string.home_menu3), getString(R.string.home_menu6)};
        for(int i = 0; i < menuTitles.length; i ++) {
            list.add(new HomeMenuItem().setIcon(icons[i]).setTitle(menuTitles[i]).setBackgroundColor(colors[i]));
        }
        mAdapter = new HomeAdapter(this, list);
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener((parent, view, position, id) -> {
            switch (position){
                case 0 :
                    IntentUtils.launch(this, SettingActivity.class);
                    break;
                case 1 :
                    IntentUtils.launch(this, SmsActivity.class);
                    break;
                case 2 :
                    IntentUtils.launch(this, SearchActivity.class);
                    break;
                case 3 :
                    IntentUtils.launch(this, SuggestionActivity.class);
                    break;
                default: break;
            }
        });
        webview = (WebView) findViewById(R.id.webView);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setHorizontalScrollBarEnabled(false);//水平不显示
        webview.setVerticalScrollBarEnabled(false); //垂直不显示
        webview.getSettings().setUseWideViewPort(true);//自适应屏幕
        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.setWebViewClient(new WebViewClient());
        //加载需要显示的网页
        webview.loadUrl("http://221.8.52.247/fdxzpapp/html/index.html");
        DataUtils.initPhoneList();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if(webview.canGoBack()) {
                webview.goBack();
                return true;
            } else {
                moveTaskToBack(true);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }

}
