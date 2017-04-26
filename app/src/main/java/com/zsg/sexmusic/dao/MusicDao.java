package com.zsg.sexmusic.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.zsg.sexmusic.model.MusicInfo;
import com.zsg.sexmusic.util.L;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsg on 2017/4/14.
 */

public class MusicDao {
    DatabaseHelper helper;
    Dao<MusicInfo, Integer> musicDaoOpe  ;
    public MusicDao(Context context) {
        helper = DatabaseHelper.getHelper(context);
        try {
            musicDaoOpe =helper.getMusicDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateMusic(MusicInfo info){
        try {
            if(musicDaoOpe.idExists((int) info.songId)){
                musicDaoOpe.update(info);
            }else
                musicDaoOpe.create(info);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MusicInfo getMusic(int songid){
        MusicInfo info=null;

        try {
            info=musicDaoOpe.queryForId(songid);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return info;
    }

    public List<MusicInfo> getMusicInfoAll(){
        try {
            return musicDaoOpe.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    //是否已经获得过网络地址
    public boolean isCacheNet(int songid){
        try {
            MusicInfo info=musicDaoOpe.queryForId(songid);
            if(info==null||info.netUrl==null||info.netUrl.isEmpty())
                return false;
            else return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isCacheLrc(int songid) {
        try {
            MusicInfo info=musicDaoOpe.queryForId(songid);
            if(info==null||info.lrc==null||info.lrc.isEmpty())
                return false;
            else return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
