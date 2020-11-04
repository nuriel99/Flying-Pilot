package models;

import android.view.View;
import android.widget.ImageView;

import interfaces.CoinInterface;

public class Coin extends GameObject implements CoinInterface {
    private int value;

    public Coin(ImageView imageView, int speed, int positionX, int positionY, int value,int delay) {
        super(imageView,speed,positionX,positionY,delay);
        imageView.setVisibility(View.VISIBLE);
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
