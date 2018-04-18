package com.chigames.firebasedemo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nudennie.white on 4/18/18.
 */

public class TrackingManager {

    private static final String activity_cycle_oncreate = "activity_oncreate";
    private static final String activity_cycle_onstart = "activity_onstart";
    private static final String activity_cycle_onresume = "activity_onresume";
    private static final String activity_cycle_onpause = "activity_onpause";
    private static final String activity_cycle_onstop = "activity_onstop";
    private static final String activity_cycle_ondestroy = "activity_ondestroy";

    private static FirebaseAnalytics mFirebaseAnalytics;

    public static void TrackActivityCycleOnCreate(Context context, String userName, String pLevel, String year){
        // Here we can set aside what exactly we will track.

        Long currentTimeMillis = System.currentTimeMillis();

        Map<String, Object> eventParams = new HashMap<>();

        eventParams.put("timestamp", currentTimeMillis);
        eventParams.put("current_year", year);
        eventParams.put("user_name", userName);
        eventParams.put("programming_level", pLevel);

        RecordAnalyticalCustomEvent(context, activity_cycle_oncreate, eventParams);

    }
    public static void TrackActivityCycleOnStart(Context context, String userName, String pLevel, String year){
        // Here we can set aside what exactly we will track.

        Long currentTimeMillis = System.currentTimeMillis();

        Map<String, Object> eventParams = new HashMap<>();

        eventParams.put("timestamp", currentTimeMillis);
        eventParams.put("current_year", year);
        eventParams.put("user_name", userName);
        eventParams.put("programming_level", pLevel);

        RecordAnalyticalCustomEvent(context, activity_cycle_onstart, eventParams);

    }
    public static void TrackActivityCycleOnResume(Context context, String userName, String pLevel, String year){
        // Here we can set aside what exactly we will track.

        Long currentTimeMillis = System.currentTimeMillis();

        Map<String, Object> eventParams = new HashMap<>();

        eventParams.put("timestamp", currentTimeMillis);
        eventParams.put("current_year", year);
        eventParams.put("user_name", userName);
        eventParams.put("programming_level", pLevel);

        RecordAnalyticalCustomEvent(context, activity_cycle_onresume, eventParams);

    }
    public static void TrackActivityCycleOnPause(Context context, String userName, String pLevel, String year){
        // Here we can set aside what exactly we will track.

        Long currentTimeMillis = System.currentTimeMillis();

        Map<String, Object> eventParams = new HashMap<>();

        eventParams.put("timestamp", currentTimeMillis);
        eventParams.put("current_year", year);
        eventParams.put("user_name", userName);
        eventParams.put("programming_level", pLevel);

        RecordAnalyticalCustomEvent(context, activity_cycle_onpause, eventParams);

    }

    public static void TrackActivityCycleOnStop(Context context, String userName, String pLevel, String year){
        // Here we can set aside what exactly we will track.

        Long currentTimeMillis = System.currentTimeMillis();

        Map<String, Object> eventParams = new HashMap<>();

        eventParams.put("timestamp", currentTimeMillis);
        eventParams.put("current_year", year);
        eventParams.put("user_name", userName);
        eventParams.put("programming_level", pLevel);

        RecordAnalyticalCustomEvent(context, activity_cycle_onstop, eventParams);

    }

    public static void TrackActivityCycleOnDestroy(Context context, String userName, String pLevel, String year){
        // Here we can set aside what exactly we will track.

        Long currentTimeMillis = System.currentTimeMillis();

        Map<String, Object> eventParams = new HashMap<>();

        eventParams.put("timestamp", currentTimeMillis);
        eventParams.put("current_year", year);
        eventParams.put("user_name", userName);
        eventParams.put("programming_level", pLevel);

        RecordAnalyticalCustomEvent(context, activity_cycle_ondestroy, eventParams);

    }


    /// <summary>
    /// Reports a custom event
    /// </summary>
    /// <param name="eventEventName">Custom event name.</param>
    /// <param name="eventParams">A map of all the attributes to be recorded.</param>
    private static void RecordAnalyticalCustomEvent(Context context, String customEventName, Map<String, Object> eventParams) {

        Bundle bundle = new Bundle();
        for(Map.Entry<String, Object> entry : eventParams.entrySet()){
            bundle.putString(entry.getKey(), entry.getValue().toString());

            Log.wtf("KEY IS: ", entry.getValue().toString());
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mFirebaseAnalytics.logEvent(customEventName, bundle);
    }
}
