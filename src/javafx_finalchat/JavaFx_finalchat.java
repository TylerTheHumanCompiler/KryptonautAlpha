package javafx_finalchat;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static javafx_finalchat.Cryptor.getCipher;
import static javafx_finalchat.Cryptor.readfromIndex;


public class JavaFx_finalchat extends Application {

    public static Mqtt mqtt;

    public static void main(String[] args) throws MqttException {
        launch(args);
}
    
    StackPane stp;

    private double xOffset = 0;
    private double yOffset = 0;
    private Boolean resizebottom = false;
    private double dx;
    private double dy;

final Slider opacityLevel = new Slider(0, 1, 1);

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        primaryStage.initStyle(StageStyle.UNIFIED);
        Cryptor.createKeyPair("12345678", "THORIUM");
        Cryptor.decryptLoginFile("12345678", "C:\\Users\\Skynet\\Java\\testk\\THORIUM.phi");
        String kontaktor = new String(readfromIndex(1));
        Cryptor.addContact("12345678", kontaktor, "THORIUM");
        mqtt = new Mqtt();
        Mqtt.authentifiktor();
        Mqtt.requestor();
        
        Button button4 = new Button ("add2");
        
        DraggableTab anker = new DraggableTab("ankertab");
                     anker.setStyle("-fx-background-color: transparent; -fx-opacity: 0.4; -fx-border-color: transparent;");
                     anker.setDetachable(false); //futch, wenn ein "anker" tab zu begin da sein soll,verändert es beim loslösen die opacity der anderen..strange!!
        TabPane tabpane = new TabPane();
                //tabpane.setStyle("-fx-background-color: transparent;  -fx-font-weight: thin;"
                  //     + " -fx-opacity: 0.6; -fx-border-color: transparent; -fx-display-caret: true;");
                  tabpane.getTabs().add(anker);
                  tabpane.getStylesheets().add(JavaFx_finalchat.class.getResource("test.css").toExternalForm());
                  
                  
 
        Label  label  = new Label("Contact & Topic");
               label.setRotate(-90);
               
        Button contactTopic = new Button();
               contactTopic.setGraphic(new Group(label));
               
               
        Button menuButton = new Button();
        
        SVGPath iconOption = new SVGPath();
        //myIcon.setFill(Color.rgb(0, 255, 0, .9));
        iconOption.setStroke(Color.WHITE);//
        iconOption.setStyle("-fx-opacity: 0.7");
        iconOption.setContent("M17.41,20.395l-0.778-2.723c0.228-0.2,0.442-0.414,0.644-0.643l2.721,0.778c0.287-0.418,0.534-0.862,0.755-1.323l-2.025-1.96c0.097-0.288,0.181-0.581,0.241-0.883l2.729-0.684c0.02-0.252,0.039-0.505,0.039-0.763s-0.02-0.51-0.039-0.762l-2.729-0.684c-0.061-0.302-0.145-0.595-0.241-0.883l2.026-1.96c-0.222-0.46-0.469-0.905-0.756-1.323l-2.721,0.777c-0.201-0.228-0.416-0.442-0.644-0.643l0.778-2.722c-0.418-0.286-0.863-0.534-1.324-0.755l-1.96,2.026c-0.287-0.097-0.581-0.18-0.883-0.241l-0.683-2.73c-0.253-0.019-0.505-0.039-0.763-0.039s-0.51,0.02-0.762,0.039l-0.684,2.73c-0.302,0.061-0.595,0.144-0.883,0.241l-1.96-2.026C7.048,3.463,6.604,3.71,6.186,3.997l0.778,2.722C6.736,6.919,6.521,7.134,6.321,7.361L3.599,6.583C3.312,7.001,3.065,7.446,2.844,7.907l2.026,1.96c-0.096,0.288-0.18,0.581-0.241,0.883l-2.73,0.684c-0.019,0.252-0.039,0.505-0.039,0.762s0.02,0.51,0.039,0.763l2.73,0.684c0.061,0.302,0.145,0.595,0.241,0.883l-2.026,1.96c0.221,0.46,0.468,0.905,0.755,1.323l2.722-0.778c0.2,0.229,0.415,0.442,0.643,0.643l-0.778,2.723c0.418,0.286,0.863,0.533,1.323,0.755l1.96-2.026c0.288,0.097,0.581,0.181,0.883,0.241l0.684,2.729c0.252,0.02,0.505,0.039,0.763,0.039s0.51-0.02,0.763-0.039l0.683-2.729c0.302-0.061,0.596-0.145,0.883-0.241l1.96,2.026C16.547,20.928,16.992,20.681,17.41,20.395zM11.798,15.594c-1.877,0-3.399-1.522-3.399-3.399s1.522-3.398,3.399-3.398s3.398,1.521,3.398,3.398S13.675,15.594,11.798,15.594zM27.29,22.699c0.019-0.547-0.06-1.104-0.23-1.654l1.244-1.773c-0.188-0.35-0.4-0.682-0.641-0.984l-2.122,0.445c-0.428-0.364-0.915-0.648-1.436-0.851l-0.611-2.079c-0.386-0.068-0.777-0.105-1.173-0.106l-0.974,1.936c-0.279,0.054-0.558,0.128-0.832,0.233c-0.257,0.098-0.497,0.22-0.727,0.353L17.782,17.4c-0.297,0.262-0.568,0.545-0.813,0.852l0.907,1.968c-0.259,0.495-0.437,1.028-0.519,1.585l-1.891,1.06c0.019,0.388,0.076,0.776,0.164,1.165l2.104,0.519c0.231,0.524,0.541,0.993,0.916,1.393l-0.352,2.138c0.32,0.23,0.66,0.428,1.013,0.6l1.715-1.32c0.536,0.141,1.097,0.195,1.662,0.15l1.452,1.607c0.2-0.057,0.399-0.118,0.596-0.193c0.175-0.066,0.34-0.144,0.505-0.223l0.037-2.165c0.455-0.339,0.843-0.747,1.152-1.206l2.161-0.134c0.152-0.359,0.279-0.732,0.368-1.115L27.29,22.699zM23.127,24.706c-1.201,0.458-2.545-0.144-3.004-1.345s0.143-2.546,1.344-3.005c1.201-0.458,2.547,0.144,3.006,1.345C24.931,22.902,24.328,24.247,23.127,24.706z");

     /*   myIcon5.addEventHandler(MouseEvent.MOUSE_PRESSED, evt -> {
            if ((textarea4.getText() != null && !textarea4.getText().isEmpty())) {
                textarea8.appendText(textarea4.getText() + "\n"); //"\n" + dateFormat.format(cal.getTime()) + "  You:\n" + 

                textarea4.setText("");
                textarea4.setPromptText("Enter Your Text Here!");
            }

        });
     */   

//AB HIER, CONTENT FÜR POPOVER...

ScrollPane scp = new ScrollPane();
      //  scp.setPrefSize(300,300); // wirkt nicht
        scp.setStyle("-fx-background-color: DAE6F3;");
        ScrollPane scp2 = new ScrollPane();
        scp2.setStyle("-fx-background-color: DAE6F3;");
        ScrollPane scp3 = new ScrollPane();
        scp3.setStyle("-fx-background-color: DAE6F3;");
         
