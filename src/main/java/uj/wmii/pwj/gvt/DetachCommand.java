package uj.wmii.pwj.gvt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.nio.file.StandardCopyOption;

public class DetachCommand extends Command {

    public DetachCommand() {
        requiresInit = true;
    }
    @Override
    protected void execute(String[] args,  ExitHandler exitHandler) {
        int numOfArgs = args.length;
        if (numOfArgs < 2) {
            exitHandler.exit(30, "Please specify file to detach.");
            return;
        }

        if (!CatalogManager.isInitialized()) {
            exitHandler.exit(-2, "Current directory is not initialized. Please use init command to initialize.");
            return;
        }

        String fileName = args[1];

        try {
            if (!CatalogManager.isFileTracked(fileName)) {
                exitHandler.exit(0, "File is not added to gvt. File: " + fileName);
                return;
            }

            CatalogManager.stopTrackingFile(fileName);
            int newVersionNum = CatalogManager.createNewVersion(getDetachMessage(numOfArgs, fileName, args));
            Files.createDirectories(Path.of(CatalogManager.FILES_DIRECTORY, "v" + newVersionNum));

        } catch (IOException e) {
            exitHandler.exit(31, "File cannot be detached. See ERR for details. File: " + fileName);
            e.printStackTrace(System.err);
            return;
        }

        exitHandler.exit(0, "File detached successfully. File: " + fileName);
    }

    private String getDetachMessage(int numOfArgs, String fileName, String[] args) {
        if (numOfArgs >= 4) {
            return args[3].replace("\\n", "\n");
        }
        return "File detached successfully. File: " + fileName;
    }
}
