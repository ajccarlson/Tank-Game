package tankrotationexample.game;

import java.awt.*;

public abstract class Wall extends DestroyableObject  {
    public abstract void drawImage(Graphics g);
    public void update() {};
}
