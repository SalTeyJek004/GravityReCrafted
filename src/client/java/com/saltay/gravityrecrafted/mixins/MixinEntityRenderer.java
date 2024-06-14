package com.saltay.gravityrecrafted.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.saltay.gravityrecrafted.IMixinEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.src.client.renderer.EntityRenderer;
import net.minecraft.src.game.entity.EntityLiving;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
abstract class MixinEntityRenderer {
    @Shadow private Minecraft mc;

    @Inject(method = "orientCamera", at = @At("HEAD"))
    public void onOrientCamera(CallbackInfo ci) {
        float temp;
        if (!((IMixinEntity) this.mc.renderViewEntity).isUpsideDown()) {
            temp = 0;
        } else {
            temp = 180;
        }
        GL11.glRotatef(temp, 0, 0, 1);
    }
}