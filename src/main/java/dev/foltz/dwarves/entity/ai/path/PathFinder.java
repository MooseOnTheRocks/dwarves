package dev.foltz.dwarves.entity.ai.path;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class PathFinder {
    public int maxRange;

    public PathFinder(int maxRange) {
        this.maxRange = maxRange;
    }

    public boolean isValidPos(World world, BlockPos pos) {
        BlockPos posAbove = pos.up();
        BlockPos posBelow = pos.down();
        BlockState above = world.getBlockState(posAbove);
        BlockState block = world.getBlockState(pos);
        BlockState below = world.getBlockState(posBelow);
        return above.isAir() && block.isAir() && !below.isAir();
    }

    public Path computePath(BlockPos from, BlockPos to, World world, int range) {
        if (from.equals(to)) {
            return null;
        }
        final Queue<PathNode> unvisited = new PriorityQueue<>(Comparator.comparingInt(node -> node.weight));
        final Set<PathNode> visited = new TreeSet<>(PathFinder::comparePathNodes);
        final PathNode origin = new PathNode(PathNode.PathNodeType.WALK_TO, from, null, 0);
        PathNode destination = null;
        visitNode(origin, from, to, pos -> isValidPos(world, pos), unvisited, visited);
        while (!unvisited.isEmpty()) {
            PathNode node = unvisited.remove();
            if (node.weight <= range) {
                System.out.println("Found nearby path!");
                destination = node;
                break;
            }
            if (node.blockPos.equals(to)) {
                System.out.println("Found a direct path!");
                destination = node;
                break;
            }
            visitNode(node, from, to, pos -> isValidPos(world, pos), unvisited, visited);
            // Found a direct path!
        }
        // If no direct path was found,
        // try finding a path to a nearby point within range.
        if (destination == null && range > 0) {
            Optional<PathNode> nearby = visited.stream()
                    .filter(node -> node.weight <= range)
                    .min(Comparator.comparingInt(node -> node.weight));
            if (nearby.isPresent()) {
                destination = nearby.get();
            }
        }
        // Uh-oh, couldn't find a way to get nearby!
        if (destination == null) {
            return null;
        }

        // Construct a path from the destination to the source.
        List<PathNode> nodes = new ArrayList<>();
        PathNode node = destination;
        while (node != null) {
//            System.out.println("Adding " + node);
            nodes.add(node);
            node = node.parent;
        }
        // Construction was from destination to source,
        // but I want source to destination: reverse the nodes.
        Collections.reverse(nodes);
        return new Path(nodes);
    }

    private void visitNode(PathNode node, BlockPos from, BlockPos destination, Predicate<BlockPos> isValidPos, Queue<PathNode> unvisited, Set<PathNode> visited) {
        if (visited.contains(node)) return;
        BlockPos pos = node.blockPos;
//        if (!isValidPos.test(pos)) return;
        visited.add(node);
//        System.out.println("Visiting: " + node);
        List<BlockPos> potentialNeighbors = ImmutableList.of(
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
        for (BlockPos neighbor : potentialNeighbors) {
            if (!isValidPos.test(neighbor) || node.blockPos.getSquaredDistance(from) > maxRange*maxRange) continue;
            unvisited.add(new PathNode(PathNode.PathNodeType.WALK_TO, neighbor, node, (int) neighbor.getSquaredDistance(destination)));
        }
    }

    protected static int comparePathNodes(PathNode n1, PathNode n2) {
        int x = n1.blockPos.getX();
        int y = n1.blockPos.getY();
        int z = n1.blockPos.getZ();
        int ox = n2.blockPos.getX();
        int oy = n2.blockPos.getY();
        int oz = n2.blockPos.getZ();
        if (y == oy) {
            if (x == ox) {
                if (z == oz) {
                    return 0;
                }
                else if (z > oz) {
                    return 1;
                }
                else {
                    return -1;
                }
            }
            else if (x > ox) {
                return 1;
            }
            else {
                return -1;
            }
        }
        else if (y > oy) {
            return 1;
        }
        else {
            return -1;
        }
    }

}
