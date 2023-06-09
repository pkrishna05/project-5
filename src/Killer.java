import processing.core.PImage;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Killer implements Movable{
    private final String id;
    private Point position;
    private final List<PImage> images;
    private int imageIndex;
    private final double actionPeriod;
    private final double animationPeriod;
    private final ImageStore imageStore;

    public Killer(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, ImageStore imageStore) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.imageStore = imageStore;
    }


    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        // Fix this to kill dude
        Optional<Entity> fullTarget = world.findNearest(getPosition(), new ArrayList<>(List.of(DudeNotFull.class, DudeFull.class)));

        if (fullTarget.isPresent() && move(world, fullTarget.get(), scheduler)) {
            move(world, fullTarget.get(), scheduler);
        } else {
            scheduler.scheduleEvent(this, Activity.createActivityAction(this, world, imageStore), getActionPeriod());
        }
    }

    public boolean move(WorldModel world, Entity target, EventScheduler scheduler) {
        // Fix this
        if (position.adjacent(target.getPosition())) {
                // Creats and adds corpse
                Corpse corpse = new Corpse("corpse", target.getPosition(), imageStore.getImageList("corpse"), 0.05, 0.4);
                world.removeEntity(scheduler, target); // Removes target from the world
                world.addEntity(corpse);
                // Executes corpse activity
                corpse.executeActivity(world, imageStore, scheduler);
                this.executeActivity(world, imageStore, scheduler); // Finds new dude target
                return true;
        } else {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!position.equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public Point nextPosition(WorldModel world, Point destPos) {
        Background[][] background = world.getBackground();
        if (!background[position.getY()][position.getX()].getId().equals("bridge")) {
            background[position.getY()][position.getX()] = new Background("affected", imageStore.getImageList("blood"));
            world.setBackground(background);
        }

        return Movable.super.nextPosition(world, destPos);

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

    public double getActionPeriod() {
        return actionPeriod;
    }

}
