/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tankrotationexample.game;


import tankrotationexample.GameConstants;
import tankrotationexample.Launcher;
import tankrotationexample.game.moveable.Tank;
import tankrotationexample.game.object_classes.DestroyableObject;
import tankrotationexample.game.object_classes.GameObject;
import tankrotationexample.game.stationary.powerups.ExtraLife;
import tankrotationexample.game.stationary.walls.BreakWall;
import tankrotationexample.game.object_classes.CollidableObject;
import tankrotationexample.game.stationary.walls.UnBreakWall;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


import static javax.imageio.ImageIO.read;

/**
 *
 * @author anthony-pc
 */
public class TRE extends JPanel implements Runnable {
    private BufferedImage world;
    private Tank t1;
    private Tank t2;
    ArrayList<GameObject> gameObjects;
    ArrayList<CollidableObject> collidableObjects;
    ArrayList<DestroyableObject> destroyableObjects;
    private Launcher lf;
    static long tick = 0;
    //private String displayTime = "";

    public TRE(Launcher lf){
        this.lf = lf;
    }

    public void setTick(long tick) { this.tick = tick; }

    public static long getTick() { return tick; }

    @Override
    public void run(){
       try {
           this.resetGame();

           while (true) {
               setTick(this.getTick() + 1);

               for (int i = 0; i < this.gameObjects.size(); i++) {
                   this.gameObjects.get(i).update();

                   for (int j = 0; j < this.gameObjects.size(); j++) {
                       if (j < this.collidableObjects.size()) {
                           if (this.collidableObjects.get(i) != this.collidableObjects.get(j))
                               this.collidableObjects.get(i).checkCollision(this.collidableObjects.get(j));
                       }

                       if (j < this.destroyableObjects.size()) {
                           if (this.gameObjects.get(i) == this.destroyableObjects.get(j) && this.destroyableObjects.get(j).isDestroyed()) {
                               for (int k = 0; k < this.collidableObjects.size(); k++) {
                                   if (this.gameObjects.get(i) == this.collidableObjects.get(k)) {
                                       /*System.out.println("Removed Game Object: " + this.gameObjects.get(i));
                                       System.out.println("Removed Collidable Object: " + this.collidableObjects.get(k));
                                       System.out.println("Removed Destroyable Object: " + this.destroyableObjects.get(j));*/

                                       collidableObjects.remove(k);
                                       break;
                                   }
                               }
                               gameObjects.remove(i);
                               destroyableObjects.remove(j);

                               if (t1.isDestroyed())
                                   resetT1();
                               if (t2.isDestroyed())
                                   resetT2();

                               break;
                           }
                       }

                       if (j >= this.collidableObjects.size() && j >= this.destroyableObjects.size()) {
                           break;
                       }
                   }
               }

               this.repaint();   // redraw game
               Thread.sleep(1000 / 144); //sleep for a few milliseconds
               //System.out.println(t1);
               /*
               * simulate an end game event
               * we will do this with by ending the game when drawn 20000 frames have been drawn
               */
               if(this.getTick() > 20000 || t1.getLives() <= 0 || t2.getLives() <= 0){
                   this.lf.setFrame("end");
                   return;
               }
            }
       } catch (InterruptedException ignored) {
           System.out.println(ignored);
       }
    }

    /**
     * Reset game to its initial state.
     */
    public void resetGame(){
        this.setTick(0);
        this.t1.setX(300);
        this.t1.setY(300);
        this.t1.setAngle(0);
        this.t2.setX(950);
        this.t2.setY(615);
        this.t2.setAngle(180);

        this.destroyableObjects.forEach(destroyableObject -> destroyableObject.setDestroyed(false));

        t1.setLives(3);
        t1.setCurrentHealth(100);
        t2.setLives(3);
        t2.setCurrentHealth(100);
    }

    public void resetT1() {
        this.t1.setX(300);
        this.t1.setY(300);
        this.t1.setAngle(0);
        this.t1.setDestroyed(false);

        this.gameObjects.add(t1);
        this.collidableObjects.add(t1);
        this.destroyableObjects.add(t1);
    }

    public void resetT2() {
        this.t2.setX(950);
        this.t2.setY(615);
        this.t2.setAngle(180);
        this.t2.setDestroyed(false);

        this.gameObjects.add(t2);
        this.collidableObjects.add(t2);
        this.destroyableObjects.add(t2);
    }


