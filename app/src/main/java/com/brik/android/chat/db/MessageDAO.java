package com.brik.android.chat.db;

import android.content.Context;

import com.brik.android.chat.entry.MessageWrapper;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by wangfengchen on 15/7/8.
 */
public class MessageDAO extends BaseDAO<MessageWrapper> {

    public MessageDAO(Context context) {
        super(context);
    }

    @Override
    public void initDao() throws SQLException {
        dao = getHelper().getDao(MessageWrapper.class);
    }

    @Override
    public int add(MessageWrapper ormMessage) {
        try {
            return dao.create(ormMessage);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void edit(MessageWrapper ormMessage) {

    }

    @Override
    public int remove(MessageWrapper ormMessage) {
        return 0;
    }

    @Override
    public List<MessageWrapper> listAll() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<MessageWrapper> getMessage(String from,long offset, long limit) throws SQLException {
        QueryBuilder<MessageWrapper, Integer> queryBuilder  = dao.queryBuilder();
        queryBuilder.where().like("from", "%"+from+"%").and().like("to", "%"+from+"%");
        return queryBuilder.offset(offset).limit(limit).query();
    }
}
