package Logic.Core;

import Exceptions.WrongColorException;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import static Logic.Core.GlobalSettings.*;

/**
 * Basic class for image
 *
 * Image saved as array of int ARGB, where
 * First 8 bits (24-31 bits) - opacity (a.k.a. alpha)
 * Second 8 bits (16-23) - red color
 * Third 8 bits (8-15) - green color
 * Forth 8 bits (0-7) - blue color
 *
 * Indexing starts from upper left corner by rows
 */

public class Image {

    private int OPACITY_MASK = 0xFF000000;
    private int RED_MASK =     0x00FF0000;
    private int GREEN_MASK =   0x0000FF00;
    private int BLUE_MASK =    0x000000FF;

    private int OPACITY_SHIFT = 24;
    private int RED_SHIFT =     16;
    private int GREEN_SHIFT =   8;

    public Image(int width, int height) {
        init(width, height);
    }

    public Image(javafx.scene.image.Image image) {
        fromFXImage(image);
    }

    private int width;
    private int height;
    private int[] pixels;
    private int x;
    private int y;

    /**
     * Returns new Pixel by index
     * Counting starts from upper left corner by rows
     * @param index
     */
    public Pixel getPixel(int index) {
        return new Pixel(index, getColor(index));
    }

    /**
     * @return pixels in ARGB to use
     */
    public int[] getPixels() {
        return pixels;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    /**
     * Tries to change pixel by index and color
     * @param index of pixel to change
     * @param color of pixel to change
     * @return TRUE, if change was SUCCESSFUL. FALSE if change was UNSUCCESSFUL (by wrong color or index)
     */
    public boolean changePixel(int index, Color color) {
        if (index < 0 || index >= width * height) {
            if (debugModeState() == DEBUG_MODE.ENABLED) {
                System.out.println("Pixel change error. Wrong index. Index " + index + " color " + color.toString()
                        + " max index " + (width * height - 1));
            }
            return false;
        }
        try {
            pixels[index] = color.toIntARGB();
        } catch (WrongColorException e) {
            if (debugModeState() == DEBUG_MODE.ENABLED)
            System.out.println("Pixel change error. Wrong color. Index " + index + " color " + color.toString());
            return false;
        }
        return true;
    }

    /**
     * Tries to change pixel by another
     * @param pixel witch changing
     * @return TRUE, if change was SUCCESSFUL. FALSE if change was UNSUCCESSFUL (by wrong color or index)
     */
    public boolean changePixel(Pixel pixel) {
        return changePixel(pixel.getIndex(), pixel.getColor());
    }

    /**
     * Tries to change all pixels by colors.
     * @param pixels color array to change
     * @return TRUE, if change was SUCCESSFUL. FALSE if change was UNSUCCESSFUL (by wrong color or array size)
     */
    public boolean changeAllPixels(Color[] pixels) {
        if (this.pixels.length != pixels.length) {
            if (GlobalSettings.debugModeState() == DEBUG_MODE.ENABLED) {
                System.out.println("All pixels change error. Wrong size "
                        + pixels.length + " must be " + this.pixels.length);
            }
            return false;
        }
        for (int i = 0; i < pixels.length; i++) {
            this.pixels[i] = pixels[i].toIntARGB();
        }
        return true;
    }

    public boolean changeAllPixels(int[] pixels) {
        if (this.pixels.length != pixels.length) {
            if (GlobalSettings.debugModeState() == DEBUG_MODE.ENABLED) {
                System.out.println("All pixels change error. Wrong size "
                        + pixels.length + " must be " + this.pixels.length);
            }
            return false;
        }
        this.pixels = pixels;
        return true;
    }

    /**
     * Converts SGE.Image to javaFX.Image
     * @return javaFX image
     */
    public javafx.scene.image.Image toFXImage() {
        WritableImage wImage = new WritableImage(width, height);
        PixelWriter pWriter= wImage.getPixelWriter();
        pWriter.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);
        return wImage;
    }

    /**
     * Converts JavaFX.Image to SGE.Image
     * @param Image from JavaFX to convert into SGE.Image
     * @return SGE.Image
     */
    public boolean fromFXImage(javafx.scene.image.Image Image) {
        init((int) Image.getWidth(), (int) Image.getHeight());
        Image.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);
        return true;
    }

    @Override
    public Image clone() {
        Image clone = new Image(width, height);
        clone.pixels = this.pixels.clone();
        clone.x = this.x;
        clone.y = this.y;
        return clone;
    }

    /**
     * Returns Color of pixel by index
     * @param index of pixel
     * @return constructed color
     */
    private Color getColor(int index) {
        return new Color(getRed(index), getGreen(index), getBlue(index), getOpacity(index));
    }
    
    private int getOpacity(int index) {
        return (pixels[index] & OPACITY_MASK) >>> OPACITY_SHIFT;
    }
    
    private int getRed(int index) {
        return (pixels[index] & RED_MASK) >>> RED_SHIFT;
    }

    private int getGreen(int index) {
        return (pixels[index] & GREEN_MASK) >>> GREEN_SHIFT;
    }

    private int getBlue(int index) {
        return pixels[index] & BLUE_MASK;
    }

    private void init(int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new int[width * height];
        y = 0;
        x = 0;
    }
}
