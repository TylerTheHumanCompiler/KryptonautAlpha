/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafx_finalchat;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.util.HashSet;
import java.util.Set;
 
/**
 *
 * @author fpiplip
 */
public class DraggableTab extends Tab{

 //   public DraggableTab(String tab_1) {
  //  }
       private static final Set<TabPane> tabPanes = new HashSet<>();
    private Label nameLabel;
    private Text dragText;
    private static final Stage markerStage;
    private Stage dragStage;
    private boolean detachable;
    private double xOffset = 0;
    private double yOffset = 0;
    private double dx;
    private double dy;
    private Boolean resizebottom = false;
 
//   public final Stage newStage = new Stage(); // war vorhin nicht als public und nicht hier oben instanziiert sonder einfach unten als Stage stage = new Stage(); !!!
    
    static {
        markerStage = new Stage();
        markerStage.initStyle(StageStyle.UNDECORATED);
        //Rectangle dummy = new Rectangle(3, 20, Color.web("#555555"));
        Rectangle dummy = new Rectangle(3, 25, Color.web("#bbbbbb"));
        //Rectangle dummy = new Rectangle(30, 10, Color.TRANSPARENT);
 
        //Rectangle dummy = new Rectangle(30, 10);
       // dummy.setStyle("-fx-background-color: transparent; -fx-opacity: 0.0"); // opacity funzt nicht, farbe schon...
        StackPane markerStack = new StackPane();
       //markerStack.setPadding(new Insets(0,0,0,100)); //funzt nicht, das rectangle nach oben zu verschieben :(
       // markerStack.setStyle("-fx-background-color: green; -fx-opacity: 0.0");
        markerStack.getChildren().add(dummy);
        markerStage.setScene(new Scene(markerStack));
    }
 
