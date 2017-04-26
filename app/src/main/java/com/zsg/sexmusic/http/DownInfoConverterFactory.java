package com.zsg.sexmusic.http;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zsg.sexmusic.json.LrcPicInfo;
import com.zsg.sexmusic.json.MusicFileDownInfo;
import com.zsg.sexmusic.util.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 自定义解析器
 * Created by zsg on 2017/4/14.
 */

public class DownInfoConverterFactory extends Converter.Factory {

    private static final String TAG ="DownInfoConverterFactory" ;
    public Gson gson = new Gson();

    public static DownInfoConverterFactory create() {

        return new DownInfoConverterFactory();
    }

    /**
     * 当factory.responseBodyConverter返回空时，表示没有匹配上，可使用下一个factory.
     *
     * @param type        //要转换的类型
     * @param annotations
     * @param retrofit
     * @return
     */
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        //进行条件判断，如果传进来的Type不是class，则匹配失败
     /*   if (String.class.equals(type)) {
            return new Converter<ResponseBody, String>() {
                @Override
                public String convert(ResponseBody value) throws IOException {
                    return value.string();
                }
            };
        }*/

        //进行条件判断，如果传进来的Type不是MusicFileDownInfo，则匹配失败
        if (MusicFileDownInfo.class.equals(type)) {
            return new Converter<ResponseBody, MusicFileDownInfo>() {
                @Override
                public MusicFileDownInfo convert(ResponseBody value) throws IOException {

                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(value.string()).getAsJsonObject();

                    //将data节点下的内容转为JsonArray
                    JsonArray jsonArray = jsonObject.get("songurl").getAsJsonObject().get("url").getAsJsonArray();

                    MusicFileDownInfo musicFileDownInfo = gson.fromJson(jsonArray.get(0), MusicFileDownInfo.class);
                    return musicFileDownInfo;

                }
            };
        }

        if (LrcPicInfo.class.equals(type)) {
            return new Converter<ResponseBody, LrcPicInfo>() {
                @Override
                public LrcPicInfo convert(ResponseBody value) throws IOException {

                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(value.string()).getAsJsonObject();

                    //将data节点下的内容转为JsonArray
                    JsonArray jsonArray = jsonObject.get("songinfo").getAsJsonArray();
                    LrcPicInfo lrcPicInfo = null;
                    String pic = "";
                    for (int i = 0; i < jsonArray.size(); i++) {
                        lrcPicInfo = gson.fromJson(jsonArray.get(i), LrcPicInfo.class);
                          L.e(TAG,lrcPicInfo.toString());
                        if (lrcPicInfo.lrclink.isEmpty()) {
                            if (!lrcPicInfo.avatar_s180.isEmpty())
                                pic = lrcPicInfo.avatar_s180;
                            continue;
                        }


                        return lrcPicInfo;
                    }
                    L.e(TAG,"pic"+pic);
                    if (lrcPicInfo != null && !pic.isEmpty())
                        lrcPicInfo.avatar_s180 = pic;
                    return lrcPicInfo;


                }
            };
        }

        return null;
    }


}


final class JsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    JsonResponseBodyConverter() {

    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(value.string());
            Gson gson = new Gson();
            return (T) jsonObj;
        } catch (JSONException e) {
            return null;
        }
    }
}

