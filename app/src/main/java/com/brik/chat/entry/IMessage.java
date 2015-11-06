package com.brik.chat.entry;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import java.util.Map;

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
    private String customType;
    private String fileUrl;
    private String filePath;
    private Long fileSize;
    private Long timeLength;

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

    public String getCustomType() {
        return customType;
    }

    public void setCustomType(String customType) {
        this.customType = customType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Long getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(Long timeLength) {
        this.timeLength = timeLength;
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
        if(message.getProperty("c-type")!=null) setCustomType((String) message.getProperty("c-type"));
        if(message.getProperty("file-url")!=null) setFileUrl((String) message.getProperty("file-url"));
        if(message.getProperty("file-path")!=null) setFilePath((String) message.getProperty("file-path"));
        if(message.getProperty("file-size")!=null) setFileSize((Long) message.getProperty("file-size"));
        if(message.getProperty("time-length")!=null) setTimeLength((Long) message.getProperty("time-length"));
    }

    public Message getMessage() {
        Message message = new Message();
        message.setBody(getBody());
        message.setFrom(getFrom());
        message.setTo(getTo());
        message.setType(Message.Type.fromString(getType()));
        if(getCustomType()!=null) message.setProperty("c-type", getCustomType());
        if(getFileUrl()!=null) message.setProperty("file-url", getFileUrl());
        if(getFilePath()!=null) message.setProperty("file-path", getFilePath());
        if(getFileSize()!=null) message.setProperty("file-size", getFileSize());
        if(getTimeLength()!=null) message.setProperty("time-length", getTimeLength());
        return message;
    }

}
