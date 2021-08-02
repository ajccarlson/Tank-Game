package tankrotationexample.game.object_classes;

public abstract class DestroyableObject extends CollidableObject {
    public abstract boolean isDestroyed();
    public abstract void setDestroyed(boolean state);
}
