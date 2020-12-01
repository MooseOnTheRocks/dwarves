package dev.foltz.dwarves.entity.ai.task;

import java.util.*;

public class SequencedTask extends Task {
    public Task currentTask;
    public Queue<Task> remainingTasks;

    public SequencedTask(Task... tasks) {
        setRemainingTasks(tasks);
        updateCurrentTask();
    }

    public void setRemainingTasks(Task... tasks) {
        remainingTasks = new LinkedList<>();
        remainingTasks.addAll(Arrays.asList(tasks));
    }

    public void updateCurrentTask() {
        currentTask = remainingTasks.isEmpty() ? Task.NONE : remainingTasks.remove();
    }

    @Override
    public boolean shouldStart() {
        return currentTask.shouldStart();
    }

    @Override
    public void start() {
        currentTask.start();
    }

    @Override
    public void tick() {
        if (currentTask.shouldStop()) {
            currentTask.stop();
            updateCurrentTask();
            if (currentTask.shouldStart()) {
                currentTask.start();
            }
            else {
                currentTask = Task.NONE;
                remainingTasks.clear();
            }
        }
        else {
            currentTask.tick();
        }
    }

    @Override
    public boolean shouldStop() {
        return remainingTasks.isEmpty() && currentTask.shouldStop();
    }

    @Override
    public void stop() {
        currentTask.stop();
    }
}
