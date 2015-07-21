package com.brik.android.chat.db.entry;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.jivesoftware.smack.packet.Message;

/**
 * Created by wangfengchen on 15/7/8.
 */
@DatabaseTable(tableName="imessage")
public class MessageWrapper {

    private Message message;

    public MessageWrapper() {

    }

    public MessageWrapper(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrmType() {
        return message.getType().name();
    }

    public void setOrmType(String ormType) {
        message.setType(Message.Type.chat);
    }

    public String getOrmBody() {
        return message.getBody();
    }

    public void setOrmBody(String ormBody) {
        message.setBody(ormBody);
    }

    public String getOrmFrom() {
        return message.getFrom();
    }

    public void setOrmFrom(String ormFrom) {
        message.setFrom(ormFrom);
    }

    public String getOrmTo() {
        return message.getTo();
    }

    public void setOrmTo(String ormTo) {
        message.setTo(ormTo);
    }
}
