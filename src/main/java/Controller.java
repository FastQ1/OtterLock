import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Controller {

    @FXML
    private Button encryptButton;

    @FXML
    private Button decryptButton;

    @FXML
    private TextField sourceField;

    @FXML
    private TextField destField;

    @FXML
    private TextField fileExtension;

    @FXML
    private Button swap;

    @FXML
    private Button sourceButton;

    @FXML
    private Button destButton;



    public void initialize() throws IOException{



            encryptButton.setOnAction(event-> {
                if(Paths.get(sourceField.getText()).toFile().isDirectory()){
                    if(destField.getText().isEmpty()){
                        try {
                        FileEncrypter.encryptFolder(Paths.get(sourceField.getText()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(Paths.get(destField.getText()).toFile().isDirectory()){
                    try{
                        FileEncrypter.encryptFolder(Paths.get(sourceField.getText()), Paths.get(destField.getText()));
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }else{
                        System.out.println("invalid destination path");
                    }

            }

            });

            decryptButton.setOnAction(event-> {

                        if(Paths.get(sourceField.getText()).toFile().isDirectory()) {
                            if (destField.getText().isEmpty()) {
                                try {
                                    FileEncrypter.decryptFolder(Paths.get(sourceField.getText()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (Paths.get(destField.getText()).toFile().isDirectory()) {
                                try {
                                    FileEncrypter.decryptFolder(Paths.get(sourceField.getText()), Paths.get(destField.getText()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                System.out.println("invalid destination path!!!");
                            }

                        }
            }
            );
        }

        public void sourceButtonAction(){
            DirectoryChooser fc=new DirectoryChooser();
            File f=fc.showDialog(null);
            {
                if (f!=null){
                    sourceField.setText(f.getAbsolutePath());
                }else{
                    System.out.println("Nah dawg");
                }
            }
        }

        public void destButtonAction(){
            DirectoryChooser fc=new DirectoryChooser();
            File f=fc.showDialog(null);
            {
                if (f!=null){
                    destField.setText(f.getAbsolutePath());
                }else{
                    System.out.println("Nah dawg");
                }
            }
        }

        public void swap(){
        String a=sourceField.getText();
        sourceField.setText(destField.getText());
        destField.setText(a);
        }








}
