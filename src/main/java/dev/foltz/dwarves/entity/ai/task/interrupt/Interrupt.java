package dev.foltz.dwarves.entity.ai.task.interrupt;

import dev.foltz.dwarves.entity.ai.task.Task;
import dev.foltz.dwarves.entity.dwarf.DwarfEntity;

public abstract class Interrupt {
    public abstract Task resolve(DwarfEntity dwarfEntity);
}
