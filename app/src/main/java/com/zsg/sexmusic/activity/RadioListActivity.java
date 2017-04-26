package com.zsg.sexmusic.activity;

import com.zsg.sexmusic.http.RetrofitUtil;
import com.zsg.sexmusic.json.GeDanInfoList;
import com.zsg.sexmusic.json.RadioInfo;
import com.zsg.sexmusic.json.RadioInfoList;
import com.zsg.sexmusic.json.RecommendListRadioInfo;
import com.zsg.sexmusic.model.MusicInfo;
import com.zsg.sexmusic.util.L;

import java.util.ArrayList;

import rx.Subscriber;

/**
 * Created by zsg on 2017/4/11.
 */

public class RadioListActivity extends PlayListActivity {
    private RecommendListRadioInfo info;

    @Override
    public void initData() {
        info = (RecommendListRadioInfo) getIntent().getSerializableExtra("info");
        //根据id获取歌单
        RetrofitUtil.getInstance().getRadioInfo(info.getAlbum_id(),10, new Subscriber<RadioInfoList>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                L.e("zsg", "getGeDanInfo onError:" + e.toString());
            }

            @Override
            public void onNext(RadioInfoList radioInfoList) {
                L.e("zsg", "getGeDanInfo:" + radioInfoList.toString());

                ArrayList<MusicInfo> musicInfoList=new ArrayList<MusicInfo>();
                for(RadioInfo radioInfo:radioInfoList.result.latest_song){
                    MusicInfo musicInfo =new MusicInfo();
                    musicInfo.songId = Integer.parseInt(radioInfo.getSong_id());
                    musicInfo.musicName = radioInfo.getSong_name();
                    musicInfo.artist=info.getDesc();
                    musicInfo.islocal = false;
                    musicInfo.albumId = info.getAlbum_id();
                    musicInfo.albumData = info.getPic();
                    musicInfoList.add(musicInfo);
                }

                mAdapter.updateData(musicInfoList);
            }
        });
    }

    public static void sort(int A[],int left,int right){
        if(left<right){
            int temp=A[left];
            int l=left;
            int hight=right;
            while(l<hight){
                while(hight>l&&A[hight]>temp)
                    hight--;

                A[l]=A[hight];
                while(hight>l&&A[l]<temp)
                    l++;
                A[hight]=A[l];
            }

            A[l]=temp;
            sort(A,0,l);
            sort(A,l+1,right);
        }
    }
}
