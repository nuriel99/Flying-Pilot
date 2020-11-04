package com.konu.flyingpilot;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.q42.android.scrollingimageview.ScrollingImageView;

import java.util.Timer;
import java.util.TimerTask;

import enums.PREF_TYPES;
import models.Coin;
import models.Enemy;
import models.Player;
import utils.MusicPlayer;

import static com.konu.flyingpilot.MainActivity.PLAYER_ANIMATION;

public class GameActivity extends  Activity {
    //Intent Const
    public static final String INTENT_SCORE = "SCORE";
    public static final String INTENT_COINS = "COINS";

    //Const
    private static final int ENEMY_DELAY = 50;
    private static final int COIN_SCORE_JUMP = 5;
    private final int SHOW_SILVER_COIN = 100;
    private final int SHOW_GOLD_COIN = 200;
    private final int OUT_OF_FRAME_POSITION = -500;
    private final int GAME_SPEED = 15;
    private final int JUMP = 100;
    private final int START_ENEMIES = 2;

    //TextViews
    private TextView scoreTV;
    private TextView startTV;
    private TextView levelupTV;
    private TextView coinsTV;

    //Buttons
    private Button pauseBtn;

    //Animations
    ScrollingImageView scrollingBackground;
    private Animation levelUpAnimation;

    // Size
    public static int frameHeight;
    public static int screenWidth;
    public static int screenHeight;

    // Score
    private int score = 0;
    private int coins = 0;
    private int ticks = 0;
    private int level = 0;

    //Initialize game thread
    private Handler gameHandler = new Handler();
    private Timer timer = new Timer();

    //Initialize sound
    private MusicPlayer sound;
    MediaPlayer backgroundMusic;
    private boolean playMusic;

    // Initialize Player
    private Player player;
    ImageView playerImage;

    // Initialize Coins
    private Coin bronzeCoin;
    private Coin silverCoin;
    private Coin goldCoin;

    // Initialize Enemies
    int maxEnemies = 5;
    int activeEnemies = 0;
    Enemy[] enemies;
    private int enemySize;
    private int enemySpeed;
    private int backgroundSpeed = 0;

    // Checkers
    private boolean isPressing = false;
    private boolean started = false;
    private boolean paused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        hideToolBr();

        initDimensions();

        initBackground();

        sound = new MusicPlayer(this);

        scoreTV = (TextView) findViewById(R.id.scoreLabel);
        startTV = (TextView) findViewById(R.id.startLabel);
        coinsTV = (TextView) findViewById(R.id.coinsLabel);


        pauseBtn = findViewById(R.id.pause_btn);

        levelupTV = (TextView) findViewById(R.id.level_up_tv);

        //Init Level Up Label
        levelUpAnimation = new AlphaAnimation(0.0f, 1.0f);
        levelUpAnimation.setDuration(600);
        levelUpAnimation.setRepeatMode(Animation.REVERSE);
        levelUpAnimation.setRepeatCount(1);

        //Set player character image from saved
        playerImage = (ImageView) findViewById(R.id.game_player);
        int selectedPlayer = getSharedPreferences(PREF_TYPES.PLAYER.toString(),MODE_PRIVATE).getInt(PREF_TYPES.SELECTED.toString(),0);
        playerImage.setImageResource(getResources().getIdentifier(PLAYER_ANIMATION+(selectedPlayer+1),"drawable",getPackageName()));

