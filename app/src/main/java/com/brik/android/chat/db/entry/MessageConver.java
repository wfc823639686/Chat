package com.brik.android.chat.db.entry;

import org.jivesoftware.smack.packet.Message;

/**
 * Created by wangfengchen on 15/7/8.
 */
public class MessageConver {

    public static OrmMessage toOrmMessage(Message message) {
        OrmMessage ormMessage = new OrmMessage();
        ormMessage.setType(message.getType().name());
        ormMessage.setBody(message.getBody());
        ormMessage.setFrom(message.getFrom());
        ormMessage.setTo(message.getTo());
        return ormMessage;
    }

    public static Message toMessage(OrmMessage ormMessage) {
        Message message = new Message();
        message.setType(Message.Type.chat);
        message.setBody(ormMessage.getBody());
        message.setFrom(ormMessage.getFrom());
        message.setTo(ormMessage.getTo());
        return message;
    }
}
