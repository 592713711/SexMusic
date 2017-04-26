package com.zsg.sexmusic.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.zsg.sexmusic.R;
import com.zsg.sexmusic.adapter.NetRecommentAdapter;
import com.zsg.sexmusic.http.RetrofitUtil;
import com.zsg.sexmusic.json.FocusItemInfo;
import com.zsg.sexmusic.json.FocusItemInfoList;
import com.zsg.sexmusic.json.RecommendInfo;
import com.zsg.sexmusic.json.RecommendList;
import com.zsg.sexmusic.json.RecommendListNewAlbumInfo;
import com.zsg.sexmusic.json.RecommendListRadioInfo;
import com.zsg.sexmusic.json.RecommendListRecommendInfo;
import com.zsg.sexmusic.util.L;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscriber;

/**
 * A simple {@link Fragment} subclass.
 */
public class NetRecommendFragment extends Fragment {


    private static String TAG = "NetRecommendFragment";

    private Unbinder unbinder;

    @BindView(R.id.recycle_view)
    public RecyclerView recyclerView;

    public NetRecommentAdapter adapter;
    private Gson gson;
    private ArrayList<String> imageNet = new ArrayList<>();     //轮播封面图片地址
    private FocusItemInfoList focusItemInfoList = null;
    private RecommendList recommendList = null;

    private View dayRec;   //加载动画
    private ViewGroup mContent;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    public NetRecommendFragment() {
        gson = new Gson();
        EventBus.getDefault().register(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContent = (ViewGroup) inflater.inflate(R.layout.fragment_net_recommend, container, false);
        unbinder = ButterKnife.bind(this, mContent);
        initData();
        getfocusPic();
        getRecommendInfo();


        return mContent;
    }

    private void getRecommendInfo() {
        startAnim();
        RetrofitUtil.getInstance().getRecommend(new Subscriber<RecommendList>() {
            @Override
            public void onCompleted() {
                stopAnim();
            }

            @Override
            public void onError(Throwable e) {
                L.e("zsg", "onError:" + e.toString());
                stopAnim();
            }

            @Override
            public void onNext(RecommendList recommendList) {
                L.e("zsg","recommendList:"+recommendList.toString() );
                adapter.updatRecommendData(recommendList);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }


    // 网络请求获取轮播图片
    public void getfocusPic() {
        RetrofitUtil.getInstance().getfocusPic(7, new Subscriber<FocusItemInfoList>() {
            @Override
            public void onCompleted() {
                L.e("zsg", "onCompleted");

            }

            @Override
            public void onError(Throwable e) {
                L.e("zsg", "error:" + e.toString());

            }

            @Override
            public void onNext(FocusItemInfoList focusItemInfoList) {
                if (focusItemInfoList.pic != null)
                    for (FocusItemInfo info : focusItemInfoList.pic) {
                        imageNet.add(info.getRandpic());
                    }
                adapter.updatBannerData(imageNet);

                //L.e("zsgxxx", "focusItemInfoList:" + focusItemInfoList.toString());
            }
        });

    }


    private void initData() {

        // recyclerView = (RecyclerView) rootview.findViewById(R.id.recycle_view);
        // recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NetRecommentAdapter(getContext());
        recyclerView.setAdapter(adapter);

        dayRec = LayoutInflater.from(getContext()).inflate(R.layout.loading_daymusic, mContent, false);

    }

    public void notifyDataChange() {
        adapter.notifyDataSetChanged();
    }

    public void startAnim() {
        ImageView view1 = (ImageView) dayRec.findViewById(R.id.loading_dayimage);
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, 1, 0.5F, 1, 0.5F);
        rotateAnimation.setDuration(5000);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setRepeatMode(Animation.INFINITE);
        view1.startAnimation(rotateAnimation);
        mContent.addView(dayRec);
    }

    public void stopAnim() {
        mContent.removeView(dayRec);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClickRecommendItem(RecommendInfo info) {
        if(info instanceof RecommendListRecommendInfo){
            RecommendListRecommendInfo recommendInfo= (RecommendListRecommendInfo) info;
            L.i(TAG, "onClickRecommendItem: "+ recommendInfo.toString());
            Intent intent=new Intent(getContext(),RecommentListActivity.class);
            intent.putExtra("info",recommendInfo);
            startActivity(intent);

        }else if(info instanceof RecommendListNewAlbumInfo){
            RecommendListNewAlbumInfo newAlbumInfo= (RecommendListNewAlbumInfo) info;
            L.i(TAG, "onClickRecommendItem: "+ newAlbumInfo.toString());
            Intent intent=new Intent(getContext(),NewAlbumListActivity.class);
            intent.putExtra("info",newAlbumInfo);
            startActivity(intent);

        }else if(info instanceof RecommendListRadioInfo){
            RecommendListRadioInfo radioInfo= (RecommendListRadioInfo) info;
            L.i(TAG, "onClickRecommendItem: "+ radioInfo.toString());
            Intent intent=new Intent(getContext(),RadioListActivity.class);
            intent.putExtra("info",radioInfo);
            startActivity(intent);
        }

    }



}
