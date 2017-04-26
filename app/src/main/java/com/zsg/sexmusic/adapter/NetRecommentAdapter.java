package com.zsg.sexmusic.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.zsg.sexmusic.R;
import com.zsg.sexmusic.json.RecommendInfo;
import com.zsg.sexmusic.json.RecommendList;
import com.zsg.sexmusic.json.RecommendListNewAlbumInfo;
import com.zsg.sexmusic.json.RecommendListRadioInfo;
import com.zsg.sexmusic.json.RecommendListRecommendInfo;
import com.zsg.sexmusic.util.GlideImageLoader;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zsg on 2017/3/31.
 */

public class NetRecommentAdapter extends RecyclerView.Adapter {
    private int count = 6;   //总共的布局类型
    public static int BANNER_TYPE = 0;
    public static int PERSONAL_TYPE = 1;
    public static int RECOMMEND_TYPE = 2;
    public static int NEWALBUM_TYPE = 3;
    public static int RADIO_TYPE = 4;
    public static int ADJUST_TYPE = 5;

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<String> bannerData = new ArrayList<>();     //轮播封面图片地址
    private ArrayList<RecommendListRecommendInfo> recommendMusicData;
    private ArrayList<RecommendListNewAlbumInfo> recommendAlbumData;
    private ArrayList<RecommendListRadioInfo> recommendRadioData;



