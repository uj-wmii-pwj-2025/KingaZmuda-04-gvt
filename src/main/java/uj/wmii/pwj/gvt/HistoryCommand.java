package uj.wmii.pwj.gvt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
public class HistoryCommand extends Command {

    public HistoryCommand() {
        requiresInit = true;
    }
    @Override
    protected void execute(String[] args,  ExitHandler exitHandler) {
        if (!CatalogManager.isInitialized()) {
            exitHandler.exit(-2, "Current directory is not initialized. Please use init command to initialize.");
            return;
        }

        int numOfArgs = args.length;
        int last = 0;

        try {
            if(numOfArgs >= 3) {
                last = Integer.parseInt(args[2]);
            }

            List<Path> versionDirs = getVersionDirectories();
            int numOfDirs = versionDirs.size();

            if (last > 0 && last < numOfDirs) {
                versionDirs = versionDirs.subList(0, last);
            }

            exitHandler.exit(0,formatHistory(versionDirs, last));

        } catch (IOException e) {
            exitHandler.exit(-3, "Underlying system problem. See ERR for details.");
            e.printStackTrace(System.err);
        }
    }

    private List<Path> getVersionDirectories() throws IOException {
        return Files.list(Path.of(CatalogManager.VERSIONS_DIRECTORY))
                .filter(Files::isDirectory)
                .sorted(Comparator.comparingInt(this::getVersionNum))
                .toList().reversed();
    }

    private int getVersionNum(Path versionDir) {
        return Integer.parseInt(versionDir.getFileName().toString().replaceAll("[^0-9]", ""));
    }

    private String formatHistory(List<Path> versionFiles, int last) throws IOException {
        StringBuilder sb = new StringBuilder();

        for (Path path : versionFiles) {
            String message = Files.readString(path.resolve("message.txt")).trim();
            int versionNum = getVersionNum(path);
            sb.append(getHistoryRecord(versionNum, message.split("\\R", 2)[0]));
        }

        if (last == 0) {
            sb.append("0: GVT initialized.\n");
        }

        return sb.toString();
    }

    private String getHistoryRecord(int displayedVersionNum, String message) {
        return (displayedVersionNum + ": " + message + '\n');
    }
}
