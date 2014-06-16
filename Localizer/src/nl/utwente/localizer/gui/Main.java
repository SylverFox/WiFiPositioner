package nl.utwente.localizer.gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import nl.utwente.localizer.datatypes.GPS;
import nl.utwente.localizer.datatypes.Node;
import nl.utwente.localizer.datatypes.Point;
import nl.utwente.localizer.main.Localizer;
import nl.utwente.localizer.main.LocalizerProgressListener;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by Joris on 05/06/2014.
 */
public class Main extends Application implements Initializable,LocalizerProgressListener {
    public static void main(String[] args) {launch(args);}

    @FXML private Button runButton;
    @FXML private ComboBox macChoiceBox;
    @FXML private ComboBox terrainChoiceBox;
    @FXML private ImageView mapsView;
    @FXML private Slider zoomSlider;

    private Localizer localizer;
    private MapMaker mapMaker;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.setIconified(false);

        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        localizer = new Localizer(this);
        mapMaker = new MapMaker((int)mapsView.getFitWidth(),(int)mapsView.getFitHeight());

        ArrayList<String> macs = localizer.getMacs();
        ObservableList<String> choices = FXCollections.observableArrayList();
        choices.addAll(macs);
        macChoiceBox.setItems(choices);

        ArrayList<String> terrain = new ArrayList<>();
        terrain.add("roadmap");
        terrain.add("satellite");
        terrain.add("terrain");
        terrain.add("hybrid");
        ObservableList<String> choices2 = FXCollections.observableArrayList();
        choices2.addAll(terrain);
        terrainChoiceBox.setItems(choices2);

        runButton.setOnMouseClicked(ev -> {
            int selectedTerrainID = terrainChoiceBox.getSelectionModel().selectedIndexProperty().getValue();
            if(selectedTerrainID >= 0) {
                String selectedTerrain = terrain.get(selectedTerrainID);
                mapMaker.setMapType(selectedTerrain);
            }

            int selectedZoom = (int) zoomSlider.getValue();
            mapMaker.setZoomLevel(selectedZoom);

            mapMaker.clearMarkers();

            int selectedMACID = macChoiceBox.getSelectionModel().selectedIndexProperty().getValue();
            if(selectedMACID >= 0) {
                String selectedMac = macs.get(selectedMACID);
                localizer.setTarget(selectedMac);
                if(!localizer.isRunning())
                    localizer.start();
            }
        });
    }

    @Override
    public void newNode(Node node) {

    }

    @Override
    public void newMarker(GPS gps) {
        mapMaker.addMarker(gps,MapMaker.STYLE_RED);
    }

    @Override
    public void newResult(GPS gps, Point point) {
        mapMaker.addMarker(gps,MapMaker.STYLE_BLUE);
        BufferedImage map = mapMaker.getMap();
        if(map != null) {
            Image img = SwingFXUtils.toFXImage(map,null);
            javafx.application.Platform.runLater(() -> mapsView.setImage(img));
        }
    }

    @Override
    public void onError(String error) {

    }
}
