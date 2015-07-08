package com.brik.android.chat.db.entry;

import android.content.Context;

import com.brik.android.chat.db.BaseDAO;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by wangfengchen on 15/7/8.
 */
public class MessageDAO extends BaseDAO<OrmMessage> {

    public MessageDAO(Context context) {
        super(context);
    }

    @Override
    public void initDao() throws SQLException {
        dao = getHelper().getDao(OrmMessage.class);
    }

    @Override
    public int add(OrmMessage ormMessage) {
        try {
            return dao.create(ormMessage);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void edit(OrmMessage ormMessage) {

    }

    @Override
    public int remove(OrmMessage ormMessage) {
        return 0;
    }

    @Override
    public List<OrmMessage> listAll() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
