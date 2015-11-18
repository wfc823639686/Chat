package com.brik.chat.entry;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.jivesoftware.smack.packet.Message;

/**
 * Created by wangfengchen on 15/7/8.
 */
@DatabaseTable(tableName="imessage")
public class IMessage implements Parcelable {

    public static final String CUSTOM_TYPE_TEXT = "text";
    public static final String CUSTOM_TYPE_AUDIO = "audio";
    public static final String CUSTOM_TYPE_IMAGE = "image";
    public static final String CUSTOM_TYPE_FILE = "file";

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
    @DatabaseField(columnName="custom_type")
    private String customType;
    @DatabaseField(columnName="file_url")
    private String fileUrl;
    @DatabaseField(columnName="file_path")
    private String filePath;
    @DatabaseField(columnName="file_size")
    private Long fileSize;
    @DatabaseField(columnName="time_length")
    private Long timeLength;
    @DatabaseField(columnName="room_id")
    private String roomId;
    @DatabaseField
    private Long timestamp;

    private String fromUser;

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

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFromUser() {
        if(from==null) return null;
        return from.split("/")[0];
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
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
        if(message.getProperty("custom-type")!=null) setCustomType((String) message.getProperty("custom-type"));
        if(message.getProperty("file-url")!=null) setFileUrl((String) message.getProperty("file-url"));
        if(message.getProperty("file-path")!=null) setFilePath((String) message.getProperty("file-path"));
        if(message.getProperty("file-size")!=null) setFileSize((Long) message.getProperty("file-size"));
        if(message.getProperty("time-length")!=null) setTimeLength((Long) message.getProperty("time-length"));
        if(message.getProperty("timestamp")!=null) setTimestamp((Long) message.getProperty("timestamp"));
    }

    public Message getMessage() {
        Message message = new Message();
        message.setBody(getBody());
        message.setFrom(getFrom());
        message.setTo(getTo());
        message.setType(Message.Type.fromString(getType()));
        if(getCustomType()!=null) message.setProperty("custom-type", getCustomType());
        if(getFileUrl()!=null) message.setProperty("file-url", getFileUrl());
        if(getFilePath()!=null) message.setProperty("file-path", getFilePath());
        if(getFileSize()!=null) message.setProperty("file-size", getFileSize());
        if(getTimeLength()!=null) message.setProperty("time-length", getTimeLength());
        if(getTimestamp()!=null) message.setProperty("timestamp", getTimestamp());
        return message;
    }



    private IMessage(Parcel in) {
        setId(in.readInt());
        setType(in.readString());
        setBody(in.readString());
        setFrom(in.readString());
        setTo(in.readString());
        setCustomType(in.readString());
        setFileUrl(in.readString());
        setFilePath(in.readString());
        setFileSize(in.readLong());
        setTimeLength(in.readLong());
        setTimestamp(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(getId());
        parcel.writeString(getType());
        parcel.writeString(getBody());
        parcel.writeString(getFrom());
        parcel.writeString(getTo());
        parcel.writeString(getCustomType());
        parcel.writeString(getFileUrl());
        parcel.writeString(getFilePath());
        parcel.writeLong(getFileSize() == null ? 0 : getFileSize());
        parcel.writeLong(getTimeLength() == null ? 0 : getTimeLength());
        parcel.writeLong(getTimestamp() == null ? 0 : getTimestamp());
    }

    public static final Parcelable.Creator<IMessage> CREATOR = new Parcelable.Creator<IMessage>() {
        public IMessage createFromParcel(Parcel in) {
            return new IMessage(in);
        }

        public IMessage[] newArray(int size) {
            return new IMessage[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "IMessage{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", body='" + body + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", customType='" + customType + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileSize=" + fileSize +
                ", timeLength=" + timeLength +
                ", roomId='" + roomId + '\'' +
                ", timestamp=" + timestamp +
                ", fromUser='" + fromUser + '\'' +
                '}';
    }
}
