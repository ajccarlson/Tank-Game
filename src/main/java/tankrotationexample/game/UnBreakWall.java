package tankrotationexample.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class UnBreakWall extends Wall {
    int x, y;
    BufferedImage wallImage;

    public UnBreakWall(int x, int y, BufferedImage wallImage) {
        this.x = x;
        this.y = y;
        this.wallImage = wallImage;
    }

    @Override
    public void drawImage(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(this.wallImage, x, y, null);
    }
}