        FlowPane flp = new FlowPane();
                 flp.setPadding(new Insets(5,5,5,5));
                 flp.setHgap(10);
                 flp.setVgap(5);
        FlowPane flp2 = new FlowPane();
                 flp2.setPadding(new Insets(5,5,5,5));
                 flp2.setHgap(10);
                 flp2.setVgap(5);
        FlowPane flp3 = new FlowPane();
                 flp3.setPadding(new Insets(5,5,5,5));
                 flp3.setHgap(10);
                 flp3.setVgap(5);

//tile.setPrefSize(150,300);
//TilePane tile = new TilePane();

        String path = "/Users/fpiplip/Documents/NetBeansProjects/Test30_imagegallery/src/bilder/"; //C:\Users\fpiplip\Documents\NetBeansProjects\Test30_imagegallery\src\bilder
        //String path = "/Users/fpiplip/Documents/NetBeansProjects/Test30_imagegallery/src/bilder/"; //C:\Users\fpiplip\Documents\NetBeansProjects\Test30_imagegallery\src\bilder
        
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (final File file : listOfFiles) {
                ImageView imageView;
                imageView = createImageView(file);
              //  imageView.prefHeight(10); // macht genau nichts
              //  imageView.prefWidth(10); //macht genau nichts
                flp.getChildren().addAll(imageView);
        }
        
        String path2 = "/Users/fpiplip/Desktop/Studium/ObjektorientierteProgrammierung/Medien/Gif/"; //
        File folder2 = new File(path2);
        File[] listOfFiles2 = folder2.listFiles();
        for (final File file2 : listOfFiles2 ) {
                ImageView imageView2;
                imageView2 = createImageView(file2);
                flp2.getChildren().addAll(imageView2);
        }
        String path3 = "/Users/fpiplip/Desktop/Coderei/Bilder/Cubes/"; //
        File folder3 = new File(path3);
        File[] listOfFiles3 = folder3.listFiles();
        for (final File file3 : listOfFiles3 ) {
                ImageView imageView3;
                imageView3 = createImageView(file3);
                flp3.getChildren().addAll(imageView3);
        }

        scp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Horizontal
        scp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical scroll bar
        scp.setFitToWidth(true);
        scp.setContent(flp);
        scp2.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Horizontal
        scp2.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical scroll bar
        scp2.setFitToWidth(true);
        scp2.setContent(flp2);
        scp3.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Horizontal
        scp3.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical scroll bar
        scp3.setFitToWidth(true);
        scp3.setContent(flp3);
        
        StackPane stack = new StackPane();
        
        Label label4 = new Label();
        label4.setText("Hallilalo");
        label4.setTextFill(Color.DARKORANGE);
        label4.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; ");
       // label.setTextAlignment(TextAlignment.CENTER); // funzt nit
        //label.setAlignment(Pos.CENTER); funzt nit
        //label.setPrefWidth(150); // funzt
        label4.setPadding(new Insets(5,0,5,100)); // Funzt
        
       // DatePicker calendar = new DatePicker();
       
        Text title = new Text("James Branch Cabell");
        
        TextArea tx = new TextArea();
        tx.setText("James Branch Cabell\n" +
        "	\n" +
        "Der Optimist erklärt,\ndass wir in der besten aller Welten leben,\nund der Pessimist fürchtet,\ndass dies wahr ist.");
        
        
         //label.setTextFill(Color.web("#0076a3"));
        Image images = new Image("file:src/test11_popover/bilder/berge.jpg");
     // ImageView images = new ImageView(new Image(Test11_popover.class.getResourceAsStream("bilder/berge.jpg")));
   
     
                 
        TitledPane tp = new TitledPane("Testtp", new Button("test"));
        TitledPane tp2 = new TitledPane("Testt22", new DatePicker());
                // tp2.setContent(new Button("Button"));

        TitledPane tp3 = new TitledPane();
        //tp3.isHover();
                   tp3.setText("Zitat");
                   tp3.setCollapsible(true);
                   tp3.setContent(tx);
        
        TitledPane picgallery = new TitledPane();
                   picgallery.setText("Hintergrundbilder");
                   picgallery.setStyle("-fx-text-fill:violet; -fx-font-weight: bold");
                   picgallery.setContent(scp);
        TitledPane picgallery2 = new TitledPane();
                   picgallery2.setContent(scp2);
        TitledPane picgallery3 = new TitledPane();
                   picgallery3.setContent(scp3);
       
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setId("color-picker");
       
        Accordion acc = new Accordion();
        acc.getPanes().addAll(tp2,tp,tp3,picgallery,picgallery2,picgallery3);
      //  stack.getChildren().addAll(acc);
     
       
        Circle circle = new Circle(50,50,50);
        
        Button btn = new Button();
        Button btn2 = new Button();
        
        VBox vbox2 = new VBox();
        vbox2.setPrefSize(500, 500); // wirk auf alles innen..
        vbox2.getChildren().addAll(label4,acc,btn,colorPicker,opacityLevel);
   
       // PopOver po = new PopOver();
       // po.setContentNode(vbox2);
      //  po.setAutoHide(true);

       // btn2.setPadding(new Insets(10, 100, 10, 200));
        
        btn.setText("Say 'Hello World'");
        btn2.setText("helloule");
        
        
                            
           
                            
                            
                            
                            
                            
                            
        
       // iconOption.setOnAction(new EventHandler<ActionEvent>() {
        iconOption.addEventHandler(MouseEvent.MOUSE_PRESSED, evt2 ->{
           // @Override
           // public void handle(ActionEvent event) {
             
            //new PopOver().show(btn);
           PopOver po = new PopOver();
           po.show(iconOption);
           po.setContentNode(vbox2);
           //po.setAutoHide(true);
           po.setTitle("Blabla");
           po.titleProperty();
           //po.getRoot().getStylesheets().add(JavaFx_finalchat.class.getResource("test.css").toExternalForm());
      //     po.setOpacity(0.6);
           //po.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_RIGHT);
   
            
        });
        
        stp = new StackPane();
   /*     VBox vbox = new VBox();
        //vbox.setPrefSize(100, 100);
        vbox.getChildren().addAll(btn,btn2,circle);
        stp.getChildren().addAll(vbox);
     */   
//DRAG AND DROP IMAGE IN FLOWPANE
        scp.setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* data is dragged over the target */
                System.out.println("onDragOver");
 
                Dragboard db = event.getDragboard();
                if(db.hasFiles()){
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
        });
        scp.setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* data dropped */
                System.out.println("onDragDropped");
  
                Dragboard db = event.getDragboard();
                 
                if(db.hasFiles()){
                     
                    for(File file:db.getFiles()){
                        //String absolutePath = file.getAbsolutePath();
                        String absolutePath = file.toURI().toString();
                        
                        Image dbimage = new Image((absolutePath),150,0,true,true,true);
                        ImageView dbImageView = new ImageView();
                        dbImageView.setImage(dbimage);
                 
                        flp.getChildren().addAll(dbImageView); 
                        
// ich kann text und dann ein bild darstellen wenn ich ein image adde, jedoch wenn ein neues bild kommt und das text in getchildren drinn ist platziert es das neue bild nicht
// wenn jedoch nur dbImageView in getchildren definiert ist, kann ich immer wieder ein neues bild hinzufügen...hmm komisch                         
                    }
                    event.setDropCompleted(true);
                }else{
                    event.setDropCompleted(false);
                }
                event.consume();
            }
        });