    /**
     * Load all resources for Tank Wars Game. Set all Game Objects to their
     * initial state as well.
     */
    public void gameInitialize() {
        this.world = new BufferedImage(GameConstants.GAME_WORLD_WIDTH,
                                       GameConstants.GAME_WORLD_HEIGHT,
                                       BufferedImage.TYPE_INT_RGB);

        this.gameObjects = new ArrayList<>();
        this.collidableObjects = new ArrayList<>();
        this.destroyableObjects = new ArrayList<>();
        try {
            /*
             * note class loaders read files from the out folder (build folder in Netbeans) and not the
             * current working directory.
             */
            InputStreamReader isr = new InputStreamReader(TRE.class.getClassLoader().getResourceAsStream("maps/map1"));
            BufferedReader mapReader = new BufferedReader(isr);

            String row = mapReader.readLine();
            if (row == null) {
                throw new IOException("no data in file");
            }
            String[] mapInfo = row.split("\t");
            int numCols = Integer.parseInt(mapInfo[0]);
            int numRows = Integer.parseInt(mapInfo[1]);

            for (int curRow = 0; curRow < numRows; curRow++) {
                row = mapReader.readLine();
                mapInfo = row.split("\t");
                for (int curCol = 0; curCol < numCols; curCol++) {
                    switch (mapInfo[curCol]) {
                        case "2":
                            BreakWall breakWallTemp = new BreakWall(curCol * 30, curRow * 30, Resource.getResourceImage("break"));

                            this.gameObjects.add(breakWallTemp);
                            this.collidableObjects.add(breakWallTemp);
                            this.destroyableObjects.add(breakWallTemp);
                            break;
                        case "4":
                            ExtraLife extraLifeTemp = new ExtraLife(curCol * 30, curRow * 30, Resource.getResourceImage("extraLife"));

                            this.gameObjects.add(extraLifeTemp);
                            this.collidableObjects.add(extraLifeTemp);
                            this.destroyableObjects.add(extraLifeTemp);
                            break;
                        case "3":
                        case "9":
                            UnBreakWall unBreakWallTemp = new UnBreakWall(curCol * 30, curRow * 30, Resource.getResourceImage("unbreak"));

                            this.gameObjects.add(unBreakWallTemp);
                            this.collidableObjects.add(unBreakWallTemp);
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        t1 = new Tank(300, 300, 0, 0, 0, Resource.getResourceImage("tank1"));
        t2 = new Tank(950, 615, 0, 0, 180, Resource.getResourceImage("tank2"));
        TankControl tc1 = new TankControl(t1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE);
        TankControl tc2 = new TankControl(t2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER);
        this.lf.getJf().addKeyListener(tc1);
        this.lf.getJf().addKeyListener(tc2);

        this.gameObjects.add(t1);
        this.collidableObjects.add(t1);
        this.destroyableObjects.add(t1);

        this.gameObjects.add(t2);
        this.collidableObjects.add(t2);
        this.destroyableObjects.add(t2);
    }


    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        Graphics2D buffer = world.createGraphics();
        buffer.setColor(new Color(89, 22, 11));
        buffer.fillRect(0,0, GameConstants.GAME_WORLD_WIDTH, GameConstants.GAME_WORLD_HEIGHT);
        buffer.setColor(new Color(210, 198, 162));
        buffer.fillRect(0,0, GameConstants.GAME_SCREEN_WIDTH, GameConstants.GAME_SCREEN_HEIGHT);
        this.gameObjects.forEach(gameObject -> gameObject.drawImage(buffer));

        BufferedImage leftHalf = world.getSubimage(this.t1.getXCoords(t1) - GameConstants.GAME_SCREEN_WIDTH / 4, this.t1.getYCoords(t1) - GameConstants.GAME_SCREEN_HEIGHT / 2, GameConstants.GAME_SCREEN_WIDTH / 2, GameConstants.GAME_SCREEN_HEIGHT - 65);
        BufferedImage rightHalf = world.getSubimage(this.t2.getXCoords(t2) - GameConstants.GAME_SCREEN_WIDTH / 4, this.t2.getYCoords(t2) - GameConstants.GAME_SCREEN_HEIGHT / 2, GameConstants.GAME_SCREEN_WIDTH / 2, GameConstants.GAME_SCREEN_HEIGHT - 65);
        BufferedImage mm = world.getSubimage(0, 0, GameConstants.GAME_SCREEN_WIDTH, GameConstants.GAME_SCREEN_HEIGHT);
        g2.drawImage(leftHalf,0,65,null);
        g2.drawImage(rightHalf,GameConstants.GAME_SCREEN_WIDTH / 2 + 4,65,null);

        g2.setColor(Color.GRAY);
        g2.fillRect(0, 0, GameConstants.GAME_SCREEN_WIDTH, 65);

        /*g2.setColor(Color.BLACK);
        g2.fillRect(GameConstants.GAME_SCREEN_WIDTH / 2 - 34, 0, 70, 60);

        if (getTick() % 100 == 0)
            displayTime = String.valueOf(getTick());

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Impact", Font.PLAIN, 30));
        g2.drawString(displayTime, GameConstants.GAME_SCREEN_WIDTH / 2 - 26, 38);*/

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Impact", Font.PLAIN, 30));
        g2.drawString("Player 1", GameConstants.GAME_SCREEN_WIDTH / 4 - 131, 42);
        g2.drawString("Player 2", GameConstants.GAME_SCREEN_WIDTH - GameConstants.GAME_SCREEN_WIDTH / 4 - 131, 42);

        for(int i = 1; i <= t1.getLives(); i++){
            g2.drawImage(Resource.getResourceImage("heart"), (Resource.getResourceImage("heart").getWidth() + 40) * i / 2, 10, null);
        }

        for(int i = 1; i <= t2.getLives(); i++){
            g2.drawImage(Resource.getResourceImage("heart"), (Resource.getResourceImage("heart").getWidth() + 40) * i / 2 + GameConstants.GAME_SCREEN_WIDTH - GameConstants.GAME_SCREEN_WIDTH / 2 + 10, 10, null);
        }

        g2.setColor(Color.RED);
        g2.fillRect(GameConstants.GAME_SCREEN_WIDTH / 4, 22, 200, 20);
        g2.fillRect(GameConstants.GAME_SCREEN_WIDTH - GameConstants.GAME_SCREEN_WIDTH / 4, 20, 200, 20);

        g2.setColor(Color.GREEN);
        g2.fillRect(GameConstants.GAME_SCREEN_WIDTH / 4, 22, 2 * t1.getCurrentHealth(), 20);
        g2.fillRect(GameConstants.GAME_SCREEN_WIDTH - GameConstants.GAME_SCREEN_WIDTH / 4, 20, 2 * t2.getCurrentHealth(), 20);

        g2.scale(0.2, 0.2);
        g2.drawImage(mm, GameConstants.GAME_SCREEN_WIDTH * 2, 3650, null);
    }
}
