package com.zsg.sexmusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.zsg.sexmusic.ICallback;
import com.zsg.sexmusic.MusicControlInterface;
import com.zsg.sexmusic.app.MyApplication;
import com.zsg.sexmusic.dao.MusicDao;
import com.zsg.sexmusic.eventbusbeen.UpdatePicMsg;
import com.zsg.sexmusic.http.RetrofitUtil;
import com.zsg.sexmusic.json.LrcPicInfo;
import com.zsg.sexmusic.json.MusicFileDownInfo;
import com.zsg.sexmusic.json.RadioInfoList;
import com.zsg.sexmusic.model.MusicInfo;
import com.zsg.sexmusic.model.PlayState;
import com.zsg.sexmusic.util.ErrorCode;
import com.zsg.sexmusic.util.L;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import line.hee.library.SocketProxyPlay;
import rx.Subscriber;

public class MusicService extends Service {
    private static final String TAG = "MusicService";
    private ArrayList<MusicInfo> musicInfos;        //当前播放的歌单
    private int currentPos = 0;       //当前播放的歌曲位置
    private MultiPlayer multiPlayer;
    private boolean isPlaying = false;
    private MusicDao musicDao;
    private RemoteCallbackList<ICallback> mCallbacks = new RemoteCallbackList<ICallback>();

    public MusicService() {
        musicInfos = new ArrayList<>();
        multiPlayer = new MultiPlayer();
        musicDao = MyApplication.getInstance().getMusicDao();
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBind;
    }


    MusicControlInterface.Stub mBind = new MusicControlInterface.Stub() {
        @Override
        public void openFile(String path) throws RemoteException {

        }

        @Override
        public void open(Map infos, int position) throws RemoteException {

        }


        @Override
        public void stop() throws RemoteException {

        }

        @Override
        public void pause() throws RemoteException {
            L.e("MusicService", "pause");
            multiPlayer.pasue();
            isPlaying = false;
        }

        @Override
        public void playAll(List<MusicInfo> infos, int pos) throws RemoteException {
            musicInfos.clear();
            musicInfos.addAll(infos);
            currentPos = pos;
            start();
        }

        @Override
        public void play(MusicInfo info) throws RemoteException {
            musicInfos.add(info);
            currentPos = musicInfos.size() - 1;
            start();
        }


        @Override
        public void prev(boolean forcePrevious) throws RemoteException {
            //上一首
            doPrev();


        }

        @Override
        public void next() throws RemoteException {
            doNext();
        }

        @Override
        public void setPosition(int pos) throws RemoteException {
            L.e(TAG,"setPosition:"+pos+" currentPos:"+currentPos);
            if(musicInfos.size()>pos&&currentPos!=pos) {
                currentPos = pos;
                start();
            }else if(currentPos!=pos){
                notifyClientError(ErrorCode.PLAY_ERROR);
            }


        }

        @Override
        public long getPlayingSongid() throws RemoteException {
            if(isplaying())
                return musicInfos.get(currentPos).songId;
            return -1;
        }

        @Override
        public boolean isplaying() throws RemoteException {
            return isPlaying;
        }

        @Override
        public void play2() throws RemoteException {
            L.e("MusicService", "play2");
            //判断当前是否有播放过的音乐
            if (musicInfos.size() == 0) {
                notifyClientError(ErrorCode.EMPTY_ERROR);
                return;
            }

            if (multiPlayer.dataSource == null || multiPlayer.dataSource.isEmpty()) {
                start();
            } else {
                multiPlayer.reStart();
            }
        }

        @Override
        public int getCurrent() throws RemoteException {
            return multiPlayer.getCurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return multiPlayer.getDuration();
        }

        @Override
        public int getPosition() throws RemoteException {
            if (musicInfos.size() > 0)
                return currentPos;
            return -1;
        }

        @Override
        public List<MusicInfo> getAllList() throws RemoteException {
            return musicInfos;
        }

        @Override
        public void seekTo(int current) throws RemoteException {
            multiPlayer.seekTo(current);
        }


        @Override
        public PlayState getCurrentState() throws RemoteException {
            if (musicInfos.size() == 0)
                return null;
            PlayState state = new PlayState();
            state.isPlaying = isPlaying;
            state.songid = musicInfos.get(currentPos).songId;
            state.current = getCurrent();
            state.duration = getDuration();
            if (multiPlayer.getDuration() != 0)
                state.currentPregress = (int) (1000 * ((double) multiPlayer.getCurrentPosition() / multiPlayer.getDuration()));
            else
                state.currentPregress = 0;
            return state;
        }

        @Override
        public void unregisterCallback(ICallback cb) {
            if (cb != null) {
                mCallbacks.unregister(cb);
            }
        }

        @Override
        public void registerCallback(ICallback cb) {
            if (cb != null) {
                mCallbacks.register(cb);
            }
        }


    };