//DRAG AND DROP SCENE RESP. STACKPANE (WINDOW)!...

stp.setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* data is dragged over the target */
                System.out.println("onDragOver");
 
                Dragboard db = event.getDragboard();
                if(db.hasFiles()){
                    event.acceptTransferModes(TransferMode.ANY);
                }
                 
                event.consume();
            }
        });

        stp.setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* data dropped */
                System.out.println("onDragDropped");
  
                Dragboard db = event.getDragboard();
                 
                if(db.hasFiles()){
                     
                    for(File file:db.getFiles()){
                        //String absolutePath = file.getAbsolutePath();
                        String absolutePath = file.toURI().toString();
                        
                        Image dbimage = new Image(absolutePath);
                        ImageView dbImageView = new ImageView();
                        dbImageView.setImage(dbimage);
                        
                        
                        
                        BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO,BackgroundSize.AUTO,false,false,false,true); //läuft noch nicht so ideal!
                                //backgroundSize.isCover();
                                BackgroundImage backgroundImage = new BackgroundImage(dbimage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
                                Background background = new Background(backgroundImage);
                                stp.setBackground(background);
                                
                    }
 
                    event.setDropCompleted(true);
                }else{
                    event.setDropCompleted(false);
                }
                event.consume();
            }
        });

//...DRAG AND DROP SCENE RESP. STACKPANE (WINDOW)!...
    
    //SAVE STATE,FUNZT NIT :(
    
 /*   public File getPersonFilePath() {
    Preferences prefs = Preferences.userNodeForPackage(Test11_popover.class);
    String filePath = prefs.get("filePath", null);
    if (filePath != null) {
        return new File(filePath);
    } else {
        return null;
    }
}
    public void setPersonFilePath(File file) {
    Preferences prefs = Preferences.userNodeForPackage(Test11_popover.class);
    if (file != null) {
        prefs.put("filePath", file.getPath());

        // Update the stage title.
        stage.setTitle("AddressApp - " + file.getName());
    } else {
        prefs.remove("filePath");

        // Update the stage title.
        stage.setTitle("AddressApp");
    }
}

    

    */

//...BIS HIER POPOVER CONTENT
     
        Button closeWindow = new Button("x");
        
        closeWindow.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent ke) {
                    primaryStage.close();
            }
        });
        
//Sidebar...
       final Pane lyricPane = createSidebarContent();
        SideBar sidebar = new SideBar(250,10,lyricPane); // vorhin wars ohne die 10!
        //sidebar.setMaxSize(600, 400);
        VBox.setVgrow(lyricPane, Priority.ALWAYS);
        
//...Sidebar

        HBox windowButton = new HBox();
             windowButton.getChildren().addAll(closeWindow);
        
        VBox sideButtons = new VBox(5);
             //sideButtons.getChildren().addAll(contactTopic,menuButton);
             sideButtons.getChildren().addAll(sidebar.getControlButton(),iconOption);
             //sideButtons.setStyle("-fx-background-color: black");
             sideButtons.setPadding(new Insets(35,5,10,5));
                  
        HBox allContent = new HBox();
             //allContent.getChildren().addAll(tabpane,sideButtons,windowButton);
             allContent.getChildren().addAll(tabpane,sideButtons);
             //allContent.setStyle("-fx-background-color:black");
        
                  
        BorderPane bp = new BorderPane();
      //  bp.setPadding(new Insets(-200,-200,200,200)); //funzt irnwie nit!! :'(
      //  bp.setStyle("-fx-opacity: 0.8; -fx-background-color: rgba(255, 255, 255, 0);"); //OPACITY FUNZT AUCH MIT VIDEO
        //HBox.setHgrow(bp, Priority.ALWAYS);
         bp.setPadding(new Insets(20, 30, 20, 30));
       // bp.setBottom();
       //allContent.setAlignment(Pos.CENTER);
        bp.setCenter(allContent);   
        bp.setRight(sidebar);
        
       // BorderPane.setMargin(tabpane,new Insets(40,100,20,100));
       
       HBox hboxx = new HBox();
      // hboxx.getChildren().addAll(button4,opacityLevel);
       //hboxx.getChildren().addAll(opacityLevel);
       //bp.setBottom(hboxx);
       
