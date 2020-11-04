package models;

import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;

import interfaces.GameObjectInterface;

import static com.konu.flyingpilot.GameActivity.frameHeight;
import static com.konu.flyingpilot.GameActivity.screenWidth;

public class GameObject implements GameObjectInterface {
    protected ImageView image;
    protected AnimationDrawable animation;
    protected int positionX;
    protected int positionY;
    protected int speed;
    protected int delay;

    protected boolean running = false;

    public GameObject(ImageView imageView, int speed,int positionX,int positionY,int delay) {
        this.image = imageView;
        this.speed = speed;
        this.positionX = positionX;
        this.positionY = positionY;
        this.delay = delay;

        imageView.setX(positionX);


        animation = (AnimationDrawable) image.getDrawable();
    }

    public void startAnimation(){
        if(!running) {
            running = true;
            animation.start();
        }
    }

    public void stopAnimation() {
        if(running) {
            running = false;
            animation.stop();
        }
    }

    public void move() {
        positionX -= speed;
        if (positionX < 0) {//If out of screen -> new height and start over in X + delay before showing again
            positionX = screenWidth + this.delay;
            positionY = (int) Math.floor(Math.random() * (frameHeight - image.getHeight()));
        }

        image.setX(positionX);
        image.setY(positionY);
    }

    public boolean isRunning(){
        return this.running;
    }

    public void setPositionX(int x) {
        positionX = x;
    }

    @Override
    public int getX() {
        return positionX;
    }

    @Override
    public int getY() {
        return positionY;
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }
}