    private void doPrev() {
        if (musicInfos.size() == 0) {
            notifyClientError(ErrorCode.EMPTY_ERROR);
            return;
        }

        if (currentPos == 0) {
            notifyClientError(ErrorCode.NOTPRE_ERROR);
            return;
        } else
            currentPos--;
        start();
    }

    private void doNext() {
        L.e(TAG, "doNext");
        if (musicInfos.size() == 0) {
            notifyClientError(ErrorCode.EMPTY_ERROR);
            return;
        }

        if (currentPos == musicInfos.size() - 1)
            currentPos = 0;
        else
            currentPos++;
        start();
    }


    public void start() {
        L.e("MusicService", "start");

        //获取当前歌曲
        final MusicInfo info = musicInfos.get(currentPos);
        isPlaying=true;
        //获取歌曲歌词
        requestLrc(info);

        //判断该歌曲是否缓存
        L.e("MusicService", "缓存:" + musicDao.isCacheNet((int) info.songId));
        if (musicDao.isCacheNet((int) info.songId)) {
            //callback  准备播放
            notifyClientStart(info);
            MusicInfo cacheInfo = musicDao.getMusic((int) info.songId);
            multiPlayer.setDataSource(cacheInfo.netUrl);
            L.e("MusicService", "已缓存:" + cacheInfo);
            return;
        }


        requestNetUrl(info);


    }

