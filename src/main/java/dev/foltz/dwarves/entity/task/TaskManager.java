package dev.foltz.dwarves.entity.task;

import java.util.Optional;

public class TaskManager {
    private Optional<Task> currentTask;

    public TaskManager() {
        currentTask = Optional.empty();
    }

    public void interrupt(Task task) {
        currentTask.ifPresent(t -> {
            t.interrupt();
        });
        currentTask = Optional.of(task);
    }

    public boolean isPerformingTask() {
        return currentTask.isPresent() && currentTask.get().status() == Task.Status.IN_PROGRESS;
    }

    public void tick() {
        currentTask.ifPresent(task -> {
            switch (task.status()) {
                case NOT_STARTED:
                    task.internal_start();
                    task.internal_tick();
                    break;
                case IN_PROGRESS:
                    task.internal_tick();
                    break;
                default:
                    currentTask = Optional.empty();
                    break;
            }
        });
    }
}
