package tankrotationexample.dependencies;

import tankrotationexample.game.TRE;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static javax.imageio.ImageIO.read;

public class Resource {
    private static Map<String, BufferedImage> resources;
    static {
        Resource.resources = new HashMap<>();
        try {
            Resource.resources.put("tank1", read(Objects.requireNonNull(TRE.class.getClassLoader().getResource("tank1.png"))));
            Resource.resources.put("tank2", read(Objects.requireNonNull(TRE.class.getClassLoader().getResource("tank2.png"))));
            Resource.resources.put("bullet", read(Objects.requireNonNull(TRE.class.getClassLoader().getResource("bullet.gif"))));
            Resource.resources.put("break", read(Objects.requireNonNull(TRE.class.getClassLoader().getResource("break.gif"))));
            Resource.resources.put("unbreak", read(Objects.requireNonNull(TRE.class.getClassLoader().getResource("unBreak.gif"))));
            Resource.resources.put("extraLife", read(Objects.requireNonNull(TRE.class.getClassLoader().getResource("extraLife.png"))));
            Resource.resources.put("extraFiringSpeed", read(Objects.requireNonNull(TRE.class.getClassLoader().getResource("extraFiringSpeed.png"))));
            Resource.resources.put("speedBoost", read(Objects.requireNonNull(TRE.class.getClassLoader().getResource("speedBoost.png"))));
            Resource.resources.put("heart", read(Objects.requireNonNull(TRE.class.getClassLoader().getResource("heart.png"))));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-5);
        }
    }

    public static BufferedImage getResourceImage(String key) {
        return Resource.resources.get(key);
    }
}
