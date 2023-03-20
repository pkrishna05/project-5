import processing.core.PImage;

import java.util.*;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Corpse implements Executable{
    private final String id;
    private Point position;
    private final List<PImage> images;
    private int imageIndex;
    private final double animationPeriod;
    private int gracePeriod = 0;
    private double actionPeriod;
    public Corpse(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.animationPeriod = animationPeriod;
        this.actionPeriod = actionPeriod;
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        this.gracePeriod++;
        System.out.println(gracePeriod);
        scheduler.scheduleEvent(this, Activity.createActivityAction(this, world, imageStore), this.actionPeriod);
        scheduler.scheduleEvent(this, Animation.createAnimationAction(this, 1), this.actionPeriod);
    }

    public double getAnimationPeriod() {
        return animationPeriod;
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

    public void setImageIndex(int index) {
        this.imageIndex = index;
    }

    public int getGracePeriod(){
        return this.gracePeriod;
    }

    @Override
    public double getActionPeriod() {
        return 0;
    }
}
