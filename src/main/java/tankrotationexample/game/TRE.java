/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tankrotationexample.game;


import tankrotationexample.GameConstants;
import tankrotationexample.Launcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;


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
    private Launcher lf;
    static long tick = 0;

    public TRE(Launcher lf){
        this.lf = lf;
    }

    @Override
    public void run(){
       try {
           this.resetGame();
           while (true) {
               this.tick++;
               this.gameObjects.forEach(gameObject -> gameObject.update());
               this.repaint();   // redraw game
               for (int i = 0; i < this.collidableObjects.size(); i++) {
                   for (int j = 0; j < this.collidableObjects.size(); j++) {
                       if (this.collidableObjects.get(i) == this.collidableObjects.get(j))
                           continue;
                       this.collidableObjects.get(i).checkCollision(this.collidableObjects.get(j));
                   }
               }
               Thread.sleep(1000 / 144); //sleep for a few milliseconds
               //System.out.println(t1);
               /*
               * simulate an end game event
               * we will do this with by ending the game when drawn 20000 frames have been drawn
               */
               if(this.tick > 20000){
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
        this.tick = 0;
        this.t1.setX(300);
        this.t1.setY(300);
        this.t1.setAngle(0);
        this.t2.setX(950);
        this.t2.setY(615);
        this.t2.setAngle(180);
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
                            this.gameObjects.add(new BreakWall(curCol * 30, curRow * 30, Resource.getResourceImage("break")));
                            this.collidableObjects.add(new BreakWall(curCol * 30, curRow * 30, Resource.getResourceImage("break")));
                            break;
                        case "3":
                        case "9":
                            this.gameObjects.add(new UnBreakWall(curCol * 30, curRow * 30, Resource.getResourceImage("unbreak")));
                            this.collidableObjects.add(new UnBreakWall(curCol * 30, curRow * 30, Resource.getResourceImage("unbreak")));
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
        //this.setBackground(Color.BLACK);

        this.gameObjects.add(t1);
        this.collidableObjects.add(t1);
        this.gameObjects.add(t2);
        this.collidableObjects.add(t2);
    }


    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Graphics2D buffer = world.createGraphics();
        buffer.setColor(Color.BLACK);
        buffer.fillRect(0,0, GameConstants.GAME_WORLD_WIDTH, GameConstants.GAME_WORLD_HEIGHT);
        this.gameObjects.forEach(gameObject -> gameObject.drawImage(buffer));
        BufferedImage leftHalf = world.getSubimage(this.t1.getXCoords(t1) - GameConstants.GAME_SCREEN_WIDTH / 4, this.t1.getYCoords(t1) - GameConstants.GAME_SCREEN_HEIGHT / 2, GameConstants.GAME_SCREEN_WIDTH / 2, GameConstants.GAME_SCREEN_HEIGHT);
        BufferedImage rightHalf = world.getSubimage(this.t2.getXCoords(t2) - GameConstants.GAME_SCREEN_WIDTH / 4, this.t2.getYCoords(t2) - GameConstants.GAME_SCREEN_HEIGHT / 2, GameConstants.GAME_SCREEN_WIDTH / 2, GameConstants.GAME_SCREEN_HEIGHT);
        BufferedImage mm = world.getSubimage(0, 0, GameConstants.GAME_SCREEN_WIDTH, GameConstants.GAME_SCREEN_HEIGHT);
        g2.drawImage(leftHalf,0,0,null);
        g2.drawImage(rightHalf,GameConstants.GAME_SCREEN_WIDTH / 2 + 4,0,null);
        g2.scale(0.2, 0.2);
        g2.drawImage(mm, GameConstants.GAME_SCREEN_WIDTH * 2, 3650, null);
    }
}
