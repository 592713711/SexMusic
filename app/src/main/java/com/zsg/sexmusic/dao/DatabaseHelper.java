package com.zsg.sexmusic.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.zsg.sexmusic.model.MusicInfo;
import com.zsg.sexmusic.util.L;

import java.sql.SQLException;

/**
 * Created by zsg on 2017/4/17.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static String DB_NAME="sexmusic.db";
    private Dao<MusicInfo, Integer> userDao;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 5);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, MusicInfo.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            TableUtils.dropTable(connectionSource, MusicInfo.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static DatabaseHelper instance;

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized DatabaseHelper getHelper(Context context)
    {
        if (instance == null)
        {
            synchronized (DatabaseHelper.class)
            {
                if (instance == null)
                    instance = new DatabaseHelper(context);
            }
        }

        return instance;
    }

    /**
     * 获得userDao
     *
     * @return
     * @throws SQLException
     */
    public Dao<MusicInfo, Integer> getMusicDao() throws SQLException
    {
        if (userDao == null)
        {
            userDao = getDao(MusicInfo.class);
        }
        return userDao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close()
    {
        super.close();
        userDao = null;
    }

}
