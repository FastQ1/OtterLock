import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

class FolderVisitor extends SimpleFileVisitor<Path> {

    public static int numFilesTouched;
    public static int numFilesAltered;

    public enum CryptoMode{
    ENCRYPT,
    DECRYPT,
    DEFAULT,
    }
    private boolean flag;
    private Path sourceRoot;
    private final CryptoMode mode;

    public FolderVisitor(CryptoMode mode) {
        numFilesTouched=0;
        this.mode=mode;
        this.flag=true;
        }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if(flag){
            sourceRoot = dir;
            System.out.println("Sourceroot in folderVisitor: "+sourceRoot.toString());
            flag=false;
        }
        Path relativizedPath = sourceRoot.relativize(dir);
            if(TempSettings.choice.equals(TempSettings.duplicateDirectoryChoice.SKIP)){
                if(TempSettings.exclusionSet.contains(relativizedPath)){
                    return FileVisitResult.SKIP_SUBTREE;
                }
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {


        if(mode==CryptoMode.ENCRYPT) {
            if(FileEncrypter.encryptFile(file)){
                numFilesAltered++;
            }
            numFilesTouched++;
            return FileVisitResult.CONTINUE;
        }
        if(mode==CryptoMode.DECRYPT){
           if(FileEncrypter.decryptFile(file)){
               numFilesAltered++;
           }
            numFilesTouched++;

            return FileVisitResult.CONTINUE;

        }
        numFilesTouched++;
            return FileVisitResult.CONTINUE;
    }
    
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        System.out.println("File visit failed at :"+file.toAbsolutePath());
        return FileVisitResult.TERMINATE;
        //Can make this a conditional
    }

    //can override other methods in SimpleFileVisitor with a conditional if the user wants to encrypt folders aswell
}