    private void requestNetUrl(final MusicInfo info) {
        //没缓存网络播放地址时  获取歌曲地址
        RetrofitUtil.getInstance().getMusicDownInfo(Long.toString(info.songId), new Subscriber<MusicFileDownInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                L.e("MusicPlayer", e.toString());
                notifyClientError(ErrorCode.NET_ERROR);
            }

            @Override
            public void onNext(MusicFileDownInfo downInfo) {
                L.e("MusicPlayer", "json:" + info.songId);
                MusicInfo oldInfo = musicDao.getMusic((int) info.songId);
                if (oldInfo == null)
                    oldInfo = info;

                oldInfo.netUrl = downInfo.getShow_link();
                oldInfo.duration = downInfo.getFile_duration();
                musicDao.updateMusic(oldInfo);
                L.e("MusicService", "加入缓存：" + musicDao.getMusicInfoAll().toString());
                //callback  准备播放
                notifyClientStart(oldInfo);
                multiPlayer.setDataSource(oldInfo.netUrl);

            }
        });
    }

    private void requestLrc(final MusicInfo info) {
        // L.e(TAG,"requestLrc:"+musicDao.getMusic((int) info.songId).toString());
        if (!musicDao.isCacheLrc((int) info.songId)) {
            //缓存歌词
            RetrofitUtil.getInstance().getLrcPic(info.musicName, info.artist, new Subscriber<LrcPicInfo>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(LrcPicInfo lrcPicInfo) {
                    L.e(TAG, "lrcPicInfo:" + lrcPicInfo.toString());
                    if (lrcPicInfo == null)
                        return;
                    MusicInfo oldInfo = musicDao.getMusic((int) info.songId);
                    if (oldInfo == null)
                        oldInfo = info;
                    oldInfo.lrc = lrcPicInfo.lrclink;
                    oldInfo.songPic = lrcPicInfo.avatar_s180;
                    musicDao.updateMusic(oldInfo);
                    EventBus.getDefault().post(new UpdatePicMsg(oldInfo.songId));
                    notifyClientUpdate((int) oldInfo.songId);

                    //获取歌词连接

                }
            });
        }
    }


    public class MultiPlayer implements MediaPlayer.OnErrorListener,
            MediaPlayer.OnCompletionListener {
        public MediaPlayer mediaPlayer;
        public String dataSource;
        public MPState mpState = MPState.IDEL;

        public MultiPlayer() {
            mediaPlayer = new MediaPlayer();
            //初始化 音乐网络缓存目录
            SocketProxyPlay.getInstance().createDefaultSavePath(MyApplication.getInstance());
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnPreparedListener(preparedListener);
        }

        public void setDataSource(String path) {
            stop();
            dataSource = path;
            SocketProxyPlay.getInstance().play(path, mediaPlayer);
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            L.e(TAG, "onCompletion");
            doNext();
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            L.e(TAG, "onError:" + what + " " + extra);
            isPlaying = false;
            notifyClientError(ErrorCode.PLAY_ERROR);
            return false;
        }

        MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                L.e(TAG, "onPrepared");
                mpState = MPState.PREPARED;
                mp.start();
                mpState = MPState.StARED;
                isPlaying = true;
                notifyClientPrepare((int) musicInfos.get(currentPos).songId);
            }
        };

        private void pasue() {
            notifyClientStop();
            mediaPlayer.pause();
            mpState = MPState.PAUSED;
            isPlaying = false;
        }

        private void reStart() {
            L.e("MultiPlayer", "reStart");
            mediaPlayer.start();
            isPlaying = true;
            notifyClientPrepare((int) musicInfos.get(currentPos).songId);
        }

        private void stop() {
            if (mediaPlayer != null) {
                isPlaying = false;
                mediaPlayer.stop();
                mediaPlayer.reset();
                mpState = MPState.IDEL;
            }
        }

        public int getCurrentPosition() {
            //不能在空闲或停止状态时 获取当前位置
            if (mpState != MPState.IDEL && mpState != MPState.STOP)
                return mediaPlayer.getCurrentPosition();
            else
                return 0;
        }

        public int getDuration() {
            if (mpState != MPState.IDEL && mpState != MPState.STOP)
                return mediaPlayer.getDuration();
            else
                return 0;

        }

        public void seekTo(int current) {
            if (mpState != MPState.IDEL && mpState != MPState.STOP)
                mediaPlayer.seekTo(current);
        }
    }


    public void notifyClientStart(MusicInfo info) {

        //回调 所有注册了的回调
        synchronized (mCallbacks) {
            int n = mCallbacks.beginBroadcast();
            try {
                int i;
                for (i = 0; i < n; i++) {
                    mCallbacks.getBroadcastItem(i).onStart((int) info.songId);
                }
            } catch (RemoteException e) {
                L.e(TAG, "remote call exception :" + e.toString());
            } catch (IllegalArgumentException illegalArgumentException) {
                L.e(TAG, "Error while diffusing message to listener:" + illegalArgumentException);
            } finally {
                try {
                    mCallbacks.finishBroadcast();
                    L.e(TAG, this.getClass().getSimpleName() + " finishBroadcast  mRemoteListeners ");
                } catch (IllegalArgumentException illegalArgumentException) {
                    L.e(TAG, "Error while diffusing message to listener  finishBroadcast " + illegalArgumentException);
                }

            }

        }
    }

    public void notifyClientStop() {
        //回调 所有注册了的回调
        synchronized (mCallbacks) {

            int n = mCallbacks.beginBroadcast();
            try {
                int i;
                for (i = 0; i < n; i++) {
                    mCallbacks.getBroadcastItem(i).onStop();
                }
            } catch (RemoteException e) {
                L.e(TAG, "remote call exception :" + e.toString());
            } catch (IllegalArgumentException illegalArgumentException) {
                L.e(TAG, "Error while diffusing message to listener:" + illegalArgumentException);
            } finally {
                try {
                    mCallbacks.finishBroadcast();
                    L.e(TAG, this.getClass().getSimpleName() + " finishBroadcast  mRemoteListeners ");
                } catch (IllegalArgumentException illegalArgumentException) {
                    L.e(TAG, "Error while diffusing message to listener  finishBroadcast " + illegalArgumentException);
                }

            }
        }
    }

    public void notifyClientError(int code) {
        //回调 所有注册了的回调
        synchronized (mCallbacks) {
            int n = mCallbacks.beginBroadcast();
            try {
                int i;
                for (i = 0; i < n; i++) {
                    mCallbacks.getBroadcastItem(i).onError(code);
                }
            } catch (RemoteException e) {
                L.e(TAG, "remote call exception :" + e.toString());
            } catch (IllegalArgumentException illegalArgumentException) {
                L.e(TAG, "Error while diffusing message to listener:" + illegalArgumentException);
            } finally {
                try {
                    mCallbacks.finishBroadcast();
                    L.e(TAG, this.getClass().getSimpleName() + " finishBroadcast  mRemoteListeners ");
                } catch (IllegalArgumentException illegalArgumentException) {
                    L.e(TAG, "Error while diffusing message to listener  finishBroadcast " + illegalArgumentException);
                }

            }
        }
    }

    public void notifyClientPrepare(int songid) {
        //回调 所有注册了的回调
        synchronized (mCallbacks) {
            int n = mCallbacks.beginBroadcast();
            try {
                int i;
                for (i = 0; i < n; i++) {
                    mCallbacks.getBroadcastItem(i).onPrepared(songid);
                }
            } catch (RemoteException e) {
                L.e(TAG, "remote call exception :" + e.toString());
            } catch (IllegalArgumentException illegalArgumentException) {
                L.e(TAG, "Error while diffusing message to listener:" + illegalArgumentException);
            } finally {
                try {
                    mCallbacks.finishBroadcast();
                    L.e(TAG, this.getClass().getSimpleName() + " finishBroadcast  mRemoteListeners ");
                } catch (IllegalArgumentException illegalArgumentException) {
                    L.e(TAG, "Error while diffusing message to listener  finishBroadcast " + illegalArgumentException);
                }

            }
        }
    }


    public void notifyClientUpdate(int songid) {
        //回调 所有注册了的回调
        synchronized (mCallbacks) {
            int n = mCallbacks.beginBroadcast();
            try {
                int i;
                for (i = 0; i < n; i++) {
                    mCallbacks.getBroadcastItem(i).onUpdate(songid);
                }
            } catch (RemoteException e) {
                L.e(TAG, "remote call exception :" + e.toString());
            } catch (IllegalArgumentException illegalArgumentException) {
                L.e(TAG, "Error while diffusing message to listener:" + illegalArgumentException);
            } finally {
                try {
                    mCallbacks.finishBroadcast();
                    L.e(TAG, this.getClass().getSimpleName() + " finishBroadcast  mRemoteListeners ");
                } catch (IllegalArgumentException illegalArgumentException) {
                    L.e(TAG, "Error while diffusing message to listener  finishBroadcast " + illegalArgumentException);
                }

            }
        }
    }

    /*
        MediaPlay 各个状态  参考：http://blog.csdn.net/turkeyzhou/article/details/7714867
     */
    public enum MPState {
        IDEL, PREPARED, STOP, StARED, PAUSED, ERROR
    }


}