stp.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        stp.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            }
        });
    stp.setOnMousePressed(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (event.getX() > primaryStage.getWidth() - 50
                    && event.getX() < primaryStage.getWidth() + 50
                    && event.getY() > primaryStage.getHeight() - 50
                    && event.getY() < primaryStage.getHeight() + 50) {
                resizebottom = true;
                dx = primaryStage.getWidth() - event.getX();
                dy = primaryStage.getHeight() - event.getY();
            } else {
                resizebottom = false;
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        }
    });

    stp.setOnMouseDragged(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (resizebottom == false) {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            } else {
                primaryStage.setWidth(event.getX() + dx);
                primaryStage.setHeight(event.getY() + dy);
            }
        }
    });

        
        //bp.setPadding(new Insets(20,20,20,20));
        //bp.setStyle("-fx-background-color: transparent");
       // bp.setStyle("-fx-opacity: 0.5; -fx-background-color: rgba(255, 255, 255, 0.5);");

        //StackPane stp = new StackPane();
        stp.getChildren().addAll(bp);
        stp.setStyle("-fx-opacity: 0.9; -fx-background-color: rgba(255, 255, 255, 0);");
        
        opacityLevel.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
                
                    primaryStage.setOpacity(new_val.doubleValue()); //OPACITY AUF STAGE GEHT AUCH,YEEEESSSSS!!
                    
                  //  tx.setStyle(STYLESHEET_MODENA);
                   // tx.setStyle(new_val.doubleValue() + "-fx-text-inner-color: yellow");
                   // tx.setStyle("-fx-control-inner-background: black");
                  //  hb.setStyle("-fx-background-color: black; -fx-opacity: 0.5");
              //      opacityValue.setText(String.format("%.2f", new_val));
                   // opacityValue.setText(String.format("%.2f; -fx-background-color: green", new_val));
            }
        });
        ImageView imageView = new ImageView();
        //Image image = new Image(new FileInputStream(imageFile));
        Image image = new Image("file:src/bilder/dreamclouds.jpg");
        //Image image = new Image("file:C/Users/fpiplip/Documents/NetBeansProjects/JavaFx_finalchat/build/classes/bilder/see.jpg"); // C:\Users\fpiplip\Documents\NetBeansProjects\JavaFx_finalchat\build\classes\bilder
        //Image image = new Image("/Users/fpiplip/Documents/NetBeansProjects/Test30_imagegallery/src/bilder/see.jpg"); // C:\Users\fpiplip\Documents\NetBeansProjects\JavaFx_finalchat\build\classes\bilder
                                imageView.setImage(image);
                              //imageView.setFitHeight(stage.getHeight() - 10);
                                imageView.setPreserveRatio(true);
                                imageView.setSmooth(true);
                                imageView.setCache(true);
        BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO,BackgroundSize.AUTO,false,false,false,true); //läuft noch nicht so ideal!
                                //backgroundSize.isCover();
                                BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
                                Background background = new Background(backgroundImage);
                                stp.setBackground(background);
                                //stp.getChildren().add(imageView);
                                
                                
                                
                    // stp.getChildren().addAll(imageView,bp);
        primaryStage.setResizable(true);
        Scene scene = new Scene(stp, 600,400);
              scene.getStylesheets().add(JavaFx_finalchat.class.getResource("test.css").toExternalForm());
        
        //bp.prefHeightProperty().bind(scene.heightProperty()); //funzt nit,brauchts nit!
        //bp.prefWidthProperty().bind(scene.widthProperty());
        scene.setFill(Color.TRANSPARENT);
        
        
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.ESCAPE) {
                    primaryStage.close();
                    try {
                        mqtt.Disconnect();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        
        primaryStage.setScene(scene);
        primaryStage.show();
        
//SMILEY PICKER EVENTHANDLER:
/*        smileshow.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            TextFlow testsmile = new TextFlow();
            ImageView smile1 = new ImageView("file:src/smileys/2.png");
            testsmile.getChildren().addAll(smile1);
            root.getChildren().add(testsmile);
        });
        smileshow2.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            TextFlow addsmile = new TextFlow();
            ImageView smile2 = new ImageView("file:src/smileys/1.png");  
            addsmile.getChildren().addAll(smile2);
            root.getChildren().add(addsmile);
        });
   */     
//BUTTON ADD TAB:
lw.setOnMouseClicked(new EventHandlerImpl(tabpane));





      }

    public class Model extends Thread {
        protected StringProperty stringProperty;

        public Model() {
            stringProperty = new SimpleStringProperty(this, "str", "");


            setDaemon(true);
        }

        public String getString() {
            return stringProperty.toString();
        }

        public void setString(String value) {
            stringProperty.set(value);
        }


        public StringProperty stringPropertyProperty() {
            return stringProperty;
        }


        @Override
        public void run() {
            while (true) {
                System.out.println("TASK #4");
                String rcvmsg = Mqtt.receive();
                if (rcvmsg.isEmpty() == false) {
                    String decryptedmsg = Cryptor.getClrtxt(rcvmsg);
                    stringProperty.set(decryptedmsg);
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(1999);} catch (InterruptedException e) {e.printStackTrace();
                }
            }
        }
    }




static ListView lw;
static String tabtitle;
private StackPane createSidebarContent() {
      
    {
// create some content to put in the sidebar.
         
     //   mqtt = new Mqtt();
        
       // Label name = new Label();
        TextField name = new TextField();
        name.setBorder(Border.EMPTY);
        name.isVisible();
        name.isHover();
        name.setPromptText("NAME");
        name.setStyle("-fx-text-box-border: transparent;");
        
        Rectangle rec = new Rectangle(50,50);
                  rec.setFill(Color.WHITESMOKE);
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10,10,10,10));
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(rec,name);      
        
        FlowPane fp = new FlowPane();
        fp.setPrefSize(500, 500); //weiss nicht ob es dieses braucht
        fp.setHgap(10);
        fp.setVgap(5);
        fp.getChildren().add(vbox);
        fp.setStyle("-fx-background-color: black");
        
        ScrollPane sp = new ScrollPane();
        
        sp.setContent(fp);
        sp.setFitToWidth(true);
        sp.isPannable();
        sp.setFitToHeight(true);
        sp.setStyle("-fx-opacity: 0.7");
        
        
                  fp.heightProperty().addListener((observable, oldVal, newVal) ->{
        sp.setVvalue(((Double) newVal));
    });     
        
//TEST MIT LIST VIEW - ADD TOPIC:

        Button addTopics = new Button("addTopic");
        Button remove = new Button("remove");
        
        addTopics.setAlignment(Pos.CENTER_RIGHT);
        TextField topicsss = new TextField();
        topicsss.setText("φ/");
      //  topicsss.setPromptText("phi/");
        
        
        HBox txfbtn2 = new HBox();
        txfbtn2.getChildren().addAll(topicsss,addTopics,remove);
        txfbtn2.setAlignment(Pos.CENTER);
        //txfbtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(addTopics, Priority.ALWAYS);
        
        TableView table = new TableView();
        TableColumn firstNameCol = new TableColumn("First Name");
        TableColumn lastNameCol = new TableColumn("Last Name");
        TableColumn emailCol = new TableColumn("Email");
        
        
        table.getColumns().addAll(firstNameCol, lastNameCol, emailCol);
        
        
        lw = new ListView(); 
        ObservableList<String> items =FXCollections.observableArrayList (
        );
                 lw.setItems(items);
                
                 
//EVENT ON ITEM OF LIST - SUBSCRIBE:
                  lw.setOnMouseClicked(new EventHandler<MouseEvent>() {
                  
                    @Override
                    public void handle(MouseEvent event) {
                       
                //        try {
//SUBSCRIBE UND SEND MESSAGE TO SELECTET TOPIC:
           /*  topictitle = (String) lw.getSelectionModel().getSelectedItem();
              mqtt.Subscribe(topictitle);
              System.out.println("you subscribed to: " + topictitle);
              
              String topic = topictitle; 
                    output = (String) lw.getSelectionModel().getSelectedItem();
                    mqtt.sendMessage(output, topic);
          } catch (MqttException ex) {
                }
     */         System.out.println("clicked on " + lw.getSelectionModel().getSelectedItem());
                    }
                }); 
//ADD TOPICS:
            addTopics.setOnAction((ActionEvent event) -> {
            lw.getItems().add(topicsss.getText());
            topicsss.clear();
            topicsss.setText("φ/");
           // topicsss.setStyle("-fx-text-fill: magenta"); 
        }); 
      
//REMOVE TOPICS:
                 remove.setOnAction(new EventHandler<ActionEvent>() {
      @Override public void handle(ActionEvent event) {
        final int selectedIdx = lw.getSelectionModel().getSelectedIndex();
        if (selectedIdx != -1) {
       //   String itemToRemove = lw.getSelectionModel().getSelectedItem();
 
          final int newSelectedIdx =
            (selectedIdx == lw.getItems().size() - 1)
               ? selectedIdx - 1
               : selectedIdx;
 
          lw.getItems().remove(selectedIdx);
     //     status.setText("Removed " + itemToRemove);
          lw.getSelectionModel().select(newSelectedIdx);
        }
      }
    });
        
        VBox vboxtopicss = new VBox();
        VBox.setVgrow(lw, Priority.ALWAYS);
       // vboxtopics.setAlignment(Pos.CENTER);
        vboxtopicss.getChildren().addAll(txfbtn2,lw);
        vboxtopicss.setPadding(new Insets(15,15,15,15));
        vboxtopicss.setStyle("-fx-background-color: black");
        
        ScrollPane sp3 = new ScrollPane();
        sp3.setContent(vboxtopicss);
        sp3.setFitToWidth(true);
        sp3.isPannable();
        sp3.setFitToHeight(true);
        sp3.setStyle("-fx-opacity: 0.7");
        
        

        //Tab topics = new Tab("list view");
        //topics.setContent(sp3);
        TabPane tb = new TabPane();
        tb.setId("tab-pane");
        
        final DraggableTab topics = new DraggableTab("Topics");
        topics.setClosable(false);
        topics.setDetachable(true);
        topics.setContent(sp3);
        topics.setStyle("-fx-background-color: black"); //für tab farbe
        topics.getGraphic().setStyle("-fx-text-fill: white"); // für tab textfarbe
        
        final DraggableTab contacts = new DraggableTab("Contacts");
        
        contacts.setClosable(false);
        contacts.setDetachable(true);
        contacts.setContent(sp);
        contacts.setStyle("-fx-background-color: black"); //für tab farbe
        contacts.getGraphic().setStyle("-fx-text-fill: white"); // für tab textfarbe
        
        //Tab contacts = new Tab("Kontakte");
        //contacts.setContent(sp);
        //contacts.setStyle("-fx-background-color: black"); //für tab farbe
        //contacts.getGraphic().setStyle("-fx-text-fill: white"); // für tab textfarbe
        
        //Tab topics = new Tab("Topics");
        //topics.setContent(sp3);
        
        //TabPane tb = new TabPane();
        tb.getTabs().addAll(contacts,topics);
        //tb.setPickOnBounds(true);
        tb.setCenterShape(true);
        
        tb.setTabMinWidth(93);
        //tb.setPadding(new Insets(10,10,10,20));
        //tb.setStyle("-fx-opacity: 0.7");
        //tb.getTabs().addAll(contacts,topics);
        //tb.setTabMinWidth(100);
 
        StackPane root = new StackPane();
        //root.prefHeight(Double.SIZE);
        root.getChildren().add(tb);
        //root.setStyle("-fx-opacity: 0.7; -fx-background-color: rgba(255, 255, 255, 0);");
        //root.setPadding(new Insets(35,0,0,0)); //toprightbottomleft
        root.setPadding(new Insets(0,0,0,0)); //toprightbottomleft

        return root; 
    } 
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

            
            
            private ImageView createImageView(final File imageFile) {
        // DEFAULT_THUMBNAIL_WIDTH is a constant you need to define
        // The last two arguments are: preserveRatio, and use smooth (slower)
        // resizing

        ImageView imageView = null;
        try {
            final Image image = new Image(new FileInputStream(imageFile), 150, 0, true,
                    true);
            imageView = new ImageView(image);
            imageView.setFitWidth(150); 
      
            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent mouseEvent) {

                    if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){

                        if(mouseEvent.getClickCount() == 1){
                            try {
                                
                                ImageView imageView = new ImageView();
                                Image image = new Image(new FileInputStream(imageFile));
                                imageView.setImage(image);
                              //imageView.setFitHeight(stage.getHeight() - 10);
                                imageView.setPreserveRatio(true);
                                imageView.setSmooth(true);
                                imageView.setCache(true);
                                
                                Media media = new Media("file:///C:/Users/fpiplip/Videos/VideoTest/animation3.mp4"); // so funktionierts, jedoch wurde das file vom pc gelesen! .jar nit!

                                MediaPlayer mediaPlayer = new MediaPlayer(media);
                                mediaPlayer.setAutoPlay(true);
                                mediaPlayer.play();
                                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

                                mediaPlayer.setMute(true);
                                MediaView mediaView = new MediaView(mediaPlayer);
                                mediaView.setSmooth(true);
                                mediaView.setCache(true);
                                mediaView.setPreserveRatio(false);
                                mediaView.fitHeightProperty().bind(stp.heightProperty());
                                mediaView.fitWidthProperty().bind(stp.widthProperty());
                                
                                
                                BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO,BackgroundSize.AUTO,false,false,false,true); //läuft noch nicht so ideal!
                                //backgroundSize.isCover();
                                BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
                                Background background = new Background(backgroundImage);
                                stp.setBackground(background);
                              //  root.getChildren().addAll(mediaView);
                                
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
                private void enableDragging(Image image) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });

        } catch (FileNotFoundException ex) {
        }
        return imageView;
    }
            
            
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    

    public class EventHandlerImpl implements EventHandler<Event> {
        private final TabPane tabpane;
        private  VBox intextflow = new VBox();


        public EventHandlerImpl(TabPane tabpane) throws MqttException, IOException {
            this.tabpane = tabpane;

        }

        @Override
        public void handle(Event event) {
            
            final ImageView smileshow = new ImageView("file:src/smileys/2.png");
            
            final TextArea textfield1 = new TextArea();
            textfield1.setStyle("-fx-control-inner-background: black; -fx-display-caret: true; -fx-prompt-text-fill: white");
            textfield1.setPromptText("Enter Your Text Here!");
            //textfield1.setPrefSize(500, 5); // funzt irgendwie nur so (mit 10000), damit bei maxwindowsize alles ausgefüllt ist!
            textfield1.setPrefHeight(10); //funzt glaub nit..
            //textfield1.setPrefSize(Double.MAX_VALUE);
            //textfield1.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            textfield1.setId("textField");
            //textfield1.setStyle("-fx-control-inner-background: black; -fx-opacity: 0.8");
            //textfield1.setStyle("-fx-control-inner-background: black");
            textfield1.setWrapText(true);
            //textfield1.setEditable(false);
//PROMPT TEXT EVENT HANDLERS
/*

            final Button button3 = new Button("Add Tab");

            String topic = new String("cryptonautphi/");
            Mqtt.Listen lpchat1 = new Mqtt.Listen();
            try {
                lpchat1.subscribe(topic);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        });
            textfield1.setOnMouseExited(new EventHandler<MouseEvent>(){
            
            public void handle(MouseEvent event) {
                //textfield1.clear();
                textfield1.setPromptText("Enter Your Text Here!");
                 textfield1.setStyle("-fx-control-inner-background: black; -fx-display-caret: false; -fx-prompt-text-fill: white");
                //textfield1.requestFocus();
            }
            };
*/          final Rectangle rec = new Rectangle();
rec.prefHeight(20);
rec.prefWidth(20);
rec.setFill(Color.BLUE);
           // final WebView textfielddrop = new WebView();
            Label label3 = new Label("Drop\nFile\nHere");
                  label3.setStyle("-fx-text-fill: white");
            final FlowPane textfielddrop = new FlowPane();
            //textfielddrop.setMaxSize(30,30);
            textfielddrop.setPrefSize(35, 58); // zu fett? ask domi!
            textfielddrop.getChildren().add(label3);
            final TextArea df = new TextArea();
                           //textfielddrop.setText("Drop\nFile");
                          // textfielddrop.setFill(Color.BLUE);
                         //  textfielddrop.setPrefSize(30, 30);
                           df.setPrefSize(10, 10);
                           textfielddrop.setStyle("-fx-prompt-text-fill: white;  -fx-border-color: white;  -fx-background-color: black; -fx-text-fill: white");
                           //textfielddrop.getChildren().add(df);
                           
                           //textfielddrop.setEditable(false);
            AnchorPane ap2 = new AnchorPane();
            ap2.getChildren().add(textfielddrop);
            ap2.setStyle("-fx-prompt-text-fill: white; -fx-background-color: black; -fx-text-fill: white");
            //ap2.setPrefHeight(10);
            //pane.getChildren().add(df);
            final Button button3 = new Button("Add Tab");
            Button green = new Button("Green");
            
            ColorPicker colorp = new ColorPicker(Color.WHITE);
                              colorp.setPrefWidth(80);
            
            Label  label5  = new Label("Send");
                   label5.setStyle("-fx-text-fill:white");
            Button button2 = new Button();
                   button2.setGraphic(label5);
                   button2.setId("black2");
                   button2.setPrefWidth(50);
                   button2.setPrefHeight(58);
            Button rece = new Button("rece");
                   //rece.setGraphic(label5);
                   rece.setId("black2");
                   rece.setPrefWidth(45);
                   //button2.setPrefSize(50, 30);
                   //button2.setPrefWidth(70);
            
            VBox buttons = new VBox(1);
            buttons.getChildren().addAll(button2);
            AnchorPane ap = new AnchorPane();
            ap.getChildren().add(buttons);
            
             //intextflow;
            Label labelOutput1 = new Label();
            Label labelObservable = new Label();
            Label labelOldvalue = new Label();
            Label labelNewvalue = new Label();
            final Boolean[] bool = {new Boolean(true)};
            final Model model = new Model();

            model.stringProperty.addListener(new ChangeListener(){
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    labelObservable.setText((String)observable.getValue());
                    labelOldvalue.setText((String)oldValue);
                    labelNewvalue.setText((String)newValue);

                    if (oldValue.equals(newValue) == false) {
                        Platform.runLater(new Runnable() {
                                              @Override
                                              public void run() {


                                                 String incoming = newValue.toString();
                                                  if(incoming.isEmpty() == false) {

                                                      System.out.println("\n\nOUTPUT:::::: " + incoming);
                                                      Text text2 = new Text(incoming);
                                                      TextFlow flowie2 = new TextFlow();
                                                      flowie2.setTextAlignment(TextAlignment.RIGHT);
                                                      flowie2.getChildren().addAll(text2);

                                                      intextflow.getChildren().addAll(flowie2);
                                                      labelOldvalue.setText((String)newValue);
                                                  }
                                              }
                                          }
                        );
                    }
                }
            });

            model.start();

            button2 = new Button("figgen");
            button2.setPrefWidth(70);
            button2.setPrefWidth(70);

            final HBox hbox = new HBox();
            hbox.getChildren().addAll(textfield1, button2, smileshow);
            //hbox.setStyle("-fx-background-color: black;");
            //hbox.setPrefWidth(10000); // funzt irgendwie nur so, damit bei maxwindowsize alles ausgefüllt ist! das schon, aber detachable geht nicht mehr mit dem!! grmpf
            HBox.setHgrow(textfield1, Priority.ALWAYS); //funzt irnwie ou nit
            HBox.setHgrow(tabpane, Priority.ALWAYS);
//COLOR PICKER:
            colorp = new ColorPicker(Color.WHITE);
        colorp.valueProperty().addListener((observable, oldColor, newColor) ->
            intextflow.setStyle(
           //   "-fx-text-fill: " + toRgbString(newColor) + ";" +
              "-fx-background-color:" + toRgbString(newColor) + ";" // Funzt hier irgendwie nicht :( sniff
            )
        );
        colorp.valueProperty().addListener((observable, oldColor, newColor) ->
            textfield1.setStyle(
           //   "-fx-text-fill: " + toRgbString(newColor) + ";" +
              "-fx-control-inner-background:" + toRgbString(newColor) + ";" 
            )
        );
        
        
            
            intextflow.setStyle("-fx-background-color: black"); //Chatausgabefenster farbe
            final ScrollPane scrollpane = new ScrollPane();
            scrollpane.setContent(intextflow);
          //  scrollpane.setMinHeight(200);
            scrollpane.setPrefHeight(300);
            scrollpane.setFitToWidth(true);
            scrollpane.isPannable();
            scrollpane.setFitToHeight(true);
            
              
            
            
            intextflow.heightProperty().addListener((observable, oldVal, newVal) ->{
                scrollpane.setVvalue(((Double) newVal));
            });
            
            final VBox vbox3 = new VBox(2);
                       //vbox3.setStyle("-fx-opacity: 0.7; -fx-background-color: rgba(255, 255, 255, 0);");  
//OPACITY SLIDER:
            final Slider opacityLevel2 = new Slider(0, 1, 0.7); // slider gleichsetzen mit eingestelltem opacity oben, dann merkt man nichts beim ersten klick..
                         //opacityLevel2.setOrientation(Orientation.VERTICAL);
                         opacityLevel2.setPrefWidth(70);
                         opacityLevel2.setId("slider");
                         opacityLevel2.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
                textfield1.setOpacity(new_val.doubleValue());
                textfielddrop.setOpacity(new_val.doubleValue());
                scrollpane.setOpacity(new_val.doubleValue());
                ap2.setOpacity(new_val.doubleValue());
                //vbox3.setOpacity(new_val.doubleValue()); // dieser ist inkl. buttons!
                
                }
            });
            VBox.setVgrow(scrollpane,Priority.ALWAYS);
            //vbox3.getChildren().addAll(scrollpane,hbox,colorp,opacityLevel2);
            vbox3.getChildren().addAll(scrollpane,hbox);
            
