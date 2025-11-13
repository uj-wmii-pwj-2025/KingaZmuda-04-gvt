package uj.wmii.pwj.gvt;

public class Gvt {

    private final ExitHandler exitHandler;
    public Gvt(ExitHandler exitHandler) {
        this.exitHandler = exitHandler;
    }

    public static void main(String... args) {
        Gvt gvt = new Gvt(new ExitHandler());
        gvt.mainInternal(args);
    }

    public void mainInternal(String... args) {
        if (args.length == 0) {
            exitHandler.exit(1, "Please specify command.");
            return;
        }

        Command command = CommandFactory.getCommand(args[0]);
        command.execute(args, exitHandler);
    }
}
