
public interface Plant extends Executable {

    int getHealth();
    void setHealth(int health);

    boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore);


    @Override
    default void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if (!transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Activity.createActivityAction(this, world, imageStore), this.getActionPeriod());
        }
    }
}
