package com.konu.flyingpilot;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import enums.PREF_TYPES;

import static com.konu.flyingpilot.GameActivity.INTENT_COINS;
import static com.konu.flyingpilot.GameActivity.INTENT_SCORE;
import static com.konu.flyingpilot.ScoreboardActivity.TOP_SCORES_NUMBER;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);

        TextView scoreLabel = (TextView) findViewById(R.id.your_score_text);
        TextView highScoreLabel = (TextView) findViewById(R.id.highScoreLabel);
        TextView coinsLabel = (TextView) findViewById(R.id.coins_collected_label);

        //Getting data from Intent
        int score = getIntent().getIntExtra(INTENT_SCORE, 0);
        scoreLabel.setText(getResources().getString(R.string.yourscore) + score);

        int coins = getIntent().getIntExtra(INTENT_COINS,0);
        coinsLabel.setText(getResources().getString(R.string.coins_collected) + "  " + coins);

        //Saving new user coins sum
        int newCoins = coins + getSharedPreferences(PREF_TYPES.PLAYER.toString(),MODE_PRIVATE).getInt(PREF_TYPES.COINS.toString(),0);
        getSharedPreferences(PREF_TYPES.PLAYER.toString(),MODE_PRIVATE).edit().putInt(PREF_TYPES.COINS.toString(),newCoins).commit();

        //Getting new top score
        int topScore = initNewScore(score);

        if (score > topScore) {
            highScoreLabel.setText(getResources().getString(R.string.highscore) + "  " + score);

        } else {
            highScoreLabel.setText(getResources().getString(R.string.highscore) + "  " + topScore);
        }

        Button image = (Button) findViewById(R.id.again_btn);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.layout_id);

        LayoutTransition transition = new LayoutTransition();
        transition.enableTransitionType(LayoutTransition.CHANGING);
        mainLayout.setLayoutTransition(transition);

        ObjectAnimator animator;
        animator = ObjectAnimator.ofFloat(image, "rotation", -90).setDuration(2000);

        animator.setRepeatMode(ValueAnimator.REVERSE);

        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

    //Get score check if needs to add to list and return new high score
    private int initNewScore(int score) {
        int[] scores = new int[TOP_SCORES_NUMBER];

        SharedPreferences scoreBoard = getSharedPreferences(PREF_TYPES.SCORE_BOARD.toString(), Context.MODE_PRIVATE);


        for (int i = 0; i < scores.length; i++) {
            scores[i] = scoreBoard.getInt(PREF_TYPES.SCORE.toString() + i, 0);
        }

        if (score > scores[TOP_SCORES_NUMBER-1]) { //Expecting sorted list. checking if in the list range
            for (int i = 0; i < scores.length; i++) {
                if (scores[i] < score) {
                    int temp1 = scores[i];
                    int temp2;
                    for (int j = i; j < scores.length - 1; j++) {//Going on the rest of the array and moving all 1 forward
                        temp2 = scores[j + 1];
                        scores[j + 1] = temp1;
                        temp1 = temp2;
                    }
                    scores[i] = score;
                    break;
                }
            }

            //Saving the new scoreboard
            SharedPreferences.Editor editor = scoreBoard.edit();
            for (int i = 0; i < scores.length; i++) {
                editor.putInt(PREF_TYPES.SCORE.toString() + i, scores[i]);
            }

            editor.commit();
        }

        return scores[0]; //Return new high score
    }


    public void retryAgain(View view) {
        //playerAnimation.stop();
        Intent intent = new Intent(this, GameActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    public void goHome(View view) {
        finish();
    }

    public void highScores(View v) {
        Intent intent = new Intent(this, ScoreboardActivity.class);
        startActivity(intent);
    }

}

