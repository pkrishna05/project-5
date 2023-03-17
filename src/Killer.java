import processing.core.PImage;

import java.awt.*;
import java.util.*;
import java.util.List;

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
            //move(world, scheduler, imageStore);
            move(world, fullTarget.get(), scheduler);
        } else {
            scheduler.scheduleEvent(this, Activity.createActivityAction(this, world, imageStore), getActionPeriod());
        }}

    public boolean move(WorldModel world, Entity target, EventScheduler scheduler) {
        // Fix this
        if (position.adjacent(target.getPosition())) {
            Entity corpse = new Corpse("corpse", target.getPosition(), imageStore.getImageList("dude"));
           world.removeEntity(scheduler, target);
           world.addEntity(corpse);
           this.executeActivity(world, imageStore, scheduler);
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
        /*
        int horiz = Integer.signum(destPos.getX() - position.getX());
        Point newPos = new Point(position.getX() + horiz, position.getY());

        if (horiz == 0 || world.isOccupied(newPos)) {
            int vert = Integer.signum(destPos.getY() - position.getY());
            newPos = new Point(position.getX(), position.getY() + vert);

            if (vert == 0 || world.isOccupied(newPos)) {
                newPos = position;
            }
        }

        return newPos;

         */



        PathingStrategy path = new AStarPathingStrategy();
        List<Point> points = path.computePath(getPosition(), destPos, p ->  world.withinBounds(p) && !world.isOccupied(p),
                (p1, p2) -> p1.adjacent(p2), PathingStrategy.CARDINAL_NEIGHBORS);
        return points.size() > 0 ? points.get(0) : getPosition();



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
