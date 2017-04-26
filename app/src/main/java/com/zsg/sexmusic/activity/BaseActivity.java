package com.zsg.sexmusic.activity;

import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.zsg.sexmusic.R;

import static android.R.attr.fragment;

/**
 * 基类  带控制栏
 * Created by zsg on 2017/3/27.
 */

public class BaseActivity extends AppCompatActivity {
    private String TAG = "BaseActivity";
    private ControlFragment controlFragment;

    /**
     * @param show 显示或关闭底部播放控制栏
     */
    protected void showQuickControl(boolean show) {
        Log.e("zsg", "showQuickControl");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (show) {
            if (controlFragment == null) {
                controlFragment = new ControlFragment();
                ft.add(R.id.bottom_container, controlFragment).commitAllowingStateLoss();
            } else {
                ft.show(controlFragment).commitAllowingStateLoss();
            }
        } else {
            if (controlFragment != null)
                ft.hide(controlFragment).commitAllowingStateLoss();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("BaseActivity", "onCreatezz");
        showQuickControl(true);
    }
}
