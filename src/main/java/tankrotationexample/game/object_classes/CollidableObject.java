package tankrotationexample.game.object_classes;

import java.awt.*;

public abstract class CollidableObject extends GameObject {
    public abstract void checkCollision(CollidableObject c);
    public abstract Rectangle getHitBox();
    public abstract boolean hasCollided();
}