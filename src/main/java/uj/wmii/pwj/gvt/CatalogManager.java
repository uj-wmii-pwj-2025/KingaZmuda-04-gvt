package uj.wmii.pwj.gvt;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Class responsible for managing the internal state of a GVT repository stored in .gvt/ directory
 *
 * Versioning directory structure:
 * .gvt/
 * ├── currentVersion.txt    # current version number (for faster access)
 * ├── tracked.txt           # tracked files for current version (for faster access)
 * ├── versions/             # info about each version
 * |   └── vX/
 * |       ├── tracked.txt   # tracked files
 * |       └── message.txt
 * └── files/                # files' contents in each version
 *     └── vX/
 *         └── tracked files in state of vX
 */

public class CatalogManager {
    public static final String GVT_DIRECTORY = ".gvt";
    public static final String VERSIONS_DIRECTORY = GVT_DIRECTORY + "/versions";
    public static final String FILES_DIRECTORY = GVT_DIRECTORY + "/files";
    public static final String CURRENT_VERSION_FILE = GVT_DIRECTORY + "/currentVersion.txt";
    public static final String TRACKED_FILES_LIST = GVT_DIRECTORY + "/tracked.txt";
    private static boolean isInitialized = false;

    public static boolean fileExists(String fileName) {
        return new File(fileName).exists();
    }

    public static boolean isInitialized() {
        return isInitialized;
    }

    public static void initCatalog() throws IOException {
        if (!isInitialized()) {
            Files.createDirectories(Path.of(GVT_DIRECTORY));
            Files.createDirectories(Path.of(VERSIONS_DIRECTORY));
            Files.createDirectories(Path.of(FILES_DIRECTORY));

            Path currentVersionFilePath = Files.createFile(Path.of(CURRENT_VERSION_FILE));
            Files.createFile(Path.of(TRACKED_FILES_LIST));

            Files.writeString(currentVersionFilePath, "0", StandardCharsets.UTF_8);

            isInitialized = true;
        }
    }

    /**
     * Checks whether file was added to the current version
     *
     * @param fileName - name of said file
     * @return true if file is tracked, otherwise - false
     */
    public static boolean isFileTracked(String fileName) throws IOException {
        return getTrackedFiles().contains(fileName);
    }

    public static List<String> getTrackedFiles() throws IOException {
        Path tracked = Path.of(TRACKED_FILES_LIST);
        return Files.readAllLines(tracked, StandardCharsets.UTF_8);
    }

    public static void startTrackingFile(String fileName) throws IOException {
        Path tracked = Path.of(TRACKED_FILES_LIST);
        List<String> trackedFilesNames = Files.readAllLines(tracked, StandardCharsets.UTF_8);
        trackedFilesNames.add(fileName);
        Files.write(tracked, trackedFilesNames, StandardCharsets.UTF_8);
    }

    public static void stopTrackingFile(String fileName) throws IOException {
        Path tracked = Path.of(TRACKED_FILES_LIST);
        List<String> trackedFilesNames = Files.readAllLines(tracked, StandardCharsets.UTF_8);
        trackedFilesNames.remove(fileName);
        Files.write(tracked, trackedFilesNames, StandardCharsets.UTF_8);
    }

    public static int createNewVersion(String message) throws IOException {
        Path currentVersionFile = Path.of(CURRENT_VERSION_FILE);
        int currentNum = Integer.parseInt(Files.readString(currentVersionFile, StandardCharsets.UTF_8).trim());

        int newNum = currentNum + 1;
        Path newVersionDir = Path.of(VERSIONS_DIRECTORY, "v" + newNum);
        Files.createDirectories(newVersionDir);
        Files.writeString(newVersionDir.resolve("message.txt"), message, StandardCharsets.UTF_8);

        Path trackedFilesListPath = Path.of(TRACKED_FILES_LIST);
        List<String> trackedFilesNames = Files.readAllLines(trackedFilesListPath, StandardCharsets.UTF_8);
        Files.write(newVersionDir.resolve("tracked.txt"), trackedFilesNames, StandardCharsets.UTF_8);

        Files.writeString(currentVersionFile, String.valueOf(newNum), StandardCharsets.UTF_8);

        return newNum;
    }
    public static boolean isVersionValid(String versionNumStr) {
        int versionNum = Integer.parseInt(versionNumStr);
        return versionNum > 0 && Files.exists(Path.of(VERSIONS_DIRECTORY, "v" + versionNumStr));
    }
}
