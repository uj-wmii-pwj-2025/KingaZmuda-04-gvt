package uj.wmii.pwj.gvt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class CommitCommand extends Command {

    public CommitCommand() {
        requiresInit = true;
    }
    @Override
    protected void execute(String[] args, ExitHandler exitHandler) {
        int numOfArgs = args.length;

        if (numOfArgs < 2) {
            exitHandler.exit(50, "Please specify file to commit.");
            return;
        }

        if (!CatalogManager.isInitialized()) {
            exitHandler.exit(-2, "Current directory is not initialized. Please use init command to initialize.");
            return;
        }

        String fileName = args[1];

        if (!CatalogManager.fileExists(fileName)) {
            exitHandler.exit(51, "File not found. File: " + args[1]);
            return;
        }

        try {

            if (!CatalogManager.isFileTracked(fileName)) {
                exitHandler.exit(0, "File is not added to gvt. File: " + args[1]);
                return;
            }

            int version = CatalogManager.createNewVersion(getCommitMessage(numOfArgs, fileName, args));
            Path snapshot = Path.of(CatalogManager.FILES_DIRECTORY, "v" + version);
            Files.createDirectories(snapshot);
            Files.copy(
                    Path.of(fileName),
                    snapshot.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            exitHandler.exit(31, "File cannot be committed. See ERR for details. File: " + fileName);
            e.printStackTrace(System.err);
            return;
        }

        exitHandler.exit(0, "File committed successfully. File: " + args[1]);
    }

    private String getCommitMessage(int numOfArgs, String fileName, String[] args) {
        if (numOfArgs >= 4) {
            return args[3].replace("\\n", "\n");
        }
        return "File committed successfully. File: " + fileName;
    }
}
