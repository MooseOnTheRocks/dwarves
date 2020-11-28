package dev.foltz.dwarves.entity.ai.task;

import dev.foltz.dwarves.entity.ai.task.interrupt.Interrupt;
import dev.foltz.dwarves.entity.dwarf.DwarfEntity;

import java.util.*;
import java.util.stream.Collectors;

public class TaskSelector {
    public DwarfEntity dwarf;
    public Set<Task> possibleTasks;
    public Set<Task> runningTasks;
    public Task interruptTask;

    public TaskSelector(DwarfEntity dwarf) {
        this.dwarf = dwarf;
        possibleTasks = new HashSet<>();
        runningTasks = new HashSet<>();
        interruptTask = Task.NONE;
    }

    public void tick() {
        if (interruptTask != Task.NONE) {
            if (interruptTask.shouldStop()) {
                interruptTask.stop();
                interruptTask = Task.NONE;
            }
            else {
                interruptTask.tick();
            }
        }
        else {
            // Remove all tasks that should be stopped.
            Set<Task> stoppedTasks = runningTasks.stream()
                    .filter(task -> task.shouldStop())
                    .map(task -> { task.stop(); return task; })
                    .collect(Collectors.toSet());
            runningTasks.removeAll(stoppedTasks);
            // Start any tasks that should and can be started.
            possibleTasks.stream()
                    .filter(task -> !conflictsWithRunningTasks(task))
                    .filter(Task::shouldStart)
                    .forEach(this::startTask);
            // Tick every running task.
            runningTasks.stream().forEach(Task::tick);
        }
    }

    public <E extends Interrupt> void interrupt(E interrupt) {
        Task resolvedTask = interrupt.resolve(dwarf);
        if (resolvedTask.shouldStart()) {
            interruptTask.onInterrupt();
            interruptTask = resolvedTask;
            interruptTask.start();
        }
    }

    public void startTask(Task task) {
        if (!conflictsWithRunningTasks(task)) {
            runningTasks.add(task);
            task.start();
        }
    }

    public boolean conflictsWithRunningTasks(Task task) {
        return runningTasks.stream().anyMatch(runningTask ->
            runningTask.requiredControls.stream().anyMatch(task.requiredControls::contains)
        );
    }

    public void addTask(Task task) {
        possibleTasks.add(task);
    }
}
