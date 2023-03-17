import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PathTesting
{

    @Test
    public void testSingleStepNoObstacles() {
        // Grid for testing --> 2D array
        boolean[][] grid = {
                { true, true, true },
                { true, true, true },
                { true, true, true }
        };

        PathingStrategy ps = new AStarPathingStrategy();
        List<Point> path = ps.computePath(
                new Point(0, 0), new Point(2, 2), // start, end
                (p) -> withinBounds(p) && grid[p.getY()][p.getX()], // canPassThrough
                (p1, p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        // expected path => [(0, 1)] <=
        assertEquals(3, path.toArray().length);
    }

    @Test
    public void testSingleStepWithOb() {
        // Grid for testing --> 2D array
        boolean[][] grid = {
                { true, false, false },
                { true, true, true },
                { false, false, true }
        };

        PathingStrategy ps = new AStarPathingStrategy();
        List<Point> path = ps.computePath(
                new Point(0, 0), new Point(2, 2), // start, end
                (p) -> withinBounds(p) && grid[p.getY()][p.getX()], // canPassThrough
                (p1, p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        List<Point> expected = Arrays.asList(new Point(0,1), new Point(1, 1), new Point(2, 1));

        // expected path => [(0, 1)] <=
        assertEquals(expected, path);
    }

    public boolean withinBounds(Point pos) {
        return pos.getY() >= 0 && pos.getY() < 3 && pos.getX() >= 0 && pos.getX() < 3;
    }
}

