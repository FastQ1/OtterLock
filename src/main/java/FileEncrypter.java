import org.w3c.dom.ls.LSOutput;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FileEncrypter {
    private static final String fileExtension=".ottered";
    private static final Path fileFolder= Paths.get("E:\\EncrypterTest\\FileFolder");
    private static final Path topSecret= Paths.get("E:\\EncrypterTest\\TopSecret");
//    private static final Path otherFolder= Paths.get();



    public static void main(String[] args) throws IOException {

//safeEncryptFolder(fileFolder, topSecret);
//safeDecryptFolder(topSecret,fileFolder);



        //        System.out.println(checkForEncryptedFiles(topSecret));;
//        System.out.println(checkForDecryptedFiles(fileFolder));
//        Objects.requireNonNull(compareDirectories(fileFolder, topSecret)).forEach(System.out::println);
//            decryptFolder(fileFolder);




    }




    public static void safeEncryptFolder(Path folder, Path locationFolder) throws IOException{

            boolean keepGoing=(compareDirectorySet(folder, locationFolder));

                if(keepGoing){
                    Set<Path> check=FileEncrypter.checkFolder(folder, true, true);
                    if (!check.isEmpty()){
                        System.out.println("Encrypted files found in source folder, including "+check.iterator().next()+"\n Please enter an option:\n1. Abort\n2. Continue");
                        Scanner s=new Scanner(System.in);
                        switch(Integer.parseInt(s.nextLine())){
                            case 1:
                                keepGoing=false;
                                break;
                            case 2:
                                break;
                        }
                    }

                    if(keepGoing){
                        Set<Path>check2=FileEncrypter.checkFolder(locationFolder, false, false);
                        if(!check2.isEmpty()){
                            System.out.println("Decrypted files found in destination folder, including "+check2.iterator().next()+"\n Please enter an option:\n1. Abort\n2. ignore file\n3. Encrypt file");
                            Scanner s=new Scanner(System.in);
                            switch(s.nextInt()){
                                case 1:
                                    keepGoing=false;
                                    break;
                                case 2:
                                    break;
                                case 3:
                                    for(Path p: check2){
                                        encryptFile(p);
                                    }
                            }
                        }
                        if(keepGoing){
                            encryptFolder(folder, locationFolder);
                        }
                    }
                }
            }



            public static void safeDecryptFolder(Path folder, Path locationFolder) throws IOException{
            boolean keepGoing=(compareDirectorySet(folder, locationFolder));
                if(keepGoing){
                    Set<Path> check=FileEncrypter.checkFolder(folder, true, false);
                    if (!check.isEmpty()){
                        System.out.println("Decrypted files found in source folder, including "+check.iterator().next()+"\n Please enter an option:\n1. Abort\n2. Continue!!");
                        Scanner s=new Scanner(System.in);
                        switch(Integer.parseInt(s.nextLine())){
                            case 1:
                                keepGoing=false;
                                break;
                            case 2:
                                break;
                        }
                    }

                    if(keepGoing){
                        Set<Path>check2=FileEncrypter.checkFolder(locationFolder, false, true);
                        if(!check2.isEmpty()){
                            System.out.println("Decrypted files found in destination folder, including "+check2.iterator().next()+"\n Please enter an option:\n1. Abort\n2. ignore file\n3. Encrypt file");
                            Scanner s=new Scanner(System.in);
                            switch(s.nextInt()){
                                case 1:
                                    keepGoing=false;
                                    break;
                                case 2:
                                    break;
                                case 3:
                                    for(Path p: check2){
                                        encryptFile(p);
                                    }
                            }
                        }
                        if(keepGoing){
                            decryptFolder(folder, locationFolder);
                        }
                    }
                }
            }


            //options like it is in main currently
        //Should include the


    public static boolean compareDirectorySet(Path source, Path location) throws IOException{
        boolean keepGoing=true;
        Set<Path>comp=compareDirectories(source,location);
        if(comp!= null) {
            TempSettings.exclusionSet=comp;
            Scanner s=new Scanner(System.in);
            System.out.println(comp.size()+" Duplicate directories found. Please enter an option: \n1. Abort" +
                    "\n2. Skip these directories\n3. Replace existing directories\n4. Move files to location directory");
            switch(Integer.parseInt(s.nextLine())){
                case 1:
                    keepGoing=false;
                    System.out.println("Process aborted");
                    break;
                case 2:
                    TempSettings.choice= TempSettings.duplicateDirectoryChoice.SKIP;
                    break;
                case 3:

                    System.out.println("This option will delete all files in the target directory. Are you sure you want to continue? 1 to continue, 2 to abort");
                    int var=Integer.parseInt(s.nextLine());
                    if(var==1){
                        TempSettings.choice= TempSettings.duplicateDirectoryChoice.REPLACE;
                        System.out.println("accepted case 3");
                        break;
                    }if(var==2){
                    break;
                }
                case 4:
                    TempSettings.choice= TempSettings.duplicateDirectoryChoice.MOVE;
            }
            System.out.println();
        }
        return keepGoing;
    }


    public static void encryptFolder(Path folder) throws IOException{
        //Check for encrypted files and
        System.out.println("hi");
        try {
            Files.walkFileTree(folder, new FolderVisitor(FolderVisitor.CryptoMode.ENCRYPT));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void encryptFolder(Path folder, Path locationFolder) throws IOException{
        FileEncrypter.encryptFolder(folder);
        FileEncrypter.moveFolder(folder, locationFolder);

    }

    public static void decryptFolder(Path folder) throws IOException{
        try {
            Files.walkFileTree(folder, new FolderVisitor(FolderVisitor.CryptoMode.DECRYPT));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void decryptFolder(Path folder, Path locationFolder) throws IOException{
        FileEncrypter.decryptFolder(folder);
        FileEncrypter.moveFolder(folder,locationFolder);
 }



        public static void moveFolder(Path folder, Path locationFolder) throws IOException{
            try {
                Files.walkFileTree(folder, new MoveFolder(folder, locationFolder));
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        public static Set<Path> checkFolder(Path folder, boolean checkingSource,boolean checkingForEncrypted) throws IOException{
        //0 checks for encrypted files, 1 checks for decrypted

            if(checkingSource){
                try (Stream<Path>files=Files.walk(folder)){
                    return files
                            .filter(Files::isRegularFile)
                            .filter(f-> !(TempSettings.choice.equals(TempSettings.duplicateDirectoryChoice.SKIP) && TempSettings.exclusionSet.contains(f.getParent())))
                            .filter(f-> (checkingForEncrypted) == f.toString().contains(fileExtension))
                            .collect(Collectors.toSet());
                }
            }
            if(!checkingSource){
                try (Stream<Path> files=Files.walk(folder)){
                    return files
                            .filter(Files::isRegularFile)
                            .filter(f-> !(TempSettings.choice.equals(TempSettings.duplicateDirectoryChoice.REPLACE)  && TempSettings.exclusionSet.contains(f.getParent())))
                            .filter(f-> (checkingForEncrypted)== f.toString().contains(fileExtension))
                            .collect(Collectors.toSet());
                }
            }
            return null;
            }



        public static Set<Path> compareDirectories(Path loc) throws IOException {
        //returns a set with relativized directory names of each subdirectory in the path loc
            HashSet<Path> filesSet = new HashSet<>();
            try (Stream<Path> files = Files.walk(loc)) {
                files
                        .skip(1)
                        .filter(Files::isDirectory)
                        .forEach(f ->
                                filesSet.add(loc.relativize(f))
                        );
                return filesSet;
            }
        }

        public static Set<Path> compareDirectories(Path loc, Path dest) throws IOException{
        //compares two subdirectories and returns a set with relativized directories names of any paths in dest also in loc, or null if set is 0
            HashSet<Path> locList=new HashSet<>(compareDirectories(loc));
            HashSet<Path> destList=new HashSet<>(compareDirectories(dest));
            if((locList.size()!=0) && (destList.size()!=0)){
                HashSet<Path> uniqueList=new HashSet<>();
                for(Path p: locList){
                    if(destList.contains(p)){
                        uniqueList.add(p);
                    }
                }
                if (uniqueList.size()!=0){
                    return uniqueList;
                }
            }
            return null;
            }




    public static boolean encryptFile(Path source) throws IOException {
        if(!source.toString().contains(fileExtension)){
            Path destination=source.getParent().resolve(source.getName(source.getNameCount()-1)+fileExtension);
            byte[] raw= Files.readAllBytes(source);
            byte[] encrypted= encrypt(raw);

            Files.write(destination, encrypted);
            try {Files.delete(source);}
            catch (IOException e){
                e.printStackTrace();
            }
        }else{
            System.out.println("File "+source.toString()+" already contains extension "+fileExtension+". No action was made on this file");
            return false;
        }
        return true;
    }

    public static boolean decryptFile(Path source) throws IOException {
        if (source.toString().contains(fileExtension)){
        Path destination=source.getParent().resolve(source.getName(source.getNameCount()-1).toString().replaceAll(fileExtension,""));
        byte[]raw=Files.readAllBytes(source);
        byte[] decrypted= decrypt(raw);

        Files.write(destination, decrypted);
        try {Files.delete(source);}
        catch (IOException e){
            e.printStackTrace();
        }}else{
            System.out.println("File "+source.toString()+" does not contain "+fileExtension+". No action was made on this file");
            return false;
        }
        return true;
    }



    public static byte[] encrypt(byte[] file){
        for (int i = 0; i <file.length; i++) {
            file[i]++;
        }
        return file;
    }


    public static byte[] decrypt(byte[] file){
        for (int i = 0; i <file.length ; i++) {
            file[i]--;
        }
        return file;
    }


}