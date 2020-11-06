package Logic.Core;

import GUI.Elements.Element;
import GUI.Elements.LayersWindow;
import GUI.Main;

/**
 * Superclass for any tool in SGE.
 * Any tool should implement this class.
 * Any tool should NOT be abstract.
 *
 * Created by: Ramis Sakhibgareev (Rapture).
 * First release: Version 1.0 SNAPSHOT.
 */


public abstract class Tool {
    protected Tool(HaveSettings haveSettings, boolean moveable) {
        this.haveSettings = haveSettings;
        this.moveable = moveable;
    }

    private boolean moveable;
    private int x;
    private int y;

    private HaveSettings haveSettings;

    protected boolean created = false;

    protected Element settingsWindow = null;

    public boolean isMoveable() {
        return moveable;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    /**
     * Not sure, will it be used, but...
     * @return haveSettings
     */
    public boolean haveSettings() {
        return haveSettings == HaveSettings.TRUE;
    }

    /**
     * Used in layers window to open settings window of tool.
     */
    public void openSettionsWindow() {
        if (haveSettings == HaveSettings.TRUE) {
            if (created) {
                Main.onTop(settingsWindow);
            } else {
                created = true;
                settingsWindow = createSettingsWindowTool();
                Main.addElement(settingsWindow);
            }
        }
    }

    /**
     * @return null if window opened or not supported. Returns settings window element otherwise.
     */
    public Element getSettionsWindow() {
        if (haveSettings == HaveSettings.TRUE) {
            if (created) {
                Main.onTop(settingsWindow);
                return null;
            } else {
                created = true;
                settingsWindow = createSettingsWindowTool();
                return settingsWindow;
            }
        } else {
            return null;
        }
    }

    /**
     * Apply tool to image.
     * @param image to which tool should apply
     */
    public void apply(Image image) {
        applyTool(image);
    }

    /**
     * Should be implemented if haveSettings = true.
     * Will open settings window (extended from Element) if right-clicked on layer
     * in layers window.
     */
    protected abstract Element createSettingsWindowTool();

    /**
     * Apply tool to image.
     * @param image to which tool should apply
     */
    protected abstract void applyTool(Image image);

    /**
     * Should be implemented for all tools (but ir not necessary)
     * @return name of tool for display in layers window.
     */
    protected String getName() {
        return "Tool superclass";
    }

    /**
     * Defines should tool have settings or not.
     */
    protected enum HaveSettings {
        TRUE, FALSE
    }
}
