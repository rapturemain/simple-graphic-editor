package GUI.Elements;

import GUI.Main;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public abstract class Element extends Region {
    /**
     * Creates element with content area (Group under)
     * @param width - width of content area.
     * @param height - height of content area.
     * @param decorated - If decorated then element will have borders &
     *                  background for all it area and will have upper line.
     *                  Background and borders may be changed by overriding createBackground method.
     */
    protected Element(int width, int height, Decorated decorated) {
        init(width, height, decorated);
    }

    /**
     * Same as prev. constructor
     * @param name - name of window
     */
    protected Element(int width, int height, Decorated decorated, String name) {
        this.name = name;
        init(width, height, decorated);
    }

    private final int BORDER_SIZE = 4;

    private int width;
    private int height;
    private String name;

    private final Element el = this;
    protected ObservableList<Node> under;
    private Group underGroup;
    private Group upper;
    private Canvas background;
    private double mouseStartX;
    private double mouseStartY;

    /**
     * TODO: TEST FUNCTION
     */
    protected final void init() {
        for (int i = 0; i < under.size(); i++) {
            if (under.get(i) instanceof Node) {
                under.get(i).addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                            Main.onTop(el);
                        }
                    }
                });
            }
        }
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Main.onTop(el);
            }
        });
    }

    /**
     * @return width of active zone (underGroup)
     */
    public int getUnderWidth() {
        return width;
    }

    /**
     * @return height of active zone (underGroup)
     */
    public int getUnderHeight() {
        return height;
    }

    protected final Group getUnderGroup() {
        return underGroup;
    }

    /**
     * Removes this element from root (closes this element)
     */
    public void close() {
        exit();
        Main.getRoot().getChildren().remove(this);
    }

    /**
     * Function, that executes when this element removes.
     */
    protected abstract void exit();

    /**
     * Creates gray background with light gray borders. Work only if element is decorated.
     */
    protected void createBackground(Decorated decorated) {
        if (decorated == Decorated.DECORATED) {
            background = new Canvas(this.getWidth(), this.getHeight());
            GraphicsContext gc = background.getGraphicsContext2D();
            gc.setFill(new Color(0.8, 0.8, 0.8, 1));
            gc.fillRect(0, 0, background.getWidth(), background.getHeight());
            gc.setFill(new Color(100.0 / 255, 100.0 / 255, 100.0 / 255, 1));
            gc.fillRect(BORDER_SIZE, BORDER_SIZE, background.getWidth() - BORDER_SIZE * 2,
                    background.getHeight() - BORDER_SIZE * 2);
            this.getChildren().add(background);
        }
    }

    /**
     * Computes element shift based on mouse move and mouse position.
     * Protected from moving element over window.
     */
    private void moveWindow(double x, double y, double sceneX, double sceneY) {
        if (sceneX > Main.getWidth() - 5) {
            this.setLayoutX(Main.getWidth() - mouseStartX - 5);
        } else if (sceneX < 5) {
            this.setLayoutX(-mouseStartX + 5);
        } else {
            this.setLayoutX(this.getLayoutX() + x - mouseStartX);
        }
        if (sceneY > Main.getHeight() - 5) {
            this.setLayoutY(Main.getHeight() - mouseStartY - 5);
        } else if (sceneY < 5) {
            this.setLayoutY(-mouseStartY + 5);
        } else {
            this.setLayoutY(this.getLayoutY() + y - mouseStartY);
        }
    }

    /**
     * Support method for moveWindow().
     */
    private void setMouseStartPosition(double x, double y) {
        this.mouseStartX = x;
        this.mouseStartY = y;
    }

    /**
     * Creates upper line with title and close button. Also can be used to drag element along the window.
     */
    private void createUpperLine(Decorated decorated) {
        if (decorated == Decorated.DECORATED) {
            this.upper = new Group();

            Canvas canvas = new Canvas(this.getWidth(), 25);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setFill(new Color(0.8, 0.8, 0.8, 1));
            gc.fillRect(0, 0, this.getWidth() - 25, 25);
            gc.setFill(Color.RED);
            gc.fillRect(this.getWidth() - 25, 0, this.getWidth(), 25);

            Button exit = new Button();
            exit.setLayoutX(this.getWidth() - 25);
            exit.setOpacity(0);
            exit.setPrefWidth(25);
            exit.setPrefHeight(25);
            exit.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    close();
                }
            });

            Button moveButton = new Button();
            moveButton.setOpacity(0);
            moveButton.setPrefWidth(this.getWidth() - 25);
            moveButton.setPrefHeight(25);
            moveButton.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                        setMouseStartPosition(event.getX(), event.getY());
                        Main.onTop(el);
                    }
                    if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                        moveWindow(event.getX(), event.getY(), event.getSceneX(), event.getSceneY());
                    }
                }
            });

            Text text = new Text(name);
            text.setFill(Color.BLACK);
            text.setLayoutX(10);
            text.setLayoutY(17);

            upper.getChildren().addAll(canvas, text, moveButton, exit);
            this.getChildren().add(upper);
        }
    }

    /**
     * Creates group for content.
     */
    private void createUnderLine(Decorated decorated) {
        this.underGroup = new Group();
        if (decorated == Decorated.DECORATED) {
            underGroup.setLayoutY(25);
            underGroup.setLayoutX(BORDER_SIZE);
        }
        under = underGroup.getChildren();
        this.getChildren().add(underGroup);
    }

    /**
     * Basic constructor
     * @param width - width of content area
     * @param height - height of content area
     * @param decorated - should window have borders and upper line
     */
    private void init(int width, int height, Decorated decorated) {
        this.width = width;
        this.height = height;
        this.setWidth(decorated == Decorated.DECORATED ? width + BORDER_SIZE * 2 : width);
        this.setHeight(decorated == Decorated.DECORATED ? height + BORDER_SIZE + 25 : height);
        createBackground(decorated);
        createUnderLine(decorated);
        createUpperLine(decorated);
    }

    protected enum Decorated {
        DECORATED, UNDECORATED
    }
}
