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
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
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
import org.jivesoftware.smackx.search.UserSearchManager;

import java.io.File;
import java.lang.String;import java.lang.System;
import java.util.ArrayList;
import java.util.Iterator;
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

    public interface SearchUsersListener {
        void onSuccess(ArrayList<String> result);
        void onFail(Throwable t);
    }

    public interface SendTalkFileListener {
        void onSuccess();
        void onFail(Throwable t);
        void onProgress(double s);
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
     * @return 1: 成功 0: 失败 -1: 用户名已被使用
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
                return -1;
            } else {
               return 0;
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

    /**
     * 搜索用户
     */
    public ArrayList<String> searchUsers(String user) throws XMPPException {
        ArrayList<String> users = new ArrayList<String>();
        UserSearchManager usm = new UserSearchManager(xmppConnection);
        Form searchForm = usm.getSearchForm("search."
                    + xmppConnection.getServiceName());
            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("search", user);
            ReportedData data = usm.getSearchResults(answerForm, "search."
                    + xmppConnection.getServiceName());
            // column:jid,Username,Name,Email
            Iterator<ReportedData.Row> it = data.getRows();
            ReportedData.Row row = null;
            while (it.hasNext()) {
                row = it.next();
                // Log.d("UserName",
                // row.getValues("Username").next().toString());
                // Log.d("Name", row.getValues("Name").next().toString());
                // Log.d("Email", row.getValues("Email").next().toString());
                // 若存在，则有返回,UserName一定非空，其他两个若是有设，一定非空
                users.add(row.getValues("Username").next().toString());
            }
        return users;
    }

    public void searchUsers(final String user, final SearchUsersListener listener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<String> result = searchUsers(user);
                    listener.onSuccess(result);
                } catch (XMPPException e) {
                    listener.onFail(e);
                }
            }
        });
    }

    /**
     * 添加好友
     *
     * @param user
     */
    public void addFriend(String user) {
        try {
            // 添加好友
            Roster roster = xmppConnection.getRoster();
            roster.createEntry(user + "@snowyoung.org", null,
                    new String[] { "friends" });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送文件
     *
     * @param to
     * @param filepath
     */
    public OutgoingFileTransfer sendTalkFile(String to, String filepath, String des) throws XMPPException {
        FileTransferManager fileTransferManager = new FileTransferManager(
                xmppConnection);
        OutgoingFileTransfer outgoingFileTransfer = fileTransferManager
                .createOutgoingFileTransfer(to + "/Spark 2.6.3");
        File insfile = new File(filepath);
        outgoingFileTransfer.sendFile(insfile, des);
        return outgoingFileTransfer;
    }

    public void sendTalkFile(final String to, final String filepath, final String des, final SendTalkFileListener listener) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    OutgoingFileTransfer transfer = sendTalkFile(to, filepath, des);
                    while(!transfer.isDone()){
                        if(transfer.getStatus().equals(FileTransfer.Status.error)){
                            System.out.println("ERROR!!! " + transfer.getError());
                            listener.onFail(new Exception(transfer.getError().getMessage()));
                        }else{
                            System.out.println(transfer.getStatus()+"进度 "+transfer.getProgress());
                            listener.onProgress(transfer.getProgress());
                        }
                        Thread.sleep(1000);
                    }
                    listener.onSuccess();
                } catch (XMPPException | InterruptedException e) {
                    listener.onFail(e);
                }
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
