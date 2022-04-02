package umcs.spotify.helper;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLConnection;
import java.util.Random;

public class AvatarHelper {

    private static final Random RANDOM = new Random();
    private static final Color[] COLORS_POOL = new Color[]{
        new Color(206, 132, 173),
        new Color(206, 150, 166),
        new Color(206, 150, 166),
        new Color(209, 167, 160),
        new Color(212, 203, 179),
        new Color(210, 224, 191)
    };

    public static BufferedImage generateAvatarFromInitials(String initials, int width, int height) {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();

        // Rectangle of random color
        var rectangle = new Rectangle2D.Double(0, 0, width, height);
        graphics.setPaint(COLORS_POOL[RANDOM.nextInt(COLORS_POOL.length)]);
        graphics.fill(rectangle);

        // Antialiassing
        graphics.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Font scale
        var fontSize = Math.min(width, height) * 0.5f;
        var font = Fonts.BUNGEE_REGULAR.deriveFont(fontSize);

        graphics.setFont(font);
        var fontMetrics = graphics.getFontMetrics();
        var fontSizeLast = width / (float) fontMetrics.stringWidth(initials) * fontSize;
        font.deriveFont(fontSizeLast);

        // Font color
        graphics.setColor(Color.BLACK);

        // Center font
        var fontRect = fontMetrics.getStringBounds(initials, graphics);
        var x = (width  - (int) fontRect.getWidth())  / 2;
        var y = (height - (int) fontRect.getHeight()) / 2 + fontMetrics.getAscent();

        // Draw font
        graphics.drawString(initials, x, y);
        return image;
    }

    public static InputStream toStream(BufferedImage buff) throws IOException {
        try (var os = new ByteArrayOutputStream()) {
            ImageIO.write(buff, "jpeg", os);
            try (var is = new ByteArrayInputStream(os.toByteArray())) {
                return is;
            }
        }
    }

    public static boolean isValidJpeg(MultipartFile multipartFile) {
        try (var is = new BufferedInputStream(multipartFile.getInputStream())) {
            var mimeType = URLConnection.guessContentTypeFromStream(is);
            return mimeType.equalsIgnoreCase("image/jpeg");
        } catch (IOException e) {
            return false;
        }
    }
}
