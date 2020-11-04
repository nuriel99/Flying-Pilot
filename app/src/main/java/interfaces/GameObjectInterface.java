package interfaces;

public interface GameObjectInterface {

    void startAnimation();

    void stopAnimation();

    void move();

    boolean isRunning();

    void setPositionX(int x);

    int getX();

    int getY();

    int getWidth();

    int getHeight();
}
