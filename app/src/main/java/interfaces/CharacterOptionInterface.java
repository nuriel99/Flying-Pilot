package interfaces;

import java.io.Serializable;

public interface CharacterOptionInterface extends Serializable {

    int getId();

    void setId(int id);

    int getImagePath();

    void setImagePath(int imagePath);

    void setHeight(Double height);

    int getHeight();

    boolean isSelected();

    void setSelected(boolean selected);

    void setOwned(boolean b);

    boolean isOwned();
}
