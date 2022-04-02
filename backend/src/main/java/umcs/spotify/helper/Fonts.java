package umcs.spotify.helper;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class Fonts {
    public static final Font BUNGEE_REGULAR = loadFont("fonts/Bungee-Regular.ttf").deriveFont(Font.PLAIN);

    private static Font loadFont(String resourceName) {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
            return Font.createFont(Font.TRUETYPE_FONT, inputStream);
        } catch (IOException | FontFormatException e) {
            throw new RuntimeException("Could not load " + resourceName, e);
        }
    }
}
