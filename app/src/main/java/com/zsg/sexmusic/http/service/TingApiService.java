package com.zsg.sexmusic.http.service;

import com.zsg.sexmusic.json.FocusItemInfoList;
import com.zsg.sexmusic.json.GeDanInfoList;
import com.zsg.sexmusic.json.LrcPicInfo;
import com.zsg.sexmusic.json.MusicFileDownInfo;
import com.zsg.sexmusic.json.RadioInfoList;
import com.zsg.sexmusic.json.RecommendList;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by zsg on 2017/4/5.
 */

public interface TingApiService {
    public static String API_TING = "http://tingapi.ting.baidu.com/v1/restserver/";

    /**
     * Get后 不能用?t=1&p=2&size=3的url链接  应该用@Query
     */
    @GET("ting")
    Observable<FocusItemInfoList> getfocusPic(@Query("num") int num,@Query("method") String method);

    @GET("ting")
    Observable<RecommendList> getRecommendList(@QueryMap Map <String, String> options);


    /**
     * 推荐歌单信息和歌曲
     *
     * @param listid 歌单id
     * @return
     */
    @GET("ting")
    Observable<GeDanInfoList> getGeDanInfo(@Query("listid") String listid, @Query("method") String method);




    /**
     * 获取专辑
     *
     * @param album_id 专辑id
     * @return
     */
    @GET("ting")
    Observable<GeDanInfoList> getAlbumInfo(@Query("album_id") String album_id, @Query("method") String method);


    /**
     * 获取节目信息
     */
    @GET("ting")
    Observable<RadioInfoList> getRadioInfo(@Query("album_id") String album_id, @Query("num") int num, @Query("method") String method);




    @GET("ting")
    Observable<MusicFileDownInfo> getMusicDownInfo(@QueryMap(encoded=true) Map <String, String> options);

    @GET("ting")
    Observable<LrcPicInfo> getLrcPic(@QueryMap(encoded=true) Map <String, String> options);


}
