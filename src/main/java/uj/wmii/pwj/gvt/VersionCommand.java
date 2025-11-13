package uj.wmii.pwj.gvt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class VersionCommand extends Command {

    public VersionCommand() {
        requiresInit = true;
    }

    @Override
    protected void execute(String[] args, ExitHandler exitHandler) {
        if (!CatalogManager.isInitialized()) {
            exitHandler.exit(-2, "Current directory is not initialized. Please use init command to initialize.");
            return;
        }

        int targetVersion = 0;
        int numOfArgs = args.length;

        if (numOfArgs > 1) {
            targetVersion = Integer.parseInt(args[1]);
        }

        try {
            List<Path> versionDirs = getVersionDirectories();

            if (versionDirs.isEmpty()) {
                if (targetVersion == 0) {
                    exitHandler.exit(0, formatVersionOutput(0, "GVT initialized."));
                } else {
                    exitHandler.exit(60, "Invalid version number: " + targetVersion + ".");
                }
                return;
            }

            targetVersion = Integer.parseInt(versionDirs.getLast().toString().replaceAll("[^0-9]", ""));
            Path dir = findVersionDir(versionDirs, targetVersion);
            exitHandler.exit(0, formatVersionOutput(targetVersion, getMessage(dir, targetVersion)));
        } catch (IOException e) {
            exitHandler.exit(-3, "Underlying system problem. See ERR for details.");
            e.printStackTrace(System.err);
        }
    }

    private List<Path> getVersionDirectories() throws IOException {
        return Files.list(Path.of(CatalogManager.VERSIONS_DIRECTORY))
                .filter(Files::isDirectory)
                .sorted(Comparator.comparingInt(this::getVersionNum))
                .toList();
    }

    private Path findVersionDir(List<Path> versionDirs, int targetVersion) {
        return versionDirs.stream()
                .filter(dir -> getVersionNum(dir) == targetVersion)
                .findFirst()
                .orElse(null);
    }

    private String formatVersionOutput(int targetVersion, String message) {
        return "Version: " + targetVersion + "\n" + message;
    }

    private int getVersionNum(Path versionDir) {
        return Integer.parseInt(versionDir.getFileName().toString().replaceAll("[^0-9]", ""));
    }

    private String getMessage(Path dir, int targetVersion) throws IOException {
        if (dir == null) {
            return "Invalid version number: " + targetVersion + ".";
        }
        return Files.readString(dir.resolve("message.txt"));
    }
}
