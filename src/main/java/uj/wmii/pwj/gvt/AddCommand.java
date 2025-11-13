package uj.wmii.pwj.gvt;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AddCommand extends Command {

    public AddCommand() {
        requiresInit = true;
    }

    @Override
    public void execute(String[] args, ExitHandler exitHandler) {
        int numOfArgs = args.length;
        if (numOfArgs < 2) {
            exitHandler.exit(20, "Please specify file to add.");
            return;
        }

        if (!CatalogManager.isInitialized()) {
            exitHandler.exit(-2, "Current directory is not initialized. Please use init command to initialize.");
            return;
        }

        String fileName = args[1];

        if (!CatalogManager.fileExists(fileName)) {
            exitHandler.exit(21, "File not found. File: " + fileName);
            return;
        }

        try {
            if (CatalogManager.isFileTracked(fileName)) {
                exitHandler.exit(0, "File already added. File: " + fileName);
                return;
            }

            CatalogManager.startTrackingFile(fileName);
            int newVersionNum = CatalogManager.createNewVersion(getAddMessage(numOfArgs, fileName, args));
            Files.createDirectories(Path.of(CatalogManager.FILES_DIRECTORY, "v" + newVersionNum));

        } catch (IOException e) {
            exitHandler.exit(22, "File cannot be added. See ERR for details. File: " + fileName);
            e.printStackTrace(System.err);
            return;
        }

        exitHandler.exit(0, "File added successfully. File: " + fileName);
    }

    private String getAddMessage(int numOfArgs, String fileName, String[] args) {
        if (numOfArgs >= 4) {
            return args[3].replace("\\n", "\n");
        }
        return "File added successfully. File: " + fileName;
    }
}
