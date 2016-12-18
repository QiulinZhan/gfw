package cn.call110.zw.model;

/**
 * Created by Zane on 2016/11/7.
 */

public class HomeMenuItem implements Comparable<Object> {
    private String title;
    private int icon;
    private String backgroundColor;
    public String getTitle() {
        return title;
    }

    public HomeMenuItem setTitle(String title) {
        this.title = title;
        return this;
    }

    public int getIcon() {
        return icon;
    }

    public HomeMenuItem setIcon(int icon) {
        this.icon = icon;
        return this;
    }

    @Override
    public int compareTo(Object o) {
        HomeMenuItem f = (HomeMenuItem) o;
        return getTitle().compareTo(f.getTitle());
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public HomeMenuItem setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }
}
