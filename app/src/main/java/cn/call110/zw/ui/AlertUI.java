package cn.call110.zw.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.call110.zw.R;
import cn.call110.zw.service.PhoneService;

/**
 * Created by Zane on 2016/11/13.
 */

public class AlertUI extends LinearLayout implements View.OnTouchListener {
    private TextView tv;
    public static int x = 0;
    public static int y = 0;
    private int xxStart, xxEnd; // 拖动位置
    private int yyStart, yyEnd; // 拖动位置

    public AlertUI(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.alert_window, this);
        tv = (TextView) findViewById(R.id.tv);
        tv.setOnTouchListener(this);
    }

    /**
     * 设置归属地信息
     *
     * @param text
     */
    public void setText(String text) {
        tv.setText(text);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            xxStart = (int) event.getX();
            yyStart = (int) event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            xxEnd = (int) event.getX();
            yyEnd = (int) event.getY();
//			System.out.println("------->xxEnd=" + xxEnd + " yyEnd=" + yyEnd);
            int offsetX = xxEnd - xxStart;
            int offsetY = yyEnd - yyStart;
            if (offsetX * offsetX + offsetY * offsetY > 1600) {
                x = x + offsetX;
                y = y + offsetY;
                xxStart = xxEnd;
                yyStart = yyEnd;
                PhoneService.refreshLocation();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
        }
        return true;
    }
}