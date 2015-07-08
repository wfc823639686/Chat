package com.brik.android.chat.db.entry;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by wangfengchen on 15/7/8.
 */
@DatabaseTable
public class OrmMessage {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String type;

    @DatabaseField
    private String body;

    @DatabaseField
    private String from;

    @DatabaseField
    private String to;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
