package dev.foltz.dwarves.entity.path;

import com.google.common.collect.ImmutableList;
import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class PathFinder {
    public static final int maxRange = 24;

    public static Optional<Path> computePath(DwarfEntity entity, World world, BlockPos from, BlockPos to, int withinRange) {
        // Don't use HashSet! Unless you also override PathNode.hashCode()!
        // Can lead to infinite loops with two PathNodes with the same pos but different hashes.
        final Set<PathNode> visited = new TreeSet<>(PathFinder::comparePathNodes);
        final Queue<PathNode> unvisited = new PriorityQueue<>(Comparator.comparingDouble(node -> node.pos.getSquaredDistance(to)));
        PathNode destination = null;

        // Start at the origin.
        unvisited.add(new PathNode(PathNode.PathNodeType.WALK_TO, from, null, 0));
        // Find a path...
        while (!unvisited.isEmpty()) {
            PathNode node = unvisited.remove();
            // Return the first found path to a block within range.
            // unvisited : PriorityQueue(distance to destination) => best first search.
            // Don't use BlockPos.getSquaredDistance(to)!
            // Must have treatAsBlockPos set to TRUE despite wanting to compare BlockPos!
            // Reason is because it only adds 0.5 to *one* of the BlockPos (to center the position), not both.
            if (node.pos.getSquaredDistance(to.getX(), to.getY(), to.getZ(), false) <= withinRange * withinRange) {
                destination = node;
                break;
            }
            visit(entity, world, node, visited, unvisited);
        }

        // Didn't find a path :c
        if (destination == null) {
            return Optional.empty();
        }
        // Found a path!
        else {
            // Nodes is constructed from destination to origin...
            List<PathNode> nodes = new ArrayList<>();
            for (PathNode node = destination; node != null; node = node.parent) {
                nodes.add(node);
            }
            // ... but we want a path from origin to destination,
            // so reverse the list.
            Collections.reverse(nodes);
            return Optional.of(new Path(nodes));
        }
    }

    private static void visit(DwarfEntity entity, World world, PathNode node, Set<PathNode> visited, Queue<PathNode> unvisited) {
        if (!visited.add(node)) {
            return;
        }
        if (node.weight >= maxRange) {
            return;
        }

        BlockPos pos = node.pos;
        List<BlockPos> neighbors = ImmutableList.of(
                pos.north(),
                pos.east(),
                pos.south(),
                pos.west(),
                pos.up().north(),
                pos.up().east(),
                pos.up().south(),
                pos.up().west(),
                pos.down().north(),
                pos.down().east(),
                pos.down().south(),
                pos.down().west()
        );

        for (BlockPos blockPos : neighbors) {
            PathNode neighborNode = new PathNode(PathNode.PathNodeType.WALK_TO, blockPos, node, node.weight + 1);
            if (!visited.contains(neighborNode) && world.isAir(blockPos) && world.isAir(blockPos.up()) && !world.isAir(blockPos.down()))
                unvisited.add(neighborNode);
        }
    }

    protected static int comparePathNodes(PathNode n1, PathNode n2) {
        return n1.pos.compareTo(n2.pos);
    }
}
