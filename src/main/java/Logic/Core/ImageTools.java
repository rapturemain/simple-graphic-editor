package Logic.Core;

/**
 * Class for image - image processing.
 * NOT STANDARD TOOL, MUST NOT EXTEND TOOL CLASS.
 *
 * Created by: Ramis Sakhibgareev (Rapture)
 * First release: Version 1.0 SNAPSHOT
 */
public abstract class ImageTools {
    /**
     * TODO: CHECK OPACITY CORRECTNESS
     * @param underImage - under
     * @param upperImage - upper
     * @param opacity - opacity of upper image
     */
    public static Image standartOverlay(Image underImage, Image upperImage, double opacity) {
        if (opacity < 0 || opacity > 1) {
            opacity = 1.0;
        }
        // Create new image with min size.
        Image image = createNewImage(underImage, upperImage);

        int[] upperPixels = upperImage.getPixels();
        int[] underPixels = underImage.getPixels();
        int[] pixels = image.getPixels();

        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                int underRed = 0;
                int underGreen = 0;
                int underBlue = 0;
                int underOpacity = 0;
                if (pixelInImage(j, i, image, underImage)) {
                    int pixel = underPixels
                            [getY(i, image, underImage) * underImage.getWidth() + getX(j, image, underImage)];
                    underRed = Color.getRed(pixel);
                    underGreen = Color.getGreen(pixel);
                    underBlue = Color.getBlue(pixel);
                    underOpacity = Color.getOpacity(pixel);
                }

                int upperRed = 0;
                int upperGreen = 0;
                int upperBlue = 0;
                int upperOpacity = 0;
                if (pixelInImage(j, i, image, upperImage)) {
                    int pixel = upperPixels
                            [getY(i, image, upperImage) * upperImage.getWidth() + getX(j, image, upperImage)];
                    upperRed = Color.getRed(pixel);
                    upperGreen = Color.getGreen(pixel);
                    upperBlue = Color.getBlue(pixel);
                    upperOpacity = Color.getOpacity(pixel);
                }

                pixels[i * image.getWidth() + j] = Color.toIntARGB(
//                        underOpacity + (int) ((upperOpacity * opacity - underOpacity) * upperOpacity * opacity / 255.0),
                        Math.max(underOpacity, (int) (upperOpacity * opacity)),
                        underRed + (int) ((upperRed - underRed) * upperOpacity * opacity / 255.0),
                        underGreen + (int) ((upperGreen - underGreen) * upperOpacity * opacity / 255.0),
                        underBlue + (int) ((upperBlue - underBlue) * upperOpacity * opacity / 255.0));
            }
        }

        if (GlobalSettings.debugModeState() == GlobalSettings.DEBUG_MODE.ENABLED) {
            System.out.println("Done overlaying images. Opacity " + opacity);
        }

        return image;
    }

    private static Image createNewImage(Image image1, Image image2) {
        Image image = new Image(Math.max(image1.getX() + image1.getWidth(), image2.getX() + image2.getWidth())
                - Math.min(image1.getX(), image2.getX()),
                Math.max(image1.getY() + image1.getHeight(), image2.getY() + image2.getHeight())
                        - Math.min(image1.getY(), image2.getY()));
        image.setX(Math.min(image1.getX(), image2.getX()));
        image.setY(Math.min(image1.getY(), image2.getY()));
        return image;
    }

    private static boolean pixelInImage(int x, int y, Image newImage, Image oldImage) {
//        return oldImage.getX() - newImage.getX() <= x
//               && oldImage.getY() - newImage.getY() <= y
//               && oldImage.getX() + oldImage.getWidth() - newImage.getX() > x
//               && oldImage.getY() + oldImage.getHeight() - newImage.getY() > y;
        if (oldImage.getX() == newImage.getX()) {
            if (oldImage.getY() == newImage.getY()) {
                return oldImage.getWidth() > x
                        && oldImage.getHeight() > y;
            } else {
                return oldImage.getWidth() > x
                        && y + newImage.getY() >= oldImage.getY() && oldImage.getY() + oldImage.getHeight() > y + newImage.getY();
            }
        } else {
            if (oldImage.getY() == newImage.getY()) {
                return x + newImage.getX() >= oldImage.getX() && oldImage.getX() + oldImage.getWidth() > x + newImage.getX()
                        && oldImage.getHeight() > y;
            } else {
                return x + newImage.getX() >= oldImage.getX() && oldImage.getX() + oldImage.getWidth() > x + newImage.getX()
                        && y + newImage.getY() >= oldImage.getY() && oldImage.getY() + oldImage.getHeight() > y + newImage.getY();
            }
        }
    }

    private static int getX(int x, Image newImage, Image oldImage) {
        return x - oldImage.getX() + newImage.getX();
    }

    private static int getY(int y, Image newImage, Image oldImage) {
        return y - oldImage.getY() + newImage.getY();
    }
}
