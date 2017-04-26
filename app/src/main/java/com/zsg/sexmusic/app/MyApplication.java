/*
 * Copyright (C) 2016 Bilibili
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zsg.sexmusic.app;

import android.app.Application;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.util.Log;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.zsg.sexmusic.MusicPlayer;
import com.zsg.sexmusic.R;
import com.zsg.sexmusic.dao.DatabaseHelper;
import com.zsg.sexmusic.dao.MusicDao;
import com.zsg.sexmusic.http.CommonInterceptor;
import com.zsg.sexmusic.http.service.TingApiService;
import com.zsg.sexmusic.model.MusicInfo;
import com.zsg.sexmusic.util.AESTools;
import com.zsg.sexmusic.util.L;
import com.zsg.sexmusic.util.ThemeHelper;
import com.zsg.sexmusic.volley.RequestManager;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import line.hee.library.SocketProxyPlay;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * @author xyczero
 * @time 16/5/2
 */
public class MyApplication extends Application implements ThemeUtils.switchColor {
    private static MyApplication mApplication;
    private static MusicDao musicDao;

    @Override
    public void onCreate() {
        super.onCreate();
        ThemeUtils.setSwitchColor(this);
        mApplication = this;
        RequestManager.initVolley(mApplication);
        MusicPlayer.getInstance().bindService(mApplication);

        //缓存代理
        SocketProxyPlay.getInstance().init(this, true);

    }


    public static MyApplication getInstance() {
        return mApplication;
    }

    @Override
    public int replaceColorById(Context context, @ColorRes int colorId) {
        //colorId 是控件初始颜色  资源   若当前主题是默认值或找不到主题   则返回初始颜色

        if (ThemeHelper.isDefaultTheme(context)) {
            return context.getResources().getColor(colorId);
        }
        String theme = getTheme(context);
        if (theme != null) {
            colorId = getThemeColorId(context, colorId, theme);
        }
        return context.getResources().getColor(colorId);
    }

    @Override
    public int replaceColor(Context context, @ColorInt int originColor) {
        //originColor 是控件初始颜色   若当前主题是默认值或找不到主题   则返回初始颜色
        if (ThemeHelper.isDefaultTheme(context)) {
            return originColor;
        }
        String theme = getTheme(context);
        int colorId = -1;

        if (theme != null) {
            colorId = getThemeColor(context, originColor, theme);
        }
        return colorId != -1 ? getResources().getColor(colorId) : originColor;
    }

    //将当前主题编号转换成文字
    private String getTheme(Context context) {
        if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_STORM) {
            return "blue";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_HOPE) {
            return "purple";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_WOOD) {
            return "green";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_LIGHT) {
            return "green_light";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_THUNDER) {
            return "yellow";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_SAND) {
            return "orange";
        } else if (ThemeHelper.getTheme(context) == ThemeHelper.CARD_FIREY) {
            return "red";
        }
        return null;
    }


    /**
     * 得到指定 主题模式 的主题颜色
     * 每种主题分为3种模式  命名规则 theme   theme_dark  theme_trans
     *
     * @param context
     * @param colorId 主题模式
     * @param theme   主题
     * @return
     */
    private
    @ColorRes
    int getThemeColorId(Context context, int colorId, String theme) {
        switch (colorId) {
            case R.color.theme_color_primary:
                return context.getResources().getIdentifier(theme, "color", getPackageName());
            case R.color.theme_color_primary_dark:
                return context.getResources().getIdentifier(theme + "_dark", "color", getPackageName());
            case R.color.playbarProgressColor:
                return context.getResources().getIdentifier(theme + "_trans", "color", getPackageName());
        }
        return colorId;
    }

    private
    @ColorRes
    int getThemeColor(Context context, int color, String theme) {
        switch (color) {
            case 0xfffb7299:
                return context.getResources().getIdentifier(theme, "color", getPackageName());
            case 0xffb85671:
                return context.getResources().getIdentifier(theme + "_dark", "color", getPackageName());
            case 0x99f0486c:
                return context.getResources().getIdentifier(theme + "_trans", "color", getPackageName());
        }
        return -1;
    }

    public synchronized MusicDao getMusicDao(){
        if(musicDao==null)
            musicDao=new MusicDao(this);
        return musicDao;
    }

}