    /**
     * Create a new draggable tab. This can be added to any normal TabPane,
     * however a TabPane with draggable tabs must *only* have DraggableTabs,
     * normal tabs and DrragableTabs mixed will cause issues!
     * <p>
     * @param text the text to appear on the tag label.
     */
    public DraggableTab(String text) {
        nameLabel = new Label(text);
        setGraphic(nameLabel);
        detachable = true;
        dragStage = new Stage();
        dragStage.initStyle(StageStyle.TRANSPARENT); //TEST vorhin wars undecorated
        StackPane dragStagePane = new StackPane();
        dragStagePane.setStyle("-fx-opacity: 0.9; -fx-background-color: rgba(255, 255, 255, 0);");
        //dragStagePane.getStylesheets().add(DraggableTab.class.getResource("test.css").toExternalForm()); //funzt nid hier..
   //     dragStagePane.setStyle("-fx-background-color:#DDDDDD;");
     //   dragStagePane.setStyle("-fx-background-color:#DDDDDD;");
     //   dragStagePane.setStyle();
      //  dragStagePane.setStyle("-fx-background-color: blue; -fx-text-fill: black; -fx-font-weight: bold; -fx-opacity: 0.5; -fx-border-color: transparent; -fx-display-caret: true;");

        
        dragText = new Text(text);
        StackPane.setAlignment(dragText, Pos.CENTER);
       // dragStagePane.setStyle("-fx-opacity: 0.5;"); //TEST
        dragStagePane.getChildren().add(dragText);
        dragStage.setScene(new Scene(dragStagePane));
        nameLabel.setOnMouseDragged(new EventHandler<MouseEvent>() {
 
            @Override
            public void handle(MouseEvent t) {
                dragStage.setWidth(nameLabel.getWidth() + 10);
                dragStage.setHeight(nameLabel.getHeight() + 10);
                dragStage.setX(t.getScreenX());
                dragStage.setY(t.getScreenY());
                dragStage.show();
                Point2D screenPoint = new Point2D(t.getScreenX(), t.getScreenY());
                tabPanes.add(getTabPane());
                InsertData data = getInsertData(screenPoint);
                if(data == null || data.getInsertPane().getTabs().isEmpty()) {
                    markerStage.hide();
                }
                else {
                    int index = data.getIndex();
                    boolean end = false;
                    if(index == data.getInsertPane().getTabs().size()) {
                        end = true;
                        index--;
                    }
                    Rectangle2D rect = getAbsoluteRect(data.getInsertPane().getTabs().get(index));
                    if(end) {
                        markerStage.setX(rect.getMaxX() + 13);
                    }
                    else {
                        markerStage.setX(rect.getMinX());
                    }
                    markerStage.setY(rect.getMaxY() + 10);
                    markerStage.show();
                }
            }
        });
        nameLabel.setOnMouseReleased(new EventHandler<MouseEvent>() {
 
            @Override
            public void handle(MouseEvent t) {
                markerStage.hide();
                dragStage.hide();
                if(!t.isStillSincePress()) {
                    Point2D screenPoint = new Point2D(t.getScreenX(), t.getScreenY());
                    TabPane oldTabPane = getTabPane();
             //       oldTabPane.setStyle("-fx-opacity: 0.5; -fx-background-color: rgba(255, 255, 255, 0);");//TEST vorhin wars ohne setstyle
                    int oldIndex = oldTabPane.getTabs().indexOf(DraggableTab.this);
                    tabPanes.add(oldTabPane);
                    InsertData insertData = getInsertData(screenPoint);
                    if(insertData != null) {
                        int addIndex = insertData.getIndex();
                        if(oldTabPane == insertData.getInsertPane() && oldTabPane.getTabs().size() == 1) {
                            return;
                        }
                        oldTabPane.getTabs().remove(DraggableTab.this);
                        if(oldIndex < addIndex && oldTabPane == insertData.getInsertPane()) {
                            addIndex--;
                        }
                        if(addIndex > insertData.getInsertPane().getTabs().size()) {
                            addIndex = insertData.getInsertPane().getTabs().size();
                        }
                        insertData.getInsertPane().getTabs().add(addIndex, DraggableTab.this);
                        insertData.getInsertPane().selectionModelProperty().get().select(addIndex);
                        return;
                    }
                    if(!detachable) {
                        return;
                    }
                    final Stage newStage = new Stage(); // diese hier war vorhin aktiviert, habs jetz oben als public instanziiert
                    final StackPane stp = new StackPane();
                   // stp.setStyle("-fx-opacity: 1; -fx-background-color: rgba(255, 255, 255, 0.7);");
                    //stp.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-font-weight: bold; -fx-opacity: 1; -fx-border-color: black; -fx-display-caret: true;");
            
                    final TabPane pane = new TabPane();
                  //  pane.setStyle("-fx-background-color: black; -fx-text-fill: transparent; -fx-font-weight: bold; -fx-opacity: 0.2; -fx-border-color: black; -fx-display-caret: true;");
                  //  pane.setStyle("-fx-opacity: 0.8; -fx-background-color: rgba(255, 255, 255, 0);");
                   // pane.getStylesheets().add(DraggableTab.class.getResource("test.css").toExternalForm()); // funzt irnwie nit hie
                    //oldTabPane.getStylesheets().add(DraggableTab.class.getResource("test.css").toExternalForm()); // funzt irnwie nit hie
                    //oldTabPane.getStylesheets().add(DraggableTab.class.getResource("test.css").toExternalForm()); // funzt irnwie nit hie
                    
                    final Slider opacityLevel22 = new Slider(0, 1, 1);
                                 opacityLevel22.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                    stp.setOpacity(new_val.doubleValue());
                    //stp.setStyle("-fx-background-color: transparent");
                   // stp.setStyle(" -fx-background-color: rgba(255, 255, 255, 0.5)");
                        }
                    });
                    
                    VBox vbx = new VBox();
                    vbx.getChildren().addAll(pane,opacityLevel22);
                    //stp.getChildren().addAll(pane,opacityLevel22); // so wars vorhin ohne vbox!
                    stp.getChildren().addAll(vbx);
                    tabPanes.add(pane);
                    newStage.setOnHiding(new EventHandler<WindowEvent>() {
 
                        @Override
                        public void handle(WindowEvent t) {
                            tabPanes.remove(pane);
                        }
                    });
                    
                    
                    getTabPane().getTabs().remove(DraggableTab.this);
                    pane.getTabs().add(DraggableTab.this);
                    pane.getTabs().addListener(new ListChangeListener<Tab>() {
 
                        @Override
                        public void onChanged(ListChangeListener.Change<? extends Tab> change) {
                            if(pane.getTabs().isEmpty()) {
                                newStage.hide();
                            }
                        }
                    });
                    Scene scene2 = new Scene(stp);
                    scene2.setFill(Color.TRANSPARENT);
                  //  scene2.setFill(Color.TRANSPARENT); // geht irgendwie nicht :(
                    //newStage.setScene(new Scene(scene2));
                    //scene2.getStylesheets().add(JavaFx_finalchat.class.getResource("test.css").toExternalForm()); //funzt nit
                    newStage.setScene(scene2);
                    
                    newStage.initStyle(StageStyle.TRANSPARENT); //hä, warum geht das jetzt nicht mehr????? wtf
                    newStage.setX(t.getScreenX());
                    newStage.setY(t.getScreenY());
                    newStage.show();
                    newStage.setResizable(true);
                   // newStage.initStyle(StageStyle.DECORATED);
                    
           //         pane.requestLayout();
           //         pane.requestFocus();
                   
    // wenn aktieviert funzt drag and drop window, jedoch ist dies nicht so geeignet, schaue mal warum ;)
/*           pane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        pane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                newStage.setX(event.getScreenX() - xOffset);
                newStage.setY(event.getScreenY() - yOffset);
            }
        });
                    
     //wenn aktiviert läuft resize window, jedoch das wieder zurückziehen gar nicht gut:(             
  /*      pane.setOnMousePressed(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (event.getX() > newStage.getWidth() - 50
                    && event.getX() < newStage.getWidth() + 50
                    && event.getY() > newStage.getHeight() - 50
                    && event.getY() < newStage.getHeight() + 50) {
                resizebottom = true;
                dx = newStage.getWidth() - event.getX();
                dy = newStage.getHeight() - event.getY();
            } else {
                resizebottom = false;
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        }
    });
        
        pane.setOnMouseDragged(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (resizebottom == false) {
                newStage.setX(event.getScreenX() - xOffset);
                newStage.setY(event.getScreenY() - yOffset);
            } else {
                newStage.setWidth(event.getX() + dx);
                newStage.setHeight(event.getY() + dy);
            }
        }
    });
    */        
                }
            }
 
        });
        
    }
 
    /**
     * Set whether it's possible to detach the tab from its pane and move it to
     * another pane or another window. Defaults to true.
     * <p>
     * @param detachable true if the tab should be detachable, false otherwise.
     */
    public void setDetachable(boolean detachable) {
        this.detachable = detachable;
    }
 
    /**
     * Set the label text on this draggable tab. This must be used instead of
     * setText() to set the label, otherwise weird side effects will result!
     * <p>
     * @param text the label text for this tab.
     */
    public void setLabelText(String text) {
        nameLabel.setText(text);
        dragText.setText(text);
    }
 
    private InsertData getInsertData(Point2D screenPoint) {
        for(TabPane tabPane : tabPanes) {
            Rectangle2D tabAbsolute = getAbsoluteRect(tabPane);
            if(tabAbsolute.contains(screenPoint)) {
                int tabInsertIndex = 0;
                if(!tabPane.getTabs().isEmpty()) {
                    Rectangle2D firstTabRect = getAbsoluteRect(tabPane.getTabs().get(0));
                    if(firstTabRect.getMaxY()+60 < screenPoint.getY() || firstTabRect.getMinY() > screenPoint.getY()) {
                        return null;
                    }
                    Rectangle2D lastTabRect = getAbsoluteRect(tabPane.getTabs().get(tabPane.getTabs().size() - 1));
                    if(screenPoint.getX() < (firstTabRect.getMinX() + firstTabRect.getWidth() / 2)) {
                        tabInsertIndex = 0;
                    }
                    else if(screenPoint.getX() > (lastTabRect.getMaxX() - lastTabRect.getWidth() / 2)) {
                        tabInsertIndex = tabPane.getTabs().size();
                    }
                    else {
                        for(int i = 0; i < tabPane.getTabs().size() - 1; i++) {
                            Tab leftTab = tabPane.getTabs().get(i);
                            Tab rightTab = tabPane.getTabs().get(i + 1);
                            if(leftTab instanceof DraggableTab && rightTab instanceof DraggableTab) {
                                Rectangle2D leftTabRect = getAbsoluteRect(leftTab);
                                Rectangle2D rightTabRect = getAbsoluteRect(rightTab);
                                if(betweenX(leftTabRect, rightTabRect, screenPoint.getX())) {
                                    tabInsertIndex = i + 1;
                                    break;
                                }
                            }
                        }
                    }
                }
                return new InsertData(tabInsertIndex, tabPane);
            }
        }
        return null;
    }
 
    private Rectangle2D getAbsoluteRect(Control node) {
        return new Rectangle2D(node.localToScene(node.getLayoutBounds().getMinX(), node.getLayoutBounds().getMinY()).getX() + node.getScene().getWindow().getX(),
                node.localToScene(node.getLayoutBounds().getMinX(), node.getLayoutBounds().getMinY()).getY() + node.getScene().getWindow().getY(),
                node.getWidth(),
                node.getHeight());
    }
 
    private Rectangle2D getAbsoluteRect(Tab tab) {
        Control node = ((DraggableTab) tab).getLabel();
        return getAbsoluteRect(node);
    }
 
    private Label getLabel() {
        return nameLabel;
    }
 
    private boolean betweenX(Rectangle2D r1, Rectangle2D r2, double xPoint) {
        double lowerBound = r1.getMinX() + r1.getWidth() / 2;
        double upperBound = r2.getMaxX() - r2.getWidth() / 2;
        return xPoint >= lowerBound && xPoint <= upperBound;
    }
 
    private static class InsertData {
 
        private final int index;
        private final TabPane insertPane;
 
        public InsertData(int index, TabPane insertPane) {
            this.index = index;
            this.insertPane = insertPane;
        }
 
        public int getIndex() {
            return index;
        }
 
        public TabPane getInsertPane() {
            return insertPane;
        }
 
    }
}

