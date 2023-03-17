import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import processing.core.*;

public final class VirtualWorld extends PApplet {
    private static String[] ARGS;

    private final int VIEW_WIDTH = 640;
    private final int VIEW_HEIGHT = 480;
    private final int TILE_WIDTH = 32;
    private final int TILE_HEIGHT = 32;

    private final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    private final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;

    private final String IMAGE_LIST_FILE_NAME = "imagelist";
    private static final String DEFAULT_IMAGE_NAME = "background_default";
    private final int DEFAULT_IMAGE_COLOR = 0x808080;

    private final String FAST_FLAG = "-fast";
    private final String FASTER_FLAG = "-faster";
    private final String FASTEST_FLAG = "-fastest";
    private final double FAST_SCALE = 0.5;
    private final double FASTER_SCALE = 0.25;
    private final double FASTEST_SCALE = 0.10;

    private String loadFile = "world.sav";
    private long startTimeMillis = 0;
    private double timeScale = 1.0;

    private ImageStore imageStore;
    private WorldModel world;
    private WorldView view;
    private EventScheduler scheduler;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        parseCommandLine(ARGS);
        loadImages(IMAGE_LIST_FILE_NAME);
        loadWorld(loadFile, this.imageStore);

        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH, TILE_HEIGHT);
        this.scheduler = new EventScheduler();
        this.startTimeMillis = System.currentTimeMillis();
        this.scheduleActions(world, scheduler, imageStore);
    }

    public void draw() {
        double appTime = (System.currentTimeMillis() - startTimeMillis) * 0.001;
        double frameTime = (appTime - scheduler.getCurrentTime())/timeScale;
        this.update(frameTime);
        view.drawViewport();
    }

    public void update(double frameTime){
        scheduler.updateOnTime(frameTime);
    }

    // Just for debugging and for P5
    // Be sure to refactor this method as appropriate
    public void mousePressed() {
        Point pressed = mouseToPoint();
        System.out.println("CLICK! " + pressed.getX() + ", " + pressed.getY());

        final Function<Point, Stream<Point>> DIAGONAL_CARDINAL_NEIGHBORS =
                point ->
                        Stream.<Point>builder()
                                .add(new Point(point.x - 1, point.y - 1))
                                .add(new Point(point.x + 1, point.y + 1))
                                .add(new Point(point.x - 1, point.y + 1))
                                .add(new Point(point.x + 1, point.y - 1))
                                .add(new Point(point.x, point.y - 1))
                                .add(new Point(point.x, point.y + 1))
                                .add(new Point(point.x, point.y + 2))
                                .add(new Point(point.x, point.y - 2))
                                .add(new Point(point.x - 1, point.y))
                                .add(new Point(point.x - 2, point.y))
                                .add(new Point(point.x + 1, point.y))
                                .add(new Point(point.x + 2, point.y))
                                .build();

        // Sets background tiles

        List<Point> neighbors = DIAGONAL_CARDINAL_NEIGHBORS
                .apply(pressed)
                .filter(p -> world.withinBounds(p))
                .toList();

        neighbors.forEach(val -> {
            // Transform dudes in this area
            Background[][] background = world.getBackground();
            background[val.getY()][val.getX()] = new Background("affected", imageStore.getImageList("blood"));
            world.setBackground(background);
        });

        // First check if space is not occupied
        Killer killer = new Killer("blood_dude", pressed, imageStore.getImageList("killer"), 0.8, 0.2, imageStore);

        world.addEntity(killer);

        killer.scheduleActions(world, imageStore, scheduler);


    }

    public void scheduleActions(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        for (Entity entity : world.getEntities()) {
            if(entity instanceof Actionable){
                ((Actionable)entity).scheduleActions(world, imageStore, scheduler);
            }

        }
    }

    private Point mouseToPoint() {
        return view.getViewport().viewportToWorld(mouseX / TILE_WIDTH, mouseY / TILE_HEIGHT);
    }

    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP -> dy -= 1;
                case DOWN -> dy += 1;
                case LEFT -> dx -= 1;
                case RIGHT -> dx += 1;
            }
            view.shiftView(dx, dy);
        }
    }

    public static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME, imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }

    public static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        Arrays.fill(img.pixels, color);
        img.updatePixels();
        return img;
    }

    public void loadImages(String filename) {
        this.imageStore = new ImageStore(createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
        try {
            Scanner in = new Scanner(new File(filename));
            imageStore.loadImages(in, this);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public void loadWorld(String file, ImageStore imageStore) {
        this.world = new WorldModel();
        try {
            Scanner in = new Scanner(new File(file));
            world.load(in, imageStore, createDefaultBackground(imageStore));
        } catch (FileNotFoundException e) {
            Scanner in = new Scanner(file);
            world.load(in, imageStore, createDefaultBackground(imageStore));
        }
    }

    public void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG -> timeScale = Math.min(FAST_SCALE, timeScale);
                case FASTER_FLAG -> timeScale = Math.min(FASTER_SCALE, timeScale);
                case FASTEST_FLAG -> timeScale = Math.min(FASTEST_SCALE, timeScale);
                default -> loadFile = arg;
            }
        }
    }

    public static void main(String[] args) {
        VirtualWorld.ARGS = args;
        PApplet.main(VirtualWorld.class);
    }

    public static List<String> headlessMain(String[] args, double lifetime){
        VirtualWorld.ARGS = args;

        VirtualWorld virtualWorld = new VirtualWorld();
        virtualWorld.setup();
        virtualWorld.update(lifetime);

        return virtualWorld.world.log();
    }
}
