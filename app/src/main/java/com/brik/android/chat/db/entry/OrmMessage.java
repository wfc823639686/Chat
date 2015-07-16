package com.brik.android.chat.db.entry;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.jivesoftware.smack.packet.Message;

/**
 * Created by wangfengchen on 15/7/8.
 */
@DatabaseTable
public class OrmMessage {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String ormType;

    @DatabaseField
    private String ormBody;

    @DatabaseField
    private String ormFrom;

    @DatabaseField
    private String ormTo;

    public int state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrmType() {
        return ormType;
    }

    public void setOrmType(String ormType) {
        this.ormType = ormType;
    }

    public String getOrmBody() {
        return ormBody;
    }

    public void setOrmBody(String ormBody) {
        this.ormBody = ormBody;
    }

    public String getOrmFrom() {
        return ormFrom;
    }

    public void setOrmFrom(String ormFrom) {
        this.ormFrom = ormFrom;
    }

    public String getOrmTo() {
        return ormTo;
    }

    public void setOrmTo(String ormTo) {
        this.ormTo = ormTo;
    }
}
