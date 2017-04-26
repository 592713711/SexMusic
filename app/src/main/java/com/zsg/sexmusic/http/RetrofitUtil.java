package com.zsg.sexmusic.http;

import android.text.TextUtils;

import com.zsg.sexmusic.app.MyApplication;
import com.zsg.sexmusic.http.service.TingApiService;
import com.zsg.sexmusic.json.FocusItemInfoList;
import com.zsg.sexmusic.json.GeDanInfoList;
import com.zsg.sexmusic.json.LrcPicInfo;
import com.zsg.sexmusic.json.MusicFileDownInfo;
import com.zsg.sexmusic.json.RadioInfoList;
import com.zsg.sexmusic.json.RecommendList;
import com.zsg.sexmusic.util.AESTools;
import com.zsg.sexmusic.util.L;
import com.zsg.sexmusic.util.NetUtil;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zsg on 2017/4/6.
 */

public class RetrofitUtil {
    private Retrofit mRetrofit;
    private TingApiService mApiService;

    private static RetrofitUtil mInstance;

    /**
     * 私有构造方法
     */
    private RetrofitUtil() {
        initRetrofit();
    }

    public static RetrofitUtil getInstance() {
        if (mInstance == null) {
            synchronized (RetrofitUtil.class) {
                mInstance = new RetrofitUtil();
            }
        }
        return mInstance;
    }

