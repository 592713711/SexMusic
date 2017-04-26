package com.zsg.sexmusic.activity;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.zsg.sexmusic.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NetMusicFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    NetRecommendFragment netRecommendFragment;
    public NetMusicFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_net_music, container, false);
        initView(view);

        return view;
    }

    private void initView(View rootView) {
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
            viewPager.setOffscreenPageLimit(2);
        }

        tabLayout = (TabLayout) rootView.findViewById(R.id.net_tab);
        changeTabTheme();
    }

    public void changeTabTheme(){
        //tab栏字体颜色
        tabLayout.setTabTextColors(R.color.text_color, ThemeUtils.getThemeColorStateList(getContext(), R.color.theme_color_primary).getDefaultColor());
        //tab栏底部横线颜色
        tabLayout.setSelectedTabIndicatorColor(ThemeUtils.getThemeColorStateList(getContext(), R.color.theme_color_primary).getDefaultColor());
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
      //  recommendFragment = new RecommendFragment();
       // recommendFragment.setChanger(this);
        netRecommendFragment=new NetRecommendFragment();
        adapter.addFragment(netRecommendFragment, "新曲");
        adapter.addFragment(new NetAllListFragment(), "歌单");
        //  adapter.addFragment(new NetFragment(), "主播电台");
        adapter.addFragment(new NetRankFragment(), "排行榜");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment,String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }

    }

    public void notifyDataChange(){
        netRecommendFragment.notifyDataChange();
    }



}
