package com.kk.taurus.avplayer.list_to_detail;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kk.taurus.avplayer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dumingwei on 2020-02-07.
 * Desc:
 */
public class RvAdapter extends RecyclerView.Adapter<RvAdapter.Vh> {


    private static final String TAG = "RvAdapter";

    private List<MediaInfo> mediaInfoList;

    private FragmentManager fragmentManager;

    public RvAdapter(List<MediaInfo> mediaInfoList, FragmentManager fragmentManager) {
        this.mediaInfoList = mediaInfoList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rv_adapter, parent, false);
        Vh vh = new Vh(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        MediaInfo mediaInfo = mediaInfoList.get(position);
        List<Fragment> fragmentList = new ArrayList<>();
        if (mediaInfo.type == MediaInfo.MEDIA_TYPE.VIDEO_AND_IMAGE) {
            VideoFragment videoFragment = VideoFragment.newInstance(mediaInfo.videoUrl);
            fragmentList.add(videoFragment);
        }
        for (String imageUrl : mediaInfo.imageUrls) {
            fragmentList.add(ImageFragment.newInstance(imageUrl));
        }
        VideoImageAdapter adapter = new VideoImageAdapter(fragmentManager, fragmentList);
        Log.d(TAG, "onBindViewHolder: position = " + position + " , " + fragmentList.size());

        //id必须大于1
        vh.viewPager.setId(position + 1);
        vh.viewPager.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return mediaInfoList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        ViewPager viewPager;

        public Vh(View itemView) {
            super(itemView);
            viewPager = itemView.findViewById(R.id.viewPager);
        }
    }

}
