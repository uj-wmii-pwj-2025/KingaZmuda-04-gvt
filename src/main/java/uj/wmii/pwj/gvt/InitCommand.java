package uj.wmii.pwj.gvt;

import java.io.IOException;

public class InitCommand extends Command {

    public InitCommand() {
        requiresInit = false;
    }
    @Override
    protected void execute(String[] args, ExitHandler exitHandler) {
        if (CatalogManager.isInitialized()) {
            exitHandler.exit(0, "Current directory is already initialized.");
            return;
        }

        try {
            CatalogManager.initCatalog();
        } catch (IOException e) {
            exitHandler.exit(-3, "Underlying system problem. See ERR for details");
            e.printStackTrace(System.err);
            return;
        }

        exitHandler.exit(0, "Current directory initialized successfully.");
    }
}
