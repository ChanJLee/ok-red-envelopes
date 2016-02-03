package com.chan.okredenvelopes.pages;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.chan.okredenvelopes.R;

public class ResponseFragment extends Fragment {
    private EditText m_editTextConnection;
    private EditText m_editTextQuestion;


    public static ResponseFragment newInstance() {
        ResponseFragment fragment = new ResponseFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View contentView = inflater.inflate(R.layout.fragment_response, container, false);
        initView(contentView);
        return contentView;
    }

    private void initView(View contentView) {
        m_editTextConnection = (EditText) contentView.findViewById(R.id.id_connection);
        m_editTextQuestion = (EditText) contentView.findViewById(R.id.id_question);

        contentView.findViewById(R.id.id_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String connection = m_editTextConnection.getText().toString();
                final String question = m_editTextQuestion.getText().toString();

                if (TextUtils.isEmpty(connection) || TextUtils.isEmpty(question)) {
                    Toast.makeText(getContext(), "不能有空白项", Toast.LENGTH_SHORT).show();
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        Email email = new SimpleEmail();
//                        email.setHostName("smtp.163.com");
//                        //email.setSmtpPort(25);
//                        email.setSslSmtpPort("465");
//                        email.setAuthenticator(new DefaultAuthenticator("17751759315@163.com", "ljc729576287"));
//                        email.setSSLOnConnect(true);

//                        Message message = m_handler.obtainMessage();
//                        try {
//                            email.setFrom(connection);
//                            email.setMsg(question);
//                            email.addTo("1355425625@qq.com");
//                            email.setSubject("红包拆拆");
//                            email.send();
//                            message.what = WHAT_SUCCESS;
//                        } catch (EmailException e) {
//                            e.printStackTrace();
//                            message.what = WHAT_ERROR;
//                        }
                        Message message = m_handler.obtainMessage();
                        message.what = WHAT_SUCCESS;
                        m_handler.sendMessageDelayed(message, 1000);
                    }
                }).start();
            }
        });
    }


    private static final int WHAT_ERROR = 0x0525;
    private static final int WHAT_SUCCESS = 0x0526;

    private Handler m_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            final int what = msg.what;

            String message = "提交成功,谢谢";
            if(what == WHAT_ERROR){
                message = "提交失败";
            }

            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    };
}
