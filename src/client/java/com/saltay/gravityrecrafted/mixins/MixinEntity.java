package com.saltay.gravityrecrafted.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.saltay.gravityrecrafted.IMixinEntity;
import net.minecraft.src.client.physics.AxisAlignedBB;
import net.minecraft.src.game.MathHelper;
import net.minecraft.src.game.entity.Entity;
import net.minecraft.src.game.nbt.NBTTagCompound;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(Entity.class)
public abstract class MixinEntity implements IMixinEntity {
    @Shadow
    public float rotationYaw;
    @Shadow
    public float rotationPitch;
    @Shadow
    public double posY;
    @Shadow
    public float yOffset;
    @Shadow
    public float ySize;
    @Final
    public AxisAlignedBB boundingBox;
    @Shadow
    public boolean onGround;

    @Shadow
    public abstract boolean isSneaking();

    protected boolean upsideDown = false;

    public boolean isUpsideDown() {
        return upsideDown;
    }

    public void setUpsideDown(boolean upsideDown) {
        this.upsideDown = upsideDown;
    }

    @Inject(method = "writeToNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/entity/Entity;writeEntityToNBT(Lnet/minecraft/src/game/nbt/NBTTagCompound;)V"))
    public void writeToNBTMIXIN(NBTTagCompound nBTTagCompound, CallbackInfo ci) {
        nBTTagCompound.setInteger("Gravity", !isUpsideDown() ? 0 : 1);
    }

    @Inject(method = "readFromNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/entity/Entity;readEntityFromNBT(Lnet/minecraft/src/game/nbt/NBTTagCompound;)V"))
    public void readFromNBTMIXIN(NBTTagCompound nBTTagCompound, CallbackInfo ci) {
        switch (nBTTagCompound.getInteger("Gravity")) {
            case 0:
                setUpsideDown(false);
                break;
            case 1:
                setUpsideDown(true);
                break;
        }
    }

    @Redirect(method = "func_346_d", at = @At(value = "FIELD", target = "Lnet/minecraft/src/game/entity/Entity;rotationYaw:F", opcode = Opcodes.PUTFIELD))
    public void func_346_dMIXIN(Entity entity, float value, float arg1, float arg2) {
        if (!isUpsideDown()) {
            this.rotationYaw = (float) ((double) this.rotationYaw + (double) arg1 * 0.15);
        } else {
            this.rotationYaw = (float) ((double) this.rotationYaw + (double) arg1 * -0.15);
        }
    }

    @Redirect(method = "func_346_d", at = @At(value = "FIELD", target = "Lnet/minecraft/src/game/entity/Entity;rotationPitch:F", opcode = Opcodes.PUTFIELD))
    public void func_346_dMIXIN2(Entity entity, float value, float arg1, float arg2) {
        if (!isUpsideDown()) {
            this.rotationPitch = (float) ((double) this.rotationPitch - (double) arg2 * 0.15);
        } else {
            this.rotationPitch = (float) ((double) this.rotationPitch - (double) arg2 * -0.15);
        }
    }

    @Redirect(method = "moveEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/src/game/entity/Entity;posY:D", opcode = Opcodes.PUTFIELD))
    public void moveEntityMIXIN(Entity entity, double value) {
        if (!isUpsideDown()) {
            this.posY = this.boundingBox.minY + (double)this.yOffset - (double)this.ySize;
        } else {
            this.posY = this.boundingBox.maxY - (double)this.yOffset + (double)this.ySize;
        }
    }

    @Redirect(method = "moveEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/client/physics/AxisAlignedBB;getOffsetBoundingBox(DDD)Lnet/minecraft/src/client/physics/AxisAlignedBB;"))
    public AxisAlignedBB moveEntityMIXIN2(AxisAlignedBB instance, double x, double y, double z) {
        return instance.getOffsetBoundingBox(x, !isUpsideDown() ? -1.0 : 1.0, z);
    }

    @Redirect(method = "moveEntity",
            at = @At(value = "FIELD", target = "Lnet/minecraft/src/game/entity/Entity;stepHeight:F", opcode = Opcodes.GETFIELD),
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/src/game/entity/Entity;stepHeight:F", shift = At.Shift.AFTER)))
    public float moveEntityMIXIN3(Entity instance) {
        return !isUpsideDown() ? instance.stepHeight : -instance.stepHeight;
    }

    @ModifyVariable(method = "moveEntity", at = @At("STORE"), ordinal = 1)
    public boolean moveEntityMIXIN4(boolean value, double x, double y, @Local(ordinal = 6) double bbcy) {
        boolean var1;
        if (!this.onGround) {
            if (!isUpsideDown()) {
                var1 = bbcy < 0.0;
            } else {
                var1 = bbcy > 0.0;
            }
            return var1 && (bbcy != y);
        }
        return this.onGround;
    }

    @Redirect(method = "moveEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/src/game/entity/Entity;onGround:Z", opcode = Opcodes.PUTFIELD))
    public void moveEntityMIXIN5(Entity entity, boolean value, double x, double y, @Local(ordinal = 6) double bbcy) {
        boolean var1;
        if (!isUpsideDown()) {
            var1 = bbcy < 0.0;
        } else {
            var1 = bbcy > 0.0;
        }
        entity.onGround = var1 && (bbcy != y);
    }

    @ModifyVariable(method = "moveEntity", at = @At("STORE"), ordinal = 3,
            slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/entity/Entity;updateFallState(DZLnet/minecraft/src/game/block/Block;III)V")))
    public int moveEntityMIXIN6(int value) {
        if (!isUpsideDown()) {
            return MathHelper.floor_double(this.posY - (double)this.yOffset + (this.isSneaking() ? 0.03 : -0.03));
        } else {
            return MathHelper.floor_double(this.posY + (double)this.yOffset - (this.isSneaking() ? 0.03 : -0.03));
        }
    }

    @ModifyConstant(method = "moveEntity", constant = @Constant(intValue = 1), slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/level/World;getBlockId(III)I"),
            to = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/block/Block;onEntityCollidedWithBlock(Lnet/minecraft/src/game/level/World;IIILnet/minecraft/src/game/entity/Entity;)V")))
    public int moveEntityMIXIN7(int constant) {
        return !isUpsideDown() ? constant : -constant;
    }

    @ModifyVariable(method = "moveEntity", at = @At("HEAD"), ordinal = 1)
    public double moveEntityMIXIN8(double y) {
        return !isUpsideDown() ? y : -y;
    }

}
