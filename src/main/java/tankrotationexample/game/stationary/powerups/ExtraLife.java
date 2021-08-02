package tankrotationexample.game.stationary.powerups;

import tankrotationexample.game.TRE;
import tankrotationexample.game.moveable.Tank;
import tankrotationexample.game.object_classes.CollidableObject;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ExtraLife extends Powerup {
    int x, y;

    private boolean destroyed = false;

    BufferedImage powerupImage;
    Rectangle hitBox;


    public ExtraLife(int x, int y, BufferedImage powerupImage) {
        this.x = x;
        this.y = y;
        this.powerupImage = powerupImage;
        this.hitBox = new Rectangle(x, y, this.powerupImage.getWidth(), this.powerupImage.getHeight());
    }

    public void update() {
        if (TRE.getTick() % 5000 == 0)
            setDestroyed(false);
    }

    @Override
    public void checkCollision(CollidableObject c) {
        if (c instanceof Tank) {
            if (this.getHitBox().intersects(c.getHitBox())) {
                destroyed = true;
                ((Tank) c).setLives(((Tank) c).getLives() + 1);
                ((Tank) c).setCurrentHealth(100);
            }
        }
    }

    @Override
    public Rectangle getHitBox() { return this.hitBox; }

    @Override
    public boolean hasCollided() { return false; }

    @Override
    public void setDestroyed(boolean state) { this.destroyed = state; }

    @Override
    public boolean isDestroyed() { return destroyed; }

    @Override
    public void drawImage(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(this.powerupImage, x, y, null);
    }
}
