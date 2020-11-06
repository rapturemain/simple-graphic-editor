package GUI;

import GUI.Elements.Element;
import GUI.Elements.LayersWindow;
import GUI.Elements.MenuLine;
import Logic.Core.Core;
import Logic.Core.GlobalSettings;
import Logic.Core.GlobalSettings.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    private static int WIDTH = 1600;
    private static int HEIGHT = 900;

    private static Group root = new Group();
    private static Scene scene;

    private static int staticObjectsCount = 3;

    public static void main(String[] args) {
        launch(args);
    }

    public static void updateAll() {
        LayersWindow.update();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.initStyle(StageStyle.UNDECORATED);
        root = new Group();
        primaryStage.setTitle("SGE");
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HEIGHT);

        scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(scene);

        primaryStage.show();

        if (GlobalSettings.debugWindowState() == DEBUG_WINDOW.ENABLED) {
            debug(primaryStage);
        } else {

        }
    }

    public static Scene getScene() {
        return scene;
    }
    public static Group getRoot() {
        return root;
    }
    public static int getWidth() {
        return WIDTH;
    }
    public static int getHeight() {
        return HEIGHT;
    }
    public static void onTop(Element element) {
        if (root.getChildren().contains(element) &&
                root.getChildren().size() - 1 != root.getChildren().indexOf(element)) {
            root.getChildren().remove(element);
            root.getChildren().add(element);
        }
    }
    public static void addElement(Element element) {
        root.getChildren().add(element);
    }
    public static void removeElement(Element element) {
        root.getChildren().remove(element);
    }

    private void debug(final Stage primaryStage) {
        Core.init();
        Canvas backGround = new Canvas(primaryStage.getWidth(), primaryStage.getHeight());
        GraphicsContext gc = backGround.getGraphicsContext2D();
        gc.setFill(new Color(0.392, 0.392, 0.392, 1));
        gc.fillRect(0, 0, backGround.getWidth(), backGround.getHeight());
        Button exit = new Button();
        exit.setPrefSize(25, 25);
        exit.setLayoutX(backGround.getWidth() - 25);
        exit.setLayoutY(0);
        exit.setOpacity(0);
        gc.setFill(Color.RED);
        gc.fillRect(backGround.getWidth() - 25, 0, backGround.getWidth(), 25);
        exit.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.close();
            }
        });
        root.getChildren().addAll(backGround, Core.getCanvas(), exit, new MenuLine());
    }
}
