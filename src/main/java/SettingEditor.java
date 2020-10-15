import java.io.*;
import java.util.Properties;

public class SettingEditor {

    private String srcFolder;
    private String destFolder;
//    private String settingsFolder="C:\\Users\\pat\\IdeaProjects\\ENCRYPTER_MAVEN\\src\\main\\java\\Settings.txt";
    private String settingsFolder="src\\main\\resources\\Settings.txt";
    private boolean checked;


    //read settings folder
    //specifically read

    public SettingEditor() throws IOException {
        System.out.println("no argument constructor called");
        File settingsFile=new File(settingsFolder);
        settingsFile.createNewFile();
        Properties p=new Properties();
        InputStream is=new FileInputStream(settingsFolder);
        p.load(is);
        if(p.getProperty("firstLoad")==null){
            OutputStream os=new FileOutputStream(settingsFolder);
            //set to default property if null
            p.setProperty("firstLoad", "f");
            p.setProperty("fileExtension", ".ottered");
            p.setProperty("checkBox", "false");
            p.setProperty("srcFolder", "");
            p.setProperty("destFolder", "");
            p.store(os, null);
        }else{
            //change this before shipping
            if(p.getProperty("checkBox")!=null){
                checked=p.getProperty("checkBox").equals("t");
            }
            if(p.getProperty("srcFolder")!=null){
                srcFolder= p.getProperty("srcFolder");
            }
            if(p.get("destFolder")!=null){
                destFolder= p.getProperty("destFolder");
            }
            System.out.println(srcFolder);
            System.out.println(destFolder);
        }
    }



    public SettingEditor(String srcFolder, String destFolder, boolean checked) throws IOException {
        this.srcFolder = srcFolder;
        this.destFolder = destFolder;
        this.checked=checked;
        Properties p=new Properties();
        OutputStream os=new FileOutputStream(settingsFolder);
        p.setProperty("firstLoad", "f");

        System.out.println(srcFolder);
        System.out.println(destFolder);
        if(srcFolder!=null){
            p.setProperty("srcFolder", srcFolder);
        }
        if(destFolder!=null){
            p.setProperty("destFolder", destFolder);
        }
        if(checked){
            p.setProperty("checkBox", "t");
        }else{
            p.setProperty("checkBox", "f");
        }
        p.store(os,null);

    }



    public String getSrcFolder() {
        return srcFolder;
    }
    public String getDestFolder() {
        return destFolder;
    }
    public boolean isChecked() {
        return checked;
    }

}
