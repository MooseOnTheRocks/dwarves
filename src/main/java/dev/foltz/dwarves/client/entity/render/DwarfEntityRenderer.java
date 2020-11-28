package dev.foltz.dwarves.client.entity.render;

import dev.foltz.dwarves.client.entity.render.model.DwarfEntityModel;
import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import dev.foltz.dwarves.DwarvesMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DwarfEntityRenderer extends MobEntityRenderer<DwarfEntity, DwarfEntityModel> {
    public DwarfEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new DwarfEntityModel(0.5f), 0.5f);
        this.features.clear();
        this.addFeature(new HeldItemFeatureRenderer(this));
    }

    @Override
    public void render(DwarfEntity dwarf, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        DwarfEntityModel model = this.getModel();
        model.leftArmPose = dwarf.getStackInHand(Hand.OFF_HAND).isEmpty() ? BipedEntityModel.ArmPose.EMPTY : BipedEntityModel.ArmPose.ITEM;
        model.rightArmPose = dwarf.getStackInHand(Hand.MAIN_HAND).isEmpty() ? BipedEntityModel.ArmPose.EMPTY : BipedEntityModel.ArmPose.ITEM;
        super.render(dwarf, yaw, tickDelta, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(DwarfEntity entity) {
        return new Identifier(DwarvesMod.MODID, "textures/entity/dwarf/dwarf.png");
    }
}
