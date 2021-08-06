package tankrotationexample.game.moveable;



import tankrotationexample.dependencies.GameConstants;
import tankrotationexample.dependencies.Resource;
import tankrotationexample.game.TRE;
import tankrotationexample.game.object_classes.CollidableObject;
import tankrotationexample.game.object_classes.DestroyableObject;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author anthony-pc
 */
public class Tank extends DestroyableObject {


    private int x;
    private int y;
    private int vx;
    private int vy;
    private int angle;
    private int R;

    private final float ROTATIONSPEED = 3.0f;

    private int firingInterval;
    private int currentHealth;
    private int lives;
    private boolean destroyed;

    private Rectangle hitBox;
    private ArrayList<Bullet> ammo;

    private BufferedImage img;
    private boolean UpPressed;
    private boolean DownPressed;
    private boolean RightPressed;
    private boolean LeftPressed;
    private boolean ShootPressed;


    public Tank(int x, int y, int vx, int vy, int angle, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.angle = angle;
        this.R = 2;
        this.img = img;
        this.hitBox = this.getHitBox();
        this.ammo = new ArrayList<>();
        this.firingInterval = 20;
        this.currentHealth = 100;
        this.lives = 3;
        this.destroyed = false;
    }

    public void setX(int x){ this.x = x; }

    public void setY(int y) { this.y = y;}

    public void setAngle(int angle) { this.angle = angle; }

    public void setR(int R) { this.R = R; }

    public void setFiringInterval(int firingInterval) { this.firingInterval = firingInterval; }

    public void setCurrentHealth(int currentHealth) { this.currentHealth = currentHealth; }

    public void setLives(int lives) {
        this.lives = lives;

        if (this.lives > 0) {
            this.currentHealth = 100;
        }
    }

    public int getX() { return x; }

    public int getXCoords(Tank t) {
        int x = t.getX();
        if (x < GameConstants.GAME_SCREEN_WIDTH / 4)
            x = GameConstants.GAME_SCREEN_WIDTH / 4;
        if (x > GameConstants.GAME_WORLD_WIDTH - GameConstants.GAME_SCREEN_WIDTH / 4)
            x = GameConstants.GAME_WORLD_WIDTH - GameConstants.GAME_SCREEN_WIDTH / 4;
        return x;
    }

    public int getY() { return y; }

    public int getYCoords(Tank t) {
        int y = t.getY();
        if (y < GameConstants.GAME_SCREEN_HEIGHT / 2)
            y = GameConstants.GAME_SCREEN_HEIGHT / 2;
        if (y > GameConstants.GAME_WORLD_HEIGHT - GameConstants.GAME_SCREEN_HEIGHT / 2)
            y = GameConstants.GAME_WORLD_HEIGHT - GameConstants.GAME_SCREEN_HEIGHT / 2;
        return y;
    }

    public int getAngle() { return angle; }

    public int getR() { return R; }

    public int getFiringInterval() { return this.firingInterval; }

    public int getCurrentHealth() { return this.currentHealth; }

    public int getLives() { return this.lives; }

    public void toggleUpPressed() {
        this.UpPressed = true;
    }

    public void toggleDownPressed() { this.DownPressed = true; }

    public void toggleRightPressed() {
        this.RightPressed = true;
    }

    public void toggleLeftPressed() {
        this.LeftPressed = true;
    }

    public void toggleShootPressed() {
        this.ShootPressed = true;
    }

    public void unToggleUpPressed() { this.UpPressed = false; }

    public void unToggleDownPressed() {
        this.DownPressed = false;
    }

    public void unToggleRightPressed() { this.RightPressed = false; }

    public void unToggleLeftPressed() {
        this.LeftPressed = false;
    }

    public void unToggleShootPressed() {
        this.ShootPressed = false;
    }

    public void update() {
        if (this.UpPressed) {
            this.moveForwards();
        }
        if (this.DownPressed) {
            this.moveBackwards();
        }

        if (this.LeftPressed) {
            this.rotateLeft();
        }
        if (this.RightPressed) {
            this.rotateRight();
        }

        if (this.ShootPressed && TRE.getTick() % firingInterval == 0) {
            Bullet b = new Bullet(x, y, angle, Resource.getResourceImage("bullet"));
            this.ammo.add(b);
        }
        for(int i = 0; i < ammo.size(); i++){
            if(ammo.get(i).hasCollided()) {
                ammo.remove(i);
            }
            else{
                ammo.get(i).update();
            }
        }

        if (this.isDestroyed()) {
            resetPowerups();
        }
    }

    private void rotateLeft() {
        this.angle -= this.ROTATIONSPEED;
    }

    private void rotateRight() {
        this.angle += this.ROTATIONSPEED;
    }

    private void moveBackwards() {
        vx = (int) Math.round(this.getR() * Math.cos(Math.toRadians(angle)));
        vy = (int) Math.round(this.getR() * Math.sin(Math.toRadians(angle)));
        x -= vx;
        y -= vy;
        checkBorder();
        this.hitBox.setLocation(x, y);
    }

    private void moveForwards() {
        vx = (int) Math.round(this.getR() * Math.cos(Math.toRadians(angle)));
        vy = (int) Math.round(this.getR() * Math.sin(Math.toRadians(angle)));
        x += vx;
        y += vy;
        checkBorder();
        this.hitBox.setLocation(x, y);
    }




    private void checkBorder() {
        if (x < 30) {
            x = 30;
        }
        if (x >= GameConstants.GAME_WORLD_WIDTH - 88) {
            x = GameConstants.GAME_WORLD_WIDTH - 88;
        }
        if (y < 30) {
            y = 30;
        }
        if (y >= GameConstants.GAME_WORLD_HEIGHT - 80) {
            y = GameConstants.GAME_WORLD_HEIGHT - 80;
        }
    }

    public void collided(int value) {
        if (this.currentHealth - value <= 0) {
            this.setDestroyed(true);
            this.setLives(this.getLives() - 1);
        }
        else
            this.currentHealth -= value;
    }

    public void resetPowerups() {
        this.setR(2);
        this.setFiringInterval(20);
    }

    @Override
    public void checkCollision(CollidableObject c) {
        if (this.getHitBox().intersects(c.getHitBox())) {
            if (c instanceof Bullet)
                collided(10);
            else {
                Rectangle intersection = this.getHitBox().intersection(c.getHitBox());

                if (intersection.height > intersection.width && this.x < intersection.x) // Intersects left
                    x -= intersection.width / 2;
                else if (intersection.height > intersection.width && this.x > c.getHitBox().x) // Intersects right
                    x += intersection.width / 2;
                else if (intersection.height < intersection.width && this.y < intersection.y) // Intersects up
                    y -= intersection.height / 2;
                else if (intersection.height < intersection.width && this.y > c.getHitBox().y) // Intersects down
                    y += intersection.height / 2;
            }
        }

        for (Bullet b : ammo) {
            b.checkCollision(c);
            c.checkCollision(b);
        }
    }

    @Override
    public Rectangle getHitBox() {
        return new Rectangle(x, y, this.img.getWidth(), this.img.getHeight());
    }

    @Override
    public boolean hasCollided() { return false; }

    @Override
    public void setDestroyed(boolean state) { this.destroyed = state; }

    @Override
    public boolean isDestroyed() { return this.destroyed; }

    @Override
    public String toString() {
        return "x=" + x + ", y=" + y + ", angle=" + angle;
    }


    public void drawImage(Graphics g) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth() / 2.0, this.img.getHeight() / 2.0);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(this.img, rotation, null);
        this.ammo.forEach(bullet -> bullet.drawImage(g));
    }
}
