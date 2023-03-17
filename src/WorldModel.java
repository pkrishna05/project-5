import processing.core.PImage;

import java.util.*;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel {
    private final int PROPERTY_KEY = 0;
    private final int PROPERTY_ID = 1;
    private final int PROPERTY_COL = 2;
    private final int PROPERTY_ROW = 3;
    private final int ENTITY_NUM_PROPERTIES = 4;
    private final int STUMP_NUM_PROPERTIES = 0;
    private final int SAPLING_NUM_PROPERTIES = 1;
    private final String OBSTACLE_KEY = "obstacle";
    private final int OBSTACLE_ANIMATION_PERIOD = 0;
    private final int OBSTACLE_NUM_PROPERTIES = 1;
    private final String DUDE_KEY = "dude";
    private final int DUDE_ACTION_PERIOD = 0;
    private final int DUDE_ANIMATION_PERIOD = 1;
    private final int DUDE_LIMIT = 2;
    private final int DUDE_NUM_PROPERTIES = 3;
    private final String HOUSE_KEY = "house";
    private final int HOUSE_NUM_PROPERTIES = 0;
    private final String FAIRY_KEY = "fairy";
    private final int FAIRY_ANIMATION_PERIOD = 0;
    private final int FAIRY_ACTION_PERIOD = 1;
    private final int FAIRY_NUM_PROPERTIES = 2;
    private final int TREE_ANIMATION_PERIOD = 0;
    private final int TREE_ACTION_PERIOD = 1;
    private final int TREE_NUM_PROPERTIES = 3;
    private int numRows;
    private int numCols;
    private Background[][] background;
    private Entity[][] occupancy;
    private Set<Entity> entities;

    public WorldModel() {}

    public static Background getBackgroundCell(WorldModel world, Point pos) {
        return world.getBackground()[pos.getY()][pos.getX()];
    }

    public void load(Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        parseSaveFile(saveFile, imageStore, defaultBackground);
        if(getBackground() == null){
            setBackground(new Background[getNumRows()][getNumCols()]);
            for (Background[] row : getBackground())
                Arrays.fill(row, defaultBackground);
        }
        if(getOccupancy() == null){
            setOccupancy(new Entity[getNumRows()][getNumCols()]);
            setEntities(new HashSet<>());
        }
    }

    public void tryAddEntity(Entity entity) {
        if (isOccupied(entity.getPosition())) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        addEntity(entity);
    }

    public void parseDude(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == DUDE_NUM_PROPERTIES) {
            Entity entity = CreateEntity.createDudeNotFull(id, pt, Double.parseDouble(properties[DUDE_ACTION_PERIOD]), Double.parseDouble(properties[DUDE_ANIMATION_PERIOD]), Integer.parseInt(properties[DUDE_LIMIT]), imageStore.getImageList(DUDE_KEY));
            this.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", DUDE_KEY, DUDE_NUM_PROPERTIES));
        }
    }

    public void parseFairy(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == FAIRY_NUM_PROPERTIES) {
            Entity entity = CreateEntity.createFairy(id, pt, Double.parseDouble(properties[FAIRY_ACTION_PERIOD]), Double.parseDouble(properties[FAIRY_ANIMATION_PERIOD]), imageStore.getImageList(FAIRY_KEY));
            this.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", FAIRY_KEY, FAIRY_NUM_PROPERTIES));
        }
    }

    public void parseTree(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == TREE_NUM_PROPERTIES) {
            Entity entity = CreateEntity.createTree(id, pt, Double.parseDouble(properties[TREE_ACTION_PERIOD]), Double.parseDouble(properties[TREE_ANIMATION_PERIOD]), Integer.parseInt(properties[Tree.TREE_HEALTH]), imageStore.getImageList(CreateEntity.TREE_KEY));
            this.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", CreateEntity.TREE_KEY, TREE_NUM_PROPERTIES));
        }
    }

    public void parseHouse(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == HOUSE_NUM_PROPERTIES) {
            Entity entity = CreateEntity.createHouse(id, pt, imageStore.getImageList(HOUSE_KEY));
            this.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", HOUSE_KEY, HOUSE_NUM_PROPERTIES));
        }
    }

    public void parseObstacle(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == OBSTACLE_NUM_PROPERTIES) {
            Entity entity = CreateEntity.createObstacle(id, pt, Double.parseDouble(properties[OBSTACLE_ANIMATION_PERIOD]), imageStore.getImageList(OBSTACLE_KEY));
            this.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", OBSTACLE_KEY, OBSTACLE_NUM_PROPERTIES));
        }
    }

    public void parseStump(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == STUMP_NUM_PROPERTIES) {
            Entity entity = CreateEntity.createStump(id, pt, imageStore.getImageList(CreateEntity.STUMP_KEY));
            this.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", CreateEntity.STUMP_KEY, STUMP_NUM_PROPERTIES));
        }
    }

    public void parseSapling(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == SAPLING_NUM_PROPERTIES) {
            int health = Integer.parseInt(properties[Sapling.SAPLING_HEALTH]);
            Entity entity = CreateEntity.createSapling(id, pt, imageStore.getImageList(CreateEntity.SAPLING_KEY));
            this.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", CreateEntity.SAPLING_KEY, SAPLING_NUM_PROPERTIES));
        }
    }

    public void moveEntity(EventScheduler scheduler, Entity entity, Point pos) {
        Point oldPos = entity.getPosition();
        if (withinBounds(pos) && !pos.equals(oldPos)) {
            setOccupancyCell(oldPos, null);
            Optional<Entity> occupant = getOccupant(pos);
            occupant.ifPresent(target -> removeEntity(scheduler, target));
            setOccupancyCell(pos, entity);
            entity.setPosition(pos);
        }
    }

    public void parseEntity(String line, ImageStore imageStore) {
        String[] properties = line.split(" ", ENTITY_NUM_PROPERTIES + 1);
        if (properties.length >= ENTITY_NUM_PROPERTIES) {
            String key = properties[PROPERTY_KEY];
            String id = properties[PROPERTY_ID];
            Point pt = new Point(Integer.parseInt(properties[PROPERTY_COL]), Integer.parseInt(properties[PROPERTY_ROW]));

            properties = properties.length == ENTITY_NUM_PROPERTIES ?
                    new String[0] : properties[ENTITY_NUM_PROPERTIES].split(" ");

            switch (key) {
                case OBSTACLE_KEY -> this.parseObstacle(properties, pt, id, imageStore);
                case DUDE_KEY -> this.parseDude(properties, pt, id, imageStore);
                case FAIRY_KEY -> this.parseFairy(properties, pt, id, imageStore);
                case HOUSE_KEY -> this.parseHouse(properties, pt, id, imageStore);
                case CreateEntity.TREE_KEY -> this.parseTree(properties, pt, id, imageStore);
                case CreateEntity.SAPLING_KEY -> this.parseSapling(properties, pt, id, imageStore);
                case CreateEntity.STUMP_KEY -> this.parseStump(properties, pt, id, imageStore);
                default -> throw new IllegalArgumentException("Entity key is unknown");
            }
        }else{
            throw new IllegalArgumentException("Entity must be formatted as [key] [id] [x] [y] ...");
        }
    }

    public void parseBackgroundRow(String line, int row, ImageStore imageStore) {
        String[] cells = line.split(" ");
        if(row < getNumRows()){
            int rows = Math.min(cells.length, getNumCols());
            for (int col = 0; col < rows; col++){
                getBackground()[row][col] = new Background(cells[col], imageStore.getImageList(cells[col]));
            }
        }
    }

    public void parseSaveFile(Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        String lastHeader = "";
        int headerLine = 0;
        int lineCounter = 0;
        while(saveFile.hasNextLine()){
            lineCounter++;
            String line = saveFile.nextLine().strip();
            if(line.endsWith(":")){
                headerLine = lineCounter;
                lastHeader = line;
                switch (line){
                    case "Backgrounds:" -> setBackground(new Background[getNumRows()][getNumCols()]);
                    case "Entities:" -> {
                        setOccupancy(new Entity[getNumRows()][getNumCols()]);
                        setEntities(new HashSet<>());
                    }
                }
            }else{
                switch (lastHeader){
                    case "Rows:" -> setNumRows(Integer.parseInt(line));
                    case "Cols:" -> setNumCols(Integer.parseInt(line));
                    case "Backgrounds:" -> this.parseBackgroundRow(line, lineCounter-headerLine-1, imageStore);
                    case "Entities:" -> this.parseEntity(line, imageStore);
                }
            }
        }
    }

    public boolean withinBounds(Point pos) {
        return pos.getY() >= 0 && pos.getY() < getNumRows() && pos.getX() >= 0 && pos.getX() < getNumCols();
    }

    public boolean isOccupied(Point pos) {
        return this.withinBounds(pos) && getOccupancyCell(pos) != null;
    }

    public Optional<Entity> findNearest(Point pos, List<Class> kinds) {
        List<Entity> ofType = new LinkedList<>();
        for (Class kind : kinds) {
            for (Entity entity : this.entities) {
                if (entity.getClass() == kind) {
                    ofType.add(entity);
                }
            }
        }

        return nearestEntity(ofType, pos);
    }

    private int distanceSquared(Point p1, Point p2) {
        int deltaX = p1.x - p2.x;
        int deltaY = p1.y - p2.y;

        return deltaX * deltaX + deltaY * deltaY;
    }

    public Optional<Entity> nearestEntity(List<Entity> entities, Point pos) {
        if (entities.isEmpty()) {
            return Optional.empty();
        } else {
            Entity nearest = entities.get(0);
            int nearestDistance = distanceSquared(nearest.getPosition(), pos);

            for (Entity other : entities) {
                int otherDistance = distanceSquared(other.getPosition(), pos);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }

    public Optional<Entity> getOccupant(Point pos) {
        if (this.isOccupied(pos)) {
            return Optional.of(getOccupancyCell(pos));
        } else {
            return Optional.empty();
        }
    }

    public Optional<PImage> getBackgroundImage(Point pos) {
        if (this.withinBounds(pos)) {
            return Optional.of(getBackgroundCell(this, pos).getCurrentImage());
        } else {
            return Optional.empty();
        }
    }

    private void setOccupancyCell(Point pos, Entity entity) {
        getOccupancy()[pos.getY()][pos.getX()] = entity;
    }

    public void removeEntityAt(Point pos) {
        if (this.withinBounds(pos) && getOccupancyCell(pos) != null) {
            Entity entity = getOccupancyCell(pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */
            entity.setPosition(new Point(-1, -1));
            getEntities().remove(entity);
            this.setOccupancyCell(pos, null);
        }
    }

    /*
               Assumes that there is no entity currently occupying the
               intended destination cell.
            */
    public void addEntity(Entity entity) {
        if (this.withinBounds(entity.getPosition())) {
            this.setOccupancyCell(entity.getPosition(), entity);
            this.entities.add(entity);
        }
    }

    public Entity getOccupancyCell(Point pos) {
        return getOccupancy()[pos.getY()][pos.getX()];
    }

    /**
     * Helper method for testing. Don't move or modify this method.
     */
    public List<String> log(){
        List<String> list = new ArrayList<>();
        for (Entity entity : getEntities()) {
            String log = entity.log();
            if(log != null) list.add(log);
        }
        return list;
    }

    public int getNumRows() {
        return numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public void setNumCols(int numCols) {
        this.numCols = numCols;
    }

    public Background[][] getBackground() {
        return background;
    }

    public void setBackground(Background[][] background) {
        this.background = background;
    }

    public Entity[][] getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(Entity[][] occupancy) {
        this.occupancy = occupancy;
    }

    public Set<Entity> getEntities() {
        return entities;
    }

    public void setEntities(Set<Entity> entities) {
        this.entities = entities;
    }

    public void removeEntity(EventScheduler scheduler, Entity entity) {
        scheduler.unscheduleAllEvents(entity);
        removeEntityAt(entity.getPosition());
    }
}
