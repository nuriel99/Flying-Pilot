package com.konu.flyingpilot;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.content.Intent;
import android.media.MediaPlayer;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import enums.PREF_TYPES;

public class MainActivity extends AppCompatActivity {

    //Const

    public static final String PLAYER_ANIMATION = "player_animation_";

    MediaPlayer mediaPlayer;
    private AnimationDrawable playerAnimation;
    Button soundBtn;
    SharedPreferences playerSP;
    private int selectedPlayer;
    boolean playing_music = false;
    private boolean playMusic;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerSP = getSharedPreferences(PREF_TYPES.PLAYER.toString(), MODE_PRIVATE);

        //Init sound
        soundBtn = findViewById(R.id.sound_btn);
        //Set button icon by user data
        playMusic = playerSP.getBoolean(PREF_TYPES.PLAY_MUSIC.toString(), true);
        soundBtn.setBackground(getResources().getDrawable(playMusic ? R.drawable.button_music : R.drawable.button_mute));

        if(playMusic) {
            initMediaPlayer();
            //playMusic();
        }

        //Init Player Character
        selectedPlayer = playerSP.getInt(PREF_TYPES.SELECTED.toString(), 0);
        setPlayer(selectedPlayer);
    }

    public void initMediaPlayer(){
        mediaPlayer = MediaPlayer.create(this, R.raw.home_sound);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    @Override
    protected void onPause() {//On Intent Pause
        super.onPause();
        playerAnimation.stop();
        stopMusic();
    }


    @Override
    protected void onResume() {//On Intent Resume
        super.onResume();
        playerAnimation.start();
        playMusic();
    }

    protected void playMusic() {
        if (playMusic && !playing_music) { //Check if needs to play + not playing
            if(mediaPlayer == null){
                initMediaPlayer();
            };
        }
    }


    protected void stopMusic() {
        if(mediaPlayer == null) return;
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        playing_music = false;
    }

    public void startGame(View view) {
        playerAnimation.stop();
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void scoreboard(View view) {
        playerAnimation.stop();
        Intent intent = new Intent(this, ScoreboardActivity.class);
        startActivity(intent);
    }

    public void changeSoundSettings(View view) {
        playMusic = !playMusic;
        if (playMusic) {
            initMediaPlayer();
            playMusic();
            soundBtn.setBackground(getResources().getDrawable(R.drawable.button_music));
        } else {
            stopMusic();
            soundBtn.setBackground(getResources().getDrawable(R.drawable.button_mute));
        }

        //Saving user choice for next time
        playerSP.edit().putBoolean("PLAY_MUSIC", playMusic).apply();
    }

    public void exit(View view) {
        ExitGameDialog cdd = new ExitGameDialog(this);
        cdd.show();
    }

    public void pickPlayer(View view) {
        PickCharacterDialog cdd = new PickCharacterDialog(this, selectedPlayer);
        cdd.show();
    }

    public void setPlayer(int id) {//Setting player image and animation from selected character
        if (playerAnimation != null) {
            playerAnimation.stop();
        }
        this.selectedPlayer = id;
        ImageView playerIV = findViewById(R.id.player_image);
        playerIV.setImageResource(getResources().getIdentifier(PLAYER_ANIMATION + (selectedPlayer + 1), "drawable", getPackageName()));
        playerAnimation = (AnimationDrawable) playerIV.getDrawable();
        playerAnimation.start();
        playerSP.edit().putInt(PREF_TYPES.SELECTED.toString(), id).commit();

    }

//    public void exit(View view) {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setMessage(getResources().getString(R.string.exit_msg));
//        builder.setCancelable(true);
//
//        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                MainActivity.this.finishAffinity();
//            }
//        });
//
//
//        builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        AlertDialog exit_dialog = builder.create();
//        exit_dialog.show();
//    }

    public void goInstructions(View view) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.instractions_dialog);
        View closePopUp = dialog.findViewById(R.id.close_image_dialog);
        View btnHome = dialog.findViewById(R.id.btn_home_dialog);

        closePopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setDimAmount(0.8f); //Set how dark the background will be
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }
}

