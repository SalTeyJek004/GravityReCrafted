package com.saltay.gravityrecrafted.mixins;

import com.saltay.gravityrecrafted.IMixinEntity;
import net.minecraft.src.client.physics.AxisAlignedBB;
import net.minecraft.src.game.MathHelper;
import net.minecraft.src.game.entity.Entity;
import net.minecraft.src.game.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity implements IMixinEntity {
    @Final public AxisAlignedBB boundingBox;
    @Shadow public boolean onGround;

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


    /*@Redirect(method = "moveEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/client/physics/AxisAlignedBB;getOffsetBoundingBox(DDD)Lnet/minecraft/src/client/physics/AxisAlignedBB;"))
    public AxisAlignedBB moveEntityMIXIN(AxisAlignedBB instance, double x, double y, double z) {
        if (!isUpsideDown()) {
            return instance.getOffsetBoundingBox(x,y,z);
        } else {
            return instance.getOffsetBoundingBox(x,-y,z);
        }
    }

    @ModifyVariable(method = "moveEntity", at = @At("STORE"), name = "onground2")
    private boolean moveEntityMIXIN2(boolean a, double x, double y, @Local(ordinal = 3) double bbcy) {
        if (!isUpsideDown()) {
            return onGround || bbcy != y && bbcy < 0.0;
        } else {
            return onGround || bbcy != y && bbcy > boundingBox.maxY;
        }
    }

    @Inject(method = "moveEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/src/game/entity/Entity;stepHeight:F", ordinal = 1))
    public void moveEntityMIXIN3(double x, double y, double z, CallbackInfo ci, @Local(ordinal = 11) LocalFloatRef stepHeight) {
        if (isUpsideDown()) {
            float temp = stepHeight.get();
            stepHeight.set(-temp);
        }
    }

    @Redirect(method = "moveEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/src/game/entity/Entity;onGround:Z", opcode = Opcodes.PUTFIELD))
    public void moveEntityMIXIN4(Entity instance, boolean value, double x, double y, @Local(ordinal = 3) double bbcy) {
        if (!isUpsideDown()) {
            instance.onGround = bbcy != y && bbcy < 0.0;
        } else {
            instance.onGround = bbcy != y && bbcy > boundingBox.maxY;
        }
    }
    */
}
