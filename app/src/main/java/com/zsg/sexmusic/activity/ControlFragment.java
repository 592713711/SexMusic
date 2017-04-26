package com.zsg.sexmusic.activity;


import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.bilibili.magicasakura.widgets.TintImageView;
import com.bilibili.magicasakura.widgets.TintProgressBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zsg.sexmusic.ICallback;
import com.zsg.sexmusic.MusicPlayer;
import com.zsg.sexmusic.R;
import com.zsg.sexmusic.app.MyApplication;
import com.zsg.sexmusic.model.MusicInfo;
import com.zsg.sexmusic.model.PlayState;
import com.zsg.sexmusic.util.ErrorCode;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import line.hee.library.tools.L;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;

/**
 * 负责控制音乐播放
 * A simple {@link Fragment} subclass.
 */
public class ControlFragment extends Fragment implements View.OnClickListener {
    public static String TAG = "ControlFragment";

    @BindView(R.id.playbar_img)
    public ImageView mAlbumArt;
    @BindView(R.id.song_progress_normal)
    public TintProgressBar mProgress;
    @BindView(R.id.control)
    public TintImageView mPlayPause;
    @BindView(R.id.playbar_info)
    public TextView mTitle;
    @BindView(R.id.playbar_singer)
    public TextView mArtist;
    @BindView(R.id.play_next)
    public ImageView next;
    @BindView(R.id.play_list)
    public ImageView playQueue;

    private MusicPlayer musicPlayer;

    public MusicInfo playMusic;
    private Handler handler;

    public Runnable mUpdateProgress = new Runnable() {

        @Override
        public void run() {
            long position = musicPlayer.getCurrent();
            long duration = musicPlayer.getDuration();
            if (duration > 0 && duration < 627080716) {
                mProgress.setProgress((int) (1000 * position / duration));
            }

            if (musicPlayer.isPlaying()) {
                mProgress.postDelayed(mUpdateProgress, 50);
            } else {
                mProgress.removeCallbacks(mUpdateProgress);
            }

        }
    };

    public static ControlFragment newInstance() {
        return new ControlFragment();
    }

    public ControlFragment() {
        // Required empty public constructor
        musicPlayer = MusicPlayer.getInstance();
        handler = new Handler();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_control, container, false);
        ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    private void initView(View rootView) {

        mProgress.setMax(1000);

        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayPause.setImageResource(musicPlayer.isPlaying() ? R.drawable.playbar_btn_play
                        : R.drawable.playbar_btn_pause);
                mPlayPause.setImageTintList(R.color.theme_color_primary);

                //改变音乐播放状态
                musicPlayer.changeState();
            }
        });

        musicPlayer.registerCallback(mCallback);

        //初始化当前播放的音乐
        PlayState state = musicPlayer.getPlayState();
        if (state != null) {
            playMusic = MyApplication.getInstance().getMusicDao().getMusic((int) state.songid);
            updateUI();
            mPlayPause.setImageResource(state.isPlaying ? R.drawable.playbar_btn_pause
                    : R.drawable.playbar_btn_play);
            mPlayPause.setImageTintList(R.color.theme_color_primary);
            mProgress.setProgress(state.currentPregress);
            if (state.isPlaying)
                mProgress.postDelayed(mUpdateProgress, 0);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //mProgress.setDrawingCacheBackgroundColor(ThemeUtils.getThemeColorStateList(getContext(), R.color.theme_color_primary).getDefaultColor());
            mProgress.setProgressTintList(ThemeUtils.getThemeColorStateList(getContext(), R.color.theme_color_primary));
        }


        next.setOnClickListener(this);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PlayingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });
    }


    public void updateUI() {

        if (playMusic != null) {

            mTitle.setText(playMusic.musicName);

            mArtist.setText(playMusic.artist);

            //  Glide.with(getContext())
            //        .load(playMusic.)
            Glide.with(getContext())
                    .load(playMusic.songPic==null?playMusic.albumData:playMusic.songPic)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder_disk_210)
                    .error(R.drawable.placeholder_disk_210)
                    .into(mAlbumArt);


        }
    }

    ICallback.Stub mCallback = new ICallback.Stub() {
        @Override
        public void onStop() throws RemoteException {
            Log.e(TAG, "onStop");
            mPlayPause.setImageResource(R.drawable.playbar_btn_play);
            mPlayPause.setImageTintList(R.color.theme_color_primary);
            mProgress.removeCallbacks(mUpdateProgress);
        }

        @Override
        public void onStart(int songid) throws RemoteException {
            Log.e(TAG, "onStart:" + songid);
            mPlayPause.setImageResource(R.drawable.playbar_btn_pause);
            mPlayPause.setImageTintList(R.color.theme_color_primary);
            playMusic = MyApplication.getInstance().getMusicDao().getMusic(songid);
            Log.e(TAG, "playMusic:" + playMusic.toString());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    updateUI();
                }
            });
            //  mProgress.postDelayed(mUpdateProgress, 0);    //在未prepared时访问 getDuration 会出错

        }

        @Override
        public void onError(int code) throws RemoteException {
            Log.e(TAG, "onError");
            if (code == ErrorCode.NET_ERROR) {
                Toast.makeText(getContext(), "网络出错，请检查网络设置", Toast.LENGTH_SHORT).show();
            } else if (code == ErrorCode.PLAY_ERROR) {
                Toast.makeText(getContext(), "播放失败！", Toast.LENGTH_SHORT).show();
            } else if (code == ErrorCode.EMPTY_ERROR) {
                Toast.makeText(getContext(), "播放列表为空！", Toast.LENGTH_SHORT).show();
            }
            mPlayPause.setImageResource(R.drawable.playbar_btn_play);
            mPlayPause.setImageTintList(R.color.theme_color_primary);
            mProgress.removeCallbacks(mUpdateProgress);
        }

        @Override
        public void onPrepared(int songid) throws RemoteException {
            //启动timer  获取进度
            //setTimer();
            mProgress.postDelayed(mUpdateProgress, 0);
        }

        @Override
        public void onUpdate(int songid) throws RemoteException {
            Log.e(TAG,"onUpdate:"+playMusic.songId+" "+songid);
            if(playMusic.songId==songid){
                playMusic = MyApplication.getInstance().getMusicDao().getMusic(songid);
                Log.e(TAG,"playMusic:"+playMusic.toString());
                updateUI();
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        musicPlayer.unregisterCallback(mCallback);
        mProgress.removeCallbacks(mUpdateProgress);
    }




    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.play_next) {
            mProgress.removeCallbacks(mUpdateProgress);
            musicPlayer.doNext();
        }
    }
}
