package com.kk.taurus.avplayer.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.kk.taurus.avplayer.R;
import com.kk.taurus.avplayer.utils.GroupValueMap;
import com.kk.taurus.avplayer.utils.RelationAssistSingleton;
import com.kk.taurus.playerbase.assist.RelationAssist;
import com.kk.taurus.playerbase.player.IPlayer;

public class DetailSeamlessPlayActivity extends AppCompatActivity {


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
        int state = mAssist.getState();
        if (state == IPlayer.STATE_PLAYBACK_COMPLETE)
            return;
        if (mAssist.isInPlaybackState()) {
            mAssist.pause();
        } else {
            mAssist.stop();
        }
    }
}
