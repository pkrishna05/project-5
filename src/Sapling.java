import processing.core.PImage;

import java.util.*;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Sapling implements Plant{
    public static final int SAPLING_HEALTH = 0;
    private final double TREE_ANIMATION_MAX = 0.600;
    private final double TREE_ANIMATION_MIN = 0.050;
    private final double TREE_ACTION_MAX = 1.400;
    private final double TREE_ACTION_MIN = 1.000;
    private final int TREE_HEALTH_MAX = 3;
    private final int TREE_HEALTH_MIN = 1;
    private final String id;
    private Point position;
    private final List<PImage> images;
    private int imageIndex;
    private final double actionPeriod;
    private final double animationPeriod;
    private int health;
    private final int healthLimit;

    public Sapling(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int health, int healthLimit) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.health = health;
        this.healthLimit = healthLimit;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        this.health++;
        Plant.super.executeActivity(world, imageStore, scheduler);
    }

    private double getNumFromRange(double max, double min) {
        Random rand = new Random();
        return min + rand.nextDouble() * (max - min);
    }

    private int getIntFromRange(int max, int min) {
        Random rand = new Random();
        return min + rand.nextInt(max-min);
    }

    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (Plant.super.transform(world, scheduler, imageStore)) {
            return true;
        } else if (health >= healthLimit) {

            Actionable tree = CreateEntity.createTree(CreateEntity.TREE_KEY + "_" + id, position, getNumFromRange(TREE_ACTION_MAX, TREE_ACTION_MIN), getNumFromRange(TREE_ANIMATION_MAX, TREE_ANIMATION_MIN), getIntFromRange(TREE_HEALTH_MAX, TREE_HEALTH_MIN), imageStore.getImageList(CreateEntity.TREE_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(tree);
            tree.scheduleActions(world, imageStore, scheduler);

            return true;
        }
        return false;
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

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
