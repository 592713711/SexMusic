package com.zsg.sexmusic.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zsg.sexmusic.ICallback;
import com.zsg.sexmusic.MusicPlayer;
import com.zsg.sexmusic.R;
import com.zsg.sexmusic.app.MyApplication;
import com.zsg.sexmusic.model.MusicInfo;
import com.zsg.sexmusic.model.PlayState;
import com.zsg.sexmusic.util.ErrorCode;
import com.zsg.sexmusic.util.ImageUtils;
import com.zsg.sexmusic.util.L;
import com.zsg.sexmusic.util.TimeUtil;
import com.zsg.sexmusic.view.AlbumViewPager;
import com.zsg.sexmusic.view.PlayerSeekBar;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PlayingActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    public static String TAG = "PlayingActivity";
    @BindView(R.id.albumArt)
    ImageView mBackAlbum;
    @BindView(R.id.playing_mode)
    ImageView mPlayingmode;
    @BindView(R.id.playing_play)
    ImageView play_btn;
    @BindView(R.id.playing_next)
    ImageView mNext;
    @BindView(R.id.playing_pre)
    ImageView mPre;
    @BindView(R.id.playing_playlist)
    ImageView mPlaylist;
    @BindView(R.id.playing_cmt)
    ImageView mCmt;
    @BindView(R.id.playing_fav)
    ImageView mFav;
    @BindView(R.id.playing_down)
    ImageView mDown;
    @BindView(R.id.playing_more)
    ImageView mMore;
    @BindView(R.id.needle)
    ImageView mNeedle;
    @BindView(R.id.music_duration_played)
    TextView mTimePlayed;
    @BindView(R.id.music_duration)
    TextView mDuration;
    @BindView(R.id.play_seek)
    PlayerSeekBar mProgress;
    @BindView(R.id.music_title)
    TextView tv_title;
    @BindView(R.id.music_artist)
    TextView tv_artist;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.view_pager)
    AlbumViewPager albumViewPager;


    private MusicInfo playingMusic;     //当前正在播放的歌曲
    private MusicPlayer musicPlayer;

    private ObjectAnimator mNeedleAnim, mRotateAnim;    //needle 和 rotate动画
    private AnimatorSet mAnimatorSet;

    public Runnable mUpdateProgress = new Runnable() {

        @Override
        public void run() {

            long position = musicPlayer.getCurrent();
            long duration = musicPlayer.getDuration();
            if (duration > 0 && duration < 627080716) {
                mProgress.setProgress((int) (1000 * position / duration));
                mTimePlayed.setText(TimeUtil.makeTimeString(position));
            }
            if (musicPlayer.isPlaying()) {
                mProgress.postDelayed(mUpdateProgress, 50);
            } else {
                mProgress.removeCallbacks(mUpdateProgress);
            }

        }
    };

    public Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);
        ButterKnife.bind(this);

        initData();

    }

    private void initData() {
        musicPlayer = MusicPlayer.getInstance();
        musicPlayer.registerCallback(mCallback);
        mProgress.setMax(1000);
        mProgress.setOnSeekBarChangeListener(this);

        mNeedleAnim = ObjectAnimator.ofFloat(mNeedle, "rotation", -30, 0);
        mNeedleAnim.setDuration(200);
        mNeedleAnim.setRepeatMode(0);
        mNeedleAnim.setInterpolator(new LinearInterpolator());

        updateState();

        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_btn.setImageResource(musicPlayer.isPlaying() ? R.drawable.play_rdi_btn_play
                        : R.drawable.play_rdi_btn_pause);
                //改变音乐播放状态
                musicPlayer.changeState();
            }
        });

        mNext.setOnClickListener(this);
        mPre.setOnClickListener(this);


        initViewPage();

    }

    private void initViewPage() {

        albumViewPager.addOnPageChangeListener(onPageChangeListener);

        albumViewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                L.e(TAG,"transformPage:"+position);
                if (position == 0) {
                    //position == 0 page完全显示  启动动画
                    if (musicPlayer.isPlaying()) {
                        mRotateAnim = (ObjectAnimator) page.getTag(R.id.tag_animator);
                        if (mRotateAnim != null && !mRotateAnim.isRunning() && mNeedleAnim != null) {
                            mAnimatorSet = new AnimatorSet();
                            mAnimatorSet.play(mNeedleAnim).before(mRotateAnim);
                            L.e(TAG," mAnimatorSet.start()1");
                            mAnimatorSet.start();
                        }
                    }

                } else if (position == -1 || position == -2 || position == 1) {

                    //停止旋转
                    mRotateAnim = (ObjectAnimator) page.getTag(R.id.tag_animator);
                    if (mRotateAnim != null) {
                        mRotateAnim.setFloatValues(0);
                        mRotateAnim.end();
                        mRotateAnim = null;
                    }
                } else {
                    //移动的时候暂停 动画 还原needle到初始位置
                    if (mNeedleAnim != null) {
                        //反向
                        mNeedleAnim.reverse();
                        mNeedleAnim.end();
                    }

                    mRotateAnim = (ObjectAnimator) page.getTag(R.id.tag_animator);
                    if (mRotateAnim != null) {
                        mRotateAnim.cancel();
                        float valueAvatar = (float) mRotateAnim.getAnimatedValue();
                        mRotateAnim.setFloatValues(valueAvatar, 360f + valueAvatar);

                    }
                }
            }
        });

        // 改变viewpager动画时间
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            MyScroller mScroller = new MyScroller(albumViewPager.getContext().getApplicationContext(), new LinearInterpolator());
            mField.set(albumViewPager, mScroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private void updateUI() {
        if (playingMusic != null) {
            L.e(TAG,"currentShowPos:"+musicPlayer.getPosition());
            currentShowPos = musicPlayer.getPosition();
            tv_title.setText(playingMusic.musicName);
            tv_artist.setText(playingMusic.artist);

            Observable<Drawable> sender = Observable.create(new Observable.OnSubscribe<Drawable>() {
                @Override
                public void call(Subscriber<? super Drawable> subscriber) {
                    Bitmap myBitmap = null;
                    try {
                        myBitmap = Glide.with(PlayingActivity.this)
                                .load(playingMusic.songPic == null ? playingMusic.albumData : playingMusic.songPic)
                                .asBitmap() //必须
                                .centerCrop()
                                .into(500, 500)
                                .get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    subscriber.onNext(ImageUtils.createBlurredImageFromBitmap(myBitmap, PlayingActivity.this, 8));
                }


            });
            sender.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())// 观察后放在主线程调用
                    .subscribe(new Observer<Drawable>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(Drawable drawable) {
                            TransitionDrawable td = new TransitionDrawable(new Drawable[]{mBackAlbum.getDrawable(), drawable});

                            mBackAlbum.setImageDrawable(td);
                            //去除过度绘制
                            td.setCrossFadeEnabled(true);
                            //200毫秒完成
                            td.startTransition(1000);

                        }
                    });


        }

        //更新viewPage

        albumViewPager.update();
        startAnim();

    }

    public void startAnim() {

        //播放时 开启动画
        if (musicPlayer.isPlaying()) {

            RoundFragment rf = (RoundFragment) albumViewPager.getAdapter().instantiateItem(albumViewPager, albumViewPager.getCurrentItem());
            //L.e(TAG,"startAnim：开启动画1 "+rf.songid+"  "+albumViewPager.getCurrentItem());
            //position == 0 page完全显示  启动动画
            if (musicPlayer.isPlaying() && rf != null && rf.getView() != null) {
                //先停止 用来重置动画  将图片恢复到初始位置
                stopAnim();
                mRotateAnim = (ObjectAnimator) rf.getView().getTag(R.id.tag_animator);
                //mRotateAnim.isRunning()  不在动画的时候执行 避免重复
                if (mRotateAnim != null && !mRotateAnim.isRunning() && mNeedleAnim != null) {
                    mAnimatorSet = new AnimatorSet();
                    mAnimatorSet.play(mNeedleAnim).before(mRotateAnim);
                   // L.e(TAG,"startAnim：开启动画2 "+rf.songid);
                    L.e(TAG," mAnimatorSet.start()2:");
                    mAnimatorSet.start();
                }
            }
        }
    }

    public void stopAnim() {

        RoundFragment rf = (RoundFragment) albumViewPager.getAdapter().instantiateItem(albumViewPager, albumViewPager.getCurrentItem());
        // L.e(TAG,"instantiateItem:"+rf.songid+"  "+rf.animator+"  "+albumViewPager.getCurrentItem());
        //移动的时候暂停 动画 还原needle到初始位置
        if (mNeedleAnim != null) {
            //反向
            mNeedleAnim.reverse();
            mNeedleAnim.end();
        }

        mRotateAnim = (ObjectAnimator) rf.getView().getTag(R.id.tag_animator);
        if (mRotateAnim != null) {
            //通过cancel方法取消的动画，必须使用reset方法或者setAnimation方法重新设置，才可以再次执行动画。
            mRotateAnim.cancel();
            float valueAvatar = (float) mRotateAnim.getAnimatedValue();
            mRotateAnim.setFloatValues(valueAvatar, 360f + valueAvatar);

        }

    }

    //更新音乐的状态
    public void updateState() {
//初始化当前播放的音乐

        PlayState state = musicPlayer.getPlayState();
        // L.e(TAG,"updateState:"+state.toString());
        if (state != null) {
            playingMusic = MyApplication.getInstance().getMusicDao().getMusic((int) state.songid);
            updateUI();
            play_btn.setImageResource(state.isPlaying ? R.drawable.play_rdi_btn_pause
                    : R.drawable.play_rdi_btn_play);
            mDuration.setText(TimeUtil.makeTimeString(state.duration));
            mProgress.setProgress(state.currentPregress);
            if (state.isPlaying)
                mProgress.postDelayed(mUpdateProgress, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicPlayer.unregisterCallback(mCallback);
        mProgress.removeCallbacks(mUpdateProgress);
    }

    ICallback.Stub mCallback = new ICallback.Stub() {
        @Override
        public void onStop() throws RemoteException {
            L.e(TAG,"onStop:");
            play_btn.setImageResource(R.drawable.play_rdi_btn_play);
            stopAnim();
            mProgress.removeCallbacks(mUpdateProgress);
        }

        @Override
        public void onStart(final int songid) throws RemoteException {
            L.e(TAG,"onStart:"+songid);
            play_btn.setImageResource(R.drawable.play_rdi_btn_pause);
            //playingMusic = MyApplication.getInstance().getMusicDao().getMusic(songid);

            updateState();

        }

        @Override
        public void onError(int code) throws RemoteException {
            Log.e(TAG, "onError");
            if (code == ErrorCode.NET_ERROR) {
                Toast.makeText(PlayingActivity.this, "网络出错，请检查网络设置", Toast.LENGTH_SHORT).show();
            } else if (code == ErrorCode.PLAY_ERROR) {
                Toast.makeText(PlayingActivity.this, "播放失败！", Toast.LENGTH_SHORT).show();
            } else if (code == ErrorCode.EMPTY_ERROR) {
                Toast.makeText(PlayingActivity.this, "播放列表为空~", Toast.LENGTH_SHORT).show();
            } else if (code == ErrorCode.NOTPRE_ERROR) {
                Toast.makeText(PlayingActivity.this, "已经是第一首了~", Toast.LENGTH_SHORT).show();
            }
            play_btn.setImageResource(R.drawable.play_rdi_btn_play);
            mProgress.removeCallbacks(mUpdateProgress);
        }

        @Override
        public void onPrepared(int songid) throws RemoteException {
            L.e(TAG, "onPrepared");
            mProgress.postDelayed(mUpdateProgress, 0);
            //start的时候 mediaplay还没有加载好资源 得到的Duration还是上一首的
            mDuration.setText(TimeUtil.makeTimeString(musicPlayer.getDuration()));
            startAnim();
        }

        @Override
        public void onUpdate(int songid) throws RemoteException {
            if (playingMusic.songId == songid) {
                playingMusic = MyApplication.getInstance().getMusicDao().getMusic(songid);
                updateUI();
            }
        }
    };

    public void doBack(View view) {
        onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.playing_next) {
            mProgress.removeCallbacks(mUpdateProgress);
            musicPlayer.doNext();
        } else if (v.getId() == R.id.playing_pre) {
            mProgress.removeCallbacks(mUpdateProgress);
            musicPlayer.doPre();
        }
    }


    /**
     * SeekBar 值改变回调  手动拖动SeekBar fromUser=true  否则fromUser=false
     *
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // L.e(TAG, "onProgressChanged: " + progress + " " + musicPlayer.getDuration());
        progress = progress * musicPlayer.getDuration() / 1000;
        //改变歌词位置
        if (fromUser) {
            //改变播放显示的时间
            mTimePlayed.setText(TimeUtil.makeTimeString(progress));
        }
    }


    //开始拖动
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        L.e(TAG, "onStartTrackingTouch");
        mProgress.removeCallbacks(mUpdateProgress);
    }

    //拖动结束
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        L.e(TAG, "onStopTrackingTouch");
        int current = seekBar.getProgress() * musicPlayer.getDuration() / 1000;
        musicPlayer.seekTo(current);
        if (musicPlayer.isPlaying())
            mProgress.postDelayed(mUpdateProgress, 0);
    }

    int currentShowPos = 0;   //当前显示信息的歌曲位置
    public ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
          //  L.e(TAG, "onPageScrolled:" + position + " " + positionOffset + " " + positionOffsetPixels);
            //positionOffset <0.5 position页面占据主动   positionOffset >0.5 position+1页面占据主动
            if (positionOffset <= 0.5f && position != 0&&position!=albumViewPager.getAdapter().getCount() - 1) {
                //显示positon信息
                if (position - 1 != currentShowPos) {
                    //更换信息
                    MusicInfo showInfo = musicPlayer.getAllList().get(position - 1);
                    tv_title.setText(showInfo.musicName);
                    tv_artist.setText(showInfo.artist);
                    currentShowPos = position - 1;
                }
            } else if (positionOffset > 0.55f && position + 1 != albumViewPager.getAdapter().getCount() - 1) {

                //显示position+1信息
                position = position + 1;
                if (position - 1 != currentShowPos) {
                    //更换信息
                    MusicInfo showInfo = musicPlayer.getAllList().get(position - 1);
                    tv_title.setText(showInfo.musicName);
                    tv_artist.setText(showInfo.artist);
                    currentShowPos = position - 1;
                }
            }
        }


        @Override
        public void onPageSelected(int position) {
            //状态改变完成触发  界面完成切换
            //切换音乐
            L.e(TAG, "onPageSelected:" + position);
            if (position == 0 && albumViewPager.getAdapter().getCount() > 2) {
                //移动到最后一首
                albumViewPager.setCurrentItem(albumViewPager.getAdapter().getCount() - 2,false);
                musicPlayer.setPosition(albumViewPager.getAdapter().getCount() - 3);
                return;
            }
            if (position == albumViewPager.getAdapter().getCount() - 1 && albumViewPager.getAdapter().getCount() > 2) {
                //移动到第一首  不显示动画
                albumViewPager.setCurrentItem(1,false);
                musicPlayer.setPosition(0);
                return;
            }


            if (albumViewPager.getAdapter().getCount() > 2)
                musicPlayer.setPosition(position - 1);

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            L.e(TAG, "onPageScrollStateChanged:" + state);
        }
    };


    //用来控制ViewPage切换的速度
    private static final int VIEWPAGER_SCROLL_TIME = 390;
    public class MyScroller extends Scroller {
        private int animTime = VIEWPAGER_SCROLL_TIME;

        public MyScroller(Context context) {
            super(context);
        }

        public MyScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, animTime);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, animTime);
        }

        public void setmDuration(int animTime) {
            this.animTime = animTime;
        }
    }

}
