import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//Most of the work is done in this class
public class FileEncrypter {
    private static final String fileExtension = ".ottered";


    public static void safeCryptFolder(Path folder, boolean checkingForEncrypted) throws IOException {
        if (FileEncrypter.cryptCheck(folder, checkingForEncrypted)) {
            if (checkingForEncrypted) {
                FileEncrypter.encryptFolder(folder);
                int temp = FolderVisitor.numFilesAltered;
                FolderVisitor.numFilesAltered = 0;
                Controller.regAlert("Success!", temp + " Files Encrypted", "Press OK to continue");
            } else {
                FileEncrypter.decryptFolder(folder);
                int temp = FolderVisitor.numFilesAltered;
                FolderVisitor.numFilesAltered = 0;
                Controller.regAlert("Success!", temp + " Files Decrypted", "Press OK to continue");
            }
        } else {
            Controller.regAlert("No Action Made", "No action was made. Your files are unaltered", "Press OK to continue");
        }


    }

    public static boolean cryptCheck(Path folder, boolean checkingForEncrypted) throws IOException {


        try {
            if (folder.getParent().toFile().getName().equals("E:") || folder.getParent().toFile().getName().equals("C:")) {
                System.out.println("Failsafe executed");
                return false;
            }
        } catch (NullPointerException e) {
            Controller.regAlert("Error", "Drive detected", "This program does not aim to encrypt an entire drive. Please choose a directory instead");
            return false;
        }


        boolean keepGoing = true;

        Set<Path> check = FileEncrypter.checkFolder(folder, true, checkingForEncrypted);

        assert check != null;
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

            if (keepGoing) {
                Set<Path> check2 = FileEncrypter.checkFolder(locationFolder, false, false);
                assert check2 != null;
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
                    int temp = FolderVisitor.numFilesAltered;
                    FolderVisitor.numFilesAltered = 0;
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

            if (keepGoing) {
                Set<Path> check2 = FileEncrypter.checkFolder(locationFolder, false, true);
                assert check2 != null;
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
                int temp = FolderVisitor.numFilesAltered;
                FolderVisitor.numFilesAltered = 0;
                Controller.regAlert("Success!", temp + " Files Decrypted.", "Press OK to continue");
            } else {
                Controller.regAlert("No Action Made", "No action was made. Your files are unaltered", "Press OK to continue");
            }
        }
    }



    public static boolean compareDirectorySet(Path source, Path location) throws IOException {
        boolean keepGoing = true;
        Set<Path> comp = compareDirectories(source, location);
        if (comp != null) {
            TempSettings.exclusionSet = comp;

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
            }
        }
        return keepGoing;
    }


    public static void encryptFolder(Path folder) throws IOException {
        Files.walkFileTree(folder, new FolderVisitor(FolderVisitor.CryptoMode.ENCRYPT));
    }

    public static void encryptFolder(Path folder, Path locationFolder) throws IOException {
        FileEncrypter.encryptFolder(folder);
        FileEncrypter.moveFolder(folder, locationFolder);

    }

    public static void decryptFolder(Path folder) throws IOException {
        Files.walkFileTree(folder, new FolderVisitor(FolderVisitor.CryptoMode.DECRYPT));
    }

    public static void decryptFolder(Path folder, Path locationFolder) throws IOException {
        FileEncrypter.decryptFolder(folder);
        FileEncrypter.moveFolder(folder, locationFolder);
    }


    public static void moveFolder(Path folder, Path locationFolder) throws IOException {
        Files.walkFileTree(folder, new MoveFolder(folder, locationFolder));
    }

    public static Set<Path> checkFolder(Path folder, boolean checkingSource, boolean checkingForEncrypted) throws IOException {

        if (checkingSource) {
            try (Stream<Path> files = Files.walk(folder)) {
                return files
                        .filter(Files::isRegularFile)
                        .filter(f -> !(TempSettings.choice.equals(TempSettings.duplicateDirectoryChoice.SKIP) && TempSettings.exclusionSet.contains(f.getParent())))
                        .filter(f -> (checkingForEncrypted) == f.toString().contains(fileExtension))
                        .collect(Collectors.toSet());
            }
        }
        try (Stream<Path> files = Files.walk(folder)) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(f -> !(TempSettings.choice.equals(TempSettings.duplicateDirectoryChoice.REPLACE) && TempSettings.exclusionSet.contains(f.getParent())))
                    .filter(f -> (checkingForEncrypted) == f.toString().contains(fileExtension))
                    .collect(Collectors.toSet());
        }
    }

    //returns a set with relativized directory names of each subdirectory in the path loc
    public static Set<Path> compareDirectories(Path loc) throws IOException {
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

    //compares two subdirectories and returns a set with relativized directories names of any paths in dest also in loc, or null if set is 0
    public static Set<Path> compareDirectories(Path loc, Path dest) throws IOException {
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
            Files.delete(source);

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
            Files.delete(source);

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