    private void initRetrofit() {
        //打印请求日志拦截器 这样才能打印 请求url
        Interceptor logRequestInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                //if (BuildConfig.DEBUG) {
                L.e("retrofitUtil", String.format("发送请求 %s on %s%n%s",
                        request.url(), chain.connection(), request.headers()));
                // }
                return chain.proceed(request);
            }
        };

        //打印响应日志拦截器
        HttpLoggingInterceptor logResponseInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {

                if (TextUtils.isEmpty(message)) return;
                String s = message.substring(0, 1);
                //如果收到想响应是json才打印
                if ("{".equals(s) || "[".equals(s)) {
                    L.e("zsg", "OkHttp====Message:" + message);
                }
            }
        });
        logResponseInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //用于添加公共参数
        CommonInterceptor commonInterceptor = new CommonInterceptor();


        /**
         * 获取缓存
         * baseInterceptor 是根据请求环境进行不同策略的操作（这儿是断网环境下的策略读取缓存）
         */
        Interceptor cacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                if (!NetUtil.isNetworkAvailable(MyApplication.getInstance())) {
                    /**
                     * 离线缓存控制  总的缓存时间=在线缓存时间+设置离线缓存时间
                     */
                    int maxStale = 60 * 60 * 24 * 28; // 离线时缓存保存4周,单位:秒
                    CacheControl tempCacheControl = new CacheControl.Builder()
                            .onlyIfCached()
                            .maxStale(maxStale, TimeUnit.SECONDS)
                            .build();
                    request = request.newBuilder()
                            .removeHeader("Pragma")   //Pragma: no-cache后会让内容无法被缓存
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            //.cacheControl(tempCacheControl)
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                    L.i("RetrofitUtil2", "intercept:no network ");
                }
                return chain.proceed(request);
            }
        };

        //只有 网络拦截器环节 才会写入缓存写入缓存,在有网络的时候 设置缓存时间
        Interceptor rewriteCacheControlInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response originalResponse = chain.proceed(request);
                int maxAge = 30 * 60; // 在线缓存在30分钟内可读取 单位:秒
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效  //Pragma: no-cache后会让内容无法被缓存
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, max-age=" + maxAge)//max-age=[秒]：表示在这个时间范围内缓存是新鲜的无需更新
                        .build();

            }
        };


        //设置缓存路径 内置存储
        //File httpCacheDirectory = new File(context.getCacheDir(), "responses");
        //外部存储
        File httpCacheDirectory = new File(MyApplication.getInstance().getExternalCacheDir(), "responses");
        //设置缓存 10M
        int cacheSize = 10 * 1024 * 1024;
        /*okhttp会根据response头信息来制定缓存  所以我们只需要改变response头信息就可以实现缓存   先把原来的头信息删除掉 再加上自己的缓存策略
        * 没有网时 不会进入rewriteCacheControlInterceptor
         */
        Cache cache = new Cache(httpCacheDirectory, cacheSize);


        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(logResponseInterceptor)
                .addInterceptor(commonInterceptor)
                .addInterceptor(logRequestInterceptor)
                .addInterceptor(cacheInterceptor)
                .addNetworkInterceptor(rewriteCacheControlInterceptor)
                .build();
        // 适配器

        mRetrofit = new Retrofit.Builder()
                .baseUrl(TingApiService.API_TING)
                .addConverterFactory(DownInfoConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();

        mApiService = mRetrofit.create(TingApiService.class);
    }


    /**
     * 获取轮播音乐封面
     *
     * @param subscriber
     */
    public void getfocusPic(int num, Subscriber<FocusItemInfoList> subscriber) {
        mApiService.getfocusPic(num, "baidu.ting.plaza.getFocusPic")
                .subscribeOn(Schedulers.io())  // 网络请求切换在io线程中调用
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())// 观察后放在主线程调用
                .subscribe(subscriber);
    }

    public void getRecommend(Subscriber<RecommendList> subscriber) {
        Map<String, String> param = new HashMap<>();
        param.put("channel", "ppzs");
        param.put("operator", "3");
        param.put("method", "baidu.ting.plaza.index");
        param.put("cuid", "89CF1E1A06826F9AB95A34DC0F6AAA14");
        mApiService.getRecommendList(param)
                .subscribeOn(Schedulers.io())  // 网络请求切换在io线程中调用
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())// 观察后放在主线程调用
                .subscribe(subscriber);
    }

    //根据id获取歌单
    public void getGeDanInfo(String listid, Subscriber<GeDanInfoList> subscriber) {
        mApiService.getGeDanInfo(listid, "baidu.ting.diy.gedanInfo")
                .subscribeOn(Schedulers.io())  // 网络请求切换在io线程中调用
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())// 观察后放在主线程调用
                .subscribe(subscriber);
    }

    //根据id获取歌单
    public void getAlbumInfo(String album_id, Subscriber<GeDanInfoList> subscriber) {
        mApiService.getAlbumInfo(album_id, "baidu.ting.album.getAlbumInfo")
                .subscribeOn(Schedulers.io())  // 网络请求切换在io线程中调用
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())// 观察后放在主线程调用
                .subscribe(subscriber);
    }

    public void getRadioInfo(String album_id, int num, Subscriber<RadioInfoList> subscriber) {
        mApiService.getRadioInfo(album_id, num, "baidu.ting.lebo.albumInfo")
                .subscribeOn(Schedulers.io())  // 网络请求切换在io线程中调用
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())// 观察后放在主线程调用
                .subscribe(subscriber);
    }

    //获取音乐下载地址信息
    public void getMusicDownInfo(String songid,Subscriber<MusicFileDownInfo> subscriber){
        long currenttime=System.currentTimeMillis();
        String str = "songid=" + songid + "&ts=" + currenttime;
        String e = AESTools.encrpty(str).trim();
        Map<String,String> option=new HashMap<>();
        option.put("songid",songid);
        option.put("ts",Long.toString(currenttime));
        option.put("e",e);
        option.put("method","baidu.ting.song.getInfos");
        mApiService.getMusicDownInfo(option)
                .subscribeOn(Schedulers.io())  // 网络请求切换在io线程中调用
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())// 观察后放在主线程调用
                .subscribe(subscriber);

    }


    public void getLrcPic(String songname, String artist,Subscriber<LrcPicInfo> subscriber){
        String ts = Long.toString(System.currentTimeMillis());
        String query = encode(songname) + "$$" + encode(artist);
        String e = AESTools.encrpty("query=" + songname + "$$" + artist + "&ts=" + ts);
        Map<String,String> option=new HashMap<>();
        option.put("type","2");
        option.put("query",query);
        option.put("ts",ts);
        option.put("e",e);
        option.put("method","baidu.ting.search.lrcpic");
        mApiService.getLrcPic(option)
                .subscribeOn(Schedulers.io())  // 网络请求切换在io线程中调用
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())// 观察后放在主线程调用
                .subscribe(subscriber);
    }



    public static String encode(String str) {
        if (str == null) return "";

        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }




}
