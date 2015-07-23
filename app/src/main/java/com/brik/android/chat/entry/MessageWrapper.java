package com.brik.android.chat.entry;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.jivesoftware.smack.packet.Message;

/**
 * Created by wangfengchen on 15/7/8.
 */
@DatabaseTable(tableName="imessage")
public class MessageWrapper {

    private Message message;

    public MessageWrapper() {}

    public MessageWrapper(Message message) {
        setMessage(message);
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
        this.setFrom(message.getFrom());
        this.setBody(message.getBody());
        this.setTo(message.getTo());
        this.setType(message.getType().name());
        this.setThread(message.getThread());
    }

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
    @DatabaseField
    private String thread;

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

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }
}
