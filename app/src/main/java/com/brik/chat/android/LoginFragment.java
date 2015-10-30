package com.brik.chat.android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.brik.chat.common.BaseFragment;
import com.google.inject.Inject;

import roboguice.inject.InjectView;

/**
 * Created by wangfengchen on 15/10/30.
 */
public class LoginFragment extends BaseFragment {

    @Inject
    private XMPPClient client;
    @Inject
    private SystemSettings settings;

    @InjectView(R.id.login_username)
    EditText usernameEdit;
    @InjectView(R.id.login_password)
    EditText passwordEdit;
    @InjectView(R.id.login_submit)
    Button submitBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.framgnet_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_submit:
                final String username = usernameEdit.getText().toString();
                final String password = passwordEdit.getText().toString();
                Log.d("username", username);
                Log.d("password", password);
                client.login(username, password, new XMPPClient.LoginListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("login", "登陆成功");
                        settings.setUsername(username);
                        settings.setPassword(password);
                    }

                    @Override
                    public void onFail(Throwable t) {
                        Log.e("login", "登陆失败", t);
                    }
                });
                break;
        }
    }

    void loginSuccess() {
        if(getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.loginSuccess();

        }
    }
}
