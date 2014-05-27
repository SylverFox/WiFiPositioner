package main;

import datatypes.DataPoint;
import datatypes.GPS;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tools.SignalStrengthConverter;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class Main extends Application implements Initializable{
    SQLiteHandle sqlhandle;

    @FXML private ChoiceBox macChoiceBox;
    @FXML private Button drawButton;
    @FXML private Canvas canvas;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Location Plotter");
        primaryStage.setScene(new Scene(root));
        primaryStage.centerOnScreen();

        primaryStage.show();
    }




    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // create sqlitehandle
        sqlhandle = new SQLiteHandle("res/CaptureDatabase");

        //get macs
        ArrayList<String> macs = null;
        try {
            macs = sqlhandle.getMacs();
        } catch (SQLException e) {}

        ObservableList<String> items = FXCollections.observableArrayList();
        items.addAll(macs);
        macChoiceBox.setItems(items);
        final ArrayList<String> finalMacs = (ArrayList<String>) macs.clone();

        drawButton.setOnMouseClicked(ev -> {
            int selectedID = macChoiceBox.getSelectionModel().selectedIndexProperty().getValue();
            if (selectedID > 0) {
                String selectedMac = finalMacs.get(selectedID);
                drawDataPoints(selectedMac);
            }
        });
    }

    private void drawDataPoints(String mac) {
        ArrayList<DataPoint> dataPointList = null;
        try {
            dataPointList = sqlhandle.getDataPoints(mac);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        //determine gps boundaries
        final double padding = 0.10; //percentage
        double latMax = Double.MIN_VALUE;
        double latMin = Double.MAX_VALUE;
        double longMax = Double.MIN_VALUE;
        double longMin = Double.MAX_VALUE;
        for(DataPoint dp : dataPointList) {
            GPS gps = dp.gps;
            if(gps.latitude > latMax)
                latMax = gps.latitude;
            if(gps.latitude < latMin)
                latMin = gps.latitude;
            if(gps.longitude > longMax)
                longMax = gps.longitude;
            if(gps.longitude < longMin)
                longMin = gps.longitude;
        }
        double cHeight = canvas.getHeight();
        double cWidth = canvas.getWidth();
        double gWidth = longMax-longMin;
        double gHeight = latMax-latMin;
        double scaleFactorY = cHeight / gHeight;
        double scaleFactorX = cWidth / gWidth;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0,0,cWidth,cHeight);

        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        gc.strokeRect(0,0,cWidth,cHeight);

        for(DataPoint dp : dataPointList) {
            GPS gps = dp.gps;
            double newY = (gps.latitude-latMin)*scaleFactorY;
            double newX = (gps.longitude-longMin)*scaleFactorX;
            drawCross(gc, newX, newY);

            int rssi = dp.rssi;

            drawStripedCircle(gc, newX, newY, SignalStrengthConverter.SSToMeters(rssi));
        }
    }

    private void drawCross(GraphicsContext gc, double x, double y) {
        gc.strokeLine(x-2,y,x+2,y);
        gc.strokeLine(x, y - 2, x, y + 2);
    }

    private void drawStripedCircle(GraphicsContext gc, double x, double y, double radius) {
        gc.strokeOval(x-radius,y-radius,radius*2,radius*2);
    }
}
