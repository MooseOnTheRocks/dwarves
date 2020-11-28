package dev.foltz.dwarves.client.entity.render.model;

import dev.foltz.dwarves.entity.dwarf.DwarfBrain;
import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DwarfEntityModel extends BipedEntityModel<DwarfEntity> {
    public DwarfEntityModel(float scale) {
        super(0.0f);
        this.textureWidth = 64;
        this.textureHeight = 64;

        // Helmet
        helmet = new ModelPart(this, 32, 0);
        helmet.addCuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, 0.5f);
        helmet.setPivot(0.0f, 8.0f, 0.0f);
        // Head
        head = new ModelPart(this, 0, 0);
        head.addCuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f);
        head.setPivot(0.0f, 8.0f, 0.0f);
        // Torso
        torso = new ModelPart(this, 16, 20);
        torso.addCuboid(-4.0f, 7.0f, -3.0f, 8.0f, 9.0f, 6.0f);
        torso.setPivot(0.0f, 1.0f, 0.0f);
        // Left arm
        leftArm = new ModelPart(this, 44, 22);
        leftArm.addCuboid(0.0f, 0.0f, -2.0f, 3.0f, 8.0f, 4.0f);
        leftArm.setPivot(4, 8, 0);
        leftArm.mirror = true;
        // Right arm
        rightArm = new ModelPart(this, 44, 22);
        rightArm.addCuboid(-3.0f, 0.0f, -2.0f, 3.0f, 8.0f, 4.0f);
        rightArm.setPivot(-4, 8, 0);
        // Left leg
        leftLeg = new ModelPart(this, 0, 22);
        leftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 7.0f, 4.0f);
        leftLeg.setPivot(2.0f, 17.0f, 0.0f);
        leftLeg.mirror = true;
        // Right leg
        rightLeg = new ModelPart(this, 0, 22);
        rightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 7.0f, 4.0f);
        rightLeg.setPivot(-2.0f, 17.0f, 0.0f);
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        super.setArmAngle(arm, matrices);
    }

    public void setSwingArmAngles(DwarfEntity dwarf, float animationProgress) {
        if (this.handSwingProgress > 0.0F) {
            Arm arm = this.getPreferredArm(dwarf);
            ModelPart modelPart = this.getArm(arm);
            float g = this.handSwingProgress;
            this.torso.yaw = MathHelper.sin(MathHelper.sqrt(g) * 6.2831855F) * 0.2F;
            ModelPart var10000;
            if (arm == Arm.LEFT) {
                var10000 = this.torso;
                var10000.yaw *= -1.0F;
            }

            this.rightArm.pivotZ = MathHelper.sin(this.torso.yaw) * 5.0F;
            this.rightArm.pivotX = -MathHelper.cos(this.torso.yaw) * 4.0F;
            this.leftArm.pivotZ = -MathHelper.sin(this.torso.yaw) * 5.0F;
            this.leftArm.pivotX = MathHelper.cos(this.torso.yaw) * 4.0F;
            var10000 = this.rightArm;
            var10000.yaw += this.torso.yaw;
            var10000 = this.leftArm;
            var10000.yaw += this.torso.yaw;
            var10000 = this.leftArm;
            var10000.pitch += this.torso.yaw;
            g = 1.0F - this.handSwingProgress;
            g *= g;
            g *= g;
            g = 1.0F - g;
            float h = MathHelper.sin(g * 3.1415927F);
            float i = MathHelper.sin(this.handSwingProgress * 3.1415927F) * -(this.head.pitch - 0.7F) * 0.75F;
            modelPart.pitch = (float)((double)modelPart.pitch - ((double)h * 1.2D + (double)i));
            modelPart.yaw += this.torso.yaw * 2.0F;
            modelPart.roll += MathHelper.sin(this.handSwingProgress * 3.1415927F) * -0.4F;
        }
    }

    @Override
    public void setAngles(DwarfEntity dwarf, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        boolean bl = dwarf.getRoll() > 4;
        boolean bl2 = dwarf.isInSwimmingPose();
        this.head.yaw = headYaw * 0.017453292F;
        if (bl) {
            this.head.pitch = -0.7853982F;
        } else if (this.leaningPitch > 0.0F) {
            if (bl2) {
                this.head.pitch = this.lerpAngle(this.leaningPitch, this.head.pitch, -0.7853982F);
            } else {
                this.head.pitch = this.lerpAngle(this.leaningPitch, this.head.pitch, headPitch * 0.017453292F);
            }
        } else {
            this.head.pitch = headPitch * 0.017453292F;
        }

        this.torso.yaw = 0.0F;
        this.rightArm.pivotZ = 0.0F;
        this.rightArm.pivotX = -4.0F;
        this.leftArm.pivotZ = 0.0F;
        this.leftArm.pivotX = 4.0F;
        float k = 1.0F;
        if (bl) {
            k = (float)dwarf.getVelocity().lengthSquared();
            k /= 0.2F;
            k *= k * k;
        }

        if (k < 1.0F) {
            k = 1.0F;
        }

        this.rightArm.pitch = MathHelper.cos(limbAngle * 0.6662F + 3.1415927F) * 2.0F * limbDistance * 0.5F / k;
        this.leftArm.pitch = MathHelper.cos(limbAngle * 0.6662F) * 2.0F * limbDistance * 0.5F / k;
        this.rightArm.roll = 0.0F;
        this.leftArm.roll = 0.0F;
        this.rightLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance / k;
        this.leftLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance / k;
        this.rightLeg.yaw = 0.0F;
        this.leftLeg.yaw = 0.0F;
        this.rightLeg.roll = 0.0F;
        this.leftLeg.roll = 0.0F;
        ModelPart var10000;
        if (this.riding) {
            var10000 = this.rightArm;
            var10000.pitch += -0.62831855F;
            var10000 = this.leftArm;
            var10000.pitch += -0.62831855F;
            this.rightLeg.pitch = -1.4137167F;
            this.rightLeg.yaw = 0.31415927F;
            this.rightLeg.roll = 0.07853982F;
            this.leftLeg.pitch = -1.4137167F;
            this.leftLeg.yaw = -0.31415927F;
            this.leftLeg.roll = -0.07853982F;
        }

        this.rightArm.yaw = 0.0F;
        this.leftArm.yaw = 0.0F;
        boolean bl3 = dwarf.getMainArm() == Arm.RIGHT;
        boolean bl4 = bl3 ? this.leftArmPose.method_30156() : this.rightArmPose.method_30156();
        if (bl3 != bl4) {
            this.setLeftArmRotations(dwarf);
            this.setRightArmRotations(dwarf);
        } else {
            this.setRightArmRotations(dwarf);
            this.setLeftArmRotations(dwarf);
        }

        setSwingArmAngles(dwarf, animationProgress);

        boolean isAdmiring = dwarf.getDataTracker().get(DwarfBrain.ADMIRING);
//        System.out.println("Renderer, dwarf.isAdmiring: " + isAdmiring + ", " + dwarf.getOffHandStack());
        if (isAdmiring) {
            this.head.pitch = 0.5F;
            this.head.yaw = 0.0F;
            if (dwarf.isLeftHanded()) {
                this.rightArm.yaw = -0.5F;
                this.rightArm.pitch = -0.9F;
            } else {
                this.leftArm.yaw = 0.5F;
                this.leftArm.pitch = -0.9F;
            }
        }

        CrossbowPosing.method_29350(this.rightArm, this.leftArm, animationProgress);
        if (this.leaningPitch > 0.0F) {
            float l = limbAngle % 26.0F;
            Arm arm = this.getPreferredArm(dwarf);
            float m = arm == Arm.RIGHT && this.handSwingProgress > 0.0F ? 0.0F : this.leaningPitch;
            float n = arm == Arm.LEFT && this.handSwingProgress > 0.0F ? 0.0F : this.leaningPitch;
            float p;
            if (l < 14.0F) {
                this.leftArm.pitch = this.lerpAngle(n, this.leftArm.pitch, 0.0F);
                this.rightArm.pitch = MathHelper.lerp(m, this.rightArm.pitch, 0.0F);
                this.leftArm.yaw = this.lerpAngle(n, this.leftArm.yaw, 3.1415927F);
                this.rightArm.yaw = MathHelper.lerp(m, this.rightArm.yaw, 3.1415927F);
                this.leftArm.roll = this.lerpAngle(n, this.leftArm.roll, 3.1415927F + 1.8707964F * this.method_2807(l) / this.method_2807(14.0F));
                this.rightArm.roll = MathHelper.lerp(m, this.rightArm.roll, 3.1415927F - 1.8707964F * this.method_2807(l) / this.method_2807(14.0F));
            } else if (l >= 14.0F && l < 22.0F) {
                p = (l - 14.0F) / 8.0F;
                this.leftArm.pitch = this.lerpAngle(n, this.leftArm.pitch, 1.5707964F * p);
                this.rightArm.pitch = MathHelper.lerp(m, this.rightArm.pitch, 1.5707964F * p);
                this.leftArm.yaw = this.lerpAngle(n, this.leftArm.yaw, 3.1415927F);
                this.rightArm.yaw = MathHelper.lerp(m, this.rightArm.yaw, 3.1415927F);
                this.leftArm.roll = this.lerpAngle(n, this.leftArm.roll, 5.012389F - 1.8707964F * p);
                this.rightArm.roll = MathHelper.lerp(m, this.rightArm.roll, 1.2707963F + 1.8707964F * p);
            } else if (l >= 22.0F && l < 26.0F) {
                p = (l - 22.0F) / 4.0F;
                this.leftArm.pitch = this.lerpAngle(n, this.leftArm.pitch, 1.5707964F - 1.5707964F * p);
                this.rightArm.pitch = MathHelper.lerp(m, this.rightArm.pitch, 1.5707964F - 1.5707964F * p);
                this.leftArm.yaw = this.lerpAngle(n, this.leftArm.yaw, 3.1415927F);
                this.rightArm.yaw = MathHelper.lerp(m, this.rightArm.yaw, 3.1415927F);
                this.leftArm.roll = this.lerpAngle(n, this.leftArm.roll, 3.1415927F);
                this.rightArm.roll = MathHelper.lerp(m, this.rightArm.roll, 3.1415927F);
            }

            p = 0.3F;
            float r = 0.33333334F;
            this.leftLeg.pitch = MathHelper.lerp(this.leaningPitch, this.leftLeg.pitch, 0.3F * MathHelper.cos(limbAngle * 0.33333334F + 3.1415927F));
            this.rightLeg.pitch = MathHelper.lerp(this.leaningPitch, this.rightLeg.pitch, 0.3F * MathHelper.cos(limbAngle * 0.33333334F));
        }

        this.helmet.copyPositionAndRotation(this.head);
    }

    private float method_2807(float f) {
        return -65.0F * f + f * f;
    }

    private void setRightArmRotations(DwarfEntity dwarf) {
        switch(this.rightArmPose) {
            case EMPTY:
                this.rightArm.yaw = 0.0F;
                break;
            case BLOCK:
                this.rightArm.pitch = this.rightArm.pitch * 0.5F - 0.9424779F;
                this.rightArm.yaw = -0.5235988F;
                break;
            case ITEM:
                this.rightArm.pitch = this.rightArm.pitch * 0.5F - 0.31415927F;
                this.rightArm.yaw = 0.0F;
                break;
            case THROW_SPEAR:
                this.rightArm.pitch = this.rightArm.pitch * 0.5F - 3.1415927F;
                this.rightArm.yaw = 0.0F;
                break;
            case BOW_AND_ARROW:
                this.rightArm.yaw = -0.1F + this.head.yaw;
                this.leftArm.yaw = 0.1F + this.head.yaw + 0.4F;
                this.rightArm.pitch = -1.5707964F + this.head.pitch;
                this.leftArm.pitch = -1.5707964F + this.head.pitch;
                break;
            case CROSSBOW_CHARGE:
                CrossbowPosing.charge(this.rightArm, this.leftArm, dwarf, true);
                break;
            case CROSSBOW_HOLD:
                CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, true);
        }
    }

    private void setLeftArmRotations(DwarfEntity dwarf) {
        switch(this.leftArmPose) {
            case EMPTY:
                this.leftArm.yaw = 0.0F;
                break;
            case BLOCK:
                this.leftArm.pitch = this.leftArm.pitch * 0.5F - 0.9424779F;
                this.leftArm.yaw = 0.5235988F;
                break;
            case ITEM:
                this.leftArm.pitch = this.leftArm.pitch * 0.5F - 0.31415927F;
                this.leftArm.yaw = 0.0F;
                break;
            case THROW_SPEAR:
                this.leftArm.pitch = this.leftArm.pitch * 0.5F - 3.1415927F;
                this.leftArm.yaw = 0.0F;
                break;
            case BOW_AND_ARROW:
                this.rightArm.yaw = -0.1F + this.head.yaw - 0.4F;
                this.leftArm.yaw = 0.1F + this.head.yaw;
                this.rightArm.pitch = -1.5707964F + this.head.pitch;
                this.leftArm.pitch = -1.5707964F + this.head.pitch;
                break;
            case CROSSBOW_CHARGE:
                CrossbowPosing.charge(this.rightArm, this.leftArm, dwarf, false);
                break;
            case CROSSBOW_HOLD:
                CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, false);
        }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        super.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
