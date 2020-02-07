package com.kk.taurus.avplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.kk.taurus.avplayer.R;
import com.kk.taurus.avplayer.utils.GroupValueMap;
import com.kk.taurus.avplayer.utils.RelationAssistSingleton;
import com.kk.taurus.playerbase.assist.RelationAssist;
import com.kk.taurus.playerbase.player.IPlayer;

public class DetailSeamlessPlayActivity extends AppCompatActivity {

    private static final String TAG = "DetailSeamlessPlayActiv";
    private FrameLayout flDetailVideoContainer;
    private RelationAssist mAssist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_seamless_play);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        flDetailVideoContainer = findViewById(R.id.flDetailVideoContainer);
        mAssist = RelationAssistSingleton.getAssist(getApplicationContext());
        mAssist.attachContainer(flDetailVideoContainer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int state = mAssist.getState();
        if (state == IPlayer.STATE_PLAYBACK_COMPLETE)
            return;
        boolean userStart = mAssist.getReceiverGroup().getGroupValue().getBoolean(GroupValueMap.USER_START_PLAY);
        if (mAssist.isInPlaybackState() && userStart) {
            mAssist.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        int state = mAssist.getState();
        if (state == IPlayer.STATE_PLAYBACK_COMPLETE)
            return;
        /*if (mAssist.isInPlaybackState()) {
            mAssist.pause();
        } else {
            mAssist.stop();
        }*/
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        if (mAssist.isInPlaybackState()) {
            mAssist.pause();
        } else {
            mAssist.stop();
        }
        Intent intent = new Intent("action_reattach");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        super.onBackPressed();
    }

}
