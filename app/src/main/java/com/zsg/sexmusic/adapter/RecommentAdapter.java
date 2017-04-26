package com.zsg.sexmusic.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zsg.sexmusic.R;
import com.zsg.sexmusic.json.RecommendInfo;
import com.zsg.sexmusic.json.RecommendListNewAlbumInfo;
import com.zsg.sexmusic.json.RecommendListRadioInfo;
import com.zsg.sexmusic.json.RecommendListRecommendInfo;
import com.zsg.sexmusic.util.L;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 推荐歌单 新专辑上架  主播电台
 * Created by zsg on 2017/4/1.
 */

public class RecommentAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private LayoutInflater inflater;

    //根据类型来判断 是用于哪种适配器  推荐歌单 新专辑上架  主播电台
    public static int MUSIC_TYPE = 0;
    public static int ALBUM_TYPE = 1;
    public static int RADIO_TYPE = 2;

    private int adapter_type;
    private ArrayList<RecommendInfo> recommendData;

    public RecommentAdapter(Context context, int type) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        adapter_type = type;
        recommendData = new ArrayList<>();
    }


    public void updateData(ArrayList<RecommendInfo> data) {
        recommendData.clear();
        recommendData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View recommend_view = inflater.inflate(R.layout.recommend_item, parent, false);
        RecyclerView.ViewHolder holder = null;
        if (viewType == MUSIC_TYPE) {
            holder = new MusicHolder(recommend_view);
        } else if (viewType == ALBUM_TYPE) {
            holder = new AlbumHolder(recommend_view);
        } else if (viewType == RADIO_TYPE) {
            holder = new RadioHolder(recommend_view);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MusicHolder) {
            MusicHolder musicHolder = (MusicHolder) holder;
            musicHolder.updateView(recommendData.get(position));
        } else if (holder instanceof AlbumHolder) {
            AlbumHolder albumHolder = (AlbumHolder) holder;
            albumHolder.updateView(recommendData.get(position));
        } else if (holder instanceof RadioHolder) {
            RadioHolder albumHolder = (RadioHolder) holder;
            albumHolder.updateView(recommendData.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return recommendData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (adapter_type == MUSIC_TYPE) {
            return MUSIC_TYPE;
        } else if (adapter_type == ALBUM_TYPE) {
            return ALBUM_TYPE;
        } else if (adapter_type == RADIO_TYPE) {
            return RADIO_TYPE;
        }
        return -1;
    }

    class MusicHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.play_view)
        ImageView art;
        @BindView(R.id.playlist_name)
        TextView name;
        @BindView(R.id.playlist_listen_count)
        TextView count_tv;
        @BindView(R.id.artist_name)
        TextView name_tv2;

        SpannableString spanString;

        public MusicHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);

            name_tv2.setVisibility(View.GONE);
            //设置图片上带小图标文字
            Bitmap b = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.index_icn_earphone);
            ImageSpan imgSpan = new ImageSpan(mContext, b, ImageSpan.ALIGN_BASELINE);
            spanString = new SpannableString("icon");
            spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        }

        public void updateView(RecommendInfo info) {

            if (info instanceof RecommendListRecommendInfo) {
                RecommendListRecommendInfo recommendInfo = (RecommendListRecommendInfo) info;
                Glide.with(mContext)
                        .load(recommendInfo.getPic())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.loading_big)
                        .error(R.mipmap.placeholder_disk_300)
                        .into(art);

                int count = Integer.parseInt(recommendInfo.getListenum());
                count_tv.setText(spanString);

                if (count > 10000) {
                    count = count / 10000;
                    count_tv.append(" " + count + "万");
                } else {
                    count_tv.append(" " + count);
                }
                name.setText(recommendInfo.getTitle());

            }
        }

        @Override
        public void onClick(View v) {
            EventBus.getDefault().post(recommendData.get(getAdapterPosition()));
        }
    }

    class AlbumHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.play_view)
        ImageView art;
        @BindView(R.id.playlist_name)
        TextView name_tv1;
        @BindView(R.id.artist_name)
        TextView name_tv2;
        @BindView(R.id.playlist_listen_count)
        TextView count_tv;


        public AlbumHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            count_tv.setVisibility(View.GONE);
            name_tv1.setSingleLine(true);
        }

        public void updateView(RecommendInfo info) {
            if (info instanceof RecommendListNewAlbumInfo) {
                RecommendListNewAlbumInfo newAlbumInfo = (RecommendListNewAlbumInfo) info;
                Glide.with(mContext)
                        .load(newAlbumInfo.getPic())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.loading_big)
                        .error(R.mipmap.placeholder_disk_300)
                        .into(art);

                name_tv1.setText(newAlbumInfo.getTitle());
                name_tv2.setText(newAlbumInfo.getAuthor());


            }
        }

        @Override
        public void onClick(View v) {
            EventBus.getDefault().post(recommendData.get(getAdapterPosition()));
        }
    }

    class RadioHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.play_view)
        ImageView art;
        @BindView(R.id.playlist_name)
        TextView name_tv1;
        @BindView(R.id.artist_name)
        TextView name_tv2;
        @BindView(R.id.playlist_listen_count)
        TextView count_tv;


        public RadioHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
            count_tv.setVisibility(View.GONE);
            name_tv1.setSingleLine(true);
        }

        public void updateView(RecommendInfo info) {
            if (info instanceof RecommendListRadioInfo) {
                RecommendListRadioInfo radioInfo = (RecommendListRadioInfo) info;
                //Log.e("zsg","加载图片："+radioInfo.getPic());
                Glide.with(mContext)
                        .load(radioInfo.getPic())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.mipmap.loading_big)
                        .error(R.mipmap.placeholder_disk_300)
                        .into(art);

                name_tv1.setText(radioInfo.getTitle());
                name_tv2.setText(radioInfo.getDesc());


            }
        }

        @Override
        public void onClick(View v) {
            EventBus.getDefault().post(recommendData.get(getAdapterPosition()));
        }

    }
}
