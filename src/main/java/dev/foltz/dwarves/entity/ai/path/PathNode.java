package dev.foltz.dwarves.entity.ai.path;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.CollisionView;
import org.jetbrains.annotations.NotNull;

public class PathNode {
    public enum PathNodeType {
        WALK_TO,
        PLACE_BLOCK,
        BREAK_BLOCK
    }

    public PathNodeType type;
    public BlockPos blockPos;
    public PathNode parent;
    public int weight;

    public PathNode(PathNodeType type, BlockPos blockPos, int weight) {
        this.type = type;
        this.blockPos = blockPos;
        this.parent = null;
        this.weight = weight;
    }

    public PathNode(PathNodeType type, BlockPos blockPos, PathNode parent, int weight) {
        this.type = type;
        this.blockPos = blockPos;
        this.parent = parent;
        this.weight = weight;
    }

    public void write(PacketByteBuf data) {
        data.writeEnumConstant(type);
        data.writeBlockPos(blockPos);
        data.writeInt(weight);
    }

    public static PathNode read(PacketByteBuf data, PathNode parent) {
        PathNodeType type = data.readEnumConstant(PathNodeType.class);
        BlockPos blockPos = data.readBlockPos();
        int weight = data.readInt();
        return new PathNode(type, blockPos, parent, weight);
    }
}
