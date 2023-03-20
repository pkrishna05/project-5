public interface Executable extends Actionable{
    void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    default void scheduleActions(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler) {
        Actionable.super.scheduleActions(world, imageStore, eventScheduler);
        eventScheduler.scheduleEvent(this, Activity.createActivityAction(this, world, imageStore), getActionPeriod());
    }

    double getActionPeriod();
}
