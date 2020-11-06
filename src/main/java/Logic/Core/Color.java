package Logic.Core;

import Exceptions.WrongColorException;

/**
 * Basic class for color
 * Colors represented in ARGB
 */

public class Color {

    public Color(int opacity, int red, int green, int blue) {
        if (opacity > 255 || opacity < 0 || red > 255 || red < 0 || green > 255 || green < 0 || blue > 255 || blue < 0) {
            throw new WrongColorException();
        }
        this.opacity = opacity;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Color(int ARGB) {
        fromIntARGB(ARGB);
    }

    private int opacity;
    private int red;
    private int green;
    private int blue;

    public int getOpacity() {
        return opacity;
    }

    public static int getOpacity(int ARGB) {
        return (ARGB & 0xFF000000) >>> 24;
    }

    public int getRed() {
        return red;
    }

    public static int getRed(int ARGB) {
        return (ARGB & 0x00FF0000) >>> 16;
    }

    public int getGreen() {
        return green;
    }

    public static int getGreen(int ARGB) {
        return (ARGB & 0x0000FF00) >>> 8;
    }

    public int getBlue() {
        return blue;
    }

    public static int getBlue(int ARGB) {
        return ARGB & 0x000000FF;
    }

    /**
     * @return integer ARGB representation of color
     */
    public int toIntARGB() {
        return toIntARGB(opacity, red, green, blue);
    }

    public static int toIntARGB(int opacity, int red, int green, int blue) {
        return (opacity << 24) | (red << 16) | (green << 8) | blue;
    }

    public void fromIntARGB(int ARGB) {
        opacity = getOpacity(ARGB);
        red = getRed(ARGB);
        green = getGreen(ARGB);
        blue = getBlue(ARGB);
    }

    @Override
    public String toString() {
        return "Opacity " + opacity + " red " + red + " green " + green + " blue " + blue;
    }

    @Override
    public boolean equals(Object obj) {
        // Class check
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        // Values check
        Color other = (Color) obj;
        return other.red == red && other.green == green && other.blue == blue && other.opacity == opacity;
    }
}
