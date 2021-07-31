package tankrotationexample.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BreakWall extends Wall {
    int x, y;
    int state = 2;
    BufferedImage wallImage;
    Rectangle hitBox;
    private int currentHealth = 20;
    private boolean destroyed = false;

    public BreakWall(int x, int y, BufferedImage wallImage) {
        this.x = x;
        this.y = y;
        this.wallImage = wallImage;
        this.hitBox = new Rectangle(x, y, this.wallImage.getWidth(), this.wallImage.getHeight());
    }

    public void collided(int value) {
        if (currentHealth - value <= 0) {
            currentHealth = 0;
            destroyed = true;
        }
        else
            currentHealth -= value;
    }

    @Override
    public void checkCollision(CollidableObject c) {
        if (c instanceof Bullet) {
            if (this.getHitBox().intersects(c.getHitBox()))
                collided(10);
        }
    }

    @Override
    public Rectangle getHitBox() { return this.hitBox; }

    @Override
    public boolean hasCollided() { return false; }

    @Override
    public boolean isDestroyed() { return destroyed; }

    @Override
    public void drawImage(Graphics g) {
        if (state > 0) {
            Graphics2D g2 = (Graphics2D) g;
            g2.drawImage(this.wallImage, x, y, null);
        }
    }
}