//POPOVER IN CHATFENSTER
            Label label2 = new Label();
            label2.setText("Styling");
            label2.setTextFill(Color.BLACK);
            label2.setPadding(new Insets(0,5,0,5));
        //label2.setStyle("-fx-font-weight: thin; -fx-font-size: 20px; ");
       // label.setTextAlignment(TextAlignment.CENTER); // funzt nit
        //label.setAlignment(Pos.CENTER); funzt nit
        //label.setPrefWidth(150); // funzt
        //label2.setPadding(new Insets(5,0,5,10)); // Funzt
            //HBox pocontent = new HBox();
            VBox pocontent = new VBox();
                 //pocontent.setPrefSize(60, 60);
                 pocontent.setPadding(new Insets(5));
                 pocontent.getChildren().addAll(label2,colorp,opacityLevel2);
            scrollpane.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent mouseEvent2) {

                    if(mouseEvent2.getButton().equals(MouseButton.PRIMARY)){

                        if(mouseEvent2.getClickCount() == 1){
                            
            PopOver po2 = new PopOver();
            po2.show(hbox);
            po2.setId("popover2");
            po2.setContentNode(pocontent);
            //po.setAutoHide(true);
            //po2.setTitle("Styling");
            po2.titleProperty();
            //po2.setAutoHide(true);
            //po2.getRoot().getStylesheets().add(JavaFx_finalchat.class.getResource("test.css").toExternalForm());
            
            //po.setArrowLocation(PopOver.ArrowLocation.LEFT_BOTTOM);
                            
                              }}}
        }); 
            
