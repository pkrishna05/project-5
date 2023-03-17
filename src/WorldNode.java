import java.util.Objects;

public class WorldNode {
    private final int h;
    private final int g;
    private final Point position;
    private final WorldNode previous;
    public WorldNode(Point position, WorldNode previous, Point target){
        this.h = calculateH(position, target);
        this.g = previous == null ? 0 : previous.getG() + 1;
        this.position = position;
        this.previous = previous;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorldNode worldNode = (WorldNode) o;
        return Objects.equals(position, worldNode.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }

    private int calculateH(Point start, Point end){
        return Math.abs(end.x-start.x) + Math.abs(end.y-start.y);
    }

    @Override
    public String toString() {
        return "WORLD NODE: Position - (" + position.x + ", " + position.y + ") - F-value: " + this.getF();
    }
    public int getG() {
        return g;
    }

    public Point getPosition() {
        return position;
    }
    public WorldNode getPrevious() {
        return previous;
    }
    public int getF(){
        return h + g;
    }
}
