package tankrotationexample.game;



import org.w3c.dom.css.Rect;
import tankrotationexample.GameConstants;

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

    private final int R = 2;
    private final float ROTATIONSPEED = 3.0f;

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


    Tank(int x, int y, int vx, int vy, int angle, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.img = img;
        this.angle = angle;
        this.hitBox = this.getHitBox();
        this.ammo = new ArrayList<>();
        this.currentHealth = 100;
        this.lives = 3;
        this.destroyed = false;
    }

    void setX(int x){ this.x = x; }

    void setY(int y) { this.y = y;}

    void setAngle(int angle) { this.angle = angle; }

    void setCurrentHealth(int value) { this.currentHealth = value; }

    void setLives(int value) {
        this.lives += value;

        if (this.lives > 0) {
            this.currentHealth = 100;
        }
    }

    void setDestroyed(boolean state) { this.destroyed = state; }

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

    public int getCurrentHealth() { return this.currentHealth; }

    public int getLives() { return this.lives; }

    void toggleUpPressed() {
        this.UpPressed = true;
    }

    void toggleDownPressed() { this.DownPressed = true; }

    void toggleRightPressed() {
        this.RightPressed = true;
    }

    void toggleLeftPressed() {
        this.LeftPressed = true;
    }

    void toggleShootPressed() {
        this.ShootPressed = true;
    }

    void unToggleUpPressed() { this.UpPressed = false; }

    void unToggleDownPressed() {
        this.DownPressed = false;
    }

    void unToggleRightPressed() { this.RightPressed = false; }

    void unToggleLeftPressed() {
        this.LeftPressed = false;
    }

    void unToggleShootPressed() {
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

        if (this.ShootPressed && TRE.tick % 20 == 0) {
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
    }

    private void rotateLeft() {
        this.angle -= this.ROTATIONSPEED;
    }

    private void rotateRight() {
        this.angle += this.ROTATIONSPEED;
    }

    private void moveBackwards() {
        vx = (int) Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = (int) Math.round(R * Math.sin(Math.toRadians(angle)));
        x -= vx;
        y -= vy;
        checkBorder();
        this.hitBox.setLocation(x, y);
    }

    private void moveForwards() {
        vx = (int) Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = (int) Math.round(R * Math.sin(Math.toRadians(angle)));
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
            this.setLives(-1);
        }
        else
            this.currentHealth -= value;
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
        /*g2d.setColor(Color.CYAN);
        g2d.drawRect(x, y, this.img.getWidth(), this.img.getHeight());*/
    }
}
