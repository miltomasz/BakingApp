package com.plumya.bakingapp.utils;

import android.text.TextUtils;

import com.plumya.bakingapp.data.model.Step;

/**
 * Created by miltomasz on 19/05/18.
 */

public class VideoUtil {

    public static final String NO_VIDEO_AVAILABLE = "";

    private VideoUtil() {}

    public static String urlForPlayback(Step step) {
        if (step == null) {
            return NO_VIDEO_AVAILABLE;
        }
        String videoUrl = step.videoURL;
        String thumbnailUrl = step.thumbnailURL;

        if (TextUtils.isEmpty(videoUrl)) {
            if (TextUtils.isEmpty(thumbnailUrl)) {
                return NO_VIDEO_AVAILABLE;
            } else {
                return thumbnailUrl;
            }
        } else {
            return videoUrl;
        }
    }
}
