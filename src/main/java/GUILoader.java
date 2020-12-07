import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUILoader extends Application {

    private Controller controller;

    @Override
    public void stop() throws Exception {
        System.out.println("stage closing");
        controller.save();
    }

    @Override
    public void start (Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        this.controller= loader.getController();

        Scene scene=new Scene(root);

//        primaryStage.sizeToScene();
//        primaryStage.setResizable(false);
        primaryStage.setTitle("OTTERED");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.centerOnScreen();



    }




}
