package cn.call110.model;

/**
 * Created by Zane on 2016/11/7.
 */

public class HomeMenuItem implements Comparable<Object> {
    private String title;
    private int icon;
    private int backgroundColor;
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

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public HomeMenuItem setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }
}
