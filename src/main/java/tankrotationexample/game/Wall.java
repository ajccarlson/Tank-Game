package tankrotationexample.game;

import java.awt.*;

public abstract class Wall extends CollidableObject  {
    public abstract void drawImage(Graphics g);
    public void update() {};
}
