package com.plumya.bakingapp.utils;

import android.text.TextUtils;

import com.plumya.bakingapp.R;
import com.plumya.bakingapp.data.model.Step;

/**
 * Created by miltomasz on 19/05/18.
 */

public class VideoUtil {

    public static final String NO_VIDEO_AVAILABLE = "";
    private static final String NUTELLA_PIE = "Nutella Pie";
    private static final String BROWNIES = "Brownies";
    private static final String CHEESECAKE = "Cheesecake";
    private static final String YELLOW_CAKE = "Yellow Cake";

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

    public static int getDefaultRecipeImage(String recipeName) {
        switch (recipeName) {
            case NUTELLA_PIE: return R.drawable.nutella_pie;
            case BROWNIES: return R.drawable.brownies;
            case CHEESECAKE: return R.drawable.cheesecake;
            case YELLOW_CAKE: return R.drawable.yellow_cake;
            default:return R.drawable.recipe_placeholder;
        }
    }
}
