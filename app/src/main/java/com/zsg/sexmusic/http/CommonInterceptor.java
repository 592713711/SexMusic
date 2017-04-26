package com.zsg.sexmusic.http;

/**
 * Created by zsg on 2017/4/6.
 */

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 封装公共参数   ?from=android&version=5.8.1.0&channel=ppzs&operator=3&method=baidu.ting.plaza.index&cuid=89CF1E1A06826F9AB95A34DC0F6AAA14
 * <p>
 */
public class CommonInterceptor implements Interceptor {


    public CommonInterceptor() {

    }

    @Override public Response intercept(Interceptor.Chain chain) throws IOException {

        Request oldRequest = chain.request();

        // 添加新的参数
        HttpUrl.Builder authorizedUrlBuilder = oldRequest.url()

                .newBuilder()
                .scheme(oldRequest.url().scheme())
                .host(oldRequest.url().host())

                .addQueryParameter("from", "android")
                .addQueryParameter("version", "5.8.1.0");
                //.addQueryParameter("format", "json");


        // 新的请求
        Request newRequest = oldRequest.newBuilder()
                .method(oldRequest.method(), oldRequest.body())
                .url(authorizedUrlBuilder.build())
                .addHeader("user-agent","Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)")
                .build();

        return chain.proceed(newRequest);
    }
}