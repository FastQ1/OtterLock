import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class GUILoader extends Application {


    @Override
    public void start (Stage primaryStage) throws Exception{
        Parent root= FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene scene=new Scene(root);

        primaryStage.setTitle("otter");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


}
