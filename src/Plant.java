
public interface Plant extends Executable {

    int getHealth();
    void setHealth(int health);

    default boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore){
        if (getHealth() <= 0) {
            Entity stump = CreateEntity.createStump(CreateEntity.STUMP_KEY + "_" + getId(), getPosition(), imageStore.getImageList(CreateEntity.STUMP_KEY));
            world.removeEntity(scheduler, this);
            world.addEntity(stump);
            return true;
        }
        return false;
    }

    @Override
    default void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if (!transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Activity.createActivityAction(this, world, imageStore), this.getActionPeriod());
        }
    }
}
