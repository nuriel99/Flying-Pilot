package interfaces;

import models.GameObject;

public interface PlayerInterface extends GameObjectInterface {

    void move(boolean up);

    boolean checkHit(GameObject object);
}
