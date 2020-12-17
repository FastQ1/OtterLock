import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

class MoveFolder implements FileVisitor<Path> {

    private final Path sourceRoot;
    private final Path targetRoot;
    private boolean skip = false;
    private boolean replace = false;

    public MoveFolder(Path sourceRoot, Path targetRoot) {
        this.sourceRoot = sourceRoot;
        this.targetRoot = targetRoot;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if (sourceRoot.equals(dir)) {
//            System.out.println("Sourceroot in moveFolder: " + sourceRoot.toString());
            return FileVisitResult.CONTINUE;
        }
        Path relativizedPath = sourceRoot.relativize(dir);
        Path resolvedPath = targetRoot.resolve(relativizedPath);

        if (TempSettings.choice.equals(TempSettings.duplicateDirectoryChoice.SKIP)) {
            if (TempSettings.exclusionSet.contains(relativizedPath)) {
                return FileVisitResult.SKIP_SUBTREE;
            }
        }

        if (TempSettings.choice.equals(TempSettings.duplicateDirectoryChoice.MOVE)) {
            if (TempSettings.exclusionSet.contains(relativizedPath)) {
                return FileVisitResult.CONTINUE;
            }
        }

        if (TempSettings.choice.equals(TempSettings.duplicateDirectoryChoice.REPLACE)) {

            for (Path p : TempSettings.exclusionSet) {
                System.out.println(p.toString());
            }

            if (TempSettings.exclusionSet.contains(relativizedPath)) {
                if (Files.exists(resolvedPath)) {
                    Files.walkFileTree(resolvedPath, new DirDelete());
                }
            }

        }
        Files.copy(dir, resolvedPath);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
        if (e == null) {
            if (dir.equals(sourceRoot)) {
                return FileVisitResult.TERMINATE;
            }
            boolean isEmpty;
            try (Stream<Path> pathStream = Files.list(dir)) {
                isEmpty = pathStream.findAny().isEmpty();
            }
            if (isEmpty) {
                Files.delete(dir);
            }
        } else {
            throw e;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Path relativizedPath = sourceRoot.relativize(file);
        Path resolvedPath = targetRoot.resolve(relativizedPath);

        System.out.println(resolvedPath.toString());
        boolean f = Files.exists(resolvedPath);
        System.out.println(f);
        if (f) {
            if (this.skip) {
            } else if (this.replace) {
                Files.delete(resolvedPath);
                Files.move(file, resolvedPath);
            } else {
                boolean validChoiceMade = true;

                do {
                    switch (Controller.dupFileAlert(relativizedPath, targetRoot)) {
                        case 1:
                            System.out.println("File Skipped");
                            validChoiceMade = true;
                            break;
                        case 2:
                            System.out.println("File replaced");
                            Files.delete(resolvedPath);
                            Files.move(file, resolvedPath);
                            validChoiceMade = true;
                            break;
                        case 3:
                            System.out.println("All duplicate files will be skipped");
                            skip = true;
                            validChoiceMade = true;

                            break;
                        case 4:
                            System.out.println("All duplicate files will be replaced");
                            Files.delete(resolvedPath);
                            Files.move(file, resolvedPath);
                            replace = true;
                            validChoiceMade = true;
                            break;
                        case 5:
                            Controller.regAlert("Process Terminated", "Process terminated. Your files may be partially moved", "Click to continue");
                            return FileVisitResult.TERMINATE;
                        case 0:
                            validChoiceMade = false;
                            break;
                    }
                } while (!validChoiceMade);


            }
        } else {
            Files.move(file, resolvedPath);
        }
        return FileVisitResult.CONTINUE;
    }


    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        System.out.println("File visit failed at :" + file.toAbsolutePath());
        return FileVisitResult.TERMINATE;
    }

}