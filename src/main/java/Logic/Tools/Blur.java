package Logic.Tools;

import GUI.Elements.Element;
import GUI.Main;
import Logic.Core.Color;
import Logic.Core.Core;
import Logic.Core.Image;
import Logic.Core.Tool;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public final class Blur extends Tool {
    public Blur() {
        super(HaveSettings.TRUE, false);
        this.size = 10;
    }

    public Blur(int size) {
        super(HaveSettings.TRUE, false);
        this.size = size;
    }

    private int size;

    @Override
    protected Element createSettingsWindowTool() {
        return new SettingsWindow();
    }

    @Override
    protected String getName() {
        return "Blur";
    }

    @Override
    protected void applyTool(Image image) {
        double[] kernel = createKernel(size > Math.min(image.getWidth(), image.getHeight()) ?
                Math.min(image.getWidth(), image.getHeight()) : size);
        int[] buffer = applyHorizontal(image.getPixels(), image.getWidth(), kernel);
        image.changeAllPixels(applyVertical(buffer, image.getWidth(), image.getHeight(), kernel));
    }

    private static double[] createKernel(int size) {
        double[] kernel = new double[size * 2 - 1];
        double sum = 1.0;
        kernel[size - 1] = 1.0;
        for (int i = 0; i < size - 1; i++) {
            kernel[i] = Math.exp(-Math.pow(((size - i - 1) * 1.0 / size), 2.0) / 0.1);
            sum += kernel[i] * 2.0;
        }

        // Normalization
        sum = 1 / sum;
        kernel[size - 1] = kernel[size - 1] * sum;
        for (int i = 0; i < size - 1; i++) {
            kernel[i] = kernel[i] * sum;
            kernel[size * 2 - i - 2] = kernel[i];
        }

        return kernel;
    }

    private static int[] applyHorizontal(int[] pixels, int width, double[] kernel) {
        int[] buffer = new int[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            buffer[i] = applyPixelHorizontal(pixels, width, i, kernel);
        }
        return buffer;
    }

    private static int applyPixelHorizontal(int[] pixels, int width, int index, double[] kernel) {
        double red = 0;
        double green = 0;
        double blue = 0;
        double opacity = 0;
        int leftEmpty = (index % width) - (kernel.length / 2);
        leftEmpty = leftEmpty > 0 ? 0 : -leftEmpty;
        int leftPixel = pixels[index - (index % width)];
        int rightEmpty = ((index % width) + (kernel.length / 2)) % width + 1;
        rightEmpty = rightEmpty > (kernel.length / 2) ? 0 : rightEmpty;
        int rightPixel = pixels[index + width - (index % width) - 1];
        for (int i = 0; i < kernel.length; i++) {
            if (i < leftEmpty) {
                red += Color.getRed(leftPixel) * kernel[i];
                green += Color.getGreen(leftPixel) * kernel[i];
                blue += Color.getBlue(leftPixel) * kernel[i];
                opacity += Color.getOpacity(leftPixel) * kernel[i];
            } else {
                if (i + 1 > kernel.length - rightEmpty) {
                    red += Color.getRed(rightPixel) * kernel[i];
                    green += Color.getGreen(rightPixel) * kernel[i];
                    blue += Color.getBlue(rightPixel) * kernel[i];
                    opacity += Color.getOpacity(rightPixel) * kernel[i];
                } else {
                    red += Color.getRed(pixels[index + i - kernel.length / 2]) * kernel[i];
                    green += Color.getGreen(pixels[index + i - kernel.length / 2]) * kernel[i];
                    blue += Color.getBlue(pixels[index + i - kernel.length / 2]) * kernel[i];
                    opacity += Color.getOpacity(pixels[index + i - kernel.length / 2]) * kernel[i];
                }
            }
        }
        return Color.toIntARGB((int) opacity,(int) red,(int) green,(int) blue);
    }

    private static int[] applyVertical(int[] pixels, int width, int height, double[] kernel) {
        int[] buffer = new int[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            buffer[i] = applyPixelVertical(pixels, width, height, i, kernel);
        }
        return buffer;
    }

    private static int applyPixelVertical(int[] pixels, int width, int height, int index, double[] kernel) {
        double red = 0;
        double green = 0;
        double blue = 0;
        double opacity = 0;
        int leftEmpty = (index / width) - (kernel.length / 2);
        leftEmpty = leftEmpty > 0 ? 0 : -leftEmpty;
        int leftPixel = pixels[index % width];
        int rightEmpty = ((index / width) + (kernel.length / 2)) % height + 1;
        rightEmpty = rightEmpty > (kernel.length / 2) ? 0 : rightEmpty;
        int rightPixel = pixels[index % width + width * (height - 1)];
        for (int i = 0; i < kernel.length; i++) {
            if (i < leftEmpty) {
                red += Color.getRed(leftPixel) * kernel[i];
                green += Color.getGreen(leftPixel) * kernel[i];
                blue += Color.getBlue(leftPixel) * kernel[i];
                opacity += Color.getOpacity(leftPixel) * kernel[i];
            } else {
                if (i + 1 > kernel.length - rightEmpty) {
                    red += Color.getRed(rightPixel) * kernel[i];
                    green += Color.getGreen(rightPixel) * kernel[i];
                    blue += Color.getBlue(rightPixel) * kernel[i];
                    opacity += Color.getOpacity(rightPixel) * kernel[i];
                } else {
                    if (index + i - kernel.length / 2 == 666000) {
                        System.out.println();
                    }
                    red += Color.getRed(pixels[index + width * (i - kernel.length / 2)]) * kernel[i];
                    green += Color.getGreen(pixels[index + width * (i - kernel.length / 2)]) * kernel[i];
                    blue += Color.getBlue(pixels[index + width * (i - kernel.length / 2)]) * kernel[i];
                    opacity += Color.getOpacity(pixels[index + width * (i - kernel.length / 2)]) * kernel[i];
                }
            }
        }
        return Color.toIntARGB((int) opacity,(int) red,(int) green,(int) blue);
    }

    private class SettingsWindow extends Element {
        public SettingsWindow() {
            super(100, 25, Decorated.DECORATED, "Blur Settings");
            final TextField text = new TextField();
            text.setMaxWidth(100);
            text.setPrefWidth(100);
            text.setText(String.valueOf(size));
            text.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Double value = new Double(text.getText());
                    size = (int) value.doubleValue();
                    Core.calculate();
                }
            });
            under.add(text);
        }

        public boolean isCreated() {
            return Blur.super.created;
        }

        @Override
        protected void exit() {
            Blur.super.created = false;
            Blur.super.settingsWindow = null;
        }
    }
}
