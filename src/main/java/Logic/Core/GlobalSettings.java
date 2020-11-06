package Logic.Core;

public class GlobalSettings {
    public GlobalSettings() {
        debugMode = true;
        debugWindow = true;
    }

    private static GlobalSettings self = new GlobalSettings();

    // Debug mode
    private boolean debugMode;
    public enum DEBUG_MODE {
        ENABLED(), DISABLED
    }
    public static DEBUG_MODE debugModeState() {
        return self.debugMode ? DEBUG_MODE.ENABLED : DEBUG_MODE.DISABLED;
    }
    public static void debugModeSet(DEBUG_MODE debugMode) {
        self.debugMode = debugMode == DEBUG_MODE.ENABLED;
    }

    // Debug window
    private boolean debugWindow;
    public enum DEBUG_WINDOW {
        ENABLED, DISABLED
    }
    public static DEBUG_WINDOW debugWindowState() {
        return self.debugWindow ? DEBUG_WINDOW.ENABLED : DEBUG_WINDOW.DISABLED;
    }
    public static void debugWindowSet(DEBUG_WINDOW debugWindow) {
        self.debugWindow = debugWindow == DEBUG_WINDOW.ENABLED;
    }
}
