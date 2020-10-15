import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FileEncrypter {
    private static final String fileExtension = ".ottered";
//    private static final Path fileFolder= Paths.get("E:\\EncrypterTest\\FileFolder");
//    private static final Path topSecret= Paths.get("E:\\EncrypterTest\\TopSecret");
//    private static final Path otherFolder= Paths.get();


    public static void main(String[] args) throws IOException {


//safeEncryptFolder(fileFolder, topSecret);
//safeDecryptFolder(topSecret,fileFolder);


        //        System.out.println(checkForEncryptedFiles(topSecret));;
//        System.out.println(checkForDecryptedFiles(fileFolder));
//        Objects.requireNonNull(compareDirectories(fileFolder, topSecret)).forEach(System.out::println);
//            decryptFolder(fileFolder);
    }


    public static boolean safeCryptFolder(Path folder, boolean checkingForEncrypted) throws IOException {
        if (FileEncrypter.cryptCheck(folder, checkingForEncrypted)) {
            if (checkingForEncrypted) {
                FileEncrypter.encryptFolder(folder);
                int temp=FolderVisitor.numFilesAltered;
                FolderVisitor.numFilesAltered=0;
                Controller.regAlert("Success!", temp + " Files Encrypted", "Press OK to continue");
            } else {
                FileEncrypter.decryptFolder(folder);
                int temp=FolderVisitor.numFilesAltered;
                FolderVisitor.numFilesAltered=0;
                Controller.regAlert("Success!", temp + " Files Decrypted", "Press OK to continue");
            }
        } else {
            Controller.regAlert("No Action Made", "No action was made. Your files are unaltered", "Press OK to continue");
        }


        return false;
    }

    public static boolean cryptCheck(Path folder, boolean checkingForEncrypted) throws IOException {
        if (folder.getParent().toFile().getName().equals("E:") || folder.getParent().toFile().getName().equals("C:")) {
            System.out.println("Failsafe executed");
            return false;
        }


        boolean keepGoing = true;

        Set<Path> check = FileEncrypter.checkFolder(folder, true, checkingForEncrypted);

        if (!check.isEmpty()) {
            switch (Controller.cryptAlert(check, true, checkingForEncrypted)) {
                case 1:
                    keepGoing = false;
                    break;
                case 4:
                    break;
            }
        }
        return keepGoing;
    }


    public static void safeEncryptFolder(Path folder, Path locationFolder) throws IOException {

        boolean keepGoing = (compareDirectorySet(folder, locationFolder));

        if (keepGoing) {
            keepGoing = (cryptCheck(folder, true));
//                    Set<Path> check=FileEncrypter.checkFolder(folder, true, true);
//                    if (!check.isEmpty()){
//                        switch(Controller.cryptAlert(check, true, true)){
//                            case 1:
//                                keepGoing=false;
//                                break;
//                            case 4:
//                                break;
//                        }
//                    }

            if (keepGoing) {
                Set<Path> check2 = FileEncrypter.checkFolder(locationFolder, false, false);
                if (!check2.isEmpty()) {
                    switch (Controller.cryptAlert(check2, false, false)) {
                        case 1:
                            keepGoing = false;
                            break;
                        case 2:
                            break;
                        case 3:
                            for (Path p : check2) {
                                encryptFile(p);
                            }
                    }
                }

                if (keepGoing) {
                    encryptFolder(folder, locationFolder);
                    int temp=FolderVisitor.numFilesAltered;
                    FolderVisitor.numFilesAltered=0;
                    Controller.regAlert("Success!", temp + " Files Encrypted.", "Press OK to continue");
                } else {
                    Controller.regAlert("No Action Made", "No action was made. Your files are unaltered", "Press OK to continue");
                }
            }
        }
    }


    public static void safeDecryptFolder(Path folder, Path locationFolder) throws IOException {
        boolean keepGoing = (compareDirectorySet(folder, locationFolder));
        if (keepGoing) {
            keepGoing = (cryptCheck(folder, false));

//                    Set<Path> check=FileEncrypter.checkFolder(folder, true, false);
//                    if (!check.isEmpty()){
//                        switch(Controller.cryptAlert(check, true, false)){
//                            case 1:
//                                keepGoing=false;
//                                break;
//                            case 4:
//                                break;
//                        }
//                    }

            if (keepGoing) {
                Set<Path> check2 = FileEncrypter.checkFolder(locationFolder, false, true);
                if (!check2.isEmpty()) {
                    switch (Controller.cryptAlert(check2, false, true)) {
                        case 1:
                            keepGoing = false;
                            break;
                        case 2:
                            break;
                        case 3:
                            for (Path p : check2) {
                                decryptFile(p);
                            }
                    }
                }

            }
            if (keepGoing) {
                decryptFolder(folder, locationFolder);
                int temp=FolderVisitor.numFilesAltered;
                FolderVisitor.numFilesAltered=0;
                Controller.regAlert("Success!", temp + " Files Decrypted.", "Press OK to continue");

            } else {
                Controller.regAlert("No Action Made", "No action was made. Your files are unaltered", "Press OK to continue");
            }
        }
    }


    //options like it is in main currently
    //Should include the


    public static boolean compareDirectorySet(Path source, Path location) throws IOException {
        boolean keepGoing = true;
        Set<Path> comp = compareDirectories(source, location);
        if (comp != null) {
            TempSettings.exclusionSet = comp;
//            System.out.println(comp.size()+" Duplicate directories found. Please enter an option: \n1. Abort" +
//                    "\n2. Skip these directories\n3. Replace existing directories\n4. Move files to location directory");
            switch (Controller.dupAlert(comp)) {
                case 1:
                    keepGoing = false;
                    break;
                case 2:
                    TempSettings.choice = TempSettings.duplicateDirectoryChoice.SKIP;
                    break;
                case 3:
                    //do a double check here
                    TempSettings.choice = TempSettings.duplicateDirectoryChoice.REPLACE;
                    break;
                case 4:
                    TempSettings.choice = TempSettings.duplicateDirectoryChoice.MOVE;
                    break;


//                    System.out.println("This option will delete all files in the target directory. Are you sure you want to continue? 1 to continue, 2 to abort");
//                    int var=Integer.parseInt(s.nextLine());
//                    if(var==1){
//                        TempSettings.choice= TempSettings.duplicateDirectoryChoice.REPLACE;
//                        System.out.println("accepted case 3");
//                        break;
//                    }if(var==2){
//                    break;
//                }
//                case 4:
//                    TempSettings.choice= TempSettings.duplicateDirectoryChoice.MOVE;
//            }
//            System.out.println();
//        }
            }
        }
        return keepGoing;
    }


    public static void encryptFolder(Path folder) throws IOException {
        //Check for encrypted files and
//        switch(Controller.dupAlert()){
//            case 1:
//                System.out.println("abort!!");
//                break;
//            case 2:
//                System.out.println("continue!!!");
//                break;
//        }
        System.out.println("hi");
        try {
            Files.walkFileTree(folder, new FolderVisitor(FolderVisitor.CryptoMode.ENCRYPT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void encryptFolder(Path folder, Path locationFolder) throws IOException {
        FileEncrypter.encryptFolder(folder);
        FileEncrypter.moveFolder(folder, locationFolder);

    }

    public static void decryptFolder(Path folder) throws IOException {
        try {
            Files.walkFileTree(folder, new FolderVisitor(FolderVisitor.CryptoMode.DECRYPT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void decryptFolder(Path folder, Path locationFolder) throws IOException {
        FileEncrypter.decryptFolder(folder);
        FileEncrypter.moveFolder(folder, locationFolder);
    }


    public static void moveFolder(Path folder, Path locationFolder) throws IOException {
        try {
            Files.walkFileTree(folder, new MoveFolder(folder, locationFolder));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Set<Path> checkFolder(Path folder, boolean checkingSource, boolean checkingForEncrypted) throws IOException {
        //0 checks for encrypted files, 1 checks for decrypted

        if (checkingSource) {
            try (Stream<Path> files = Files.walk(folder)) {
                return files
                        .filter(Files::isRegularFile)
                        .filter(f -> !(TempSettings.choice.equals(TempSettings.duplicateDirectoryChoice.SKIP) && TempSettings.exclusionSet.contains(f.getParent())))
                        .filter(f -> (checkingForEncrypted) == f.toString().contains(fileExtension))
                        .collect(Collectors.toSet());
            }
        }
        if (!checkingSource) {
            try (Stream<Path> files = Files.walk(folder)) {
                return files
                        .filter(Files::isRegularFile)
                        .filter(f -> !(TempSettings.choice.equals(TempSettings.duplicateDirectoryChoice.REPLACE) && TempSettings.exclusionSet.contains(f.getParent())))
                        .filter(f -> (checkingForEncrypted) == f.toString().contains(fileExtension))
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

    public static Set<Path> compareDirectories(Path loc, Path dest) throws IOException {
        //compares two subdirectories and returns a set with relativized directories names of any paths in dest also in loc, or null if set is 0
        HashSet<Path> locList = new HashSet<>(compareDirectories(loc));
        HashSet<Path> destList = new HashSet<>(compareDirectories(dest));
        if ((locList.size() != 0) && (destList.size() != 0)) {
            HashSet<Path> uniqueList = new HashSet<>();
            for (Path p : locList) {
                if (destList.contains(p)) {
                    uniqueList.add(p);
                }
            }
            if (uniqueList.size() != 0) {
                return uniqueList;
            }
        }
        return null;
    }


    public static boolean encryptFile(Path source) throws IOException {
        if (!source.toString().contains(fileExtension)) {
            Path destination = source.getParent().resolve(source.getName(source.getNameCount() - 1) + fileExtension);
            byte[] raw = Files.readAllBytes(source);
            byte[] encrypted = encrypt(raw);

            Files.write(destination, encrypted);
            try {
                Files.delete(source);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File " + source.toString() + " already contains extension " + fileExtension + ". No action was made on this file");
            return false;
        }
        return true;
    }

    public static boolean decryptFile(Path source) throws IOException {
        if (source.toString().contains(fileExtension)) {
            Path destination = source.getParent().resolve(source.getName(source.getNameCount() - 1).toString().replaceAll(fileExtension, ""));
            byte[] raw = Files.readAllBytes(source);
            byte[] decrypted = decrypt(raw);

            Files.write(destination, decrypted);
            try {
                Files.delete(source);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File " + source.toString() + " does not contain " + fileExtension + ". No action was made on this file");
            return false;
        }
        return true;
    }


    public static byte[] encrypt(byte[] file) {
        for (int i = 0; i < file.length; i++) {
            file[i]++;
        }
        return file;
    }


    public static byte[] decrypt(byte[] file) {
        for (int i = 0; i < file.length; i++) {
            file[i]--;
        }
        return file;
    }


}