package Logic.Core;

import GUI.Main;

import java.util.LinkedList;
import java.util.List;

public class Layers {
    private static List<Layer> layers = new LinkedList<>();
    private static int lastLayerNameNumber = 1;

    private static List<Layer> selectedLayers = new LinkedList<>();

    public static void moveLayersPosition(int dx, int dy) {
        for (Layer l : selectedLayers) {
            l.moveLayer(dx, dy);
        }
    }

    public static List<Layer> getSelectedLayers() {
        return selectedLayers;
    }

    /**
     * @return number of layers to display
     */
    public static int getSize() {
        return layers.size();
    }

    // TODO: ADD INDEX CHECK

    /**
     * @param index of layer
     * @return layer
     */
    public static Layer getLayer(int index) {
        if (isIndexRight(index)) {
            return layers.get(index);
        } else {
            return null;
        }
    }

    /**
     * @param layer to add
     */
    public static void addLayer(Layer layer) {
        if (layer.getName() == null) {
            layer.setName(getNextNewLayerName());
        }
        layers.add(layer);
        Core.calculate();
    }

    /**
     * Moves layer from index to index.
     * @param indexFrom move from
     * @param indexTo move to
     * @return false if not successful
     */
    public static boolean moveLayer(int indexFrom, int indexTo) {
        if (indexFrom == indexTo) {
            return false;
        }
        if (isIndexRight(indexFrom) && isIndexRight(indexTo)) {
            if (indexTo > indexFrom) {
                layers.add(indexTo + 1, layers.get(indexFrom));
                layers.remove(indexFrom);
            } else {
                layers.add(indexTo, layers.get(indexFrom));
                layers.remove(indexFrom + 1);
            }
            Core.calculate();
            return true;
        } else {
            return false;
        }
    }

    public static void removeLayer(int index) {
        if (isIndexRight(index)) {
            layers.remove(index);
            Core.calculate();
        }
    }

    /**
     * @param index of layer
     * @return name of layer
     */
    public static String getLayerName(int index) {
        if (isIndexRight(index)) {
            return layers.get(index).getName();
        } else {
            return "Error: index is wrong " + index + " >= " + layers.size();
        }
    }

    /**
     * @param index of layer
     * @return Image of layer
     */
    public static Image getLayerImage(int index) {
        if (isIndexRight(index)) {
            return layers.get(index).getImage();
        } else {
            return null;
        }
    }

    /**
     *  Clears the layers to display.
     */
    public static void clear() {
        layers.clear();
        lastLayerNameNumber = 1;
        Core.calculate();
    }

    /**
     * @param index to check
     * @return true, if index is in bounds.
     */
    private static boolean isIndexRight(int index) {
        return index < layers.size() && index >= 0;
    }

    /**
     * @return a new name based on used number of default names
     */
    private static String getNextNewLayerName() {
        return "Image " + lastLayerNameNumber++;
    }
}
