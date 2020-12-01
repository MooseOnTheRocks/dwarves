package dev.foltz.dwarves.entity.ai.task;

public class TaskSelector {
    public Task currentTask;

    public TaskSelector() {
        currentTask = Task.NONE;
    }

    public void tick() {
        if (currentTask == Task.NONE) {
        }
        else if (currentTask.shouldStop()) {
            currentTask.stop();
            currentTask = Task.NONE;
        }
        else {
            currentTask.tick();
        }
    }

    public void interrupt(Task task) {
        currentTask.stop();
        if (task.shouldStart()) {
            currentTask = task;
            currentTask.start();
        }
    }
}
