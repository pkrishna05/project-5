public class Activity implements Action{
    private Executable entity;
    private final WorldModel world;
    private final ImageStore imageStore;

    public Activity(Executable entity, WorldModel world, ImageStore imageStore) {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
    }

    public static Activity createActivityAction(Executable entity, WorldModel world, ImageStore imageStore) {
        return new Activity(entity, world, imageStore);
    }

    public void execute(EventScheduler scheduler) {
        entity.executeActivity(world, imageStore, scheduler);
    }
}
