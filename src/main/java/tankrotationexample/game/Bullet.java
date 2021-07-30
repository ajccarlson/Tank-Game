package tankrotationexample.game;

import org.w3c.dom.css.Rect;
import tankrotationexample.GameConstants;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Bullet extends CollidableObject {
    int x, y ,vx, vy ,angle;
    int R = 7;
    BufferedImage bulletImage;
    Rectangle hitBox;
    private boolean collided = false;

    public Bullet(int x, int y, int angle, BufferedImage bulletImage) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.bulletImage = bulletImage;
        this.hitBox = this.getHitBox();
    }

    public void moveForwards() {
        vx = (int) Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = (int) Math.round(R * Math.sin(Math.toRadians(angle)));
        x += vx;
        y += vy;
        checkBorder();
    }

    public void checkBorder() {
        if (x < 30) {
            x = 30;
        }
        if (x >= GameConstants.GAME_WORLD_WIDTH - 88) {
            x = GameConstants.GAME_WORLD_WIDTH - 88;
        }
        if (y < 40) {
            y = 40;
        }
        if (y >= GameConstants.GAME_WORLD_HEIGHT - 80) {
            y = GameConstants.GAME_WORLD_HEIGHT - 80;
        }
    }

    @Override
    public void checkCollision(CollidableObject c) {
        if (this.getHitBox().intersects(c.getHitBox())) {
            collided = true;
        }
    }

    @Override
    public Rectangle getHitBox() {
        return new Rectangle(x, y, this.bulletImage.getWidth(), this.bulletImage.getHeight());
    }

    @Override
    public boolean hasCollided() { return collided; }

    public void update() {
        moveForwards();
    }

    public void drawImage(Graphics g) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.bulletImage.getWidth() / 2.0, this.bulletImage.getHeight() / 2.0);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(this.bulletImage, rotation, null);
    }
}
