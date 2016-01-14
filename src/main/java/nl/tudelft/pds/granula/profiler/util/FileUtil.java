package nl.tudelft.pds.granula.profiler.util;

import java.io.File;
import java.nio.file.Path;

/**
 * Created by wlngai on 14-1-16.
 */
public class FileUtil {

    public static boolean fileExists(Path path) {
        return path.toFile().exists();
    }
}
