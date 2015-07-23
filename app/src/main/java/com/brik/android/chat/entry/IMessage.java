package com.brik.android.chat.entry;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

/**
 * Created by wangfengchen on 15/7/8.
 */
@DatabaseTable(tableName="imessage")
public class IMessage {

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

    public IMessage(){}

    public IMessage(Message message) {
        setMessage(message);
    }

    public void setMessage(Message message) {
        setBody(message.getBody());
        setFrom(message.getFrom());
        setType(message.getType().name());
        setTo(message.getTo());
    }

    public Message getMessage() {
        Message message = new Message();
        message.setBody(getBody());
        message.setFrom(getFrom());
        message.setTo(getTo());
        message.setType(Message.Type.fromString(getType()));
        return message;
    }

}
