import processing.core.PImage;

import java.util.*;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Corpse implements Actionable{
    private final String id;
    private Point position;
    private final List<PImage> images;
    private int imageIndex;
    private int gracePeriod = 0;
    private double animationPeriod;

    public Corpse(String id, Point position, List<PImage> images, double animationPeriod) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.animationPeriod = animationPeriod;
    }

    public String getId() {
        return id;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public List<PImage> getImages() {
        return images;
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public double getAnimationPeriod(){
        return this.animationPeriod;
    }

    public void scheduleActions(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler){
        gracePeriod++;
        eventScheduler.scheduleEvent(this, Animation.createAnimationAction(this, 0), getAnimationPeriod());
    }

    public void setImageIndex(int index){
        this.imageIndex = index;
    }

}
