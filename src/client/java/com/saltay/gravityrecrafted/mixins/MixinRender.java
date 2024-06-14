package com.saltay.gravityrecrafted.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.saltay.gravityrecrafted.IMixinEntity;
import net.minecraft.src.client.renderer.entity.Render;
import net.minecraft.src.client.renderer.entity.RenderManager;
import net.minecraft.src.game.entity.Entity;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(Render.class)
public class MixinRender {

    @Shadow protected RenderManager renderManager;

    @ModifyVariable(method = "renderString", at = @At("STORE"), ordinal = 0)
    public float renderStringMIXIN(float value) {
        if (!((IMixinEntity)renderManager.livingPlayer).isUpsideDown()) {
            return value;
        } else {
            return -value;
        }
    }

    @Redirect(method = "renderString", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glTranslated(DDD)V"))
    public void renderStringMIXIN2(double _x, double _y, double _z, Entity entity, String label, double x, double y, double z) {
        if (!((IMixinEntity)entity).isUpsideDown()) {
            GL11.glTranslated(x,y + (double)entity.height + 0.5,z);
        } else {
            GL11.glTranslated(x, y - (double) entity.height - 0.5, z);
        }
    }

}
