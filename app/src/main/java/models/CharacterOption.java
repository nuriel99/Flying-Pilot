package models;

import interfaces.CharacterOptionInterface;

public class CharacterOption implements CharacterOptionInterface {

    private int id;
    private int imagePath;
    private Double height;
    private boolean isSelected;
    private boolean isOwned;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImagePath() {
        return imagePath;
    }

    public void setImagePath(int imagePath) {
        this.imagePath = imagePath;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public int getHeight() {
        return (int) Math.round(height);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setOwned(boolean b) {
        isOwned = b;
    }

    public boolean isOwned() {
        return isOwned;
    }
}
