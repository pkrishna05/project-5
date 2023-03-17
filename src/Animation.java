public class Animation implements Action{
    private Actionable entity;
    private final int repeatCount;

    public Animation(Actionable entity, int repeatCount) {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }

    public static Animation createAnimationAction(Actionable entity, int repeatCount) {
        return new Animation(entity, repeatCount);
    }

    public void execute(EventScheduler scheduler) {
        entity.nextImage();

        if (repeatCount != 1) {
            scheduler.scheduleEvent(entity, createAnimationAction(entity, Math.max(repeatCount - 1, 0)), entity.getAnimationPeriod());
        }
    }
}
