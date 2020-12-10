package dev.foltz.dwarves.entity.task;

import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.PositionImpl;

public class WalkTowardPositionTask extends Task {
    public DwarfEntity dwarf;
    public Position destination;
    public double speed;

    public WalkTowardPositionTask(DwarfEntity dwarf, BlockPos destination, double speed) {
        this(dwarf, new PositionImpl(destination.getX() + 0.5, destination.getY(), destination.getZ() + 0.5), speed);
    }

    public WalkTowardPositionTask(DwarfEntity dwarf, Position destination, double speed) {
        super(30);
        this.dwarf = dwarf;
        this.destination = destination;
        this.speed = speed;
    }

    @Override
    protected void onTicked() {
        if (dwarf.getPos().isInRange(destination, 0.4)) {
            succeed();
            return;
        }

        if (dwarf.isOnGround() || dwarf.isTouchingWater()) {
            double dlook = destination.getY() + 1 - dwarf.getEyeY();
            if (dlook < -1) {
                dlook = -0.2;
            }
            else if (dlook > 0.5) {
                dlook = 0.2;
            }
            else {
                dlook = 0;
            }
            dwarf.getLookControl().lookAt(destination.getX(), dwarf.getEyeY() + dlook, destination.getZ());
            dwarf.getMoveControl().moveTo(destination.getX(), destination.getY(), destination.getZ(), speed);
        }
    }
}