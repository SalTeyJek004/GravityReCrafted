package com.saltay.gravityrecrafted.mixins;

import com.saltay.gravityrecrafted.IMixinEntity;
import net.minecraft.src.client.renderer.entity.Render;
import net.minecraft.src.client.renderer.entity.RenderLiving;
import net.minecraft.src.client.renderer.entity.RenderLiving2;
import net.minecraft.src.game.entity.EntityLiving;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderLiving2.class)
public abstract class MixinRenderLiving2 extends Render {

    @Redirect(method = "doRenderLiving", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glScalef(FFF)V"))
    public void doRnderLivingMIXIN(float x, float y, float z, EntityLiving entity) {
        if (!((IMixinEntity)entity).isUpsideDown()) {
            GL11.glScalef(x, y, z);
        } else {
            GL11.glScalef(-x, -y, z);
        }
    }
}
