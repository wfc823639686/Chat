package com.brik.chat.db;

import android.content.Context;


import com.brik.chat.entry.IMessage;
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

    public List<IMessage> getMessage(String user,long offset, long limit) throws SQLException {
        QueryBuilder<IMessage, Integer> queryBuilder  = dao.queryBuilder();
//        queryBuilder.where().like("from", "%"+user+"%").or().like("to", "%"+user+"%");
        queryBuilder.where().eq("room_id", user).and().eq("type", "chat");
        return queryBuilder.offset(offset).limit(limit).query();
    }

    public List<IMessage> getGroupMessage(String groupId,long offset, long limit) throws SQLException {
        QueryBuilder<IMessage, Integer> queryBuilder  = dao.queryBuilder();
        queryBuilder.where().eq("room_id", groupId).and().eq("type", "groupchat");
        return queryBuilder.offset(offset).limit(limit).query();
    }
}
