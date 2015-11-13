package com.brik.chat.utils;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import org.jivesoftware.smack.packet.Message;

/**
 * Created by wangfengchen on 15/11/13.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {


    private static final Class<?>[] classes = new Class[]{
            Message.class
    };


    public static void main(String[] args) throws Exception {

        writeConfigFile("ormlite_config.txt", classes);

    }

}