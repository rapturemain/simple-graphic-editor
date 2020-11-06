package Logic.Core;

import GUI.Elements.Element;
import com.sun.istack.internal.Nullable;

public class Layer {
    public Layer(Image image) {
        this.image = image;
        this.type = Type.IMAGE_LAYER;
        this.opacity = 1.0;
    }

    public Layer(Tool tool) {
        this.tool = tool;
        this.type = Type.TOOL_LAYER;
        this.name = this.tool.getName();
        this.opacity = 1.0;
    }

    private Type type;
    private Image image;
    private Tool tool;
    private String name;
    private double opacity;

    public Image apply(Image image) {
        if (this.type == Type.IMAGE_LAYER) {
            return ImageTools.standartOverlay(image, this.image, opacity);
        } else {
            Image im = image.clone();
            tool.apply(im);
            return ImageTools.standartOverlay(image, im, opacity);
        }
    }

    public Element getSettingsWindow() {
        if (type == Type.TOOL_LAYER) {
            return tool.getSettionsWindow();
        } else {
            return null;
        }
    }

    public void moveLayer(int dx, int dy) {
        if (type == Type.IMAGE_LAYER) {
            image.setX(image.getX() + dx);
            image.setY(image.getY() + dy);
        }
        if (type == Type.TOOL_LAYER && tool.isMoveable()) {
            tool.setX(tool.getX() + dx);
            tool.setY(tool.getY() + dy);
        }
        if (type == Type.GROUP_LAYER) {
            // TODO
        }
    }

    public void resetPostition() {

    }

    public void setOpacity(double opacity) {
        if (opacity >= 0 && opacity <= 1) {
            this.opacity = opacity;
        }
    }

    public double getOpacity() {
        return opacity;
    }

    public Type getType() {
        return type;
    }

    public Image getImage() {
        return image;
    }

    public Tool getTool() {
        return tool;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    enum Type {
        IMAGE_LAYER, TOOL_LAYER, GROUP_LAYER
    }
}
