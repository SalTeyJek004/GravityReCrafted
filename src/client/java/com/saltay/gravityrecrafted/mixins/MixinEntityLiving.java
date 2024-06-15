package com.saltay.gravityrecrafted.mixins;

import com.saltay.gravityrecrafted.IMixinEntity;
import net.minecraft.src.client.physics.AxisAlignedBB;
import net.minecraft.src.client.player.EntityPlayerSP;
import net.minecraft.src.game.MathHelper;
import net.minecraft.src.game.block.Block;
import net.minecraft.src.game.block.BlockAir;
import net.minecraft.src.game.block.StepSound;
import net.minecraft.src.game.entity.Entity;
import net.minecraft.src.game.entity.EntityLiving;
import net.minecraft.src.game.entity.animals.EntitySheep;
import net.minecraft.src.game.entity.monster.EntityWendigo;
import net.minecraft.src.game.entity.player.EntityPlayer;
import net.minecraft.src.game.level.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EntityLiving.class)
public abstract class MixinEntityLiving extends Entity implements IMixinEntity{
    @Shadow protected float moveStrafing;
    @Shadow protected float moveForward;

    public MixinEntityLiving(World world) {
        super(world);
    }
/*
    @Redirect(method = "moveEntityWithHeading", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/entity/EntityLiving;moveEntity(DDD)V"))
    public void moveEntityWithHeadingMIXIN(EntityLiving entity, double x, double y, double z) {
        if (!isUpsideDown()) {
            entity.moveEntity(x,y, z);
        } else {
            entity.moveEntity(x, y * -1, z);
        }
    }

 */

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/entity/EntityLiving;moveEntityWithHeading(FF)V"))
    public void onLivingUpdateMIXIN(EntityLiving instance, float var3, float var4) {

        if (!isUpsideDown()) {
            instance.moveEntityWithHeading(moveStrafing, moveForward);
        } else {
            instance.moveEntityWithHeading(-moveStrafing, moveForward);
        }
    }

    @Inject(method = "getEyeHeight", at = @At("RETURN"), cancellable = true)
    public void getEyeHeightMIXIN(CallbackInfoReturnable<Float> cir) {
        if (!isUpsideDown()) {
            cir.setReturnValue(height * 0.85F);
        } else {
            cir.setReturnValue(height * -0.85F);
        }
    }
}
