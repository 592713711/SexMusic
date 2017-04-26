package com.zsg.sexmusic.json;

/**
 * 主播电台 json对象
 * Created by wm on 2016/7/28.
 */
public class RecommendListRadioInfo extends RecommendInfo{


    /**
     * desc :
     * itemid : 13251909
     * title : 失恋是一场重症感冒，但总有痊愈的那一天
     * album_id : 7495592
     * type : lebo
     * channelid : 11373552
     * pic : http://business.cdn.qianqian.com/qianqian/pic/bos_client_4720389bb6d30da36e7b1b08d93bb006.jpg
     */

    private String desc;
    private String itemid;
    private String title;
    private String album_id;
    private String type;
    private String channelid;
    private String pic;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChannelid() {
        return channelid;
    }

    public void setChannelid(String channelid) {
        this.channelid = channelid;
    }

    public String getPic() {
        return pic;
    }

    @Override
    public String toString() {
        return "RecommendListRadioInfo{" +
                "desc='" + desc + '\'' +
                ", itemid='" + itemid + '\'' +
                ", title='" + title + '\'' +
                ", album_id='" + album_id + '\'' +
                ", type='" + type + '\'' +
                ", channelid='" + channelid + '\'' +
                ", pic='" + pic + '\'' +
                '}';
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    @Override
    public String getInfoId() {
        return getAlbum_id();
    }

    @Override
    public String getInfoTitle() {
        return getTitle();
    }

    @Override
    public String getInfoExtra() {
        return getDesc();
    }

    @Override
    public String getInfoPic() {
        return getPic();
    }
}
