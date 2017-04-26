package com.zsg.sexmusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;

import com.zsg.sexmusic.http.RetrofitUtil;
import com.zsg.sexmusic.json.MusicFileDownInfo;
import com.zsg.sexmusic.model.MusicInfo;
import com.zsg.sexmusic.model.PlayState;
import com.zsg.sexmusic.service.MusicService;
import com.zsg.sexmusic.util.L;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * 用来控制音乐播放的中间类
 * Created by zsg on 2017/4/13.
 */

public class MusicPlayer {
    private MusicControlInterface mService = null;
    private static MusicPlayer mInstance;


    private MusicPlayer() {

    }

    public static MusicPlayer getInstance() {
        if (mInstance == null)
            mInstance = new MusicPlayer();
        return mInstance;
    }

    public void bindService(Context context) {
        context.bindService(new Intent(context, MusicService.class), serviceConnection, context.BIND_AUTO_CREATE);
    }

    public void unbindService(Context context) {
        if (serviceConnection != null)
            context.unbindService(serviceConnection);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = MusicControlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    public void playAllMusic(ArrayList<MusicInfo> data, int i) {
        try {
            mService.playAll(data, i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean isPlaying() {
        boolean b = false;
        try {
            b = mService.isplaying();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return b;
    }

    public void changeState() {
        try {
            if (isPlaying()) {
                mService.pause();
            } else {
                mService.play2();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void registerCallback(ICallback cb) {
        try {
            if (cb != null)
                mService.registerCallback(cb);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void unregisterCallback(ICallback cb) {
        try {
            if (cb != null)
                mService.unregisterCallback(cb);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public PlayState getPlayState() {
        PlayState state = null;
        try {
            state = mService.getCurrentState();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return state;
    }

    public int getCurrent() {
        int current = 0;
        try {
            current = mService.getCurrent();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return current;
    }

    public int getDuration() {
        int duration = 0;
        try {
            duration = mService.getDuration();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return duration;
    }

    public void doNext() {
        try {
            mService.next();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void seekTo(int current) {
        try {
            mService.seekTo(current);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void doPre() {
        try {
            mService.prev(false);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<MusicInfo> getAllList() {
        try {
            return (ArrayList<MusicInfo>) mService.getAllList();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    //获取当前播放歌曲 在播放列表中的位置
    public int getPosition() {
        try {
            return mService.getPosition();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void setPosition(int position) {
        try {
            mService.setPosition(position);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public long getPlayingSongId(){
        try {
            return mService.getPlayingSongid();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
