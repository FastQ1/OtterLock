//Use this class later when implementing UI
//Play around with access modifiers here. I should figure out how to use packages, as package-private is the obvious thing here

import java.nio.file.Path;

class Settings {
    static String fileExtension;
    static Path sourceFolder;
    static Path destinationFolder;
    static Path sourceFile;

    static Path destinationFile;
    //this one currently unused, later I should add a way to move files to a specific folder
    //and that's where this can be passed to an overloaded encryptFile method

    public boolean continueOnFalseFile;
    public boolean deleteSourceFolder;

    public boolean isDeleteSourceFolder() {
        return deleteSourceFolder;
    }

    public void setDeleteSourceFolder(boolean deleteSourceFolder) {
        this.deleteSourceFolder = deleteSourceFolder;
    }

    public static void setFileExtension(String fileExtension) {
        //automatically append "." before fileExtension
        Settings.fileExtension = "."+fileExtension;
    }

    public static void setSourceFolder(Path sourceFolder) {
        Settings.sourceFolder = sourceFolder;
    }

    public static void setDestinationFolder(Path destinationFolder) {
        Settings.destinationFolder = destinationFolder;
    }

    public static void setSourceFile(Path sourceFile) {
        Settings.sourceFile = sourceFile;
    }

    public static void setDestinationFile(Path destinationFile) {
        Settings.destinationFile = destinationFile;
    }

    public void setContinueOnFalseFile(boolean continueOnFalseFile) {
        this.continueOnFalseFile = continueOnFalseFile;
    }

    public static String getFileExtension() {
        return fileExtension;
    }

    public static Path getSourceFolder() {
        return sourceFolder;
    }

    public static Path getDestinationFolder() {
        return destinationFolder;
    }

    public static Path getSourceFile() {
        return sourceFile;
    }

    public static Path getDestinationFile() {
        return destinationFile;
    }

    public boolean isContinueOnFalseFile() {
        return continueOnFalseFile;
    }
}
