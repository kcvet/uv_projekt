package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;

public class Main extends Application {

    private javafx.scene.control.Label listView;

    TabPane tabPane;
    Button add_tab;


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
       // ListView<String> listView1 = new ListView<>();
        //ObservableList<String> items = FXCollections.observableArrayList ("Single", "Double", "Suite", "Family App");
        //listView1.setItems(items);
        //Label label = new Label();
//        listView.setText("Something pls work");


        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 780, 600));


        primaryStage.show();
    }





    public static void main(String[] args) {
            //ListView<String> listView1 = new ListView<>();
        //ObservableList<String> items =FXCollections.observableArrayList ("Single", "Double", "Suite", "Family App");
        //listView1.setItems(items);




        launch(args);

        File folder = new File(System.getProperty("user.dir"));
        File[] listOfFiles = folder.listFiles();



        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }

    }
}
