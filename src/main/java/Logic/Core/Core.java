package Logic.Core;

import GUI.Main;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Basic class to logic.
 * Any calculation is processed there.
 *
 * Created by: Ramis Sakhibgareev (Rapture)
 * First release: Version 1.0 SNAPSHOT
 */

public class Core {
    private Core() {
    }

    /**
     * Canvas on which image will be draw in GUI.
     */
    private static Canvas canvas;

    private static boolean created = false;

    static boolean calculating = false;

    private static double lastX = 0;
    private static double lastY = 0;

    private static Image image = null;

    private static double x = 0;
    private static double y = 0;

    private static ThreadPoolExecutor tpe;
    /**
     * @return canvas for GUI.
     */
    public static Canvas getCanvas() {
        return canvas;
    }

    /**
     * Creates canvas. Should be always be called at startup of program.
     */
    public static void init() {
        canvas = new Canvas(Main.getWidth(), Main.getHeight() - 25);
        canvas.setLayoutY(25);
        canvas.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (event.getEventType() == MouseEvent.MOUSE_PRESSED && event.isPrimaryButtonDown()) {
                    lastX = event.getX();
                    lastY = event.getY();
                }

                if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && event.isControlDown()
                        && event.isPrimaryButtonDown() && Layers.getSelectedLayers().size() > 0) {
                    Layers.moveLayersPosition((int) (event.getX() - lastX), (int) (event.getY() - lastY));
                    lastX = event.getX();
                    lastY = event.getY();
                    Core.calculate();
                }

                if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && event.isAltDown()
                        && event.isPrimaryButtonDown()) {
                    x += event.getX() - lastX;
                    y += event.getY() - lastY;
                    lastX = event.getX();
                    lastY = event.getY();
                    canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    canvas.getGraphicsContext2D().drawImage(image.toFXImage(), x, y);
                }
            }
        });
        created = true;
        calculating = false;

        tpe = new ThreadPoolExecutor(4, 4, 300, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
    }

    /**
     * Calculates new image based in layers.
     * First layer must be Image.
     */
    public static void calculate() {
        if (!created) {
            if (GlobalSettings.debugModeState() == GlobalSettings.DEBUG_MODE.ENABLED) {
                System.out.println("ERROR: CORE CLASS IS NOT INITIALIZED, CALCULATION IS NOT AVAILABLE");
            }
            return;
        }
        if (Layers.getSize() == 0 || Layers.getLayer(0).getType() != Layer.Type.IMAGE_LAYER) {
            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            if (GlobalSettings.debugModeState() == GlobalSettings.DEBUG_MODE.ENABLED) {
                System.out.println("ERROR: FIRST LAYER IS NOT IMAGE");
            }
//            Main.updateAll();
        } else {
//            if (!calculating) {
//                calculating = true;
//                Thread thread = new Thread() {
//                    @Override
//                    public void run() {
//                        Image image = Layers.getLayer(0).getImage().clone();
//                        for (int i = 1; i < Layers.getSize(); i++) {
//                            image = Layers.getLayer(i).apply(image);
//                        }
//                        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
//                        canvas.getGraphicsContext2D().drawImage(image.toFXImage(), 0, 0);
//                        Platform.runLater(new Runnable() {
//                            @Override
//                            public void run() {
//                                Main.updateAll();
//                                calculating = false;
//                            }
//                        });
//                    }
//                };
//                thread.setDaemon(true);
//                thread.setPriority(Thread.MAX_PRIORITY);
//                thread.run();
//            }
            image = Layers.getLayer(0).getImage().clone();
            for (int i = 1; i < Layers.getSize(); i++) {
                image = Layers.getLayer(i).apply(image);
            }
            canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            canvas.getGraphicsContext2D().drawImage(image.toFXImage(), x, y);
        }
        Main.updateAll();
    }

    public static void execute(Runnable runnable) {
        tpe.execute(runnable);
    }

    public static int getPoolSize() {
        return tpe.getPoolSize();
    }
    public static boolean isPoolcompleted() {
        return tpe.isTerminated();
    }

    /**
     * Open image in new layer.
     * @param image to open
     */
    public static void openImage(Image image) {
        Layers.addLayer(new Layer(image));
    }

    public static boolean isCalculating() {
        return calculating;
    }

    public static boolean isAvailable() {
        return !calculating;
    }

    /**
     * Clear layers and then open new image.
     * @param image to open
     */
    public static void openNewImage(Image image) {
        Layers.clear();
        Layers.addLayer(new Layer(image));
    }
}
