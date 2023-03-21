import processing.core.PImage;

import java.util.*;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Fairy implements Movable{
    private final String id;
    private Point position;
    private final List<PImage> images;
    private int imageIndex;
    private final double actionPeriod;
    private final double animationPeriod;

    public Fairy(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }


    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = world.findNearest(this.position, new ArrayList<>(List.of(Stump.class, Corpse.class)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();

            if (fairyTarget.get().getClass() == Corpse.class){
                if(((Corpse)fairyTarget.get()).getGracePeriod() > 10){
                    if(move(world, fairyTarget.get(), scheduler)){
                        scheduler.unscheduleAllEvents(fairyTarget.get());
                        DudeNotFull dude = new DudeNotFull("dudeNotFull", tgtPos, imageStore.getImageList("dude"), 1, 0, 0.4, 0.3);
                        world.addEntity(dude);
                        dude.scheduleActions(world, imageStore, scheduler);
                    }

                }
            } else {
                if(move(world, fairyTarget.get(), scheduler)){
                    Actionable sapling = CreateEntity.createSapling(CreateEntity.SAPLING_KEY + "_" + fairyTarget.get().getId(), tgtPos, imageStore.getImageList(CreateEntity.SAPLING_KEY));
                    world.addEntity(sapling);
                    sapling.scheduleActions(world, imageStore, scheduler);
                }

            }

            }

        scheduler.scheduleEvent(this, Activity.createActivityAction(this, world, imageStore), this.actionPeriod);
    }

    public boolean move(WorldModel world, Entity target, EventScheduler scheduler) {
        if (position.adjacent(target.getPosition())) {
            world.removeEntityAt(target.getPosition());
            return true;
        } else {
            return Movable.super.move(world, target, scheduler);
        }
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
