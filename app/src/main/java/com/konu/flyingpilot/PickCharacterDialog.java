package com.konu.flyingpilot;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import enums.PREF_TYPES;
import models.CharacterOption;

import static com.konu.flyingpilot.MainActivity.PLAYER_ANIMATION;

public class PickCharacterDialog extends Dialog implements View.OnClickListener  {

    protected int PRICE = 200;
    public boolean canBuy = false;

    public int coins;
    private int selectedPlayer;
    public MainActivity mainActivity;
    public Dialog d;
    private double height;
    private TextView coinsTV;


    public PickCharacterDialog(MainActivity a, int selectedPlayer) {
        super(a);
        this.mainActivity = a;
        this.selectedPlayer = selectedPlayer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pick_player_dialog);

        //Setting dialog dimensions
        WindowManager wm = (WindowManager) mainActivity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        double width = metrics.widthPixels*.9;
        height = metrics.heightPixels*.85;
        Window win = this.getWindow();
        if (win != null) {
            win.setLayout((int) width, (int) height);
            win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        //Setting background opacity drakness
        win.setDimAmount(0.85f);

        //Setting RecyclerView
        final RecyclerView mHorizontalRecyclerView = (RecyclerView) findViewById(R.id.horizontalRecyclerView);
        CharacterImageOptionsAdapter horizontalAdapter = new CharacterImageOptionsAdapter(fillWithData(), this);
        final LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false);
        mHorizontalRecyclerView.setLayoutManager(horizontalLayoutManager);
        mHorizontalRecyclerView.setAdapter(horizontalAdapter);
        mHorizontalRecyclerView.setScrollbarFadingEnabled(false);

        ImageView close = findViewById(R.id.close_pick_dialog);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        coins = mainActivity.getSharedPreferences(PREF_TYPES.PLAYER.toString(),Context.MODE_PRIVATE).getInt(PREF_TYPES.COINS.toString(),0);
        coinsTV = findViewById(R.id.coins_pick_player);
        coinsTV.setText(mainActivity.getResources().getString(R.string.coins) + ": "+ coins);

        canBuy = coins >= PRICE;

    }


    private ArrayList<CharacterOption> fillWithData() {
        //Get saved characters of the user
        String s = mainActivity.getSharedPreferences(PREF_TYPES.PLAYER.toString(),Context.MODE_PRIVATE).getString(PREF_TYPES.CHARACTERS.toString(),"");

        ArrayList<CharacterOption> playerOptionArrayList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {//Pulling images and checking if they are owned
            CharacterOption playerOption = new CharacterOption();
            playerOption.setId(i);
            playerOption.setImagePath(mainActivity.getResources().getIdentifier(PLAYER_ANIMATION + (i+1), "drawable", mainActivity.getPackageName()));
            playerOption.setHeight(height*0.7);
            playerOptionArrayList.add(playerOption);
            playerOption.setSelected(selectedPlayer == i);
            playerOption.setOwned(i==0 || s.indexOf("p"+i) > -1);
        }

        return playerOptionArrayList;
    }

    @Override
    public void onClick(View view) {

    }

    public void setPlayer(int id) {
        mainActivity.setPlayer(id);

    }

    public void buyCharacter(int id) {//Adding character to the player and decrease the money
        String s = mainActivity.getSharedPreferences(PREF_TYPES.PLAYER.toString(), Context.MODE_PRIVATE).getString(PREF_TYPES.CHARACTERS.toString(), "");
        s += "p" + id;
        mainActivity.getSharedPreferences(PREF_TYPES.PLAYER.toString(), Context.MODE_PRIVATE).edit().putString(PREF_TYPES.CHARACTERS.toString(), s).commit();
        coins -= PRICE;
        mainActivity.getSharedPreferences(PREF_TYPES.PLAYER.toString(), Context.MODE_PRIVATE).edit().putInt(PREF_TYPES.COINS.toString(), coins).commit();
        coinsTV.setText(mainActivity.getResources().getString(R.string.coins) + ": " + coins);
        canBuy = coins >= PRICE;

    }
}
