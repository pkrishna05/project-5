import processing.core.PImage;

import java.util.*;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class DudeFull extends Dude{

    public DudeFull(String id, Point position, List<PImage> images, int resourceLimit, double actionPeriod, double animationPeriod) {
        super(id, position, images, resourceLimit, actionPeriod, animationPeriod);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = world.findNearest(getPosition(), new ArrayList<>(List.of(House.class)));

        if (fullTarget.isPresent() && move(world, fullTarget.get(), scheduler)) {
            transform(world, scheduler, imageStore);
        } else {
            scheduler.scheduleEvent(this, Activity.createActivityAction(this, world, imageStore), getActionPeriod());
        }
    }

    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        Actionable dude = CreateEntity.createDudeNotFull(super.getId(), super.getPosition(), super.getActionPeriod(), super.getAnimationPeriod(), super.getResourceLimit(), super.getImages());

        world.removeEntity(scheduler, this);

        world.addEntity(dude);
        dude.scheduleActions(world, imageStore, scheduler);
        return true;
    }

    public boolean move(WorldModel world, Entity target, EventScheduler scheduler) {
        if (super.getPosition().adjacent(target.getPosition())) {
            return true;
        } else {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!super.getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }


}
