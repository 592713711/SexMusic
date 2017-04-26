package com.zsg.sexmusic.util;

import com.zsg.sexmusic.Config;
import com.zsg.sexmusic.volley.RequestParams;

/**
 * 用来得到各种通信的url路径
 * Created by zsg on 2017/3/30.
 */

public class UrlUtil {
    /**
     * 轮播音乐封面
     *
     * @param num 数量
     * @return
     */
    public static String getfocusPic(int num) {

        StringBuffer sb=initParams();
        sb.append("&method=").append("baidu.ting.plaza.getFocusPic")
                .append("&num=").append(num);
        return sb.toString();
    }

    /**
     * 获取推荐信息url
     * @return
     */
    public static String getRecommend(){

        String url="http://tingapi.ting.baidu.com/v1/restserver/ting?from=android&version=5.8.1.0&channel=ppzs&operator=3&method=baidu.ting.plaza.index&cuid=89CF1E1A06826F9AB95A34DC0F6AAA14";
                    http://tingapi.ting.baidu.com/v1/restserver/ting/?channel=ppzs&operator=3&cuid=89CF1E1A06826F9AB95A34DC0F6AAA14&method=baidu.ting.plaza.index&from=android&version=5.8.1.0&format=json
        return url;
    }

    /**
     * 初始化RequestParams 加入必要的参数
     */
    private static StringBuffer initParams() {
        StringBuffer sb = new StringBuffer(Config.URL_BASE);
        sb.append("?from=android&version=5.6.5.6&format=json");

        return sb;
    }
}
