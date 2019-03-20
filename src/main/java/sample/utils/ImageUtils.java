package sample.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtils {


    public static Image bufferImage2Image(BufferedImage image) {
        return image == null ? null : SwingFXUtils.toFXImage(image, null);

    }

    public static BufferedImage image2BufferImage(Image image) {
        return image == null ? null : SwingFXUtils.fromFXImage(image, null);
    }

    public static void main(String[] args) {

    }

}
