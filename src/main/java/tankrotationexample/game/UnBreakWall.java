package tankrotationexample.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class UnBreakWall extends Wall {
    int x, y;

    BufferedImage wallImage;
    Rectangle hitBox;


    public UnBreakWall(int x, int y, BufferedImage wallImage) {
        this.x = x;
        this.y = y;
        this.wallImage = wallImage;
        this.hitBox = new Rectangle(x, y, this.wallImage.getWidth(), this.wallImage.getHeight());
    }

    @Override
    public void checkCollision(CollidableObject c) {}

    @Override
    public Rectangle getHitBox() { return this.hitBox; }

    @Override
    public boolean hasCollided() { return false; }

    @Override
    public boolean isDestroyed() { return false; }

    @Override
    public void drawImage(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(this.wallImage, x, y, null);
    }
}
