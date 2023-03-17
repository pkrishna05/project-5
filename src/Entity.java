import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public interface Entity {

    default PImage getCurrentImage() {
        return getImages().get(getImageIndex() % getImages().size());
    }


    default String log(){
        return this.getId().isEmpty() ? null :
                String.format("%s %d %d %d", this.getId(), this.getPosition().getX(), this.getPosition().getY(), this.getImageIndex());
    }

    String getId();

    Point getPosition();

    void setPosition(Point position);
    List<PImage> getImages();

    int getImageIndex();

}
