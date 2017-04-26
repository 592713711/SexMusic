package com.zsg.sexmusic.json;

import java.util.ArrayList;

/**
 * Created by zsg on 2017/4/1.
 */

public class RecommendList {
    public Result result;

    @Override
    public String toString() {
        return "RecommendList{" +
                "result=" + result.toString() +
                '}';
    }

    public ArrayList<RecommendListRadioInfo> getRadioList(){
        return result.radio.result;
    }

    public ArrayList<RecommendListNewAlbumInfo> getAlubmList(){
        return result.mix_1.result;
    }

    public ArrayList<RecommendListRecommendInfo> getMusicList(){
        return result.diy.result;
    }
}

class Result{
    public Redio radio;
    public  Album mix_1;
    public Recommend  diy;

    @Override
    public String toString() {
        return "Result{" +
                "radio=" + radio.toString() +
                ", mix_1=" + mix_1.toString() +
                ", diy=" + diy.toString() +
                '}';
    }
}

class Redio{
    public  String error_code;
    public  ArrayList<RecommendListRadioInfo>  result;

    @Override
    public String toString() {
        return "Redio{" +
                "error_code='" + error_code.toString() + '\'' +
                ", result" + result.toString() +
                '}';
    }
}

class Album{
    public  String error_code;
    public  ArrayList<RecommendListNewAlbumInfo>  result;

    @Override
    public String toString() {
        return "Redio{" +
                "error_code='" + error_code.toString() + '\'' +
                ", result" + result.toString() +
                '}';
    }
}

class Recommend{
    public String error_code;
    public ArrayList<RecommendListRecommendInfo>  result;

    @Override
    public String toString() {
        return "Redio{" +
                "error_code='" + error_code.toString() + '\'' +
                ", result" + result.toString() +
                '}';
    }
}
