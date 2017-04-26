package com.zsg.sexmusic.json;

/**
 * 推荐歌单 json对象
 * Created by wm on 2016/7/28.
 */
public class RecommendListRecommendInfo extends RecommendInfo {


    /**
     * pic : http://business.cdn.qianqian.com/qianqian/pic/bos_client_e0958e5ded6b5eaaf579218077b0d69f.jpg
     * title : 歌声里装了一个世界，大气女声来袭
     * tag : 华语,流行,散步
     * collectnum : 523
     * listid : 6910
     * listenum : 38349
     * type : gedan
     */

    private String pic;
    private String title;
    private String tag;
    private String collectnum;
    private String listid;
    private String listenum;
    private String type;

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCollectnum() {
        return collectnum;
    }

    public void setCollectnum(String collectnum) {
        this.collectnum = collectnum;
    }

    public String getListid() {
        return listid;
    }

    public void setListid(String listid) {
        this.listid = listid;
    }

    public String getListenum() {
        return listenum;
    }

    public void setListenum(String listenum) {
        this.listenum = listenum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "RecommendListRecommendInfo{" +
                "pic='" + pic + '\'' +
                ", title='" + title + '\'' +
                ", tag='" + tag + '\'' +
                ", collectnum='" + collectnum + '\'' +
                ", listid='" + listid + '\'' +
                ", listenum='" + listenum + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public String getInfoId() {
        return getListid();
    }

    @Override
    public String getInfoTitle() {
        return getTitle();
    }

    @Override
    public String getInfoExtra() {
        return getTag();
    }

    @Override
    public String getInfoPic() {
        return getPic();
    }
}
