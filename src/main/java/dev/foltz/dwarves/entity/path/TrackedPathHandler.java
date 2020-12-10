package dev.foltz.dwarves.entity.path;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.network.PacketByteBuf;

import java.util.Optional;

public class TrackedPathHandler implements TrackedDataHandler<Optional<Path>> {
    @Override
    public void write(PacketByteBuf data, Optional<Path> maybePath) {
        data.writeBoolean(maybePath.isPresent());
        maybePath.ifPresent(path -> path.write(data));
    }

    @Override
    public Optional<Path> read(PacketByteBuf data) {
        return data.readBoolean() ? Optional.of(Path.readStatic(data)) : Optional.empty();
    }

    @Override
    public Optional<Path> copy(Optional<Path> maybePath) {
        return maybePath;
    }
}
