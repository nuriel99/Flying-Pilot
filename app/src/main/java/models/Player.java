package models;

import android.widget.ImageView;

import interfaces.PlayerInterface;

import static com.konu.flyingpilot.GameActivity.frameHeight;

public class Player extends GameObject implements PlayerInterface {

    public Player(ImageView imageView, int speed, int positionX, int positionY) {
        super(imageView, speed, positionX, positionY,0);
    }

    public void move(boolean up){
        if(up){
            positionY -= speed;
        }else{
            positionY += speed;
        }

        // Check if player position inside the screen
        if (positionY < 0)
            positionY = 0;
        else if (positionY > frameHeight - image.getHeight())
            positionY = frameHeight - image.getHeight();

        image.setY(positionY);
    }

    //Checking if object hit the player
    //Take the players points and the object points and check if one inside the other
    public boolean checkHit(GameObject object) {
        if (object.getX() < 0 || object.getY() < 0) return false;
        int endPlayerX = this.positionX + (int)(image.getWidth()*0.85);
        int endPlayerY = this.positionY + (int)(image.getHeight()*0.8);
        int enemyX = object.getX();
        int endEnemyX = enemyX + (int)(object.getWidth()*0.9);
        int enemyY = object.getY();
        int endEnemyY = enemyY + (int)(object.getHeight()*0.8);


        return ((positionX > enemyX && positionX < endEnemyX) || (enemyX > positionX && enemyX < endPlayerX)) &&
                ((positionY > enemyY && positionY < endEnemyY) || (enemyY > positionY && enemyY < endPlayerY));
    }
}