//DRAG AND DROP IMAGE IN FLOWPANE - CHATWINDOW
             
        textfield1.setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* data is dragged over the target */
                System.out.println("onDragOver");
 
                Dragboard db = event.getDragboard();
                if(db.hasFiles()){
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
        });
        textfield1.setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* data dropped */
                System.out.println("onDragDropped");
  
                Dragboard db = event.getDragboard();
                 
                if(db.hasFiles()){
                     
                    for(File file:db.getFiles()){
                        //String absolutePath = file.getAbsolutePath();
                        String absolutePath = file.toURI().toString();
                        
                        Image dbimage = new Image((absolutePath),150,0,true,true,true);
                        ImageView dbImageView = new ImageView();
                        dbImageView.setImage(dbimage);
                 
                        intextflow.getChildren().addAll(dbImageView); 
                        
// ich kann text und dann ein bild darstellen wenn ich ein image adde, jedoch wenn ein neues bild kommt und das text in getchildren drinn ist platziert es das neue bild nicht
// wenn jedoch nur dbImageView in getchildren definiert ist, kann ich immer wieder ein neues bild hinzufügen...hmm komisch                         
                    }
                    event.setDropCompleted(true);
                }else{
                    event.setDropCompleted(false);
                }
                event.consume();
            }
        });
