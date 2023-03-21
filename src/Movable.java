import java.util.List;

public interface Movable extends Executable{
    default boolean move(WorldModel world, Entity target, EventScheduler scheduler){
        Point nextPos = nextPosition(world, target.getPosition());

        if (!getPosition().equals(nextPos)) {
            world.moveEntity(scheduler, this, nextPos);
        }
        return false;
    }
    default Point nextPosition(WorldModel world, Point destPos){
        PathingStrategy path = new AStarPathingStrategy();
        List<Point> points = path.computePath(getPosition(), destPos, p ->  world.withinBounds(p) && !world.isOccupied(p),
                (p1, p2) -> p1.adjacent(p2), PathingStrategy.CARDINAL_NEIGHBORS);
        return points.size() > 0 ? points.get(0) : getPosition();
    }
}
