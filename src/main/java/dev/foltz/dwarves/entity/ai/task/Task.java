package dev.foltz.dwarves.entity.ai.task;

import java.util.EnumSet;

public abstract class Task {
    public static final Task NONE = new Task() {
        public boolean shouldStart() { return true; }
        public void start() {}
        public void tick() {}
        public boolean shouldStop() { return true; }
        public void stop() {}
    };

    public abstract boolean shouldStart();

    public abstract void start();

    public abstract void tick();

    public abstract boolean shouldStop();

    public abstract void stop();
}
