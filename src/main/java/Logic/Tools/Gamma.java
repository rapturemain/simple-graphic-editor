package Logic.Tools;

import GUI.Elements.Element;
import Logic.Core.Color;
import Logic.Core.GlobalSettings;
import Logic.Core.Image;
import Logic.Core.Tool;

public final class Gamma extends Tool {
    public Gamma() {
        super(HaveSettings.TRUE, false);
        gamme = 0.5;
    }

    public Gamma(double gamma) {
        super(HaveSettings.TRUE, false);
        this.gamme = gamma;
    }

    private double gamme;

    @Override
    protected String getName() {
        return "Gamma";
    }

    @Override
    protected Element createSettingsWindowTool() {
        return null;
    }

    @Override
    protected void applyTool(Image image) {
        apply(image, gamme);
        if (GlobalSettings.debugModeState() == GlobalSettings.DEBUG_MODE.ENABLED) {
            System.out.println("Done applying gamma. Gamma: " + gamme);
        }
    }

    private static boolean apply(Image image, double gamma) {
        int[] pixels = image.getPixels();
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = changePixel(pixels[i], gamma);
        }
        return true;
    }

    private static int changePixel(int ARGB, double gamma) {
        int red = Color.getRed(ARGB) + (int)(255 * gamma * 0.33);
        int green = Color.getGreen(ARGB) + (int)(255 * gamma * 0.33);
        int blue =  Color.getBlue(ARGB) + (int)(255 * gamma * 0.33);
        return Color.toIntARGB(Color.getOpacity(ARGB), red > 255 ? 255 : red < 0 ? 0 : red,
                green > 255 ? 255 : green < 0 ? 0 : green, blue > 255 ? 255 : blue < 0 ? 0 : blue);
    }
}
