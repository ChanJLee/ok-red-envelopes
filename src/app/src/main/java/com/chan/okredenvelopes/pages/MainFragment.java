package com.chan.okredenvelopes.pages;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;

import com.chan.okredenvelopes.R;
import com.chan.okredenvelopes.service.MonitorService;

import java.util.List;

public class MainFragment extends Fragment {
    private Button m_buttonStart;
    private Button m_buttonEnd;
    private boolean m_started = false;


    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_main, container, false);
        initContentView(contentView);
        return contentView;
    }

    private void initContentView(View contentView){
        contentView.findViewById(R.id.id_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = getContext();
                Intent intent = MonitorService.newIntent(context);
                context.startService(intent);
                m_buttonStart.setEnabled(false);
                m_buttonEnd.setEnabled(true);
                m_started = true;
            }
        });

        contentView.findViewById(R.id.id_end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context context = getContext();
                Intent intent = MonitorService.newIntent(context);
                context.stopService(intent);
                getActivity().finish();
            }
        });

        m_buttonStart = (Button) contentView.findViewById(R.id.id_start);
        m_buttonEnd = (Button) contentView.findViewById(R.id.id_end);
    }

    @Override
    public void onResume() {
        super.onResume();

        m_buttonStart.setEnabled(!m_started);
        m_buttonEnd.setEnabled(m_started);

        final Activity activity = getActivity();
        AccessibilityManager manager = (AccessibilityManager)
                activity.getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = manager.
                getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);

        boolean found = false;
        for (AccessibilityServiceInfo info : runningServices) {
            if (info.getId().equals(activity.getPackageName() + "/.service.MonitorService")) {
                found = true;
                break;
            }
        }

        if(!found){
            m_buttonEnd.setEnabled(false);
            m_buttonStart.setEnabled(false);
        }
    }
}
