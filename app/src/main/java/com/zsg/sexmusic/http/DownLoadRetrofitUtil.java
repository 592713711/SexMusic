package com.zsg.sexmusic.http;

import com.zsg.sexmusic.http.service.DownLoadService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * 下载文件工具类
 * Created by zsg on 2017/4/21.
 */

public class DownLoadRetrofitUtil {
    private Retrofit mRetrofit;
    private DownLoadService mApiService;


    private static DownLoadRetrofitUtil mInstance;

    /**
     * 私有构造方法
     */
    private DownLoadRetrofitUtil() {
        initRetrofit();
    }

    public static DownLoadRetrofitUtil getInstance() {
        if (mInstance == null) {
            synchronized (RetrofitUtil.class) {
                mInstance = new DownLoadRetrofitUtil();
            }
        }
        return mInstance;
    }

    private void initRetrofit() {
        DownloadInterceptor interceptor = new DownloadInterceptor();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                //.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();


        mRetrofit = new Retrofit.Builder()
                //.baseUrl(url)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();


    }
}
