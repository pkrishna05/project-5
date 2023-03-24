import processing.core.PImage;

import java.util.List;

public class CreateEntity {

    public static final String TREE_KEY = "tree";
    public static final String STUMP_KEY = "stump";
    public static final String SAPLING_KEY = "sapling";
    private static final double SAPLING_ACTION_ANIMATION_PERIOD = 1.000; // have to be in sync since grows and gains health at same time
    private static final int SAPLING_HEALTH_LIMIT = 5;

    // don't technically need resource count ... full
    public static DudeFull createDudeFull(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images) {
        return new DudeFull(id, position, images, 0, actionPeriod, animationPeriod);
    }

    // need resource count, though it always starts at 0
    public static DudeNotFull createDudeNotFull(String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
        return new DudeNotFull(id, position, images, resourceLimit, 0, actionPeriod, animationPeriod);
    }

    public static Fairy createFairy(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images) {
        return new Fairy(id, position, images, actionPeriod, animationPeriod);
    }

    public static House createHouse(String id, Point position, List<PImage> images) {
        return new House(id, position, images);
    }

    public static Obstacle createObstacle(String id, Point position, double animationPeriod, List<PImage> images) {
        return new Obstacle(id, position, images, animationPeriod);
    }

    // health starts at 0 and builds up until ready to convert to Tree
    public static Sapling createSapling(String id, Point position, List<PImage> images) {
        return new Sapling(id, position, images, SAPLING_ACTION_ANIMATION_PERIOD, SAPLING_ACTION_ANIMATION_PERIOD, 0, SAPLING_HEALTH_LIMIT);
    }

    public static Stump createStump(String id, Point position, List<PImage> images) {
        return new Stump(id, position, images);
    }

    public static Tree createTree(String id, Point position, double actionPeriod, double animationPeriod, int health, List<PImage> images) {
        return new Tree(id, position, images, actionPeriod, animationPeriod, health);
    }

    public static void createKiller(Point pos, ImageStore imageStore, WorldModel world, EventScheduler scheduler){
        Killer killer = new Killer("blood_dude", pos, imageStore.getImageList("killer"), 0.3, 0.1, imageStore);
        world.addEntity(killer);
        killer.scheduleActions(world, imageStore, scheduler);
    }

    public static void createHealer(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images, WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Healer healer = new Healer(id, position, images, actionPeriod, animationPeriod);
        world.addEntity(healer);
        healer.scheduleActions(world, imageStore, scheduler);
    }
}
