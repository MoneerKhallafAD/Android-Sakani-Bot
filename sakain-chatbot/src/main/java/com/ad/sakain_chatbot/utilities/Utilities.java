package com.ad.sakain_chatbot.utilities;

import android.content.Context;
import android.util.DisplayMetrics;

public class Utilities {
    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
