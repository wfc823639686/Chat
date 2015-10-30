package com.brik.chat.android;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.brik.chat.common.BaseFragment;
import com.google.inject.Inject;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Created by wangfengchen on 15/7/23.
 */
public class CreateMultiUserChatFragment extends BaseFragment implements View.OnClickListener{

    private MultiUserChat muc;

    @Inject
    private LayoutInflater layoutInflater;

    @InjectView(R.id.cmuc_edit_name)
    private EditText nameEdit;
    @InjectView(R.id.cmuc_edit_pass)
    private EditText passEdit;
    @InjectView(R.id.cmuc_edit_intro)
    private EditText introEdit;
    @InjectView(R.id.cmuc_submit)
    private Button submitButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_multi_user_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        submitButton.setOnClickListener(this);
    }

    void createForExc(final String roomName, final String password, final String intro) {
        AsyncTask<Void, Void, Integer> async = new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    create(roomName, password, intro);
                    return 1;
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer result) {
                if(result==null) {
                    Toast.makeText(getActivity(), "创建失败", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "创建成功", Toast.LENGTH_LONG).show();
                }
            }

        };
        async.execute();
    }

    void create(String roomName, String password, String intro) throws XMPPException {
        //----创建手动配置聊天室----
        muc = new MultiUserChat(XMPPClient.getInstance().getXMPPConnection(),
               "room2@conference.snowyoung.org");
        muc.create("brik");
        //获取聊天室的配置表单
        Form form = muc.getConfigurationForm();
        //根据原始表单创建一个要提交的新表单
        Form submitForm = form.createAnswerForm();
        //向提交的表单添加默认答复
        for(Iterator<FormField> fields = form.getFields(); fields.hasNext();) {
            FormField field = fields.next();
            if(!FormField.TYPE_HIDDEN.equals(field.getType()) && field.getVariable() != null) {
                submitForm.setDefaultAnswer(field.getVariable());
            }
        }

        //重新设置聊天室名称
        submitForm.setAnswer("muc#roomconfig_roomname", roomName);
        //设置聊天室的新拥有者
        List<String> owners = new ArrayList<>();
        owners.add(XMPPClient.getInstance().getUser());
        submitForm.setAnswer("muc#roomconfig_roomowners", owners);
        //设置密码
        if(password!=null && !password.isEmpty()) {
            submitForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);
            submitForm.setAnswer("muc#roomconfig_roomsecret", password);
        }

        //设置描述
        if(intro!=null && !intro.isEmpty()) {
            submitForm.setAnswer("muc#roomconfig_roomdesc", intro);
        }
        //设置聊天室是持久聊天室，即将要被保存下来
        submitForm.setAnswer("muc#roomconfig_persistentroom", true);
        //发送已完成的表单到服务器配置聊天室
        muc.sendConfigurationForm(submitForm);
        muc.join("喝醉的毛毛虫");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cmuc_submit:
                String rootName = nameEdit.getText().toString();
                String password = passEdit.getText().toString();
                String intro = introEdit.getText().toString();
                createForExc(rootName, password, intro);
                break;
        }
    }
}
