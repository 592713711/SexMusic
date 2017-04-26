package com.zsg.sexmusic.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zsg on 2017/4/17.
 */

public class PlayState implements Parcelable{
    public boolean isPlaying=false;
    public long songid;
    public int currentPregress;
    public int duration;
    public int current;

    public PlayState(){

    }


    protected PlayState(Parcel in) {
        isPlaying = in.readByte() != 0;
        songid = in.readLong();
        currentPregress = in.readInt();
        duration = in.readInt();
        current = in.readInt();
    }

    public static final Creator<PlayState> CREATOR = new Creator<PlayState>() {
        @Override
        public PlayState createFromParcel(Parcel in) {
            return new PlayState(in);
        }

        @Override
        public PlayState[] newArray(int size) {
            return new PlayState[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isPlaying ? 1 : 0));
        dest.writeLong(songid);
        dest.writeInt(currentPregress);
        dest.writeInt(duration);
        dest.writeInt(current);
    }

    @Override
    public String toString() {
        return "PlayState{" +
                "isPlaying=" + isPlaying +
                ", songid=" + songid +
                ", currentPregress=" + currentPregress +
                ", duration=" + duration +
                ", current=" + current +
                '}';
    }
}
