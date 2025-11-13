package uj.wmii.pwj.gvt;

public abstract class Command {

    public boolean requiresInit;

    protected abstract void execute(String[] args, ExitHandler exitHandler);
}
