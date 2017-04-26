// MusiclInterface.aidl
package com.zsg.sexmusic;
import  com.zsg.sexmusic.model.MusicInfo;
import  com.zsg.sexmusic.model.PlayState;
import com.zsg.sexmusic.ICallback;
// Declare any non-default types here with import statements

interface MusicControlInterface {
     void openFile(String path);
     void open(in Map infos, int position);
     void stop();
     void pause();
     void playAll(in List<MusicInfo> infos,int currentPos);
     void play(in MusicInfo info);
     void prev(boolean forcePrevious);
     void next();
     void setPosition(int pos);     //得到当前正在播放音乐的 位置
     long getPlayingSongid();  //得到当前正在播放音乐的 id
     boolean isplaying();
     void play2();      //单独点击播放
     int getCurrent();
     int getDuration();
     int getPosition();
     List<MusicInfo> getAllList();
     void seekTo(int current);
     PlayState getCurrentState();

    void registerCallback(ICallback cb);
    void unregisterCallback(ICallback cb);
}
