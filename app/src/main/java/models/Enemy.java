package models;

import android.widget.ImageView;

import interfaces.EnemyInterface;

public class Enemy extends GameObject implements EnemyInterface {
    public Enemy(ImageView imageView, int speed, int positionX, int positionY,int delay) {
        super(imageView, speed, positionX, positionY,delay);
    }

    public void move(){
        if (!this.isRunning()) {
            this.startAnimation();
        }

        super.move();
    }

    public void increaseSpeed() {
        this.speed = (int) (this.speed * 1.2);
    }
}
