package com.kk.taurus.avplayer.list_to_detail;

import java.util.List;

/**
 * Created by dumingwei on 2020-02-07.
 * Desc:
 */
public class MediaInfo {

    enum MEDIA_TYPE {

        IAMGE,
        VIDEO_AND_IMAGE
    }

    public String videoUrl;

    public List<String> imageUrls;

    MEDIA_TYPE type = MEDIA_TYPE.VIDEO_AND_IMAGE;

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public MEDIA_TYPE getType() {
        return type;
    }

    public void setType(MEDIA_TYPE type) {
        this.type = type;
    }
}
