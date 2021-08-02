package tankrotationexample.game.stationary.walls;

import tankrotationexample.game.object_classes.DestroyableObject;

import java.awt.*;

public abstract class Wall extends DestroyableObject {
    public abstract void drawImage(Graphics g);
    public void update() {};
}