//DRAG AND DROP IMAGE IN FLOWPANE - DropZone for SendDATA
             
        textfielddrop.setOnDragOver(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* data is dragged over the target */
                System.out.println("onDragOver");
 
                Dragboard db = event.getDragboard();
                if(db.hasFiles()){
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
        });
        textfielddrop.setOnDragDropped(new EventHandler <DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                /* data dropped */
                System.out.println("onDragDropped");
  
                Dragboard db = event.getDragboard();
                 
                if(db.hasFiles()){
                     
                    for(File file:db.getFiles()){
                        //String absolutePath = file.getAbsolutePath();
                        String absolutePath = file.toURI().toString();
                        //ImageView smile2 = new ImageView("file:src/smileys/1.png");
                        ImageView smile2 = new ImageView("file:src/icons/icon7.png");
                        //smile2.prefHeight(58);
                        Image dbimage = new Image((absolutePath),35,35,true,true,true);
                        ImageView dbImageView = new ImageView();
                        dbImageView.setImage(dbimage);
                        dbImageView.setOpacity(0);
                        
//hier pic oder data to buffer einfügen!!?
                        
                        Label placeholder = new Label();
                              placeholder.setText("Send?");
                              placeholder.setStyle("-fx-insets: 2; -fx-text-fill: white");
                        //textfielddrop.getChildren().addAll(dbImageView);
                        textfielddrop.setStyle("-fx-prompt-text-fill: white;  -fx-border-color: white;  -fx-background-color: black; -fx-text-fill: white");
                        textfielddrop.getChildren().clear();
                        textfielddrop.getChildren().addAll(placeholder,smile2); 
                        
// ich kann text und dann ein bild darstellen wenn ich ein image adde, jedoch wenn ein neues bild kommt und das text in getchildren drinn ist platziert es das neue bild nicht
// wenn jedoch nur dbImageView in getchildren definiert ist, kann ich immer wieder ein neues bild hinzufügen...hmm komisch                         
                    }
                    event.setDropCompleted(true);
                }else{
                    event.setDropCompleted(false);
                }
                event.consume();
            }
        });
//RIGHT CLICK REMOVE DATA FROM FIELD FLOWPANE DATA
        textfielddrop.setOnMouseClicked(new EventHandler <MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                /* data is dragged over the target */
                if (event.getButton() == MouseButton.SECONDARY) {
                textfielddrop.getChildren().clear();
                
                Label label4 = new Label("Drop\nFile\nHere");
                      label4.setStyle("-fx-text-fill: white");
                textfielddrop.setStyle("-fx-prompt-text-fill: white;  -fx-border-color: white;  -fx-background-color: black; -fx-text-fill: white");
                textfielddrop.getChildren().add(label4);
                
                event.consume();
            }}
        });

           
//EVENTHANDLER BUTTON:
button2.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandlerImpl1(textfield1, intextflow));
//rece.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandlerImpl2(textfield1, intextflow));


//EVENTHANDLER BUTTON2:
//green.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandlerImpl2(textfield1, intextflow));


tabtitle = (String) lw.getSelectionModel().getSelectedItem();
final DraggableTab tab1 = new DraggableTab(tabtitle);
//tab1.setText(tabtitle);
        tab1.setClosable(true);
        tab1.setDetachable(true);
        tab1.setContent(vbox3);
        tab1.setStyle("-fx-background-color: black"); //für tab farbe
        tab1.getGraphic().setStyle("-fx-text-fill: white"); // für tab textfarbe
//tabpane.setStyle("-fx-background-color: transparent"); //funzt noni ganz, das dr header transparent isch...
tabpane.getTabs().add(tab1);
tabpane.getSelectionModel().select(tab1);
        }

   
       private String toRgbString(Color c) {
            return "rgb("
                          + to255Int(c.getRed())
                    + "," + to255Int(c.getGreen())
                    + "," + to255Int(c.getBlue())
             + ")";
    }

    private int to255Int(double d) {
        return (int) (d * 255);
    }
        
        }

        private static class EventHandlerImpl1 implements EventHandler<MouseEvent> {

            private final TextArea textfield1;
        private static VBox intextflow;

            public EventHandlerImpl1(TextArea textfield1, VBox intextflow) {
                this.textfield1 = textfield1;
                EventHandlerImpl1.intextflow = intextflow;
            }

            @Override
            public void handle(MouseEvent event) {
            int kontaktnr = 3;                                                                                      // wird übergeben
            String clrtxt = textfield1.getText();
            if (clrtxt.isEmpty() == false) {
                String cYph3r = null;
                try {
                    cYph3r = getCipher(kontaktnr, clrtxt);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                TextFlow test = new TextFlow();
                test.setTextAlignment(TextAlignment.LEFT);
                test.getChildren().addAll(new Text(clrtxt));
                intextflow.getChildren().addAll(test);
                
                textfield1.clear();
                textfield1.requestFocus();
                clrtxt = "";
                String chattopic = "cryptonautphi/";
                try {
                    Mqtt.sendMessage(cYph3r, chattopic);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                System.out.print("\nJAVACHAT SEND:\n************\n" + cYph3r.substring(23) + "...\n\n");
                //    checkmessage = cYph3r;
                cYph3r = "";
            }


        }
        }}







/**
     * Animates a node on and off screen to the left.
     */
    class SideBar extends VBox
    {
        /**
         * @return a control button to hide and show the sidebar
         */
        public Button getControlButton()
        {
            return controlButton;
        }
        private final Button controlButton;

        /**
         * creates a sidebar containing a vertical alignment of the given nodes
         */
        SideBar(final double expandedWidth,final double expandedHeight, Node... nodes)
        {
            getStyleClass().add("sidebar");
            this.setPrefWidth(expandedWidth);
            this.setMinWidth(0);
            this.setPrefHeight(expandedHeight);
            this.setMinHeight(0);
            

// create a bar to hide and show.

            setAlignment(Pos.CENTER);
            getChildren().addAll(nodes);

// create a button to hide and show the sidebar.
        Label  label  = new Label("Contact & Topic");
               label.setRotate(-90);
               label.setStyle("-fx-text-fill:white");
               
            controlButton = new Button();
            controlButton.setId("dark-blue");
            controlButton.setGraphic(new Group(label));
            controlButton.getStyleClass().add("hide-left");
            //controlButton.setPadding(new Insets(10,10,10,10));

// apply the animations when the button is pressed.
            controlButton.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent actionEvent)
                {
// create an animation to hide sidebar.
                    final Animation hideSidebar = new Transition()
                    {
                        {
                            setCycleDuration(Duration.millis(1000));
                        }

                        @Override
                        protected void interpolate(double frac)
                        {
                            final double curWidth = expandedWidth * (1 - frac);
                            setPrefWidth(curWidth);
                            setTranslateX(-expandedWidth + curWidth);
                            
                        }
                    };
                    hideSidebar.onFinishedProperty().set(new EventHandler<ActionEvent>()
                    {
                        @Override
                        public void handle(ActionEvent actionEvent)
                        {
                            setVisible(false); //true macht schon was sweetes, es schiebt sich dahinter oder so, guckst du mal!
                            
                            Label  label  = new Label("Contact & Topic ^");
                                   label.setRotate(-90);
                                   label.setStyle("-fx-text-fill:white");
                            //controlButton.setText("Show");
                            controlButton.setGraphic(new Group(label));
                            controlButton.getStyleClass().remove("hide-left");
                            controlButton.getStyleClass().add("show-right");
                        }
                    });
// create an animation to show a sidebar.
                    final Animation showSidebar = new Transition()
                    {
                        {
                            setCycleDuration(Duration.millis(1000));
                        }

                        @Override
                        protected void interpolate(double frac)
                        {
                            final double curWidth = expandedWidth * frac;
                            setPrefWidth(curWidth);
                            setTranslateX(-expandedWidth + curWidth);
                            final double curHeight = expandedHeight * frac;
                            setPrefHeight(curHeight);
                            setTranslateY(-expandedHeight + curHeight); //witzige animation wenn das drin, vorhin war es ohne Height!
                        }
                    };
                    showSidebar.onFinishedProperty().set(new EventHandler<ActionEvent>()
                    {
                        @Override
                        public void handle(ActionEvent actionEvent)
                        {
                            Label  label  = new Label("Contact & Topic");
                                   label.setRotate(-90);
                                   label.setStyle("-fx-text-fill:white");
                            //controlButton.setText("Show");
                            controlButton.setGraphic(new Group(label));
                            controlButton.setId("black2");
                            
                            //controlButton.setText("<<");
                            controlButton.getStyleClass().add("hide-left");
                            controlButton.getStyleClass().remove("show-right");
                        }
                    });
                    if (showSidebar.statusProperty().get() == Animation.Status.STOPPED && hideSidebar.statusProperty().get() == Animation.Status.STOPPED)
                    {
                        if (isVisible())
                        {
                            hideSidebar.play();
                        }
                        else
                        {
                            setVisible(true);
                            showSidebar.play();
                        }
                    }
                }
            });
        }
    






    }







