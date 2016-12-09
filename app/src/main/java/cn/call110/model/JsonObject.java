package cn.call110.model;

import java.util.List;

/**
 * Created by Zane on 2016/11/9.
 */

public class JsonObject {
    private String code;
    private List<Phone> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Phone> getData() {
        return data;
    }

    public void setData(List<Phone> data) {
        this.data = data;
    }
}
