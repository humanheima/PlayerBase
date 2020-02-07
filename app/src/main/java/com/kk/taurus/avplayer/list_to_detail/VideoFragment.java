package com.kk.taurus.avplayer.list_to_detail;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.kk.taurus.avplayer.App;
import com.kk.taurus.avplayer.R;
import com.kk.taurus.avplayer.play.DataInter;
import com.kk.taurus.avplayer.play.ReceiverGroupManager;
import com.kk.taurus.avplayer.ui.DetailSeamlessPlayActivity;
import com.kk.taurus.avplayer.utils.GroupValueMap;
import com.kk.taurus.avplayer.utils.RelationAssistSingleton;
import com.kk.taurus.playerbase.assist.AssistPlay;
import com.kk.taurus.playerbase.assist.OnAssistPlayEventHandler;
import com.kk.taurus.playerbase.assist.RelationAssist;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment {


    private static final String VIDEO_URL = "VIDEO_URL";

    private String url;

    private static final String TAG = "VideoFragment";

    private FrameLayout flVideoContainer;
    private Button btnToDetail;
    private ImageView ivPlay;

    private RelationAssist mAssist;
    private ReceiverGroup receiverGroup;


    private OnAssistPlayEventHandler eventHandler = new OnAssistPlayEventHandler() {
        @Override
        public void onAssistHandle(AssistPlay assist, int eventCode, Bundle bundle) {
            super.onAssistHandle(assist, eventCode, bundle);
            switch (eventCode) {
                case DataInter.Event.EVENT_CODE_REQUEST_BACK:
                    //onBackPressed();
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

    public VideoFragment() {
        // Required empty public constructor
    }

    /**
     * @param url 视频播放路径
     */
    public static VideoFragment newInstance(String url) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putString(VIDEO_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(VIDEO_URL);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action_reattach");
        receiver = new AttachReceiver(
                new AttachReceiver.AttachCallback() {
                    @Override
                    public void attach(Context context, Intent intent) {
                        reAttach();
                    }
                });
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, intentFilter);
        initAssist();
    }

    private void initAssist() {
        mAssist = RelationAssistSingleton.getAssist(App.get());
        mAssist.getSuperContainer().setBackgroundColor(Color.BLACK);
        mAssist.setEventAssistHandler(eventHandler);

        receiverGroup = ReceiverGroupManager.get().getLiteReceiverGroup(getActivity());
        receiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_NETWORK_RESOURCE, true);
        mAssist.setReceiverGroup(receiverGroup);

        DataSource dataSource = new DataSource();
        dataSource.setData("https://mov.bn.netease.com/open-movie/nos/mp4/2016/01/11/SBC46Q9DV_hd.mp4");
        dataSource.setTitle("神奇的珊瑚");

        mAssist.setDataSource(dataSource);
        //标记用户手动播放或者暂停
        mAssist.getReceiverGroup().getGroupValue().putBoolean(GroupValueMap.USER_START_PLAY, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        flVideoContainer = view.findViewById(R.id.flVideoContainer);
        mAssist.attachContainer(flVideoContainer);
        ivPlay = view.findViewById(R.id.ivPlay);
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAssist.play();
            }
        });
        btnToDetail = view.findViewById(R.id.btnToDetail);

        btnToDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetailSeamlessPlayActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        return view;
    }

    public void reAttach() {
        Log.d(TAG, "reAttach: ");
        if (mAssist != null && flVideoContainer != null) {
            mAssist.attachContainer(flVideoContainer);
            if (mAssist.isInPlaybackState()) {
                mAssist.resume();
            } else {
                mAssist.play();
            }
        }
    }


    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach: ");
        mAssist = null;
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
        super.onDetach();
    }
}
