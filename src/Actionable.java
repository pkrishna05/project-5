public interface Actionable extends Entity {
    default void scheduleActions(WorldModel world, ImageStore imageStore, EventScheduler eventScheduler){
        eventScheduler.scheduleEvent(this, Animation.createAnimationAction(this, 0), getAnimationPeriod());
    }
    double getAnimationPeriod();

    void setImageIndex(int index);
    default void nextImage(){
        setImageIndex(getImageIndex() + 1);
    }

}
