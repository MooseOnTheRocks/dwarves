package dev.foltz.dwarves.entity.task;

public abstract class Task {
    private Status currentStatus;
    protected int timeout;
    protected int tryingTime;

    public Task(int timeout) {
        currentStatus = Status.NOT_STARTED;
        this.timeout = timeout;
        tryingTime = 0;
    }

    public Task() {
        this(0);
    }

    public Status status() {
        return currentStatus;
    }

    public boolean shouldStart() {
        return true;
    }

    protected final void internal_start() {
        if (!shouldStart()) return;
        currentStatus = Status.IN_PROGRESS;
        tryingTime = 0;
        onStarted();
    }

    protected void onStarted() {}

    protected final void internal_tick() {
        if (currentStatus != Status.IN_PROGRESS) return;
        onTicked();
        if (timeout > 0) {
            if (tryingTime >= timeout) {
                fail();
            }
            tryingTime += 1;
        }
    }

    protected void onTicked() {}

    protected void onStopped() {}

    protected final void fail() {
        currentStatus = Status.FAILURE;
        onStopped();
    }

    protected final void succeed() {
        currentStatus = Status.SUCCESS;
        onStopped();
    }

    protected final void interrupt() {
        currentStatus = Status.INTERRUPTED;
        onStopped();
    }

    public enum Status {
        NOT_STARTED,
        IN_PROGRESS,
        SUCCESS,
        FAILURE,
        INTERRUPTED
    }
}
