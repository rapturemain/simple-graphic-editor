package GUI.Elements;

import GUI.Main;
import Logic.Core.Core;
import Logic.Core.Image;
import Logic.Core.Layer;
import Logic.Core.Layers;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.List;

public class LayersWindow extends Element {
    private LayersWindow() {
        super(400, 425, Decorated.DECORATED, "Layers");
        entry = new LinkedList<>();

        offsetY = 0;

        isLayerSelected = false;

        EventHandler<Event> scrollEventHandler = new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                offsetY += ((ScrollEvent) event).getDeltaY() * 0.7;
                if (offsetY > (under.size() - 4) * LAYER_HEIGHT - LayersWindow.super.getUnderHeight() / 50.0 - 25) {
                    offsetY = (under.size() - 4) * LAYER_HEIGHT - LayersWindow.super.getUnderHeight() / 50.0 - 25;
                }
                if (offsetY < 0) {
                    offsetY = 0;
                }
                LayersWindow.super.getUnderGroup().setLayoutY(offsetY + 25);
                updateWindow();
            }
        };
        this.addEventHandler(ScrollEvent.SCROLL, scrollEventHandler);

        this.getUnderGroup().addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            private double mouseStartY = 0;
            double startY = 0;
            int newIndex = 0;

            private int moveFrom;
            private int moveTo;

            private long startTime;

            private boolean clickedOnSettingsRegion = false;

            @Override
            public void handle(MouseEvent event) {
                if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    getFirst(event.getY());
                    Canvas canvas = (Canvas) under.get(moveFrom);
                    startY = canvas.getLayoutY();
                    setMouseStartPosition(event);

                    under.remove(canvas);
                    under.add(canvas);
                    newIndex = under.size() - 1;

                    startTime = System.currentTimeMillis();
                }

                if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                    if (event.getY() + offsetY > 30 || !isLayerSelected) {
                        under.get(newIndex).setLayoutY(startY - mouseStartY + event.getY());
                    }
                }

                if (event.isSecondaryButtonDown()) {
                    getFirst(event.getY());
                    Layers.removeLayer(moveFrom);
                }

                if (event.getButton() == MouseButton.MIDDLE && event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    getFirst(event.getY());
                    Layer layer = Layers.getLayer(moveFrom);
                    if (layer != null) {
                        Element settings = layer.getSettingsWindow();
                        if (settings != null) {
                            settings.setLayoutX(event.getSceneX());
                            settings.setLayoutY(event.getSceneY());
                            Main.addElement(settings);
                        }
                    }
                }

                if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                    getSecond(event.getY());
                    boolean unsel = false;
                    if (moveFrom == moveTo && System.currentTimeMillis() - startTime < 350) {
                        Entry anEntry = entry.get(moveFrom);
                        if (isLayerSelected) {
                            for (int i = 0; i < entry.size(); i++) {
                                if (entry.get(i).isSelected()) {
                                    entry.get(i).unSelect();
                                    Layers.getSelectedLayers().remove(entry.get(i).getLayer());
                                    if (anEntry == entry.get(i)) {
                                        isLayerSelected = false;
                                        under.remove(layerSettingRegion);
                                        layerSettingRegion = null;
                                        unsel = true;
                                    }
                                }
                            }
                        }
                        if (!unsel) {
                            isLayerSelected = true;
                            layerSettingRegion = new LayerSettingRegion(moveFrom);
                            anEntry.select();
                            Layers.getSelectedLayers().add(anEntry.getLayer());
                        }
                        LayersWindow.update();
                    } else {
                        if (!Layers.moveLayer(moveFrom, moveTo)) {
                            LayersWindow.update();
                        }
                    }
                }
            }

            private void getFirst(double y) {
                moveFrom = (int) ((LayersWindow.super.getUnderHeight() - y - 1) / LayersWindow.LAYER_HEIGHT);
            }

            private void getSecond(double y) {
                moveTo = (int) ((LayersWindow.super.getUnderHeight() - y - 1) / LayersWindow.LAYER_HEIGHT);
            }

            private void setMouseStartPosition(MouseEvent event) {
                this.mouseStartY = event.getY();
            }
        });

        updateWindow();
        super.init();
    }

    private static boolean created = false;
    private static LayersWindow lw;
    private static final int LAYER_HEIGHT = 60;
    private static final int LAYER_SETTINGS_REGION_HEIGHT = 30;

    private List<Entry> entry;
    private double offsetY;

    private boolean isLayerSelected = false;
    private LayerSettingRegion layerSettingRegion = null;

    public static void create() {
        if (!created) {
            created = true;
            lw = new LayersWindow();
            lw.setLayoutY(25);
            Main.addElement(lw);
        }
    }

    public static void remove() {
        if (created) {
            created = false;
            Main.removeElement(lw);
            lw = null;
        }
    }

    public static void createOrRemove() {
        if (created) {
            remove();
        } else {
            create();
        }
    }

    public static void update() {
        if (created) {
            lw.updateWindow();
        }
    }

    @Override
    protected void exit() {
        created = false;
        lw = null;
    }

    private void updateWindow() {
        clearUnused();
        under.clear();
        for (int i = 0; i < Layers.getSize(); i++) {
            int layoutY = this.getUnderHeight() - LAYER_HEIGHT * (i + 1);
            Entry entry = getEntry(i);
            // Above
            if (layoutY + offsetY < LAYER_SETTINGS_REGION_HEIGHT * (isLayerSelected ? 1.0 : 0)) {
                entry.getCanvas().getGraphicsContext2D().clearRect(0, 0,
                        this.getUnderWidth(),
                        -(layoutY + offsetY - LAYER_SETTINGS_REGION_HEIGHT * (isLayerSelected ? 1.0 : 0)));
                entry.modify();
            }
            // Below
            if (layoutY + offsetY + LAYER_HEIGHT > this.getUnderHeight()) {
                entry.getCanvas().getGraphicsContext2D().clearRect(0,
                        this.getUnderHeight() - layoutY - offsetY > 0 ? this.getUnderHeight() - layoutY - offsetY : 0,
                        this.getUnderWidth(), LAYER_HEIGHT);
                entry.modify();
            }
            under.add(entry.getCanvas());
            under.get(i).setLayoutY(layoutY);
        }
        if (isLayerSelected) {
            layerSettingRegion.updateLayout();
            under.add(layerSettingRegion);
        }
    }

    private Entry getEntry(int index) {
        Layer layer = Layers.getLayer(index);
        for (int i = 0; i < entry.size(); i++) {
            if (layer == entry.get(i).getLayer()) {
                if (entry.get(i).isModified()) {
                   entry.get(i).updateSelected();
                }
                return entry.get(i);
            }
        }
        Entry entry = new Entry(layer, new LayerCanvas(index, false), index);
        this.entry.add(entry);
        return entry;
    }

    private void clearUnused() {
        List<Entry> used = new LinkedList<>();
        for (int i = 0; i < entry.size(); i++) {
            for (int j = 0; j < Layers.getSize(); j++) {
                if (Layers.getLayer(j) == entry.get(i).getLayer()) {
                    used.add(entry.get(i));
                    break;
                }
            }
        }
        entry = used;
    }

    private class Entry {
        private Entry(Layer layer, LayerCanvas canvas, int index) {
            this.layer = layer;
            this.canvas = canvas;
            this.index = index;
            this.modified = false;
            this.selected = false;
        }

        private Layer layer;
        private LayerCanvas canvas;
        private boolean modified;
        private boolean selected;
        private int index;

        public Layer getLayer() {
            return layer;
        }

        public LayerCanvas getCanvas() {
            return canvas;
        }

        public boolean isModified() {
            return modified;
        }

        public void modify() {
            this.modified = true;
        }

        public void updateSelected() {
            this.canvas = new LayerCanvas(index, selected);
            this.modified = false;
        }

        public void select() {
            this.selected = true;
            this.modified = true;
        }

        public void unSelect() {
            this.selected = false;
            this.modified = true;
        }

        public boolean isSelected() {
            return selected;
        }
        public void cropCanvas(double height, boolean cropUnder) {
            this.canvas = new LayerCanvas(index, selected, height, cropUnder);
            this.modified = true;
        }
    }

    private class LayerSettingRegion extends Group {
        private LayerSettingRegion(int index) {
            layer = Layers.getLayer(index);

            slider = new Slider();
            slider.setLayoutX(10);
            slider.setLayoutY(LAYER_SETTINGS_REGION_HEIGHT / 3.0 - 2);
            slider.setMaxWidth(LayersWindow.super.getUnderWidth() - 10);
            slider.setPrefWidth(80);
            slider.setMax(100);
            slider.setMin(0);
            slider.adjustValue(layer.getOpacity() * 100);
            slider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    layer.setOpacity(newValue.doubleValue() / 100.0);
//                    Core.calculate();
                }
            });
            slider.setOnMouseReleased(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    Core.calculate();
                }
            });

            this.setLayoutY(-offsetY);
            this.getChildren().add(slider);
        }

        private void updateLayout() {
            this.setLayoutY(-offsetY);
        }

        private Slider slider;
        private Layer layer;
    }

    private class LayerCanvas extends Canvas {
        private LayerCanvas(int index, boolean selected) {
            super(LayersWindow.super.getUnderWidth(), LayersWindow.LAYER_HEIGHT);
            // Background (if selected)
            if (selected) {
                this.getGraphicsContext2D().setFill(new Color(0.5, 0.5, 0.5, 1));
                this.getGraphicsContext2D().fillRect(0, 0, this.getWidth(), this.getHeight());
            }
            this.getGraphicsContext2D().setFill(new Color(0, 0, 0, 1));


            Image coreImage = Layers.getLayerImage(index);
            if (coreImage == null) {
                this.getGraphicsContext2D().fillRect(5, 2,
                        LayersWindow.LAYER_HEIGHT - 4, LayersWindow.LAYER_HEIGHT - 4);
            } else {
                int width = coreImage.getWidth();
                int height = coreImage.getHeight();
                if (width > height) {
                    this.getGraphicsContext2D().drawImage(coreImage.toFXImage(), 5, 2,
                            LayersWindow.LAYER_HEIGHT - 4, this.getHeight() * height / width - 4);
                } else {
                    this.getGraphicsContext2D().drawImage(coreImage.toFXImage(), 5, 2,
                            this.getWidth() * width / height - 4, this.getHeight() - 4);
                }
            }

            this.getGraphicsContext2D().setFill(Color.WHITE);
            this.getGraphicsContext2D().fillText(Layers.getLayerName(index),
                    LayersWindow.LAYER_HEIGHT + 10, LayersWindow.LAYER_HEIGHT / 2.0 + 5);

            this.getGraphicsContext2D().setFill(new Color(0.6, 0.6, 0.6, 1));
            this.getGraphicsContext2D().fillRect(LayersWindow.LAYER_HEIGHT + 10, 0,
                    this.getWidth() - 20 - LayersWindow.LAYER_HEIGHT, 1);
            this.getGraphicsContext2D().fillRect(LayersWindow.LAYER_HEIGHT + 10,
                    this.getHeight() - 1, this.getWidth() - 20 - LayersWindow.LAYER_HEIGHT, 1);
        }

        private LayerCanvas(int index, boolean selected, double canvasHe, boolean cropUnder) {
            super(LayersWindow.super.getUnderWidth(), canvasHe);
            double he = cropUnder ? 0 : -canvasHe;
            // Background (if selected)
            if (selected) {
                this.getGraphicsContext2D().setFill(new Color(0.5, 0.5, 0.5, 1));
                this.getGraphicsContext2D().fillRect(0, he, this.getWidth(), this.getHeight());
            }
            this.getGraphicsContext2D().setFill(new Color(0, 0, 0, 1));


            Image coreImage = Layers.getLayerImage(index);
            if (coreImage == null) {
                this.getGraphicsContext2D().fillRect(5, 2 + he,
                        LayersWindow.LAYER_HEIGHT - 4, LayersWindow.LAYER_HEIGHT - 4);
            } else {
                int width = coreImage.getWidth();
                int height = coreImage.getHeight();
                if (width > height) {
                    this.getGraphicsContext2D().drawImage(coreImage.toFXImage(), 5, 2 + he,
                            LayersWindow.LAYER_HEIGHT - 4, this.getHeight() * height / width - 4);
                } else {
                    this.getGraphicsContext2D().drawImage(coreImage.toFXImage(), 5, 2 + he,
                            this.getWidth() * width / height - 4, this.getHeight() - 4);
                }
            }

            this.getGraphicsContext2D().setFill(Color.WHITE);
            this.getGraphicsContext2D().fillText(Layers.getLayerName(index),
                    LayersWindow.LAYER_HEIGHT + 10, LayersWindow.LAYER_HEIGHT / 2.0 + 5 + he);

            this.getGraphicsContext2D().setFill(new Color(0.6, 0.6, 0.6, 1));
            this.getGraphicsContext2D().fillRect(LayersWindow.LAYER_HEIGHT + 10, he,
                    this.getWidth() - 20 - LayersWindow.LAYER_HEIGHT, 1);
            this.getGraphicsContext2D().fillRect(LayersWindow.LAYER_HEIGHT + 10,
                    this.getHeight() - 1 + he, this.getWidth() - 20 - LayersWindow.LAYER_HEIGHT, 1);
        }
    }
}
