package com.kk.taurus.avplayer.list_to_detail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Crete by dumingwei on 2020-02-07
 * <p>
 * Desc: 收到这个广播，RelationAssist需要重新attach不同的到view上
 */
public class AttachReceiver extends BroadcastReceiver {

    private AttachCallback attachCallback;

    public AttachReceiver(AttachCallback attachCallback) {
        this.attachCallback = attachCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        attachCallback.attach(context, intent);
    }

    public interface AttachCallback {

        void attach(Context context, Intent intent);
    }


}