package dev.foltz.dwarves.entity.ai.task;

import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.EnumSet;

public class LookAtPlayerTask extends Task {

    DwarfEntity dwarf;
    PlayerEntity nearbyPlayer;
    int lookTimeLeft;
    long lastLookTime;
    float maxRange;

    public LookAtPlayerTask(DwarfEntity dwarf) {
        super(EnumSet.of(EntityControlType.LOOK));
        this.dwarf = dwarf;
        maxRange = 8.0f;
    }

    public PlayerEntity findNearesetPlayer() {
        nearbyPlayer = dwarf.world.getClosestPlayer(dwarf, maxRange);
        return nearbyPlayer;
    }

    @Override
    public boolean shouldStart() {
        return dwarf.world.getTime() - lastLookTime > 120 && findNearesetPlayer() != null;
    }

    @Override
    public void start() {
        lookTimeLeft = dwarf.getRandom().nextInt(80) + 20;
    }

    @Override
    public void tick() {
        dwarf.getLookControl().lookAt(nearbyPlayer.getX(), nearbyPlayer.getEyeY(), nearbyPlayer.getZ());
        lookTimeLeft -= 1;
    }

    @Override
    public boolean shouldStop() {
        return lookTimeLeft <= 0 || dwarf.distanceTo(nearbyPlayer) > maxRange;
    }

    @Override
    public void stop() {
        lastLookTime = dwarf.world.getTime();
    }
}
