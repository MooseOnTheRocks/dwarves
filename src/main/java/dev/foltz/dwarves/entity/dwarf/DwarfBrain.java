package dev.foltz.dwarves.entity.dwarf;

import dev.foltz.dwarves.entity.ai.task.TaskSelector;

public class DwarfBrain {
    public DwarfEntity dwarf;
    public TaskSelector taskSelector;

    public DwarfBrain(DwarfEntity dwarf) {
        this.dwarf = dwarf;
        taskSelector = new TaskSelector();
    }

    public void tick() {
        taskSelector.tick();
    }
}