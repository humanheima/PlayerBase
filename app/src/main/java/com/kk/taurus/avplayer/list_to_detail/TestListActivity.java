package com.kk.taurus.avplayer.list_to_detail;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kk.taurus.avplayer.R;
import com.kk.taurus.avplayer.utils.RelationAssistSingleton;

import java.util.ArrayList;
import java.util.List;

public class TestListActivity extends AppCompatActivity {

    private RecyclerView rv;

    private RvAdapter adapter;

    private List<MediaInfo> mediaInfoList;

    public static void launch(Context context) {
        Intent intent = new Intent(context, TestListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list);
        rv = findViewById(R.id.rv);

        initMediaData();

        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RvAdapter(mediaInfoList, getSupportFragmentManager());

        rv.setAdapter(adapter);
    }

    private void initMediaData() {
        mediaInfoList = new ArrayList<>();

        MediaInfo video = new MediaInfo();
        video.setType(MediaInfo.MEDIA_TYPE.VIDEO_AND_IMAGE);
        video.setVideoUrl("https://mov.bn.netease.com/open-movie/nos/mp4/2016/06/22/SBP8G92E3_hd.mp4");
        List<String> imageUrls = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            imageUrls.add("http://open-image.nosdn.127.net/image/snapshot_movie/2016/11/b/a/c36e048e284c459686133e66a79e2eba.jpg");
        }
        video.setImageUrls(imageUrls);

        mediaInfoList.add(video);


        for (int i = 0; i < 14; i++) {
            MediaInfo image = new MediaInfo();
            image.setType(MediaInfo.MEDIA_TYPE.IAMGE);
            image.setImageUrls(imageUrls);

            mediaInfoList.add(image);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RelationAssistSingleton.releaseAssist();
    }
}
