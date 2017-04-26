package com.zsg.sexmusic.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.zsg.sexmusic.MusicPlayer;
import com.zsg.sexmusic.R;
import com.zsg.sexmusic.app.MyApplication;
import com.zsg.sexmusic.eventbusbeen.UpdatePicMsg;
import com.zsg.sexmusic.model.MusicInfo;
import com.zsg.sexmusic.util.EventBusUtil;
import com.zsg.sexmusic.util.GlideCircleTransform;
import com.zsg.sexmusic.util.L;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RoundFragment extends Fragment {
    @BindView(R.id.sdv)
    ImageView iv_disk;

    private String disk_path;
    public long songid;
    public ObjectAnimator animator;

    public RoundFragment() {
        EventBus.getDefault().register(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public static RoundFragment newInstance(String albumpath, long songid) {
        RoundFragment fragment = new RoundFragment();
        Bundle bundle = new Bundle();
        bundle.putString("album", albumpath);
        bundle.putLong("songid", songid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        setAnim();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_round, container, false);
        ButterKnife.bind(this, v);
        disk_path = getArguments().getString("album");
        songid = getArguments().getLong("songid", 0l);
        L.e("zsg", "disk_path:" + disk_path);
        initView();
        return v;
    }

    private void initView() {
        Glide.with(getContext())
                .load(disk_path)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.placeholder_disk_play_program)
                .error(R.drawable.placeholder_disk_play_program)
                .transform(new GlideCircleTransform(getContext()))
                .into(iv_disk);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(UpdatePicMsg msg) {
        if (msg.songid == songid) {
            MusicInfo info = MyApplication.getInstance().getMusicDao().getMusic((int) songid);
            disk_path = info.songPic;
            initView();
        }
    }


    public void setAnim() {
        animator = ObjectAnimator.ofFloat(getView(), "rotation", new float[]{0.0F, 360.0F});
        animator.setRepeatCount(Integer.MAX_VALUE);
        animator.setDuration(25000L);
        animator.setInterpolator(new LinearInterpolator());
        if (getView() != null)
            getView().setTag(R.id.tag_animator, this.animator);
    }
}
