package com.chigames.firebasedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAnalytics firebaseAnalytics;
    Map<String, String> values = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        values.put("name", "Ttsukasa");
        values.put("programming_level", "Amazing");
        values.put("year", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));


        TrackingManager.TrackActivityCycleOnCreate(
                this,
                values.get("name"),
                values.get("programming_level"),
                values.get("year"));
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // set u button click handlers
        findViewById(R.id.btn_sendEvent1).setOnClickListener(this);
        findViewById(R.id.btn_sendEvent2).setOnClickListener(this);
        findViewById(R.id.btn_sendEvent3).setOnClickListener(this);

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();





        dbref.push().setValue(values, new DatabaseReference.CompletionListener() {


            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    Log.i("Info", "Save Successful");
                } else {
                    Log.i("Info", "Save Failed");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        // The bundle that will hold the data sent to
        // the Analytics package
        Bundle params = new Bundle();
        params.putInt("ButtonID", v.getId());
        String btnName = "test";

        switch (v.getId()) {
            case R.id.btn_sendEvent1:
                btnName = "Button_1_Click";
                setStatus("Button 1 Clicked");

                firebaseAnalytics.logEvent(btnName, params);
                break;

            case R.id.btn_sendEvent2:
                btnName = "Button_2_Click";
                setStatus("Button 2 Clicked");

                firebaseAnalytics.logEvent(btnName, params);
                break;

            case R.id.btn_sendEvent3:
                btnName = "Button_3_Click";
                setStatus("Button 3 Clicked");

                firebaseAnalytics.logEvent(btnName, params);
                break;
        }
        Log.d("Info", "Button click Logged: " + btnName);

    }

    private void setStatus(String text){
        TextView tvStatus = (TextView) findViewById(R.id.tv_Status);
        tvStatus.setText(text);
    }

    @Override
    protected void onStart() {
        super.onStart();
        TrackingManager.TrackActivityCycleOnStart(
                this,
                values.get("name"),
                values.get("programming_level"),
                values.get("year"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        TrackingManager.TrackActivityCycleOnStop(
                this,
                values.get("name"),
                values.get("programming_level"),
                values.get("year"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TrackingManager.TrackActivityCycleOnDestroy(
                this,
                values.get("name"),
                values.get("programming_level"),
                values.get("year"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        TrackingManager.TrackActivityCycleOnPause(
                this,
                values.get("name"),
                values.get("programming_level"),
                values.get("year"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        TrackingManager.TrackActivityCycleOnResume(
                this,
                values.get("name"),
                values.get("programming_level"),
                values.get("year"));
    }
}
