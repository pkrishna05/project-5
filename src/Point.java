import java.util.List;
import java.util.Optional;

/**
 * A simple class representing a location in 2D space.
 */
public final class Point {
    public final int x;
    public final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public boolean equals(Object other) {
        if(other == null) return false;
        return other instanceof Point && ((Point)other).x == this.x && ((Point) other).y == this.y;
    }

    public int hashCode() {
        int result = 17;
        result = result * 31 + x;
        result = result * 31 + y;
        return result;
    }

    public boolean adjacent(Point p2) {
        return (getX() == p2.getX() && Math.abs(getY() - p2.getY()) == 1) || (getY() == p2.getY() && Math.abs(getX() - p2.getX()) == 1);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