        scoreTV.setText(getResources().getString(R.string.score) + ": 0");
        coinsTV.setText(getResources().getString(R.string.coins) + ": 0");
    }

    private void initBackgroundMusic(){
        backgroundMusic = MediaPlayer.create(this, R.raw.ingame_music);
        backgroundMusic.setLooping(true);
        backgroundMusic.start();
    }

    private void initDimensions() {
        // Get screen size.
        WindowManager wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;
    }

    private void initBackground() { //Settings background speed from screen width
        scrollingBackground = (ScrollingImageView) findViewById(R.id.scrolling_background);
        scrollingBackground.stop();
        backgroundSpeed = (int) (screenWidth * 0.005);
        scrollingBackground.setSpeed(backgroundSpeed);
    }

    //Move Objects and Check for hit.
    //Increase score and coins if need
    public void moveBoard() {

        bronzeCoin.move();

        if (score > SHOW_SILVER_COIN) {
            if (!silverCoin.isRunning()) {
                silverCoin.startAnimation();
            }
            silverCoin.move();
        }

        if (score > SHOW_GOLD_COIN) {
            if (!goldCoin.isRunning()) {
                goldCoin.startAnimation();
            }
            goldCoin.move();
        }


        if (level < (int) score / JUMP) { //Check if level up
            level = (int) score / JUMP;
            if (activeEnemies < maxEnemies) { //Adding more enemies
                enemies[activeEnemies] = createNewEnemy();
            } else {
                for (int i = 0; i < activeEnemies; i++) { //If all enemies display, increasing speed
                    enemies[i].increaseSpeed();

                }
            }

            scrollingBackground.setSpeed(backgroundSpeed + level);
            levelupTV.startAnimation(levelUpAnimation);
        }

        for (int i = 0; i < activeEnemies; i++) {//Check if one of the enemies hit
            if (player.checkHit(enemies[i])) {
                sound.playOverSound();

                stopAndShowResults(score);
                return;
            }
            enemies[i].move();
        }

        // Move player
        player.move(isPressing);

        checkCoinHit();

        ticks++;

        score = ticks / 10;

        scoreTV.setText(getResources().getString(R.string.score) + ": " + score);
        coinsTV.setText(getResources().getString(R.string.coins) + ": " + coins);
    }


    //Check for coins hit and increase both score and coins if needed
    public void checkCoinHit() {

        if (player.checkHit(bronzeCoin)) {
            coins += bronzeCoin.getValue();
            score += COIN_SCORE_JUMP;
            bronzeCoin.setPositionX(OUT_OF_FRAME_POSITION);
            sound.playCollectSound();
        }

        if (player.checkHit(silverCoin)) {
            coins += silverCoin.getValue();
            score += COIN_SCORE_JUMP;
            silverCoin.setPositionX(OUT_OF_FRAME_POSITION);
            sound.playCollectSound();
        }

        // gold
        if (player.checkHit(goldCoin)) {
            coins += goldCoin.getValue();
            score += COIN_SCORE_JUMP;
            goldCoin.setPositionX(OUT_OF_FRAME_POSITION);
            sound.playCollectSound();
        }
    }

    //Open new intent with results and killing current one
    private void stopAndShowResults(int score) {
        // Stop Timer
        timer.cancel();

        if(backgroundMusic !=null){
            try{
                backgroundMusic.stop();
                backgroundMusic.release();
            }catch (Exception e){}
        }

        // Show Result

        Intent intent = new Intent(GameActivity.this, ResultActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(INTENT_SCORE, score);
        intent.putExtra(INTENT_COINS,coins);
        startActivity(intent);
        finish();
    }

    private void initGameObjects() {
        FrameLayout frame = (FrameLayout) findViewById(R.id.game_frame);
        frameHeight = frame.getHeight();

        //Init Player
        player = new Player(playerImage, (int) (screenHeight * 0.008), (int) playerImage.getX(), (int) playerImage.getY());

        //Init coins
        int speed = (int) (screenWidth * 0.005);
        bronzeCoin = new Coin((ImageView) findViewById(R.id.bronze_coin), speed, OUT_OF_FRAME_POSITION, OUT_OF_FRAME_POSITION, 5, 10);
        silverCoin = new Coin((ImageView) findViewById(R.id.silver_coin), speed, OUT_OF_FRAME_POSITION, OUT_OF_FRAME_POSITION, 10, 50);
        goldCoin = new Coin((ImageView) findViewById(R.id.gold_coin), speed, OUT_OF_FRAME_POSITION, OUT_OF_FRAME_POSITION, 20, 100);

        //Init Enemies
        enemySize = (int) (playerImage.getHeight() / 1.5);
        int maxEnemiesScreen = (int) (frameHeight - playerImage.getHeight()) / enemySize;
        enemies = new Enemy[Math.min(maxEnemies, maxEnemiesScreen)];
        enemySpeed = speed;
        for(int i=0;i<START_ENEMIES;i++){
            enemies[i] = createNewEnemy();
        }

        //String animations
        scrollingBackground.start();
        player.startAnimation();
        bronzeCoin.startAnimation();
    }

    //Creating new enemy and pulling image by index
    private Enemy createNewEnemy() {
        FrameLayout frame = (FrameLayout) findViewById(R.id.game_frame);
        ImageView imageView = new ImageView(this);
        int index = ++activeEnemies;
        imageView.setImageResource(index > 5 ? R.drawable.bird_animation_5 : this.getResources().getIdentifier("bird_animation_" + index, "drawable", this.getPackageName()));
        imageView.setLayoutParams(new ViewGroup.LayoutParams(enemySize, enemySize));
        frame.addView(imageView);
        return new Enemy(imageView, enemySpeed + index, OUT_OF_FRAME_POSITION, OUT_OF_FRAME_POSITION, ENEMY_DELAY * index);
    }

    //Detecting screen press
    public boolean onTouchEvent(MotionEvent me) {
        if (started) {
            if (me.getAction() == MotionEvent.ACTION_DOWN) {
                isPressing = true;

            } else if (me.getAction() == MotionEvent.ACTION_UP) {
                isPressing = false;
            }
        } else {
            started = true;

            //Calling here because waiting for objects to load
            initGameObjects();

            playMusic = getSharedPreferences(PREF_TYPES.PLAYER.toString(),MODE_PRIVATE).getBoolean(PREF_TYPES.PLAY_MUSIC.toString(),true);
            if(playMusic){
                initBackgroundMusic();
            }

            findViewById(R.id.start_help_label).setVisibility(View.GONE);
            startTV.setVisibility(View.GONE);

            //Starting game thread
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    gameHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            moveBoard();
                        }
                    });
                }
            }, 0, GAME_SPEED);

        }

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseMusic();

        if (!paused) {
            try {
                paused = true;

                //Stop the timer
                timer.cancel();
                scrollingBackground.stop();

                pauseBtn.setBackgroundResource(R.drawable.resume_btn);
            } catch (Exception e) {

            }
        }
    }

    private void resumeMusic() {
        if(!playMusic || backgroundMusic == null) return;
        try {
            backgroundMusic.start();
        }catch (Exception e){};
    }

    private void pauseMusic() {
        if(backgroundMusic == null) return;
        try {
            backgroundMusic.pause();
        }catch (Exception e){}
    }

    public void pauseGame(View view) {
        if (started) {
            if (!paused) {

                paused = true;
                //Stop the timer
                timer.cancel();
                scrollingBackground.stop();
                pauseMusic();

                pauseBtn.setBackgroundResource(R.drawable.resume_btn);
            } else {
                paused = false;

                pauseBtn.setBackgroundResource(R.drawable.pause_btn);
                resumeMusic();
                //Create and start the timer
                scrollingBackground.start();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        gameHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                moveBoard();
                            }
                        });
                    }
                }, 0, GAME_SPEED);
            }
        }
    }

    public void hideToolBr(){

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i("someting", "Turning immersive mode mode off. ");
        } else {
            Log.i("someting1", "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)

    }
}