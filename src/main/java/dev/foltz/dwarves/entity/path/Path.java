package dev.foltz.dwarves.entity.path;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

import java.util.*;

/*
 * A Path describes a sequence of moves to get from one position to another.
 * Paths have at least two PathNodes.
 */
public class Path implements Iterator<PathNode> {
    public final List<PathNode> pathNodes;
    private int pathNodeIndex;
    public final BlockPos from;
    public final BlockPos to;

    protected Path(List<PathNode> pathNodes) {
        this.pathNodes = pathNodes;
        pathNodeIndex = 0;
        from = pathNodes.get(0).pos;
        to = pathNodes.get(pathNodes.size() - 1).pos;
    }

    public boolean contains(BlockPos blockPos) {
        return pathNodes.stream().anyMatch(node -> node.pos.equals(blockPos));
    }

    @Override
    public boolean hasNext() {
        return pathNodeIndex < pathNodes.size() && !pathNodes.isEmpty();
    }

    @Override
    public PathNode next() {
        return pathNodes.get(pathNodeIndex++);
    }

    public void write(PacketByteBuf data) {
        System.out.println(pathNodes.size());
        data.writeInt(pathNodes.size());
        for (PathNode node : pathNodes) {
            node.write(data);
        }
    }

    public static Path readStatic(PacketByteBuf data) {
        int length = data.readInt();
        List<PathNode> nodes = new ArrayList<>();
        PathNode parent = null;
        for (int i = 0; i < length; i++) {
            PathNode node = PathNode.read(data, parent);
            nodes.add(node);
            parent = node;
        }
        return new Path(nodes);
    }

    @Override
    public String toString() {
        return pathNodes.toString();
    }
}
