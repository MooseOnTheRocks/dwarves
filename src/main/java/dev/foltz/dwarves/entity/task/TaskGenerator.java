package dev.foltz.dwarves.entity.task;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Runs one task at a time.
 * Every time a task is finished, a new task is generated from taskSupplier.
 * Stopping is signaled by the taskSupplier returning Optional#empty();
 */
public class TaskGenerator extends Task {
    private Supplier<Optional<Task>> taskSupplier;
    private Optional<Task> peekedTask;
    private Optional<Task> currentTask;

    public TaskGenerator(Supplier<Optional<Task>> taskSupplier) {
        super(100);
        this.taskSupplier = taskSupplier;
        peekedTask = taskSupplier.get();
        currentTask = Optional.empty();
    }

    @Override
    public boolean shouldStart() {
        return peekedTask.isPresent() && peekedTask.get().shouldStart();
    }

    @Override
    protected void onStarted() {
        currentTask = peekedTask;
        peekedTask = taskSupplier.get();
        currentTask.get().internal_start();
    }

    @Override
    protected void onTicked() {
        if (!peekedTask.isPresent() && !currentTask.isPresent()) {
            succeed();
            return;
        }

        currentTask.ifPresent(task -> {
            switch (task.status()) {
                case NOT_STARTED:
                    task.internal_start();
                    task.internal_tick();
                    break;
                case IN_PROGRESS:
                    task.internal_tick();
                    tryingTime = 0;
                    break;
                case FAILURE:
                    fail();
                    break;
                default:
                    currentTask = peekedTask;
                    peekedTask = taskSupplier.get();
                    tryingTime = 0;
                    // This is *kind-of* a hack...
                    // But it should have the correct behavior, so don't worry for now!
                    // Doing this for smooth task transitions.
                    // There should be no "idle" tick when there is another task that could be executing.
                    if (currentTask.isPresent()) {
                        Task t = currentTask.get();
                        if (t.status() == Status.NOT_STARTED) {
                            t.internal_start();
                            t.internal_tick();
                        }
                    }
                    break;
            }
        });
    }



    @Override
    protected void onStopped() {
        currentTask.ifPresent(task -> task.interrupt());
    }
}
