package dev.foltz.dwarves.entity.ai.task;

import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.util.math.Box;

import java.util.EnumSet;
import java.util.List;

public class WalkToNearbyItemTask extends Task {
    DwarfEntity dwarf;
    ItemEntity nearbyItem;
    Path pathToItem;
    int tryingTime;

    public WalkToNearbyItemTask(DwarfEntity dwarf) {
        super(EnumSet.of(EntityControlType.MOVE));
        this.dwarf = dwarf;
    }

    public ItemEntity findNearbyItem() {
        Box box = dwarf.getBoundingBox().expand(16, 4, 16);
        List<Entity> entities = dwarf.world.getOtherEntities(dwarf, box, other -> other instanceof ItemEntity);
        if (entities.size() >= 1) {
            nearbyItem = (ItemEntity) entities.get(0);
        }
        else {
            nearbyItem = null;
        }
        return nearbyItem;
    }

    @Override
    public boolean shouldStart() {
        return findNearbyItem() != null;
    }

    @Override
    public void start() {
        tryingTime = 0;
    }

    @Override
    public void tick() {
        tryingTime++;
//        dwarf.getNavigation().startMovingTo(nearbyItem, 1.0d);
        pathToItem = dwarf.getNavigation().findPathTo((Entity)nearbyItem, 0);
        if (dwarf.brain.shouldAdmire(nearbyItem.getStack())) {
            dwarf.getNavigation().startMovingAlong(pathToItem, 1.2d);
        }
        else {
            dwarf.getNavigation().startMovingAlong(pathToItem, 1.0d);
        }
        dwarf.getLookControl().lookAt(nearbyItem.getX(), nearbyItem.getY(), nearbyItem.getZ());
    }

    @Override
    public boolean shouldStop() {
        return nearbyItem.removed || tryingTime > 300;
    }

    @Override
    public void stop() {
        dwarf.getNavigation().stop();
    }
}
