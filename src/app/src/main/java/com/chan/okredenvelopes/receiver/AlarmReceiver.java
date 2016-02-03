package com.chan.okredenvelopes.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String INTENT_ACTION = "com.chan.ok.red.envelopes";


    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }

    public static Intent newIntent(){
        return new Intent(INTENT_ACTION);
    }
}
