package com.zsg.sexmusic.eventbusbeen;

/**
 * Created by zsg on 2017/4/20.
 */

public class UpdatePicMsg extends Message{
    public long songid;

    public UpdatePicMsg(long songId) {
        this.songid=songId;
    }
}
