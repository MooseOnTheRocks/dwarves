package dev.foltz.dwarves.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.foltz.dwarves.entity.path.PathNode;
import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class PathRenderer {
    public static final boolean smoothLines = false;
    public static final float lineWidth = 1.0f;
    public static void render() {
        ClientWorld world = MinecraftClient.getInstance().world;
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        PlayerEntity player = MinecraftClient.getInstance().player;
        ShapeContext shapeContext = ShapeContext.of(player);
        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        if (smoothLines) GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(lineWidth);
        GL11.glBegin(GL11.GL_LINES);
//        BlockPos.Mutable pos = player.getBlockPos().mutableCopy();
        Box box = player.getBoundingBox().expand(20);
        List<DwarfEntity> nearbyDwarves = world.getEntitiesByClass(DwarfEntity.class, box, entity -> true);
        nearbyDwarves.forEach(dwarf -> {
            dwarf.getPath().ifPresent(path -> {
                if (path == null) return;
                for (PathNode node : path.pathNodes) {
                    BlockPos pos = node.pos;
                    switch (node.type) {
                        case WALK_TO:
                            renderCross(camera, world, pos, 0x0000ff, ShapeContext.of(dwarf));
                            break;
                        case PLACE_BLOCK:
                            renderCross(camera, world, pos, 0x00ff00, ShapeContext.of(dwarf));
                            break;
                        default:
                            renderCross(camera, world, pos, 0xff0000, ShapeContext.of(dwarf));
                            break;
                    }
                }
            });
        });
        GL11.glEnd();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        if (smoothLines) GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    public static void renderCross(Camera camera, World world, BlockPos pos, int color, ShapeContext shapeContext) {
        double d0 = camera.getPos().x;
        double d1 = camera.getPos().y - .005D;
//        VoxelShape upperOutlineShape = world.getBlockState(pos).getCollisionShape(world, pos, shapeContext);
//        if (!upperOutlineShape.isEmpty())
//            d1 -= upperOutlineShape.getMax(Direction.Axis.Y);
        double d2 = camera.getPos().z;

        int red = (color >> 16) & 255;
        int green = (color >> 8) & 255;
        int blue = color & 255;
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        RenderSystem.color4f(red / 255f, green / 255f, blue / 255f, 1f);
        GL11.glVertex3d(x + .01 - d0, y - d1, z + .01 - d2);
        GL11.glVertex3d(x - .01 + 1 - d0, y - d1, z - .01 + 1 - d2);
        GL11.glVertex3d(x - .01 + 1 - d0, y - d1, z + .01 - d2);
        GL11.glVertex3d(x + .01 - d0, y - d1, z - .01 + 1 - d2);
    }
}
