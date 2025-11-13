package uj.wmii.pwj.gvt;

public class CommandFactory {
    public static Command getCommand(String command) {
        return switch(command) {
            case "init" -> new InitCommand();
            case "add" -> new AddCommand();
            case "commit" -> new CommitCommand();
            case "detach" -> new DetachCommand();
            case "checkout" -> new CheckoutCommand();
            case "version" -> new VersionCommand();
            case "history" -> new HistoryCommand();
            default -> null;
        };
    }
}
