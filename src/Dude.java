import processing.core.PImage;

import java.util.List;

public abstract class Dude implements Movable{
    private final String id;
    private Point position;
    private final List<PImage> images;
    private int imageIndex;
    private int resourceLimit;
    private final double actionPeriod;
    private final double animationPeriod;

    public Dude(String id, Point position, List<PImage> images, int resourceLimit, double actionPeriod, double animationPeriod) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    public Point nextPosition(WorldModel world, Point destPos) {

        PathingStrategy path = new AStarPathingStrategy();

        List<Point> points = path.computePath(getPosition(), destPos,
                p -> world.withinBounds(p) && (!world.isOccupied(p) || world.getOccupancyCell(p).getClass() == Stump.class),
                (p1, p2) -> p1.adjacent(p2), PathingStrategy.CARDINAL_NEIGHBORS);


        //System.out.println(points.size());
        return points.size() > 0 ? points.get(0) : getPosition();
    }

    abstract boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore);

    public String getId() {
        return id;
    }

    public double getAnimationPeriod() {
        return animationPeriod;
    }

    public int getResourceLimit(){
        return resourceLimit;
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
