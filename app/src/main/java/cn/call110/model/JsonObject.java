package cn.call110.model;

import java.util.List;

/**
 * Created by Zane on 2016/11/9.
 */

public class JsonObject {
    private String code;
    private List<Phone> white;
    private List<Phone> black;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public List<Phone> getWhite() {
        return white;
    }

    public void setWhite(List<Phone> white) {
        this.white = white;
    }

    public List<Phone> getBlack() {
        return black;
    }

    public void setBlack(List<Phone> black) {
        this.black = black;
    }
}
