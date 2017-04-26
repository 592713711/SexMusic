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
import com.zsg.sexmusic.json.RecommendListRecommendInfo;
import com.zsg.sexmusic.model.MusicInfo;
import com.zsg.sexmusic.util.L;

import java.util.ArrayList;

import rx.Subscriber;

/**
 * 推荐歌单显示详情
 * Created by zsg on 2017/4/11.
 */

public class RecommentListActivity extends PlayListActivity {
    private RecommendListRecommendInfo info;

    @Override
    public void initData() {
        info = (RecommendListRecommendInfo) getIntent().getSerializableExtra("info");
        //根据id获取歌单
        RetrofitUtil.getInstance().getGeDanInfo(info.getListid(), new Subscriber<GeDanInfoList>() {
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
                for(GeDanGeInfo geDanGeInfo:geDanInfoList.content){
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

        // 显示关注人数
        Bitmap b = BitmapFactory.decodeResource(this.getResources(), R.mipmap.index_icn_earphone);
        ImageSpan imgSpan = new ImageSpan(this, b, ImageSpan.ALIGN_BASELINE);
        SpannableString spanString = new SpannableString("icon");
        spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int count = Integer.parseInt(info.getListenum());
        tv_listencount.setText(spanString);

        if (count > 10000) {
            count = count / 10000;
            tv_listencount.append(" " + count + "万");
        } else {
            tv_listencount.append(" " + count);
        }


    }


}
