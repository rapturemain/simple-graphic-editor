package Logic.Tools;

import GUI.Elements.Element;
import Logic.Core.*;

public final class Grayscale extends Tool {
    public Grayscale() {
        super(HaveSettings.FALSE, false);
        created = true;
    }


    @Override
    protected String getName() {
        return "Grayscale";
    }

    @Override
    protected Element createSettingsWindowTool() {
       return new SettingsWindow();
    }

    @Override
    protected void applyTool(Image image) {
        long time = System.nanoTime();
        int[] pixels = image.getPixels();
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = convertPixel(pixels[i], GrayscaleMode.V2);
        }
        if (GlobalSettings.debugModeState() == GlobalSettings.DEBUG_MODE.ENABLED) {
            System.out.println("Done applying grayscale. Time: " + Long.toString(System.nanoTime() - time));
        }
    }

    public static boolean convert(Image image, GrayscaleMode grayscaleMode) {
        int[] pixels = image.getPixels();
//        for (int core = 0; core < Core.getPoolSize(); core++) {
//            int finalCore = core;
//            Core.execute(new Runnable() {
//                @Override
//                public void run() {
//                    for (int i = finalCore * (pixels.length / Core.getPoolSize());
//                         i < (finalCore + 1) * (pixels.length / Core.getPoolSize()); i++) {
//                        pixels[i] = convertPixel(pixels[i], grayscaleMode);
//                    }
//                }
//            });
//        }
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = convertPixel(pixels[i], grayscaleMode);
        }
        return true;
    }

    private static int convertPixel(int ARGB, GrayscaleMode grayscaleMode) {
        switch (grayscaleMode.getMethod()) {
            case COEFFICIENT:
                int gray;
                double[] w = GrayscaleMode.getCoefficients(grayscaleMode);
                double doubleGray = (Color.getRed(ARGB) * w[0]) + (Color.getGreen(ARGB) * w[1]) + (Color.getBlue(ARGB) * w[2]);
                if (doubleGray < (int) doubleGray) {
                    gray = (int) doubleGray + 1;
                } else {
                    gray = (int) doubleGray;
                }
                return Color.toIntARGB(Color.getOpacity(ARGB), gray, gray, gray);
        }
        return 0;
    }

    private enum Method {
        COEFFICIENT
    }

    private enum GrayscaleMode {
        V1(0, Method.COEFFICIENT), // 06.08.2019 by Rapture Uses coefficient Method. R - 0.25, G - 0.55, B - 0.2
        V2(1, Method.COEFFICIENT); // 07.08.2019 by Rapture Uses coefficient Method. R - 0.2126, G - 0.7152, B - 0.0722

        GrayscaleMode(int index, Method method) {
            this.index = index;
            this.method = method;
        }

        private int index;
        private Method method;

        private int getIndex() {
            return index;
        }

        private Method getMethod() {
            return method;
        }

        private static double[][] coefficients =
                {{0.33, 0.33, 0.33},
                        {0.2126, 0.7152, 0.0722}};

        private static double[] getCoefficients(GrayscaleMode grayscaleMode) {
            return coefficients[grayscaleMode.getIndex()];
        }
    }

    private class SettingsWindow extends Element {
        private SettingsWindow() {
            super(400, 400, Decorated.DECORATED);
        }

        @Override
        protected void exit() {
            created = false;
        }
    }
}
