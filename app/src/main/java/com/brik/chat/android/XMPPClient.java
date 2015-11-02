package com.brik.chat.android;

import android.util.Log;

import com.google.inject.Inject;

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
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;

import java.lang.String;import java.lang.System;
import java.util.concurrent.ExecutorService;

/**
 * Created by wangfengchen on 15/6/26.
 */
public class XMPPClient {

    public interface ConnectListener {
        void onSuccess();
        void onFail(Throwable t);
    }

    public interface RosterListener {
        void onComplete(Roster roster);
    }

    public interface LoginListener {
        void onSuccess();
        void onFail(Throwable t);
    }

    public interface RegisterListener {
        void onComplete(int result);
    }

    @Inject
    ExecutorService executor;

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
        configure(ProviderManager.getInstance());
    }

    public void connect(final ConnectListener listener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    connect();
                    listener.onSuccess();
                } catch (XMPPException e) {
                    listener.onFail(e);
                }
            }
        });
    }

    public void register(final String username, final String password, final RegisterListener listener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                int result = register(username, password);
                listener.onComplete(result);
            }
        });
    }

    /**
     * 用户注册
     * @param username 注册用户名
     * @param password 注册密码
     * @return 1: 成功 0: 失败
     */
    public int register(String username, String password) {
        Registration reg = new Registration();
        reg.setType(IQ.Type.SET);
        reg.setTo(xmppConnection.getServiceName());
        reg.setUsername(username);
        reg.setPassword(password);
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
                return 0;
            } else {
               return -1;
            }
        } else if (result.getType() == IQ.Type.RESULT) {
            return 1;
        }
        return 0;
    }

    public XMPPConnection getXMPPConnection() {
        return xmppConnection;
    }

    public String serviceName() {
        return xmppConnection.getServiceName();
    }

    public void login(String username, String password) throws XMPPException {
        xmppConnection.login(username, password, "iphone 6终极土豪");
    }

    public void login(final String username, final String password, final LoginListener listener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    login(username, password);
                    listener.onSuccess();
                } catch (XMPPException e) {
                    listener.onFail(e);
                }
            }
        });
    }

    public String getUser() {
        return "123456@snowyoung.org";
    }

    public Roster getRoster() {
        return xmppConnection.getRoster();
    }

    public void getRoster(final RosterListener listener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                listener.onComplete(getRoster());
            }
        });
    }

    public ChatManager getChatManager() {
        return xmppConnection.getChatManager();
    }

    public void configure(ProviderManager pm) {

        //  Private Data Storage
        pm.addIQProvider("query","jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());

        //  Time
        try {
            pm.addIQProvider("query","jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (ClassNotFoundException e) {
            Log.w("TestClient", "Can't load class for org.jivesoftware.smackx.packet.Time");
        }

        //  Roster Exchange
        pm.addExtensionProvider("x","jabber:x:roster", new RosterExchangeProvider());

        //  Message Events
        pm.addExtensionProvider("x","jabber:x:event", new MessageEventProvider());

        //  Chat State
        pm.addExtensionProvider("active","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("composing","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("paused","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("inactive","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("gone","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());

        //  XHTML
        pm.addExtensionProvider("html","http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());

        //  Group Chat Invitations
        pm.addExtensionProvider("x","jabber:x:conference", new GroupChatInvitation.Provider());

        //  Service Discovery # Items
        pm.addIQProvider("query","http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());

        //  Service Discovery # Info
        pm.addIQProvider("query","http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());

        //  Data Forms
        pm.addExtensionProvider("x","jabber:x:data", new DataFormProvider());

        //  MUC User
        pm.addExtensionProvider("x","http://jabber.org/protocol/muc#user", new MUCUserProvider());

        //  MUC Admin
        pm.addIQProvider("query","http://jabber.org/protocol/muc#admin", new MUCAdminProvider());

        //  MUC Owner
        pm.addIQProvider("query","http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());

        //  Delayed Delivery
        pm.addExtensionProvider("x","jabber:x:delay", new DelayInformationProvider());

        //  Version
        try {
            pm.addIQProvider("query","jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
            //  Not sure what's happening here.
        }

        //  VCard
        pm.addIQProvider("vCard","vcard-temp", new VCardProvider());

        //  Offline Message Requests
        pm.addIQProvider("offline","http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());

        //  Offline Message Indicator
        pm.addExtensionProvider("offline","http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());

        //  Last Activity
        pm.addIQProvider("query","jabber:iq:last", new LastActivity.Provider());

        //  User Search
        pm.addIQProvider("query","jabber:iq:search", new UserSearch.Provider());

        //  SharedGroupsInfo
        pm.addIQProvider("sharedgroup","http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());

        //  JEP-33: Extended Stanza Addressing
        pm.addExtensionProvider("addresses","http://jabber.org/protocol/address", new MultipleAddressesProvider());

        //   FileTransfer
        pm.addIQProvider("si","http://jabber.org/protocol/si", new StreamInitiationProvider());

        pm.addIQProvider("query","http://jabber.org/protocol/bytestreams", new BytestreamsProvider());

        //  Privacy
        pm.addIQProvider("query","jabber:iq:privacy", new PrivacyProvider());
        pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
        pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
        pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
        pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
        pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
        pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());
    }

}
