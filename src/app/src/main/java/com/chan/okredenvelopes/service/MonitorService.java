package com.chan.okredenvelopes.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.chan.okredenvelopes.MainActivity;
import com.chan.okredenvelopes.R;
import com.chan.okredenvelopes.receiver.AlarmReceiver;

import java.util.List;


public class MonitorService extends AccessibilityService {

    private static final String QQ_PACKAGE = "com.tencent.mobileqq";
    private static final String MIC_CHAT_PACKAGE = "com.tencent.mm";
    private static final String QQ_CHAT_INTERFACE = "com.tencent.mobileqq.activity.SplashActivity";
    private static final String MM_CHAT_INTERFACE = "com.tencent.mm.ui.LauncherUI";
    private static final String MM_OPEN_PACKAGE_INTERFACE = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = getServiceInfo();
        info.packageNames = new String[]{QQ_PACKAGE, MIC_CHAT_PACKAGE};
        setServiceInfo(info);
        super.onServiceConnected();
    }

    /*
    * com.tencent.mobileqq:id/name   口令
    * com.tencent.mobileqq:id/input 输入框
    * com.tencent.mobileqq:id/fun_btn 发送按钮
    *  com.tencent.mobileqq/.activity.SplashActivity  界面
    * */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();

        if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            unlockScreen();

            Log.d(TAG, String.valueOf(event.getParcelableData().toString()));
            List<CharSequence> texts = event.getText();
            if (!texts.isEmpty()) {
                for (CharSequence text : texts) {
                    String content = text.toString();
                    if (content.contains("红包")) {

                        Parcelable parcelable = event.getParcelableData();
                        //模拟打开通知栏消息
                        if (parcelable != null && parcelable instanceof Notification) {
                            Notification notification = (Notification) parcelable;
                            PendingIntent pendingIntent = notification.contentIntent;
                            try {
                                pendingIntent.send();
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            return;
        }

        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            AccessibilityNodeInfo nodeInfo = event.getSource();

            if (null != nodeInfo) {

                String clazzName = event.getClassName().toString();
                Log.d(TAG, clazzName);

                AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
                if (MM_CHAT_INTERFACE.equals(clazzName)) {
                    //开始抢红包
                    getPacket();
                } else if (MM_OPEN_PACKAGE_INTERFACE.equals(clazzName)) {
                    //开始打开红包
                    openPacket();
                }else if(QQ_CHAT_INTERFACE.equals(clazzName)){
                    recycleQQ(accessibilityNodeInfo);
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, MainActivity.newIntent(this), 0);
        Notification notification = new Notification.Builder(this)
                .setTicker(getText(R.string.app_name))
                .setWhen(System.currentTimeMillis())
                .setContentText("插件正在运行")
                .setContentTitle(getText(R.string.app_name))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        final int minutes = 5 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + minutes;

        Intent i = AlarmReceiver.newIntent();
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);

        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = AlarmReceiver.newIntent();
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.cancel(pi);
        super.onDestroy();
    }

    /**
     * 查找到
     */
    @SuppressLint("NewApi")
    private void openPacket() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo
                    .findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b2c");
            for (AccessibilityNodeInfo n : list) {
                n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }

    }

    @SuppressLint("NewApi")
    private void getPacket() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        recycleMM(rootNode);
    }

    /**
     * 打印一个节点的结构
     * @param info
     */
    @SuppressLint("NewApi")
    public boolean recycleMM(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            if(info.getText() != null){
                if("领取红包".equals(info.getText().toString())){

                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    AccessibilityNodeInfo parent = info.getParent();
                    while (parent != null) {
                        if(parent.isClickable()){
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            return true;
                        }
                        parent = parent.getParent();
                    }

                }
            }

        } else {

            final int count = info.getChildCount();
            for (int i = count - 1; i >= 0; i--) {
                if(info.getChild(i)!=null && recycleMM(info.getChild(i))){
                    return true;
                }
            }
        }

        return false;
    }

    private static final String TAG = "chan_debug";

    @SuppressWarnings("deprecated")
    private void unlockScreen() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        final KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("MyKeyguardLock");
        keyguardLock.disableKeyguard();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");

        wakeLock.acquire();
    }

    @SuppressLint("NewApi")
    public boolean recycleQQ(AccessibilityNodeInfo info) {

        final CharSequence description = info.getContentDescription();

        if (!TextUtils.isEmpty(description)) {
            Log.d(TAG, (String) description);
            if (((String) description).startsWith("口令:")) {
                final int start = 3;
                final int end = description.length() - 7;
                final String argument = ((String) description).substring(start, end);
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                        argument);
                final AccessibilityNodeInfo accessibilityNodeInfo = getRootInActiveWindow();
                List<AccessibilityNodeInfo> inputs =
                        accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mobileqq:id/input");
                if (!inputs.isEmpty()) {
                    inputs.get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                }

                List<AccessibilityNodeInfo> sends =
                        accessibilityNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mobileqq:id/fun_btn");
                if (!sends.isEmpty()) {
                    sends.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }

                return true;
            }
        }


        final int count = info.getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            if (info.getChild(i) != null && recycleQQ(info.getChild(i))) {
                return true;
            }
        }

        return false;
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, MonitorService.class);
    }
}
