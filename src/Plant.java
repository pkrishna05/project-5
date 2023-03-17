
public interface Plant extends Executable {

    int getHealth();
    void setHealth(int health);

    boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore);

    default boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        return transform(world, scheduler, imageStore);
    }

}
