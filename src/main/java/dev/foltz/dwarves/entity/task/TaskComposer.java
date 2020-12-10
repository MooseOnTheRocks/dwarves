package dev.foltz.dwarves.entity.task;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class TaskComposer {
    public static Task sequence(Task... tasks) {
        return new TaskGenerator(new Supplier<Optional<Task>>() {
            int index = 0;

            @Override
            public Optional<Task> get() {
                if (index >= tasks.length) {
                    return Optional.empty();
                }
                else {
                    return Optional.of(tasks[index++]);
                }
            }
        });
    }

    public static Task repeat(int times, Supplier<Task> taskSupplier) {
        return new TaskGenerator(new Supplier<Optional<Task>>() {
            int tasksLeft = times;

            @Override
            public Optional<Task> get() {
                if (tasksLeft > 0) {
                    return Optional.of(taskSupplier.get());
                }
                else {
                    tasksLeft -= 1;
                    return Optional.empty();
                }
            }
        });
    }

    public static Task repeatWhile(BooleanSupplier predicate, Supplier<Task> taskSupplier) {
        return new TaskGenerator(new Supplier<Optional<Task>>() {
            @Override
            public Optional<Task> get() {
                if (predicate.getAsBoolean()) {
                    return Optional.of(taskSupplier.get());
                }
                else {
                    return Optional.empty();
                }
            }
        });
    }
}
