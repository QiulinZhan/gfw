package cn.call110.zw.model;

/**
 * Created by zhan on 2016/11/17.
 */

public class Banner {
    private Integer imageId;
    private String text;

    public Integer getImageId() {
        return imageId;
    }

    public Banner setImageId(Integer imageId) {
        this.imageId = imageId;
        return this;
    }

    public String getText() {
        return text;
    }

    public Banner setText(String text) {
        this.text = text;
        return this;
    }
}
