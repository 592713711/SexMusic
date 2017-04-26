package com.zsg.sexmusic.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.view.ViewHelper;
import com.zsg.sexmusic.R;
import com.zsg.sexmusic.adapter.GeDanAdapter;
import com.zsg.sexmusic.http.RetrofitUtil;
import com.zsg.sexmusic.json.GeDanGeInfo;
import com.zsg.sexmusic.json.GeDanInfoList;
import com.zsg.sexmusic.json.RecommendInfo;
import com.zsg.sexmusic.json.RecommendListRecommendInfo;
import com.zsg.sexmusic.util.CommonUtils;
import com.zsg.sexmusic.util.GlideImageLoader;
import com.zsg.sexmusic.util.ImageUtils;
import com.zsg.sexmusic.util.L;
import com.zsg.sexmusic.view.DividerItemDecoration;

import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 显示网络播放列表的活动
 */
public abstract class PlayListActivity extends BaseActivity implements ObservableScrollViewCallbacks {
    @BindView(R.id.recyclerview)
    public ObservableRecyclerView recyclerView;
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.album_art)
    public ImageView album_art;
    @BindView(R.id.playlist_art)
    public ImageView playlist_art;
    @BindView(R.id.playlist_listen_count)
    public TextView tv_listencount;
    @BindView(R.id.album_title)
    public TextView tv_albumtitle;
    @BindView(R.id.headerview)
    public FrameLayout header_content;  //头部布局
    @BindView(R.id.headerdetail)
    public RelativeLayout headerdetail;


    public ActionBar actionBar;
    public GeDanAdapter mAdapter;

    private RecommendInfo info;

    private int mActionBarSize;
    private int mStatusSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //占据状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_play_list);
        ButterKnife.bind(this);
        info = (RecommendInfo) getIntent().getSerializableExtra("info");
        initView();
    }

    private void initView() {
        recyclerView.setScrollViewCallbacks(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        mAdapter=new GeDanAdapter(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mActionBarSize = CommonUtils.getActionBarHeight(this);
        mStatusSize = CommonUtils.getStatusHeight(this);


        initData();
        setupToolbar();
        setHeaderView();
    }

    public abstract void initData();

    private void setHeaderView() {
        // final Bitmap bitmap=ImageUtils.getArtworkQuick(PlayListActivity.this, Uri.parse(info.getPic()), 300, 300);
        //通过Glide 加载图片 得到Bitmap

        //将图片模糊化
        Observable<Drawable> sender = Observable.create(new Observable.OnSubscribe<Drawable>() {
            @Override
            public void call(Subscriber<? super Drawable> subscriber) {

                try {
                    Bitmap myBitmap = Glide.with(PlayListActivity.this)
                            .load(info.getInfoPic())
                            .asBitmap() //必须
                            .centerCrop()
                            .into(500, 500)
                            .get();
                    subscriber.onNext(ImageUtils.createBlurredImageFromBitmap(myBitmap, PlayListActivity.this, 18));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        });

        sender.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())// 观察后放在主线程调用
                .subscribe(new Observer<Drawable>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Drawable drawable) {
                        album_art.setImageDrawable(drawable);
                    }
                });


        //设置专辑图片
        Glide.with(this).load(info.getInfoPic())
                .placeholder(R.drawable.placeholder_disk_210)
                .error(R.drawable.placeholder_disk_210)
                .into(playlist_art);


        tv_albumtitle.setText(info.getInfoTitle());

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.actionbar_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("歌单");
        //得到状态栏高度 将toolbar移动到状态栏下面
        toolbar.setPadding(0, CommonUtils.getStatusHeight(this), 0, 0);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbar.setSubtitle(info.getInfoExtra());

    }

    //recycleview滑动回调 scrollY为recyclerview 已经移动到的距离
    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        updateViews(scrollY, false);



        //判断当前移动到的位置 是不是在 header+标题栏+状态栏之内
        if(scrollY>0){
            toolbar.setTitle(info.getInfoTitle());
            toolbar.setSubtitle(info.getInfoExtra());
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.toolbar_background));
        }
        if (scrollY == 0) {
            toolbar.setTitle("歌单");
            actionBar.setBackgroundDrawable(null);
        }


        //header_content.getHeight() - mActionBarSize - mStatusSize
        float a = (float) scrollY / (header_content.getHeight() - mActionBarSize - mStatusSize);
        //L.e("PlayListActivity","header_content:"+(header_content.getHeight() - mActionBarSize - mStatusSize)+"   "+scrollY);
        headerdetail.setAlpha(1f - a);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    protected void updateViews(int scrollY, boolean animated) {
        // ViewHelper.setTranslationY(header_content, getHeaderTranslationY(scrollY));
        if(scrollY>header_content.getHeight() - mActionBarSize - mStatusSize)
            scrollY=header_content.getHeight() - mActionBarSize - mStatusSize;
        ViewHelper.setTranslationY(header_content, -scrollY);

    }

    //得到header_content
  /*  protected float getHeaderTranslationY(int scrollY) {
        final int headerHeight = header_content.getHeight();
        int headerTranslationY = mActionBarSize + mStatusSize - headerHeight;
        if (mActionBarSize + mStatusSize <= -scrollY + headerHeight) {
            headerTranslationY = -scrollY;
        }
        return headerTranslationY;
    }*/
}
