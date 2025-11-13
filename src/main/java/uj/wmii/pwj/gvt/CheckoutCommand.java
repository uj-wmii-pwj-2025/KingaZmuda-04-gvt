package uj.wmii.pwj.gvt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class CheckoutCommand extends Command {

    public CheckoutCommand() {
        requiresInit = true;
    }
    @Override
    protected void execute(String[] args,  ExitHandler exitHandler) {
        int numOfArgs = args.length;

        if (numOfArgs < 2) {
            exitHandler.exit(50, "Please specify file to commit.");
            return;
        }

        if (!CatalogManager.isInitialized()) {
            exitHandler.exit(-2, "Current directory is not initialized. Please use init command to initialize.");
            return;
        }

        try {
            int version = Integer.parseInt(args[1]);

            if (CatalogManager.isVersionValid("" + version)) {
                if (version != 0) {
                    List<String> trackedFilesNames = CatalogManager.getTrackedFiles();

                    for (String fileName : trackedFilesNames) {
                        Path targetPath = Path.of(fileName);
                        Path snapshot = Path.of(CatalogManager.FILES_DIRECTORY, "v" + version).resolve(fileName);

                        if (Files.exists(snapshot)) {
                            Files.copy(snapshot, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        } else {
                            Files.writeString(targetPath, "");
                        }

                    }

                }
                exitHandler.exit(0, "Checkout successful for version: " + version);
            }

            exitHandler.exit(60, "Invalid version number: " + version);

        } catch (IOException e) {
        exitHandler.exit(-3, "Underlying system problem. See ERR for details.");
        e.printStackTrace(System.err);
        return;
        }
    }
}
