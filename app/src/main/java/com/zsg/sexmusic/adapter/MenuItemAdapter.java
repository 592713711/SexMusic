package com.zsg.sexmusic.adapter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.bilibili.magicasakura.utils.ThemeUtils;
import com.bilibili.magicasakura.widgets.TintTextView;
import com.zsg.sexmusic.R;
import com.zsg.sexmusic.activity.HomeActivity;
import com.zsg.sexmusic.model.LvMenuItem;
import com.zsg.sexmusic.util.ThemeHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuItemAdapter extends BaseAdapter {
    private final int mIconSize;
    private LayoutInflater mInflater;
    private Context mContext;

    public MenuItemAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;

        mIconSize = context.getResources().getDimensionPixelSize(R.dimen.drawer_icon_size);//24dp
    }

    private List<LvMenuItem> mItems = new ArrayList<LvMenuItem>(
            Arrays.asList(
                    new LvMenuItem(R.mipmap.topmenu_icn_night, "夜间模式"),
                    new LvMenuItem(R.drawable.topmenu_icn_skin, "主题换肤"),
                    new LvMenuItem(R.mipmap.topmenu_icn_time, "定时关闭音乐"),
                    new LvMenuItem(R.mipmap.topmenu_icn_vip, "下载歌曲品质"),
                    new LvMenuItem(R.mipmap.topmenu_icn_exit, "退出")

            ));


    @Override
    public int getCount() {
        return mItems.size();
    }


    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LvMenuItem item = mItems.get(position);
        switch (item.type) {
            case LvMenuItem.TYPE_NORMAL:
                Log.e("zsg","TYPE_NORMAL");
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.design_drawer_item, parent,
                            false);
                }
                TintTextView itemView = (TintTextView) convertView;
                itemView.setText(item.name);
                Drawable icon = mContext.getResources().getDrawable(item.icon);
                // setIconColor(icon);
                if (icon != null) {
                    icon.setBounds(0, 0, mIconSize, mIconSize);
                    //设置TextView的 左边图标
                    itemView.setCompoundDrawables(icon, null, null, null);
                    //因为程序初始化主题时 这边还没有加载 所以要手动设置主题颜色   这里的theme_color_primary不代表颜色 只代表颜色的模式
                    //具体颜色和主题一致
                    itemView.setCompoundDrawableTintList(R.color.theme_color_primary,0,0,0);
                   // itemView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
                }

                break;
            case LvMenuItem.TYPE_NO_ICON:
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.design_drawer_item_subheader,
                            parent, false);
                }
                TextView subHeader = (TextView) convertView;
                subHeader.setText(item.name);
                break;
            case LvMenuItem.TYPE_SEPARATOR:
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.design_drawer_item_separator,
                            parent, false);
                }
                break;
        }

        return convertView;
    }

    public void setIconColor(Drawable icon) {
        int textColorSecondary = android.R.attr.textColorSecondary;
        TypedValue value = new TypedValue();
        if (!mContext.getTheme().resolveAttribute(textColorSecondary, value, true)) {
            return;
        }
        int baseColor = mContext.getResources().getColor(value.resourceId);
        icon.setColorFilter(baseColor, PorterDuff.Mode.MULTIPLY);
    }


}