package com.saltay.gravityrecrafted.mixins;

import com.saltay.gravityrecrafted.IMixinEntity;
import net.minecraft.src.client.model.ModelBase2;
import net.minecraft.src.client.renderer.entity.RenderLiving2;
import net.minecraft.src.client.renderer.entity.RenderPlayer;
import net.minecraft.src.game.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public class MixinRenderPlayer extends RenderLiving2 {
    public MixinRenderPlayer(ModelBase2 model, float sizeOfShadow) {
        super(model, sizeOfShadow);
    }

    @Inject(method = "renderPlayer", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/client/renderer/entity/RenderLiving2;doRenderLiving(Lnet/minecraft/src/game/entity/EntityLiving;DDDFF)V"))
    public void renderPlayerMIXIN(EntityPlayer player, double x, double y, double z, float yaw, float deltaTicks, CallbackInfo ci) {
        GL11.glPushMatrix();
        if (((IMixinEntity)player).isUpsideDown()) {
            GL11.glTranslatef(0.0f,player.height + 1.5f,0.0f);
        }
    }
    @Inject(method = "renderPlayer", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/client/renderer/entity/RenderLiving2;doRenderLiving(Lnet/minecraft/src/game/entity/EntityLiving;DDDFF)V",
            shift = At.Shift.AFTER))
    public void renderPlayerMIXIN2(EntityPlayer player, double x, double y, double z, float yaw, float deltaTicks, CallbackInfo ci) {
        GL11.glPopMatrix();
    }
}
