package dev.foltz.dwarves.entity.path;

import java.util.Arrays;
import java.util.Optional;

public enum AdjacentDirection {
    // WEST -X
    // EAST +X
    // DOWN -Y
    // UP   +Y
    // NORTH -Z
    // SOUTH +Z
    WEST_DOWN_NORTH (-1, -1, -1),
    WEST_DOWN       (-1, -1, 0),
    WEST_DOWN_SOUTH (-1, -1, 1),
    WEST_NORTH      (-1, 0, -1),
    WEST            (-1, 0, 0),
    WEST_SOUTH      (-1, 0, 1),
    WEST_UP_NORTH   (-1, 1, -1),
    WEST_UP         (-1, 1, 0),
    WEST_UP_SOUTH   (-1, 1, 1),
    DOWN_NORTH      (0, -1, -1),
    DOWN            (0, -1, 0),
    DOWN_SOUTH      (0, -1, 1),
    NORTH           (0, 0, -1),
    SOUTH           (0, 0, 1),
    UP_NORTH(0, 1, -1),
    UP              (0, 1, 0),
    UP_SOUTH(0, 1, 1),
    EAST_DOWN_NORTH (1, -1, -1),
    EAST_DOWN       (1, -1, 0),
    EAST_DOWN_SOUTH (1, -1, 1),
    EAST_NORTH      (1, 0, -1),
    EAST            (1, 0, 0),
    EAST_SOUTH      (1, 0, 1),
    EAST_UP_NORTH   (1, 1, -1),
    EAST_UP         (1, 1, 0),
    EAST_UP_SOUTH   (1, 1, 1);

    public final int x, y, z;
    AdjacentDirection(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Optional<AdjacentDirection> of(int x, int y, int z) {
        return Arrays.stream(values())
                .filter(adj -> adj.x == x && adj.y == y && adj.z == z)
                .findAny();
    }
}
