package dev.foltz.dwarves.entity.dwarf;

import dev.foltz.dwarves.entity.task.TaskManager;
import net.minecraft.world.World;

/**
 * The Brain is responsible for interpreting the World and producing Tasks.
 * i.e. the Brain observes the state of the World and decides what to do next.
 * The Brain must decide what is important, the World merely provide an objective description.
 */
public class DwarfBrain {
    public World world;
    public TaskManager taskManager;

    public DwarfBrain(World world) {
        this.world = world;
        taskManager = new TaskManager();
    }

    public void tick() {
        taskManager.tick();
    }
}