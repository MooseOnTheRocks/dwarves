package dev.foltz.dwarves.entity.ai.task;

import dev.foltz.dwarves.entity.dwarf.DwarfEntity;

import java.util.EnumSet;

public class IdleTask extends Task {
    DwarfEntity dwarf;
    int taskTime;
    long lastIdleTime;

    public IdleTask(DwarfEntity dwarf) {
        super(EnumSet.of(EntityControlType.MOVE));
        this.dwarf = dwarf;
    }

    @Override
    public boolean shouldStart() {
        return dwarf.world.getTime() - lastIdleTime > 200;
    }

    @Override
    public void start() {
        taskTime = dwarf.getRandom().nextInt(80) + 40;
        dwarf.getNavigation().stop();
    }

    @Override
    public void tick() {
        taskTime -= 1;
    }

    @Override
    public boolean shouldStop() {
        return taskTime <= 0;
    }

    @Override
    public void stop() {
        lastIdleTime = dwarf.world.getTime();
    }
}
