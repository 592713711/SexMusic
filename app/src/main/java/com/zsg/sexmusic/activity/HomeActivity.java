package com.zsg.sexmusic.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.zsg.sexmusic.R;
import com.zsg.sexmusic.adapter.MenuItemAdapter;
import com.zsg.sexmusic.dialog.CardPickerDialog;
import com.zsg.sexmusic.util.ThemeHelper;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity implements CardPickerDialog.ThemeClickListener ,View.OnClickListener{
    private ActionBar ab;
    private DrawerLayout drawerLayout;
    private ListView mLvLeftMenu;       //侧滑抽屉的选项菜单
    private ViewPager mainViewPage;     //网络音乐列表，我的音乐列表，个人信息
    private ImageView mbarNet,mbarMusic,mbarInfo;   //三个导航按钮
    private ArrayList<ImageView> mBarList=new ArrayList<>();

    private NetMusicFragment netMusicFragment;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        intiView();
        setToolBar();
        setUpDrawer();
        initPager();

    }

    private void intiView() {
        mbarNet= (ImageView) findViewById(R.id.bar_net);
        mbarMusic= (ImageView) findViewById(R.id.bar_music);
        mbarInfo= (ImageView) findViewById(R.id.bar_info);
        mBarList.add(mbarNet);
        mBarList.add(mbarMusic);
        mBarList.add(mbarInfo);
        mbarNet.setOnClickListener(this);
        mbarMusic.setOnClickListener(this);
        mbarInfo.setOnClickListener(this);
        drawerLayout= (DrawerLayout) findViewById(R.id.fd);
        mLvLeftMenu = (ListView) findViewById(R.id.id_lv_left_menu);
    }

    public void initPager(){

        mainViewPage= (ViewPager) findViewById(R.id.main_viewpager);
        ViewPagerAdapter pagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        netMusicFragment=new NetMusicFragment();
        pagerAdapter.addFragment(netMusicFragment);
        pagerAdapter.addFragment(new SelfMusicFragment());
        pagerAdapter.addFragment(new SelfInfoFragment());
        mainViewPage.setAdapter(pagerAdapter);

        mainViewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeBarSelect(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        changeBarSelect(0);
        mainViewPage.setCurrentItem(0);
    }

    //初始化工具栏 添加图标
    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);     // 给左上角图标的左边加上一个的图标  id为为 R.id.home 。对应ActionBar.DISPLAY_HOME_AS_UP
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setTitle("");

        //ThemeHelper.setTheme(MainActivity.this, currentTheme);
    }

    /**
     * 设置侧滑内容
     */
    private void setUpDrawer() {
        LayoutInflater inflater = LayoutInflater.from(this);
        //通过向listview addHeaderView添加头部的图片  选项菜单放在listview中点击
        mLvLeftMenu.addHeaderView(inflater.inflate(R.layout.nav_header_main, mLvLeftMenu, false));
        mLvLeftMenu.setAdapter(new MenuItemAdapter(this));
        mLvLeftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        drawerLayout.closeDrawers();
                        break;
                    case 2:
                        CardPickerDialog dialog = new CardPickerDialog();
                        dialog.setClickListener(HomeActivity.this);
                        dialog.show(getSupportFragmentManager(), "theme");
                        drawerLayout.closeDrawers();

                        break;
                    case 3:

                        drawerLayout.closeDrawers();

                        break;
                    case 4:

                        drawerLayout.closeDrawers();

                        break;
                    case 5:
                        finish();
                        drawerLayout.closeDrawers();

                }
            }
        });
    }


    //改变主题
    public void changeTheme(int theme){
        Log.e("zsg","changeTheme:"+theme);
        //设置要切换的主题编号
        //ThemeHelper.setTheme(this, ThemeHelper.CARD_LIGHT);
        ThemeHelper.setTheme(this,theme);
        ThemeUtils.refreshUI(this, new ThemeUtils.ExtraRefreshable() {
                    @Override
                    public void refreshGlobal(Activity activity) {
                        //for global setting, just do once
                        if (Build.VERSION.SDK_INT >= 21) {
                            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(null, null, ThemeUtils.getThemeAttrColor(HomeActivity.this, android.R.attr.colorPrimary));
                            setTaskDescription(taskDescription);
                            getWindow().setStatusBarColor(ThemeUtils.getColorById(HomeActivity.this, R.color.theme_color_primary_dark));
                        }
                    }

                    @Override
                    public void refreshSpecificView(View view) {
                        //TODO: will do this for each traversal
                    }
                }
        );
        netMusicFragment.changeTabTheme();
        netMusicFragment.notifyDataChange();
    }

    public void changeBarSelect(int position){
        for(int i=0;i<mBarList.size();i++){
            if(i==position)
                mBarList.get(i).setSelected(true);
            else
                mBarList.get(i).setSelected(false);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * 在ActionBar 左边加上一个的图标  点击回调在onOptionsItemSelected里
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case android.R.id.home: //Menu icon
                drawerLayout.openDrawer(Gravity.LEFT);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onChangeThemeClick(int currentTheme) {
        //切换主题
        changeTheme(currentTheme);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.bar_net){
            mainViewPage.setCurrentItem(0);
        }else if(v.getId()==R.id.bar_music){
            mainViewPage.setCurrentItem(1);
        }else if(v.getId()==R.id.bar_info){
            mainViewPage.setCurrentItem(2);
        }
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment) {
            mFragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

    }
}
