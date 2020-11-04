package com.konu.flyingpilot;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import enums.PREF_TYPES;


public class ScoreboardActivity extends Activity {

    public static int TOP_SCORES_NUMBER = 10;

    String[] highScoreTable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scoreboard_activity);

        //Change board depend on locale
        if (Locale.getDefault().getLanguage().equals("iw")) {
            LinearLayout linearLayout = findViewById(R.id.score_layout);
            linearLayout.setBackground(getResources().getDrawable(R.drawable.background_score_heb));
        }

        highScoreTable = new String[TOP_SCORES_NUMBER];

        //Read scoreboard from user data and show it
        SharedPreferences scoreBoard = getSharedPreferences(PREF_TYPES.SCORE_BOARD.toString(), Context.MODE_PRIVATE);

        ListView listView = findViewById(R.id.list_view);
        ArrayList<Map<String, Object>> data = new ArrayList<>();

        for (int i = 0; i < TOP_SCORES_NUMBER; i++) {
            highScoreTable[i] = scoreBoard.getInt(PREF_TYPES.SCORE.toString() + i, 0) + "";
            HashMap<String, Object> score = new HashMap<>();

            score.put("image", this.getResources().getIdentifier(PREF_TYPES.SCORE.toString().toLowerCase() + (i + 1), "drawable", this.getPackageName()));//Get score index image by index
            score.put("value", highScoreTable[i]);
            data.add(score);
        }

        String[] from = {"image", "value"};
        int[] to = {R.id.numbers_img, R.id.numbers_view};

        //Attach data to listview
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.score_cell, from, to);
        listView.setAdapter(simpleAdapter);
    }

    public void goHome(View view) {
        finish();
    }
}
