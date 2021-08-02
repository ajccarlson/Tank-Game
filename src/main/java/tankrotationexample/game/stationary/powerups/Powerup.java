package tankrotationexample.game.stationary.powerups;

import tankrotationexample.game.object_classes.DestroyableObject;

import java.awt.*;

public abstract class Powerup extends DestroyableObject {
    public abstract void drawImage(Graphics g);
    public abstract void update();
}