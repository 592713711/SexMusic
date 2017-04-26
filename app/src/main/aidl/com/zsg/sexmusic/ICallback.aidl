// ICallBack.aidl
package com.zsg.sexmusic;

//回调
interface ICallback {
    //暂停播放
   void onStop();

   //开始播放
   void onStart(int songid);

   //播放失败
   void onError(int code);

   //准备完毕
   void onPrepared(int songid);

   //songid 歌曲改变
   void onUpdate(int songid);

}
