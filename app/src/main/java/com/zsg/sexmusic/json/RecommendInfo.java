package com.zsg.sexmusic.json;

import java.io.Serializable;

/**
 * Created by zsg on 2017/4/1.
 */

public abstract class RecommendInfo implements Serializable {
    public abstract String getInfoId();

    public abstract String getInfoTitle();

    public abstract String getInfoExtra();     //RecommendInfo->tag    NewAlbum->desc   Radio->

    public abstract String getInfoPic();
}
