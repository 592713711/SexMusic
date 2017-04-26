package com.zsg.sexmusic.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.zsg.sexmusic.R;
import com.zsg.sexmusic.http.RetrofitUtil;
import com.zsg.sexmusic.json.GeDanGeInfo;
import com.zsg.sexmusic.json.GeDanInfoList;
import com.zsg.sexmusic.json.RadioInfo;
import com.zsg.sexmusic.json.RecommendListNewAlbumInfo;
import com.zsg.sexmusic.json.RecommendListRecommendInfo;
import com.zsg.sexmusic.model.MusicInfo;
import com.zsg.sexmusic.util.L;

import java.util.ArrayList;

import rx.Subscriber;

/**
 * Created by zsg on 2017/4/11.
 */

public class NewAlbumListActivity extends PlayListActivity{
    private RecommendListNewAlbumInfo info;

    @Override
    public void initData() {
        info = (RecommendListNewAlbumInfo) getIntent().getSerializableExtra("info");
        //根据id获取歌单
        RetrofitUtil.getInstance().getAlbumInfo(info.getType_id(), new Subscriber<GeDanInfoList>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                L.e("zsg", "getGeDanInfo onError:" + e.toString());
            }

            @Override
            public void onNext(GeDanInfoList geDanInfoList) {
                L.e("zsg", "getGeDanInfo:" + geDanInfoList.toString());
                ArrayList<MusicInfo> musicInfoList=new ArrayList();
                for(GeDanGeInfo geDanGeInfo:geDanInfoList.songlist){
                    MusicInfo musicInfo =new MusicInfo();
                    musicInfo.songId = Integer.parseInt(geDanGeInfo.getSong_id());
                    musicInfo.musicName = geDanGeInfo.getTitle();
                    musicInfo.artist=geDanGeInfo.getAuthor();
                    musicInfo.islocal = false;
                    musicInfo.albumId = info.getInfoId();
                    musicInfo.albumData = info.getPic();
                    musicInfoList.add(musicInfo);
                }

                mAdapter.updateData(musicInfoList);
            }
        });


    }
}
