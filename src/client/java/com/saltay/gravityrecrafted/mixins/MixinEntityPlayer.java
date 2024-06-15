package com.saltay.gravityrecrafted.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.saltay.gravityrecrafted.IMixinEntity;
import net.minecraft.src.game.entity.EntityLiving;
import net.minecraft.src.game.entity.other.EntityItem;
import net.minecraft.src.game.entity.player.EntityPlayer;
import net.minecraft.src.game.item.ItemStack;
import net.minecraft.src.game.level.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLiving {
    @Shadow protected abstract void entityInit();

    public MixinEntityPlayer(World world) {
        super(world);
    }

    @Inject(method = "dropPlayerItemWithRandomChoice", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/entity/player/EntityPlayer;joinEntityItemWithWorld(Lnet/minecraft/src/game/entity/other/EntityItem;)V"))
    public void dropPlayerItemWithRandomChoiceMIXIN(ItemStack itemStack, boolean arg2, CallbackInfo ci, @Local(name = "entityItem") EntityItem entityItem) {
        ((IMixinEntity)entityItem).setUpsideDown(((IMixinEntity)this).isUpsideDown());
    }
}
