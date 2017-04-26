package com.zsg.sexmusic.json;

import java.util.ArrayList;

/**
 * Created by zsg on 2017/4/10.
 */

public class GeDanInfoList {
    public String error_code;
    public String title;
    public ArrayList<GeDanGeInfo> content;
    public ArrayList<GeDanGeInfo> songlist;

    @Override
    public String toString() {
        return "GeDanInfoList{" +
                "error_code='" + error_code + '\'' +
                ", title='" + title + '\'' +
                ", content=" + content +
                '}';
    }
}
