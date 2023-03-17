public interface Executable extends Actionable{
    void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    default void scheduleActions(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler) {
        eventScheduler.scheduleEvent(this, Activity.createActivityAction(this, world, imageStore), getActionPeriod());
        eventScheduler.scheduleEvent(this, Animation.createAnimationAction(this, 0), getAnimationPeriod());
    }

    double getActionPeriod();
}
