package com.brik.chat.db;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by wangfengchen on 15/5/26.
 */
public abstract class BaseDAO<T> {

    protected Dao<T, Integer> dao = null;
    //    private Context context = null;
    private OrmLiteSqliteOpenHelper helper = null;

    public BaseDAO(Context context) {
        helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        try {
            initDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public abstract void initDao() throws SQLException;

    public abstract int add(T t);

    /**
     * 更新
     */
    public abstract void edit(T t);

    public abstract int remove(T t);


    public abstract List<T> listAll();

    @Override
    protected void finalize() throws Throwable {
        OpenHelperManager.release();//释放掉helper
        super.finalize();
    }

    public OrmLiteSqliteOpenHelper getHelper() {
        return helper;
    }
}
