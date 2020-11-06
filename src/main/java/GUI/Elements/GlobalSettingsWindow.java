package GUI.Elements;

import GUI.Main;
import Logic.Core.GlobalSettings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.paint.Color;

public class GlobalSettingsWindow extends Element {
    public GlobalSettingsWindow() {
        super(400, 400, Decorated.DECORATED, "Global settings");
        // Checkboxes
        // Debug mode
        CheckBox debugMode = new CheckBox("Debug mode");
        debugMode.setLayoutX(10);
        debugMode.setLayoutY(10);
        debugMode.setTextFill(Color.LIGHTGRAY);
        debugMode.setSelected(GlobalSettings.debugModeState() == GlobalSettings.DEBUG_MODE.ENABLED);
        debugMode.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (GlobalSettings.debugModeState() == GlobalSettings.DEBUG_MODE.ENABLED) {
                    GlobalSettings.debugModeSet(GlobalSettings.DEBUG_MODE.DISABLED);
                } else {
                    GlobalSettings.debugModeSet(GlobalSettings.DEBUG_MODE.ENABLED);
                }
            }
        });
        // Debug window
        CheckBox debugWindow = new CheckBox("Debug window");
        debugWindow.setLayoutX(10);
        debugWindow.setLayoutY(40);
        debugWindow.setTextFill(Color.LIGHTGRAY);
        debugWindow.setSelected(GlobalSettings.debugWindowState() == GlobalSettings.DEBUG_WINDOW.ENABLED);
        debugWindow.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (GlobalSettings.debugWindowState() == GlobalSettings.DEBUG_WINDOW.ENABLED) {
                    GlobalSettings.debugWindowSet(GlobalSettings.DEBUG_WINDOW.DISABLED);
                } else {
                    GlobalSettings.debugWindowSet(GlobalSettings.DEBUG_WINDOW.ENABLED);
                }
            }
        });
        under.addAll(debugMode, debugWindow);
        super.init();
    }

    private static boolean created = false;

    private static GlobalSettingsWindow gsw;

    public static boolean isCreated() {
        return created;
    }

    public static void create() {
        if (!created) {
            created = true;
            gsw = new GlobalSettingsWindow();
            gsw.setLayoutY(25);
            Main.addElement(gsw);
        }
    }

    public static void remove() {
        if (created) {
            created = false;
            Main.removeElement(gsw);
            gsw = null;
        }
    }

    public static void createOrRemove() {
        if (created) {
            remove();
        } else {
            create();
        }
    }

    @Override
    protected void exit() {
        created = false;
        gsw = null;
    }
}
