package com.chan.okredenvelopes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.chan.okredenvelopes.pages.HelpFragment;
import com.chan.okredenvelopes.pages.MainFragment;
import com.chan.okredenvelopes.pages.ResponseFragment;
import com.chan.okredenvelopes.pages.SettingFragment;
import com.chan.okredenvelopes.service.MonitorService;

public class MainActivity extends FragmentActivity {

    private static final String[] TAGS = {"首页", "设置", "帮助", "反馈"};
    private static final short INDEX_MAIN = 0;
    private static final short INDEX_SETTING = 1;
    private static final short INDEX_HELP = 2;
    private static final short INDEX_RESPONSE = 3;


    private TabLayout m_tabLayout;
    private int m_containerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_containerId = R.id.id_container;

        m_tabLayout = (TabLayout) findViewById(R.id.id_tab_layout);

        final int size = TAGS.length;
        for(int i = 0; i < size; ++i){
            final TabLayout.Tab tab = m_tabLayout.newTab();
            tab.setText(TAGS[i]);
            m_tabLayout.addTab(tab);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(m_containerId, new MainFragment());
        fragmentTransaction.commit();

        m_tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                final CharSequence text = tab.getText();

                int i = 0;
                for(; i < TAGS.length; ++i){
                    if(TAGS[i].equals(text)){
                        break;
                    }
                }

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (i) {
                    case INDEX_MAIN:
                        fragmentTransaction.replace(m_containerId, MainFragment.newInstance());
                        break;
                    case INDEX_SETTING:
                        fragmentTransaction.replace(m_containerId, SettingFragment.newInstance());
                        break;
                    case INDEX_HELP:
                        fragmentTransaction.replace(m_containerId, HelpFragment.newInstance());
                        break;
                    case INDEX_RESPONSE:
                        fragmentTransaction.replace(m_containerId, ResponseFragment.newInstance());
                        break;
                }

                fragmentTransaction.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }
}
