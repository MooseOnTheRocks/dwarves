package dev.foltz.dwarves.entity.path;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class PathNode {
    public enum PathNodeType {
        WALK_TO,
        PLACE_BLOCK
    }

    public PathNodeType type;
    public BlockPos pos;
    public PathNode parent;
    public int weight;

    public PathNode(PathNodeType type, BlockPos blockPos, PathNode parent, int weight) {
        this.type = type;
        this.pos = blockPos;
        this.parent = parent;
        this.weight = weight;
    }

    public void write(PacketByteBuf data) {
        data.writeEnumConstant(type);
        data.writeBlockPos(pos);
        data.writeInt(weight);
    }

    public static PathNode read(PacketByteBuf data, PathNode parent) {
        PathNodeType type = data.readEnumConstant(PathNodeType.class);
        BlockPos blockPos = data.readBlockPos();
        int weight = data.readInt();
        return new PathNode(type, blockPos, parent, weight);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PathNode) {
            PathNode node = (PathNode) obj;
            return pos.equals(node.pos) && type.equals(node.type);
        }
        else {
            return false;
        }
    }
}
