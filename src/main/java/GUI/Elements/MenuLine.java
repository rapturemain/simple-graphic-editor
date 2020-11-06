package GUI.Elements;

import GUI.Main;
import Logic.Core.Core;
import Logic.Core.Image;
import Logic.Core.Layer;
import Logic.Core.Layers;
import Logic.Tools.Blur;
import Logic.Tools.Gamma;
import Logic.Tools.Grayscale;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class MenuLine extends Element {
    public MenuLine() {
        super(Main.getWidth(), 25, Decorated.UNDECORATED);
        rightButtonsEdge = 0;
        under.add(createButton("Global settings", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                GlobalSettingsWindow.createOrRemove();
            }
        }));
        under.add(createButton("Open file", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
                File selectedFile = fileChooser.showOpenDialog(new Stage());
                if (selectedFile != null) {
                    Core.openImage(new Image(new javafx.scene.image.Image(selectedFile.toURI().toString())));
                }
            }
        }));
        under.add(createButton("Layers", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                LayersWindow.createOrRemove();
            }
        }));

        under.add(createButton("Grayscale", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Layers.addLayer(new Layer(new Grayscale()));
                Core.calculate();
            }
        }));

        under.add(createButton("Gamma", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Layers.addLayer(new Layer(new Gamma(0.2)));
                Core.calculate();
            }
        }));

        under.add(createButton("Blur", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Layers.addLayer(new Layer(new Blur(50)));
                Core.calculate();
            }
        }));
    }

    private int rightButtonsEdge;

    private Button createButton(String text, EventHandler<ActionEvent> event) {
        Button button = new Button(text);
        button.setPrefWidth(text.length() * 6 + 20);
        button.setTextAlignment(TextAlignment.LEFT);
        button.addEventHandler(ActionEvent.ACTION, event);
        button.setLayoutX(rightButtonsEdge);
        rightButtonsEdge += text.length() * 6 + 20;
        return button;
    }

    @Override
    protected void exit() {

    }
}
