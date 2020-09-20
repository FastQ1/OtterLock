import java.nio.file.Path;
import java.util.Set;

public class TempSettings {
    public enum duplicateDirectoryChoice{
        SKIP,
        REPLACE,
        MOVE,
        DEFAULT,
    }
    public static duplicateDirectoryChoice choice=duplicateDirectoryChoice.DEFAULT;
    public static Set<Path> exclusionSet;

}
