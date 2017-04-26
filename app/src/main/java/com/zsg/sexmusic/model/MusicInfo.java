/**
 * Copyright (lrc_arrow) www.longdw.com
 */
package com.zsg.sexmusic.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
@DatabaseTable(tableName = "tb_musicinfo")
public class MusicInfo implements Parcelable {

    @DatabaseField(id = true)
    public long songId = -1;
    @DatabaseField(columnName = "albumId")
    public String albumId;
    @DatabaseField(columnName = "albumName")
    public String albumName;
    @DatabaseField(columnName = "albumData")
    public String albumData;
    @DatabaseField(columnName = "songPic")
    public String songPic;
    @DatabaseField(columnName = "duration")
    public int duration;
    @DatabaseField(columnName = "musicName")
    public String musicName;
    @DatabaseField(columnName = "artist")
    public String artist;
    @DatabaseField(columnName = "artistId")
    public long artistId;
    @DatabaseField(columnName = "data")
    public String data;
    @DatabaseField(columnName = "folder")
    public String folder;
    @DatabaseField(columnName = "lrc")
    public String lrc;
    @DatabaseField(columnName = "islocal")
    public boolean islocal;
    @DatabaseField(columnName = "sort")
    public String sort;
    @DatabaseField(columnName = "netUrl")
    public String netUrl;       //网络下载地址

    public MusicInfo() {

    }

    protected MusicInfo(Parcel in) {
        songId = in.readLong();
        albumId = in.readString();
        albumName = in.readString();
        albumData = in.readString();
        duration = in.readInt();
        musicName = in.readString();
        artist = in.readString();
        artistId = in.readLong();
        data = in.readString();
        folder = in.readString();
        lrc = in.readString();
        islocal = in.readByte() != 0;
        sort = in.readString();
        netUrl = in.readString();
    }

    public static final Creator<MusicInfo> CREATOR = new Creator<MusicInfo>() {
        @Override
        public MusicInfo createFromParcel(Parcel in) {
            return new MusicInfo(in);
        }

        @Override
        public MusicInfo[] newArray(int size) {
            return new MusicInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(songId);
        dest.writeString(albumId);
        dest.writeString(albumName);
        dest.writeString(albumData);
        dest.writeInt(duration);
        dest.writeString(musicName);
        dest.writeString(artist);
        dest.writeLong(artistId);
        dest.writeString(data);
        dest.writeString(folder);
        dest.writeString(lrc);
        dest.writeByte((byte) (islocal ? 1 : 0));
        dest.writeString(sort);
        dest.writeString(netUrl);
    }

    @Override
    public String toString() {
        return "MusicInfo{" +
                "songId=" + songId +
                ", albumId='" + albumId + '\'' +
                ", albumName='" + albumName + '\'' +
                ", albumData='" + albumData + '\'' +
                ", songPic='" + songPic + '\'' +
                ", duration=" + duration +
                ", musicName='" + musicName + '\'' +
                ", artist='" + artist + '\'' +
                ", artistId=" + artistId +
                ", data='" + data + '\'' +
                ", folder='" + folder + '\'' +
                ", lrc='" + lrc + '\'' +
                ", islocal=" + islocal +
                ", sort='" + sort + '\'' +
                ", netUrl='" + netUrl + '\'' +
                '}';
    }
}