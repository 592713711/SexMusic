package com.zsg.sexmusic.view;


import android.content.Context;
import android.graphics.PointF;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.zsg.sexmusic.MusicPlayer;
import com.zsg.sexmusic.activity.RoundFragment;
import com.zsg.sexmusic.app.MyApplication;
import com.zsg.sexmusic.dao.MusicDao;
import com.zsg.sexmusic.model.MusicInfo;
import com.zsg.sexmusic.util.L;

import java.util.ArrayList;

public class AlbumViewPager extends ViewPager {
    ViewPagerAdapter adapter;
    Context context;
    MusicDao musicDao;


    public AlbumViewPager(Context context) {
        super(context, null);

    }

    public AlbumViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        datas = MusicPlayer.getInstance().getAllList();
        this.context = context;
        adapter = new ViewPagerAdapter(((AppCompatActivity) context).getSupportFragmentManager());
        this.setAdapter(adapter);

        musicDao = MyApplication.getInstance().getMusicDao();

    }

    public void update() {

        datas = MusicPlayer.getInstance().getAllList();
        int pos = MusicPlayer.getInstance().getPosition();
        setCurrentItem(pos + 1);
    }


    PointF downPoint = new PointF();
    OnSingleTouchListener onSingleTouchListener;
    ArrayList<MusicInfo> datas;


    @Override
    public boolean onTouchEvent(MotionEvent evt) {
        switch (evt.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录按下时候的坐标
                downPoint.x = evt.getX();
                downPoint.y = evt.getY();
                if (this.getChildCount() > 1) { //有内容，多于1个时
                    // 通知其父控件，现在进行的是本控件的操作，不允许拦截
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                // 在up时判断是否按下和松手的坐标为一个点
                if (PointF.length(evt.getX() - downPoint.x, evt.getY()
                        - downPoint.y) < (float) 5.0) {
                    onSingleTouch(this);
                    return true;
                }
                break;
        }
        return super.onTouchEvent(evt);
    }

    public void onSingleTouch(View v) {
        if (onSingleTouchListener != null) {
            onSingleTouchListener.onSingleTouch(v);
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public RoundFragment getItem(int position) {
            if (position == 0 || position == datas.size() + 1)
                return RoundFragment.newInstance("", 0);

            MusicInfo info = musicDao.getMusic((int) datas.get(position - 1).songId);
            if (info == null)
                return RoundFragment.newInstance(datas.get(position - 1).albumData, datas.get(position - 1).songId);

            else if (info.songPic == null || info.songPic.isEmpty())
                return RoundFragment.newInstance(info.albumData, info.songId);

            return RoundFragment.newInstance(info.songPic, info.songId);


        }

        @Override
        public int getCount() {
            return datas.size() + 2;
        }

    }

    public interface OnSingleTouchListener {
        public void onSingleTouch(View v);
    }

    public void setOnSingleTouchListener(
            OnSingleTouchListener onSingleTouchListener) {
        this.onSingleTouchListener = onSingleTouchListener;
    }
}