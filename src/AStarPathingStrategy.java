import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AStarPathingStrategy implements PathingStrategy{

    @Override
    public List<Point> computePath(Point start, Point end, Predicate<Point> canPassThrough, BiPredicate<Point, Point> withinReach, Function<Point, Stream<Point>> potentialNeighbors) {

        PriorityQueue<WorldNode> openList = new PriorityQueue<>(Comparator.comparing(WorldNode::getF));
        Map<Point, WorldNode> openListMap = new HashMap<>();
        HashSet<Point> closedList = new HashSet<>();

        // Step 1
        WorldNode current = new WorldNode(start, null, end);

        // Put
        openListMap.put(current.getPosition(), current);
        openList.add(current);

        while(!openList.isEmpty()){

            //Remove
            current = openList.remove();
            openListMap.remove(current.getPosition());

            if(withinReach.test(current.getPosition(), end)){
                return backtracePath(current); // Finished finding valid path
            }

            //Step 3
            List<Point> neighbors = potentialNeighbors.apply(current.getPosition())
                    .filter(p -> !closedList.contains(p))
                    .filter(canPassThrough).toList();

            for(Point neighbor : neighbors){
                WorldNode neighborNode = new WorldNode(neighbor, current, end);
                // Step 3B. Check if in open list
                WorldNode openListNode = openListMap.get(neighbor);
                if(openListNode != null){
                    if (neighborNode.getG() < openListNode.getG()) {
                        // remove
                        openList.remove(openListNode);
                        openListMap.remove(openListNode.getPosition());
                    }
                }

                //Step 3f.
                openList.add(neighborNode);
                openListMap.put(neighbor, neighborNode);
            }
            //Step 5
            openList.remove(current);
            openListMap.remove(current.getPosition());
            closedList.add(current.getPosition());

        }
        return new LinkedList<>(); // No valid path found
    }

    private List<Point> backtracePath(WorldNode current){
        List<Point> path = new LinkedList<>();
        while(current.getPrevious() != null){
            path.add(0, current.getPosition());
            current = current.getPrevious();
        }
        return path;
    }
}
