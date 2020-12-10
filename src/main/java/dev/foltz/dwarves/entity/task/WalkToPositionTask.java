package dev.foltz.dwarves.entity.task;

import dev.foltz.dwarves.entity.path.Path;
import dev.foltz.dwarves.entity.path.PathFinder;
import dev.foltz.dwarves.entity.path.PathNode;
import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;
import java.util.function.Supplier;

public class WalkToPositionTask extends TaskGenerator {
    public DwarfEntity dwarf;

    public WalkToPositionTask(DwarfEntity dwarf, BlockPos destination, int range, double speed) {
        super(new Supplier<Optional<Task>>() {
            int index = 0;
            Optional<Path> maybePath;
            BlockPos target;

            @Override
            public Optional<Task> get() {
                if (index == 0) {
                    maybePath = new PathFinder().computePath(dwarf, dwarf.world, dwarf.getBlockPos(), destination, range);
                    dwarf.setPath(maybePath);
                }
                index += 1;

                if (!maybePath.isPresent() || !maybePath.get().hasNext()) {
                    return Optional.empty();
                }
                else {
                    PathNode node = maybePath.get().next();
                    if (node.type == PathNode.PathNodeType.PLACE_BLOCK) {
                        BlockPos pos = node.pos.down();
                        return Optional.of(TaskComposer.sequence(
                            new PlaceBlockTask(dwarf, pos, Blocks.COBBLESTONE.getDefaultState())
                        ));
                    }
                    else {
                        return Optional.of(new WalkTowardPositionTask(dwarf, node.pos, speed));
                    }
                }
            }
        });
    }
}
