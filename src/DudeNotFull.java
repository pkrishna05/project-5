import processing.core.PImage;

import java.util.*;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class DudeNotFull extends Dude{
    private int resourceCount;

    public DudeNotFull(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, double actionPeriod, double animationPeriod) {
        super(id, position, images, resourceLimit, actionPeriod, animationPeriod);
        this.resourceCount = resourceCount;
    }

    public void executeActivity( WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(super.getPosition(), new ArrayList<>(Arrays.asList(Tree.class, Sapling.class)));

        if (target.isEmpty() || !move(world, target.get(), scheduler) || !transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Activity.createActivityAction(this, world, imageStore), super.getActionPeriod());
        }
    }

    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.resourceCount >= this.getResourceLimit()) {
            Actionable dude = CreateEntity.createDudeFull(this.getId(), this.getPosition(), this.getActionPeriod(), this.getAnimationPeriod(), this.getImages());

            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(dude);
            dude.scheduleActions(world, imageStore, scheduler);

            return true;
        }

        return false;
    }

    public boolean move(WorldModel world, Entity target, EventScheduler scheduler) {
        if (super.getPosition().adjacent(target.getPosition())) {
            this.resourceCount += 1;
            if(target instanceof Plant){ // TODO: Ask about this
                ((Plant)target).setHealth(((Plant)target).getHealth()-1);
            }
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
