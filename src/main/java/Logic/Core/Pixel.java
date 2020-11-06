package Logic.Core;

public class Pixel {
    public Pixel (int index, Color color) {
        this.index = index;
        this.color = color;
    }

    private int index;
    private Color color;

    public int getIndex() {
        return index;
    }

    public Color getColor() {
        return color;
    }
}
