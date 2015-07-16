package com.brik.android.chat.db.entry;

import org.jivesoftware.smack.packet.Message;

/**
 * Created by wangfengchen on 15/7/8.
 */
public class MessageConver {

    public static OrmMessage toOrmMessage(Message message) {
        OrmMessage ormMessage = new OrmMessage();
        ormMessage.setOrmType(message.getType().name());
        ormMessage.setOrmBody(message.getBody());
        ormMessage.setOrmFrom(message.getFrom());
        ormMessage.setOrmTo(message.getTo());
        return ormMessage;
    }

    public static Message toMessage(OrmMessage ormMessage) {
        Message message = new Message();
        message.setType(Message.Type.chat);
        message.setBody(ormMessage.getOrmBody());
        message.setFrom(ormMessage.getOrmFrom());
        message.setTo(ormMessage.getOrmTo());
        return message;
    }
}
