package dev.foltz.dwarves.entity.ai.task;

import java.util.EnumSet;

public abstract class Task {

    public enum Priority implements Comparable<Priority> {
        URGENT,
        NOT_IMPORTANT
    }

    public enum Status {
        SUCCESS,
        FAILURE,
        WORKING
    }

    public static final Task NONE = new Task(EnumSet.noneOf(EntityControlType.class)) {
        public boolean shouldStart() { return false; }
        public void start() {}
        public void tick() {}
        public boolean shouldStop() { return true; }
        public void stop() {}
    };

    public final EnumSet<EntityControlType> requiredControls;
    public Priority priority;

    public Task(EnumSet<EntityControlType> requiredControls) {
        this(requiredControls, Priority.NOT_IMPORTANT);
    }

    public Task(EnumSet<EntityControlType> requiredControls, Priority priority) {
        this.requiredControls = requiredControls;
        this.priority = priority;
    }

    public Priority getPriority() {
        return priority;
    }

    public abstract boolean shouldStart();

    public abstract void start();

    public abstract void tick();

    public abstract boolean shouldStop();

    public abstract void stop();

    public void onInterrupt() {
        stop();
    }
}
