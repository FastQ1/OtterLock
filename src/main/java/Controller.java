import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;

public class Controller {

    private boolean selected=false;

    @FXML
    private Button encryptButton;

    @FXML
    private Button decryptButton;

    @FXML
    private TextField sourceField;

    @FXML
    private TextField destField;

    @FXML
    private CheckBox cBox;

    @FXML
    private ProgressIndicator progressIndicator;




    public void initialize() throws IOException {
        SettingEditor se = new SettingEditor();
        cBox.setSelected(se.isChecked());
        selected=cBox.isSelected();
        destField.setDisable(!selected);
        sourceField.setText(se.getSrcFolder());
        destField.setText(se.getDestFolder());
        progressIndicator.setVisible(false);
        progressIndicator.setProgress(-1);
        cBox.setOnAction((event) -> {
            selected = cBox.isSelected();
            destField.setDisable(!selected);
        });



        encryptButton.setOnAction(event -> {
            if (Paths.get(sourceField.getText()).toFile().isDirectory()) {
                if (selected) {
                    if(checkPath(sourceField.getText(), destField.getText())){
                        if(Paths.get(destField.getText()).toFile().isDirectory()){
                            try {
                                progressIndicator.setVisible(true);
                                FileEncrypter.safeEncryptFolder(Paths.get(sourceField.getText()), Paths.get(destField.getText()));
                            }catch (OutOfMemoryError e){
                                OOMAlert();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }finally {
                                progressIndicator.setVisible(false);
                            }
                        }else{
                            invalidDirectoryAlert();
                        }
                    }
                } else {
                    try {
                        progressIndicator.setVisible(true);
                        FileEncrypter.safeCryptFolder(Paths.get(sourceField.getText()), true);
                    }catch (OutOfMemoryError e){
                        OOMAlert();
                    } catch (IOException e) {
                        e.printStackTrace();

                    }finally {
                        progressIndicator.setVisible(false);
                    }
                }
            }else{
               invalidDirectoryAlert();
            }

        });

        decryptButton.setOnAction(event -> {
            if (Paths.get(sourceField.getText()).toFile().isDirectory()) {
                if (selected) {
                    if(checkPath(sourceField.getText(), destField.getText())) {
                        if (Paths.get(destField.getText()).toFile().isDirectory()) {
                            try {
                                progressIndicator.setVisible(true);
                                FileEncrypter.safeDecryptFolder(Paths.get(sourceField.getText()), Paths.get(destField.getText()));
                            }catch (OutOfMemoryError e){
                                OOMAlert();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }finally {
                                progressIndicator.setVisible(false);
                            }
                        } else {
                            invalidDirectoryAlert();
                        }
                    }
                } else {
                    try {
                        FileEncrypter.safeCryptFolder(Paths.get(sourceField.getText()), false);
                    } catch (OutOfMemoryError e){
                        OOMAlert();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally{
                        progressIndicator.setVisible(false);
                    }
                }
            }else{
                invalidDirectoryAlert();
            }
        });


    }



    private void OOMAlert(){
        regAlert("Out of Memory", "Try crypting a smaller amount of data", "Press OK to continue");
    }

        private boolean checkPath(String source, String dest){
        Path pathSource=Paths.get(source);
        Path pathDest=Paths.get(dest);

        if(dest.contains(pathSource.getName(pathSource.getNameCount()-1).toString())){
            regAlert("Invalid Directory Mapping", "Your destination folder cannot be a child of your source folder", "Press OK to continue");
            return false;
            }else if(source.contains(pathDest.getName(pathDest.getNameCount()-1).toString())){
            regAlert("Invalid Directory Mapping", "Your source folder cannot be a child of your destination folder", "Press OK to continue");
            return false;
        }
        return true;

        }


    public void sourceButtonAction() {
        DirectoryChooser fc = new DirectoryChooser();
        File f = fc.showDialog(null);
        {
            if (f != null) {
                sourceField.setText(f.getAbsolutePath());
            } else {
                System.out.println("Nah dawg");
            }
        }
    }

    public void destButtonAction() {
        DirectoryChooser fc = new DirectoryChooser();
        File f = fc.showDialog(null);
        {
            if (f != null) {
                destField.setText(f.getAbsolutePath());
            } else {
                System.out.println("Nah dawg");
            }
        }
    }

    public void swap() {
        String a = sourceField.getText();
        sourceField.setText(destField.getText());
        destField.setText(a);
    }

    public static int dupAlert(Set<Path> dirSet) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if(dirSet.size()==1){
            alert.setTitle("Duplicate Directory");
            alert.setHeaderText(dirSet.size()+" Duplicate directory found -  "+dirSet.iterator().next());

        }else{
            alert.setTitle("Duplicate Directories");
            alert.setHeaderText(dirSet.size()+" Duplicate directories found, including"+dirSet.iterator().next());
        }
        alert.setContentText("Choose your option.");


        ButtonType buttonTypeTwo = new ButtonType("Skip");
        ButtonType buttonTypeThree = new ButtonType("Replace");
        ButtonType buttonTypeFour = new ButtonType("Move");

            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeTwo, buttonTypeThree, buttonTypeFour, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeTwo) {
            return 2;
        }else if (result.get()== buttonTypeThree){
            if(confirmationAlert("Confirmation", "This will delete all files in the destination folder's duplicate directories. Are you sure you want to proceed?", "Choose an option:")) {
                return 3;
            }else{
                return 1;
            }
            }else if (result.get()==buttonTypeFour){
            return 4;
        }else if(result.get()==buttonTypeCancel){
            return 1;
        }else{
            return 1;
        }

    }

    public static int cryptAlert(Set<Path> cryptedSet, boolean source, boolean encrypted) {
        String encryptedOrDecrypted;
        String sourceOrDestination;
        String oppositeCrypt;
        if (encrypted){
            encryptedOrDecrypted="encrypt";
            oppositeCrypt="decrypt";
        }else{
            encryptedOrDecrypted="decrypt";
            oppositeCrypt="encrypt";
        }
        if(source){
            sourceOrDestination="source";
        }else{
            sourceOrDestination="destination";
        }
        String capitalCrypt=encryptedOrDecrypted.substring(0, 1).toUpperCase()+encryptedOrDecrypted.substring(1);
        String capitalOppositeCrypt=oppositeCrypt.substring(0, 1).toUpperCase()+oppositeCrypt.substring(1);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if(cryptedSet.size()==1){
            alert.setTitle(capitalCrypt+"ed File Found");
            alert.setHeaderText(encryptedOrDecrypted+"ed file "+cryptedSet.iterator().next().getFileName() +" found in "+sourceOrDestination+" folder.");

        }else{
            alert.setTitle(cryptedSet.size()+" "+capitalCrypt+"ed Files Found");
            alert.setHeaderText(cryptedSet.size()+" "+capitalCrypt+"ed files found in "+sourceOrDestination+" folder, including "+cryptedSet.iterator().next());
        }
        alert.setContentText("Choose your option.");

        ButtonType buttonTypeOne=new ButtonType("Skip");
        ButtonType buttonTypeTwo = new ButtonType("Move");
        ButtonType buttonTypeThree = new ButtonType(capitalOppositeCrypt);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        if(source){
            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);
        }else{
            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeThree, buttonTypeCancel);
        }


        Optional<ButtonType> result = alert.showAndWait();
        if(result.get()== buttonTypeOne){
            return 2;
        }
        else if (result.get() == buttonTypeTwo) {
            return 2;
        }else if (result.get()== buttonTypeThree){
                return 3;
        }else if(result.get()==buttonTypeCancel){
            return 1;
        }else{
            return 1;
        }

    }





    public static int dupFileAlert(Path relativizedPath, Path targetRoot){
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Duplicate Files Found");
        alert.setHeaderText("File "+relativizedPath+" already exists in directory "+targetRoot);
        alert.setContentText("Please choose an option");

        ButtonType btn1=new ButtonType("Skip");
        ButtonType btn2=new ButtonType("Replace");
        ButtonType btn3=new ButtonType("Skip all");
        ButtonType btn4=new ButtonType("Replace all");
        ButtonType cancel=new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btn1, btn2, btn3, btn4, cancel);
        Optional<ButtonType>result= alert.showAndWait();

        if(result.get()==btn1){
            return 1;
        }else if (result.get()==btn2){
            return 2;
        }else if (result.get()==btn3){
            return 3;
        }else if (result.get()==btn4){
            return 4;
        }else if (result.get()==cancel){
            if(yesNoAlert("Confirmation", "Exiting now will likely leave you with a partially moved folder. Would you like to continue?", "Choose your option:")){
                return 5;
            }else{
                return 0;
            }
        }else{
            if(yesNoAlert("Confirmation", "Exiting now will likely leave you with a partially encrypted folder. Would you like to continue?", "Choose your option:")){
                return 5;
            }else{
                return 0;
            }
        }


    }


        public static void invalidDirectoryAlert(){
            regAlert("Invalid directory", "Please enter a valid directory", "Press OK to continue");
        }



    public static boolean yesNoAlert(String title, String header, String context){
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result=alert.showAndWait();
        if(result.get()==ButtonType.YES){
            return true;
        }else{
            return false;
        }
    }


    public static boolean confirmationAlert(String title, String header, String context){
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);
        Optional<ButtonType> result=alert.showAndWait();
        if(result.get()==ButtonType.OK){
            return true;
        }else{
            return false;
        }
    }

    public static void regAlert(String title, String header, String context){
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);
        Optional<ButtonType> result=alert.showAndWait();
        //maybe need to make buttons do stuff to break out

    }

    public void save() throws IOException {
        SettingEditor s=new SettingEditor(sourceField.getText(), destField.getText(), cBox.isSelected());
    }

}
