package com.kk.taurus.avplayer.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.kk.taurus.avplayer.R;
import com.kk.taurus.avplayer.list_to_detail.AttachReceiver;
import com.kk.taurus.avplayer.play.DataInter;
import com.kk.taurus.avplayer.play.ReceiverGroupManager;
import com.kk.taurus.avplayer.utils.GroupValueMap;
import com.kk.taurus.avplayer.utils.RelationAssistSingleton;
import com.kk.taurus.playerbase.assist.AssistPlay;
import com.kk.taurus.playerbase.assist.OnAssistPlayEventHandler;
import com.kk.taurus.playerbase.assist.RelationAssist;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;

public class TestToDetailActivity extends AppCompatActivity {


    private static final String TAG = "TestToDetailActivity";

    private FrameLayout flVideoContainer;
    private Button btnToDetail;

    private RelationAssist mAssist;
    private ReceiverGroup receiverGroup;


    private OnAssistPlayEventHandler eventHandler = new OnAssistPlayEventHandler() {
        @Override
        public void onAssistHandle(AssistPlay assist, int eventCode, Bundle bundle) {
            super.onAssistHandle(assist, eventCode, bundle);
            switch (eventCode) {
                case DataInter.Event.EVENT_CODE_REQUEST_BACK:
                    onBackPressed();
                    break;
                case DataInter.Event.EVENT_CODE_ERROR_SHOW:
                    mAssist.stop();
                    break;
                default:
                    break;
            }
        }
    };
    private AttachReceiver receiver;

    public static void launch(Context context) {
        Intent intent = new Intent(context, TestToDetailActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_to_detail);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        flVideoContainer = findViewById(R.id.flVideoContainer);
        btnToDetail = findViewById(R.id.btnToDetail);
        btnToDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestToDetailActivity.this, DetailSeamlessPlayActivity.class);
                startActivity(intent);
            }
        });

        mAssist = RelationAssistSingleton.getAssist(getApplicationContext());
        mAssist.getSuperContainer().setBackgroundColor(Color.BLACK);
        mAssist.setEventAssistHandler(eventHandler);

        receiverGroup = ReceiverGroupManager.get().getLiteReceiverGroup(this);
        receiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_NETWORK_RESOURCE, true);
        mAssist.setReceiverGroup(receiverGroup);

        DataSource dataSource = new DataSource();
        dataSource.setData("https://mov.bn.netease.com/open-movie/nos/mp4/2016/01/11/SBC46Q9DV_hd.mp4");
        dataSource.setTitle("神奇的珊瑚");

        mAssist.setDataSource(dataSource);
        //标记用户手动播放或者暂停
        mAssist.getReceiverGroup().getGroupValue().putBoolean(GroupValueMap.USER_START_PLAY, true);
        mAssist.attachContainer(flVideoContainer);
        mAssist.play();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action_reattach");

        receiver = new AttachReceiver(
                new AttachReceiver.AttachCallback() {
                    @Override
                    public void attach(Context context, Intent intent) {
                        Log.d(TAG, "onActivityResult: ");
                        mAssist.attachContainer(flVideoContainer);
                    }
                });
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        int state = mAssist.getState();
        if (state == IPlayer.STATE_PLAYBACK_COMPLETE)
            return;
        boolean userStart = receiverGroup.getGroupValue().getBoolean(GroupValueMap.USER_START_PLAY);
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

    @Override
    protected void onDestroy() {
        RelationAssistSingleton.releaseAssist();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        mAssist = null;
        super.onDestroy();
    }
}
