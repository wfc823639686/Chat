package com.brik.chat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.brik.chat.entry.IMessage;
import com.j256.ormlite.android.DatabaseTableConfigUtil;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    // 数据库名称
    private static final String DATABASE_NAME = "iczcache1.db";
    // 数据库version
    private static final int DATABASE_VERSION = 12;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // 可以用配置文件来生成 数据表，有点繁琐，不喜欢用
        // super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            //            建立Commodity表
            DatabaseTableConfig<IMessage> config = DatabaseTableConfigUtil.fromClass(connectionSource, IMessage.class);
            TableUtils.createTableIfNotExists(connectionSource, config);
            Log.e(TAG, "onCreate tables -----> ");
        } catch (SQLException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.e(TAG, "onUpgrade tables -----> ");
            Log.e(TAG, "oldVersion -> "+oldVersion);
            Log.e(TAG, "newVersion -> "+newVersion);
            TableUtils.dropTable(connectionSource, IMessage.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }

    /**
     * 释放 DAO
     */
    @Override
    public void close() {
        super.close();
    }

}