    public NetRecommentAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        //初始化各我数据源
        recommendMusicData = new ArrayList<>();
        recommendAlbumData = new ArrayList<>();
        recommendRadioData = new ArrayList<>();



    }

    public void updatBannerData(ArrayList<String> data) {
        this.bannerData = data;
        notifyItemChanged(0);
    }

    public void updatRecommendData(RecommendList recommendList) {
        recommendMusicData = recommendList.getMusicList();
        recommendAlbumData = recommendList.getAlubmList();
        recommendRadioData = recommendList.getRadioList();
        notifyItemChanged(2);
        notifyItemChanged(3);
        notifyItemChanged(4);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if (viewType == BANNER_TYPE) {
            View banner_view = inflater.inflate(R.layout.banner_layout, parent, false);
            holder = new BannerHolder(banner_view);
        } else if (viewType == PERSONAL_TYPE) {
            View personal_view = inflater.inflate(R.layout.personal_layout, parent, false);
            holder = new RecommendHolder(personal_view);
        } else if (viewType == ADJUST_TYPE) {
            View bottom_view = inflater.inflate(R.layout.bottom_layout, parent, false);
            holder = new BottomHolder(bottom_view);
        } else if (viewType == RECOMMEND_TYPE) {
            View normal_view = inflater.inflate(R.layout.normal_layout, parent, false);
            holder = new NormalHolder(normal_view, RECOMMEND_TYPE);
        } else if (viewType == NEWALBUM_TYPE) {
            View normal_view = inflater.inflate(R.layout.normal_layout, parent, false);
            holder = new NormalHolder(normal_view, NEWALBUM_TYPE);
        } else if (viewType == RADIO_TYPE) {
            View normal_view = inflater.inflate(R.layout.normal_layout, parent, false);
            holder = new NormalHolder(normal_view, RADIO_TYPE);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BannerHolder && bannerData.size() != 0) {
            ((BannerHolder) holder).updateBanner(bannerData);
        } else if (holder instanceof RecommendHolder) {

        } else if (holder instanceof BottomHolder) {

        } else if (holder instanceof NormalHolder) {
            NormalHolder normalHolder = (NormalHolder) holder;
            if (normalHolder.type == RECOMMEND_TYPE) {
                normalHolder.list_name.setText("推荐歌单");
                normalHolder.updateView(getInfo1(recommendMusicData));
            } else if (normalHolder.type == NEWALBUM_TYPE) {
                normalHolder.list_name.setText("新专辑上架");
                normalHolder.updateView(getInfo2(recommendAlbumData));
            } else if (normalHolder.type == RADIO_TYPE) {
                normalHolder.list_name.setText("主播电台");
                normalHolder.updateView(getInfo3(recommendRadioData));
            }
        }
    }


    @Override
    public int getItemCount() {
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return BANNER_TYPE;
            case 1:
                return PERSONAL_TYPE;
            case 2:
                return RECOMMEND_TYPE;
            case 3:
                return NEWALBUM_TYPE;
            case 4:
                return RADIO_TYPE;
            case 5:
                return ADJUST_TYPE;
        }
        return -1;
    }


    public class BannerHolder extends RecyclerView.ViewHolder {
        private Banner banner;

        public BannerHolder(View itemView) {
            super(itemView);
            initView(itemView);
        }

        private void initView(View rootview) {
            banner = (Banner) rootview.findViewById(R.id.banner);
            //设置图片加载器
            banner.setImageLoader(new GlideImageLoader());
            //设置banner样式
            //banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
            //设置自动轮播，默认为true
            banner.isAutoPlay(true);
            //设置轮播时间
            banner.setDelayTime(5000);
            //设置指示器位置（当banner模式中有指示器时）
            banner.setIndicatorGravity(BannerConfig.CENTER);
        }

        private void updateBanner(ArrayList<String> imageNet) {
            banner.setImages(imageNet);
            banner.start();

        }

    }

    public class NormalHolder extends RecyclerView.ViewHolder {
        private int type;
        @BindView(R.id.r_dujia)
        public ImageView icon;
        @BindView(R.id.list_name)
        public TextView list_name;
        @BindView(R.id.more)
        public TextView do_more;
        @BindView(R.id.recommend_playlist_recyclerview)
        public RecyclerView recyclerView;

        public RecommentAdapter adapter;

        public NormalHolder(View itemView, int type) {
            super(itemView);
            this.type = type;

            ButterKnife.bind(this,itemView);
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
            if (type == RECOMMEND_TYPE) {
                adapter = new RecommentAdapter(mContext, RecommentAdapter.MUSIC_TYPE);
            } else if (type == NEWALBUM_TYPE) {
                do_more.setVisibility(View.GONE);
                adapter = new RecommentAdapter(mContext, RecommentAdapter.ALBUM_TYPE);
            } else if (type == RADIO_TYPE) {
                do_more.setVisibility(View.GONE);
                adapter = new RecommentAdapter(mContext, RecommentAdapter.RADIO_TYPE);
            }

            recyclerView.setAdapter(adapter);
        }

        public void updateView(ArrayList<RecommendInfo> data) {
            adapter.updateData(data);
        }
    }

    public class RecommendHolder extends RecyclerView.ViewHolder {
        public RecommendHolder(View itemView) {
            super(itemView);
        }
    }

    public class BottomHolder extends RecyclerView.ViewHolder {
        public BottomHolder(View itemView) {
            super(itemView);
        }
    }


    //将子类数组转换为父类数组
    public ArrayList<RecommendInfo> getInfo1(ArrayList<RecommendListRecommendInfo> datas) {
        ArrayList<RecommendInfo> infos = new ArrayList<>();
        for (RecommendListRecommendInfo data : datas) {
            infos.add(data);
        }

        return infos;
    }

    public ArrayList<RecommendInfo> getInfo2(ArrayList<RecommendListNewAlbumInfo> datas) {
        ArrayList<RecommendInfo> infos = new ArrayList<>();
        for (RecommendListNewAlbumInfo data : datas) {
            infos.add(data);
        }

        return infos;
    }

    public ArrayList<RecommendInfo> getInfo3(ArrayList<RecommendListRadioInfo> datas) {
        ArrayList<RecommendInfo> infos = new ArrayList<>();
        for (RecommendListRadioInfo data : datas) {
            infos.add(data);
        }

        return infos;
    }

}

