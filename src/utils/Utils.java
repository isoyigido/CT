package utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Utils {
    public static void saveAsJPGWithBlackBackground(BufferedImage image, String outputPath, int x_margin, int y_margin) {
        if (image == null || outputPath == null) {
            throw new IllegalArgumentException("Image and output path cannot be null.");
        }

        // Create a new RGB image
        BufferedImage rgbImage = new BufferedImage(
                image.getWidth() + (x_margin * 2),
                image.getHeight() + (y_margin * 2),
                BufferedImage.TYPE_INT_RGB
        );

        // Draw the original image onto the rgb image
        Graphics2D g = rgbImage.createGraphics();
        g.setColor(Constants.BG_COLOR); // background color
        g.fillRect(0, 0, rgbImage.getWidth(), rgbImage.getHeight());
        g.drawImage(image, x_margin, y_margin, null);
        g.dispose();

        // Save as JPG
        try {
            ImageIO.write(rgbImage, "jpg", new File(outputPath));
            System.out.println(outputPath + " saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
