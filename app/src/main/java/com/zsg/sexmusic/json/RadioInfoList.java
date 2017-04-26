package com.zsg.sexmusic.json;

import java.util.ArrayList;

/**
 * Created by zsg on 2017/4/11.
 */

public class RadioInfoList {
    public int error_code;
    public Result result;


    public class Result {
        public ArrayList<RadioInfo> latest_song;

        @Override
        public String toString() {
            return "Result{" +
                    "latest_song=" + latest_song +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "RadioInfoList{" +
                "error_code=" + error_code +
                ", result=" + result.toString() +
                '}';
    }
}
