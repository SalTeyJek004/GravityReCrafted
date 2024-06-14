package com.saltay.gravityrecrafted.mixins;

import com.saltay.gravityrecrafted.IMixinEntity;
import net.minecraft.src.game.entity.EntityLiving;
import net.minecraft.src.game.entity.animals.EntityFox;
import net.minecraft.src.game.level.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityFox.class)
public abstract class MixinEntityFox extends EntityLiving implements IMixinEntity {

    public MixinEntityFox(World world) {
        super(world);
    }

    @Inject(method = "getEyeHeight", at = @At("RETURN"), cancellable = true)
    public void getEyeHeightMIXIN(CallbackInfoReturnable<Float> cir) {
        if (!isUpsideDown()) {
            cir.setReturnValue(height * 0.8F);
        } else {
            cir.setReturnValue(height * -0.8F);
        }
    }
}
