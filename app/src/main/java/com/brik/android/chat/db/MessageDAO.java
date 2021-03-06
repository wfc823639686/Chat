package com.brik.android.chat.db;

import android.content.Context;

import com.brik.android.chat.entry.IMessage;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by wangfengchen on 15/7/8.
 */
public class MessageDAO extends BaseDAO<IMessage> {

    public MessageDAO(Context context) {
        super(context);
    }

    @Override
    public void initDao() throws SQLException {
        dao = getHelper().getDao(IMessage.class);
    }

    @Override
    public int add(IMessage ormMessage) {
        try {
            return dao.create(ormMessage);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void edit(IMessage ormMessage) {

    }

    @Override
    public int remove(IMessage ormMessage) {
        return 0;
    }

    @Override
    public List<IMessage> listAll() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<IMessage> getMessage(String from,long offset, long limit) throws SQLException {
        QueryBuilder<IMessage, Integer> queryBuilder  = dao.queryBuilder();
        queryBuilder.where().like("from", "%"+from+"%").or().like("to", "%"+from+"%");
        return queryBuilder.offset(offset).limit(limit).query();
    }
}
