package com.zsg.sexmusic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zsg.sexmusic.MusicPlayer;
import com.zsg.sexmusic.R;
import com.zsg.sexmusic.json.GeDanGeInfo;
import com.zsg.sexmusic.model.MusicInfo;
import com.zsg.sexmusic.util.L;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zsg on 2017/4/10.
 */

public class GeDanAdapter extends RecyclerView.Adapter {
    private static int ALL_PLAY_TYPE = 0;
    private static int SINGLE_PLAY_TYPE = 1;

    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<MusicInfo> data;

    public GeDanAdapter(Context context) {

        mContext = context;
        inflater = LayoutInflater.from(mContext);
        data = new ArrayList<>();
    }

    public void updateData(ArrayList<MusicInfo> data){

        this.data=data;

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType==ALL_PLAY_TYPE){
            View v=inflater.inflate(R.layout.header_common_item, parent, false);
            HeaderItemHolder holder=new HeaderItemHolder(v);
            return holder;
        }else{
            View v=inflater.inflate(R.layout.playlist_detail_item, parent, false);
            NomalItemHolder holder=new NomalItemHolder(v);
            return holder;
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0)
            return ALL_PLAY_TYPE;
        else
            return SINGLE_PLAY_TYPE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(position==0){
            //显示头
        }else {
            MusicInfo info=data.get(position-1);
            NomalItemHolder nomalItemHolder= (NomalItemHolder) holder;
            nomalItemHolder.initView(info);
        }
    }

    @Override
    public int getItemCount() {
        return data.size()+1;
    }

    class NomalItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.trackNumber)
        TextView tv_num;
        @BindView(R.id.song_title)
        TextView tv_title;
        @BindView(R.id.song_artist)
        TextView tv_artist;
        @BindView(R.id.popup_menu)
        ImageView iv_domore;

        public NomalItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);

        }

        public void initView(MusicInfo info){

            tv_num.setText(getAdapterPosition()+"");
            tv_title.setText(info.musicName);
            tv_artist.setText(info.artist);
        }

        @Override
        public void onClick(View v) {
            MusicPlayer.getInstance().playAllMusic(data,getAdapterPosition()-1);
        }
    }

    class HeaderItemHolder extends RecyclerView.ViewHolder{
        public HeaderItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