//BUTTON EVENTHANDLER:
        
        
        //HAB HIER EINE CONVERT ANONYMUS TO MEMBER GEMACHT, WEISS ABER NICHT WAS DAS FÜR EIN EVENTHANDLER IST!!
        //AH,SCHAUE GANZ UNTEN,ES HAT EINE KLASSE ANGELEGT ODER SO..EV.NÜTZLICH FÜR ANDERES;),ALTER EVENTHANDLER CODE SIEHE UNTEN
  
 //       button2.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            /*     WebView browser = new WebView();
            WebEngine webEngine = browser.getEngine();
            webEngine.loadContent(htmleditor.getHtmlText()); // TEST mit html editor, das funzt, aber alignment automatisch nid!
            */
/*            Text text = new Text(textfield.getText());
            
            text.setFill(Color.BLACK);
            ImageView smile2 = new ImageView("file:src/smileys/1.png"); //TEST: ImageView war vorhin nicht hier,daher widr löschen 
            TextFlow test = new TextFlow();
            test.setTextAlignment(TextAlignment.RIGHT);
            test.getChildren().addAll(text,smile2);
            
            root.getChildren().addAll(test);
            
            textfield.clear();
            textfield.requestFocus();
        });
        
    }}   
 

//HIER DER ALTE CODE DES EVENTHANDLERS DES BUTTONS,JETZT STEHT DA EINE KLASSE GLEICH OBENDRAN...
/*
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
              
            Text text = new Text(textfield.getText());
            text.setFill(Color.DARKMAGENTA);
            TextFlow test = new TextFlow();
            test.setTextAlignment(TextAlignment.LEFT);
            test.getChildren().addAll(text);
              
            root.getChildren().add(test);
              
            textfield.clear();
            textfield.requestFocus();
        }
        });
        */


        
    
//TEST SHOW IMAGE ON TEXTAREA - FUNZT NID SO GANZ - von https://books.google.ch/books?id=RloWBAAAQBAJ&pg=PA486&lpg=PA486&dq=javafx+textarea+get+image&source=bl&ots=QBuNOOlObZ&sig=YOA7nccDgucTzK7Rt-cjtupVatg&hl=de&sa=X&ved=0ahUKEwin3IGOxv_JAhVlj3IKHcXcAH0Q6AEIUDAJ#v=onepage&q=javafx%20textarea%20get%20image&f=false
/*        smileshow2.addEventFilter(MouseEvent.MOUSE_CLICKED, test -> {
            final String imageUrl = "file:src/smileys/1.png";
            Image image = new Image(imageUrl);
            textfield.setPrefSize(image.getWidth(),image.getHeight());
            textfield.setText(""+ imageUrl);            
        });
*/  








//EVENTHANDLER ADD TAB MIT KOMPLETTEM NEUEM INHALT: habe es oben schon einmal geschrieben,aber dann eine klasse daraus gemacht! darum hier als backup
/*

//TEST ADD TAB WITH CONTEN AND EVENTHANDLERS:
button4.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        

        ImageView smileshow = new ImageView("file:src/smileys/2.png");
        ImageView smileshow2 = new ImageView("file:src/smileys/1.png");
        
     // TextField textfield = new TextField();
        TextArea textfield = new TextArea();
        textfield.setPrefSize(500, 50);
        textfield.setId("textField");
        
        Button button4 = new Button ("add2");
        Button button3 = new Button("Add Tab");
        Button button = new Button("Left");
        Button button2 = new Button("Right");
        button.setPrefWidth(70);
        button2.setPrefWidth(70);

        VBox root = new VBox();
      //  root.setPadding(new Insets(50,50,50,50));
      
      ScrollPane scrollpane = new ScrollPane();
        scrollpane.setContent(root);
        scrollpane.prefWidthProperty().bind(root.widthProperty());
        //scrollpane.setBorder(Border.EMPTY);
        scrollpane.setFitToWidth(true);
       //scrollpane.setFitToHeight(false);
        scrollpane.isPannable();           //   WENN SCROLLPANE NICHT DRINNE IST, SCHREIBTS IM AKTUELLSTEN TAB HINEIN!! ABER JA, IMMER NUR IN DIESES DANN
        
        root.heightProperty().addListener((observable, oldVal, newVal) ->{
        scrollpane.setVvalue(((Double) newVal).doubleValue());
    });
         
        HBox hbox = new HBox();
        hbox.getChildren().addAll(textfield,button,button2,smileshow,smileshow2,button3,button4);
     // hbox.getChildren().addAll(htmleditor,button,button2); //TEST mit htmleditor
        
        VBox vbox3 = new VBox();
        VBox.setVgrow(tabpane, Priority.ALWAYS); //SEHR WICHTIG!! damit der abstand maximiert wird zwischen tab header und textarea unten!!
        vbox3.getChildren().addAll(hbox);  
         
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandlerImpl(textfield, root)); 
        
        final Tab tab = new Tab("Tab " + (tabpane.getTabs().size() + 1));
        tab.setContent(scrollpane);
        tabpane.getTabs().add(tab);
        tabpane.getSelectionModel().select(tab);
      }
    });

*/


