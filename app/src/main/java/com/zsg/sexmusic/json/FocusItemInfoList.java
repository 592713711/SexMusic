package com.zsg.sexmusic.json;

import java.util.ArrayList;

/**
 * Created by zsg on 2017/3/30.
 */

public class FocusItemInfoList {
   public  ArrayList<FocusItemInfo> pic;
   public String error_code;

   @Override
   public String toString() {
      return "FocusItemInfoList{" +
              "pic=" + pic +
              ", error_code='" + error_code + '\'' +
              '}';
   }
}
