package com.brik.android.chat;

import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.ProviderManager;import java.lang.String;import java.lang.System;

/**
 * Created by wangfengchen on 15/6/26.
 */
public class XMPPClient {

    private XMPPConnection xmppConnection;

    private static XMPPClient xmppClient = new XMPPClient();

    public static XMPPClient getInstance() {
        return xmppClient;
    }

    public void connect() throws XMPPException {
        XMPPConnection.DEBUG_ENABLED = true;
        final ConnectionConfiguration connectionConfig = new ConnectionConfiguration(
                "snowyoung.org", 5222, "snowyoung.org");
        xmppConnection = new XMPPConnection(connectionConfig);
        xmppConnection.connect();
    }

    public void register() {
        Registration reg = new Registration();
        reg.setType(IQ.Type.SET);
        reg.setTo(xmppConnection.getServiceName());
        reg.setUsername("123456");
        reg.setPassword("123456");
        reg.addAttribute("android", "geolo_createUser_android");
        System.out.println("reg:" + reg);
        PacketFilter filter = new AndFilter(new PacketIDFilter(reg
                .getPacketID()), new PacketTypeFilter(IQ.class));
        PacketCollector collector = xmppConnection
                .createPacketCollector(filter);
        xmppConnection.sendPacket(reg);

        IQ result = (IQ) collector.nextResult(SmackConfiguration
                .getPacketReplyTimeout());
        // Stop queuing results
        collector.cancel();
        if (result == null) {
            System.out.println("服务器错误");
        } else if (result.getType() == IQ.Type.ERROR) {
            if (result.getError().toString().equalsIgnoreCase(
                    "conflict(409)")) {

            } else {

            }
        } else if (result.getType() == IQ.Type.RESULT) {

        }
    }

    public void login(String username, String password) throws XMPPException {
        xmppConnection.login(username, password, "iphone 6终极土豪");
    }

    public Roster getRoster() {
        return xmppConnection.getRoster();
    }

    public ChatManager getChatManager() {
        return xmppConnection.getChatManager();
    }

}
