public interface Movable extends Executable{
    boolean move(WorldModel world, Entity target, EventScheduler scheduler);
    Point nextPosition(WorldModel world, Point destPos);
}
