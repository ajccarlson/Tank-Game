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
    public static BufferedImage bulletImage;
    ArrayList<Wall> walls;
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
                this.t1.update(); // update tank
                this.t2.update(); // update tank
                this.repaint();   // redraw game
               if (this.t1.getHitBox().intersects(this.t2.getHitBox())) {
                   System.out.println("Tanks have collided");
               }
                Thread.sleep(1000 / 144); //sleep for a few milliseconds
                //System.out.println(t1);
                /*
                 * simulate an end game event
                 * we will do this with by ending the game when drawn 2000 frames have been drawn
                 */
                if(this.tick > 2000){
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
        this.t2.setX(700);
        this.t2.setY(700);
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

        BufferedImage t1img = null;
        BufferedImage t2img = null;
        BufferedImage breakWall = null;
        BufferedImage unBreakWall = null;
        walls = new ArrayList<>();
        try {
            /*
             * note class loaders read files from the out folder (build folder in Netbeans) and not the
             * current working directory.
             */
            t1img = read(Objects.requireNonNull(TRE.class.getClassLoader().getResource("tank1.png")));
            t2img = read(Objects.requireNonNull(TRE.class.getClassLoader().getResource("tank2.png")));
            TRE.bulletImage = read(Objects.requireNonNull(TRE.class.getClassLoader().getResource("bullet.gif")));
            breakWall = read(Objects.requireNonNull(TRE.class.getClassLoader().getResource("break.gif")));
            unBreakWall = read(Objects.requireNonNull(TRE.class.getClassLoader().getResource("unBreak.gif")));
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
                            this.walls.add(new BreakWall(curCol * 30, curRow * 30, breakWall));
                            break;
                        case "3":
                        case "9":
                            this.walls.add(new UnBreakWall(curCol * 30, curRow * 30, unBreakWall));
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        t1 = new Tank(300, 300, 0, 0, 0, t1img);
        t2 = new Tank(500, 500, 0, 0, 0, t2img);
        TankControl tc1 = new TankControl(t1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE);
        TankControl tc2 = new TankControl(t2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER);
        this.lf.getJf().addKeyListener(tc1);
        this.lf.getJf().addKeyListener(tc2);
        //this.setBackground(Color.BLACK);
    }


    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Graphics2D buffer = world.createGraphics();
        buffer.setColor(Color.BLACK);
        buffer.fillRect(0,0,GameConstants.GAME_WORLD_WIDTH,GameConstants.GAME_WORLD_HEIGHT);
        this.walls.forEach(wall -> wall.drawImage(buffer));
        this.t1.drawImage(buffer);
        this.t2.drawImage(buffer);
        BufferedImage leftHalf = world.getSubimage(0, 0, GameConstants.GAME_SCREEN_WIDTH / 2, GameConstants.GAME_SCREEN_HEIGHT);
        BufferedImage rightHalf = world.getSubimage(this.t2.getX(), this.t2.getY(), GameConstants.GAME_SCREEN_WIDTH / 2, GameConstants.GAME_SCREEN_HEIGHT);
        BufferedImage mm = world.getSubimage(0, 0, GameConstants.GAME_WORLD_WIDTH, GameConstants.GAME_WORLD_HEIGHT);
        g2.drawImage(leftHalf,0,0,null);
        g2.drawImage(rightHalf,GameConstants.GAME_SCREEN_WIDTH / 2 + 4,0,null);
        g2.scale(.10, .10);
        g2.drawImage(mm, 200, 200, null);
    }
}
