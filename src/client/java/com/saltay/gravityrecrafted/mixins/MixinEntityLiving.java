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

    @Override
    public void moveEntity(double x, double y, double z) {
        if (this.noClip) {
            this.boundingBox.offset(x, y, z);
            this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0;
            if (!isUpsideDown()) {
                this.posY = this.boundingBox.minY + (double)this.yOffset - (double)this.ySize;
            } else {
                this.posY = this.boundingBox.maxY - (double)this.yOffset + (double)this.ySize;
            }
            this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0;
        } else {
            this.ySize *= 0.4F;
            double var7 = this.posX;
            double var9 = this.posZ;
            if (this.isInWeb) {
                this.isInWeb = false;
                x *= 0.25;
                y *= 0.05;
                z *= 0.25;
                this.motionX = 0.0;
                this.motionY = 0.0;
                this.motionZ = 0.0;
            }

            if (this.isInQuicksand) {
                this.isInQuicksand = false;
                x *= 0.25;
                y *= 0.2;
                z *= 0.25;
                this.motionX = 0.0;
                this.motionY = 0.0;
                this.motionZ = 0.0;
            }

            if ((Entity)this instanceof EntitySheep) {
                EntitySheep sheep = (EntitySheep)(Entity)this;
                if (sheep.isGrazing() && this.onGround) {
                    x = 0.0;
                    z = 0.0;
                }
            }

            if ((Entity)this instanceof EntityWendigo && this.dataWatcher.getWatchableObjectByte(15) == 1) {
                x *= 1.5;
                z *= 1.5;
            }

            double bbcx = x;
            double bbcy = y;
            double bbcz = z;
            AxisAlignedBB thisbb = this.boundingBox.copy();
            boolean sneaking = this.onGround && this.isSneaking();
            if (sneaking) {
                double di;
                for(di = 0.05;
                    x != 0.0
                            && this.worldObj
                            .getCollidingBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(x, !isUpsideDown() ? -1.0 : 1.0, 0.0))
                            .size()
                            == 0;
                    bbcx = x
                ) {
                    if (x < di && x >= -di) {
                        x = 0.0;
                    } else if (x > 0.0) {
                        x -= di;
                    } else {
                        x += di;
                    }
                }

                for(;
                    z != 0.0
                            && this.worldObj
                            .getCollidingBoundingBoxes(this, this.boundingBox.getOffsetBoundingBox(0.0, !isUpsideDown() ? -1.0 : 1.0, z))
                            .size()
                            == 0;
                    bbcz = z
                ) {
                    if (z < di && z >= -di) {
                        z = 0.0;
                    } else if (z > 0.0) {
                        z -= di;
                    } else {
                        z += di;
                    }
                }
            }

            List<?> bblist = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(x, y, z));

            for(int bbi = 0; bbi < bblist.size(); ++bbi) {
                y = ((AxisAlignedBB)bblist.get(bbi)).calculateYOffset(this.boundingBox, y);
            }

            this.boundingBox.offset(0.0, y, 0.0);
            if (!this.field_9293_aM && bbcy != y) {
                z = 0.0;
                y = 0.0;
                x = 0.0;
            }

            boolean onground2 = this.onGround || bbcy != y && (!isUpsideDown() ? bbcy < 0.0 : bbcy > 0.0);

            for(int multip = 0; multip < bblist.size(); ++multip) {
                x = ((AxisAlignedBB)bblist.get(multip)).calculateXOffset(this.boundingBox, x);
            }

            this.boundingBox.offset(x, 0.0, 0.0);
            if (!this.field_9293_aM && bbcx != x) {
                z = 0.0;
                y = 0.0;
                x = 0.0;
            }

            for(int var42 = 0; var42 < bblist.size(); ++var42) {
                z = ((AxisAlignedBB)bblist.get(var42)).calculateZOffset(this.boundingBox, z);
            }

            this.boundingBox.offset(0.0, 0.0, z);
            if (!this.field_9293_aM && bbcz != z) {
                z = 0.0;
                y = 0.0;
                x = 0.0;
            }

            if (this.stepHeight > 0.0F && onground2 && (sneaking || this.ySize < 0.05F) && (bbcx != x || bbcz != z)) {
                double multip7 = x;
                double multip6 = y;
                double slabz = z;
                x = bbcx;
                y = !isUpsideDown() ? (double)this.stepHeight : (double)-this.stepHeight;
                z = bbcz;
                AxisAlignedBB aabb = this.boundingBox.copy();
                this.boundingBox.setBB(thisbb);
                bblist = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(bbcx, y, bbcz));

                for(int multip5 = 0; multip5 < bblist.size(); ++multip5) {
                    y = ((AxisAlignedBB)bblist.get(multip5)).calculateYOffset(this.boundingBox, y);
                }

                this.boundingBox.offset(0.0, y, 0.0);
                if (!this.field_9293_aM && bbcy != y) {
                    z = 0.0;
                    y = 0.0;
                    x = 0.0;
                }

                for(int var44 = 0; var44 < bblist.size(); ++var44) {
                    x = ((AxisAlignedBB)bblist.get(var44)).calculateXOffset(this.boundingBox, x);
                }

                this.boundingBox.offset(x, 0.0, 0.0);
                if (!this.field_9293_aM && bbcx != x) {
                    z = 0.0;
                    y = 0.0;
                    x = 0.0;
                }

                for(int var45 = 0; var45 < bblist.size(); ++var45) {
                    z = ((AxisAlignedBB)bblist.get(var45)).calculateZOffset(this.boundingBox, z);
                }

                this.boundingBox.offset(0.0, 0.0, z);
                if (!this.field_9293_aM && bbcz != z) {
                    z = 0.0;
                    y = 0.0;
                    x = 0.0;
                }

                if (!this.field_9293_aM && bbcy != y) {
                    z = 0.0;
                    y = 0.0;
                    x = 0.0;
                } else {
                    y = !isUpsideDown() ? (double)(-this.stepHeight) :  (double)this.stepHeight;

                    for(int var46 = 0; var46 < bblist.size(); ++var46) {
                        y = ((AxisAlignedBB)bblist.get(var46)).calculateYOffset(this.boundingBox, y);
                    }

                    this.boundingBox.offset(0.0, y, 0.0);
                }

                if (multip7 * multip7 + slabz * slabz >= x * x + z * z) {
                    x = multip7;
                    y = multip6;
                    z = slabz;
                    this.boundingBox.setBB(aabb);
                } else {
                    double var41 = this.boundingBox.minY - (double)((int)this.boundingBox.minY);
                    if (var41 > 0.0) {
                        this.ySize = (float)((double)this.ySize + var41 + 0.01);
                    }
                }
            }

            if (!this.worldObj.multiplayerWorld || (Entity)this instanceof EntityPlayer || !((Entity)this instanceof EntityLiving)) {
                this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0;
                if (!isUpsideDown()) {
                    this.posY = this.boundingBox.minY + (double) this.yOffset - (double) this.ySize;
                } else {
                    this.posY = this.boundingBox.maxY - (double) this.yOffset + (double) this.ySize;
                }
                this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0;
            }

            this.isCollidedHorizontally = bbcx != x || bbcz != z;
            this.isCollidedVertically = bbcy != y;
            this.onGround = bbcy != y && (!isUpsideDown() ? bbcy < 0.0 : bbcy > 0.0);
            this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
            int multip2 = MathHelper.floor_double(this.posX);
            int multip3;
            if (!isUpsideDown()) {
                multip3 = MathHelper.floor_double(this.posY - (double)this.yOffset + (this.isSneaking() ? 0.03 : -0.03));
            } else {
                multip3 = MathHelper.floor_double(this.posY + (double)this.yOffset - (this.isSneaking() ? 0.03 : -0.03));
            }
            int multip4 = MathHelper.floor_double(this.posZ);
            int multip5 = this.worldObj.getBlockId(multip2, multip3, multip4);
            Block blockFallenOn = Block.blocksList[multip5];
            this.updateFallState(y, this.onGround, blockFallenOn, multip2, multip3, multip4);
            if (bbcx != x) {
                this.motionX = 0.0;
            }

            if (bbcy != y) {
                this.motionY = 0.0;
            }

            if (bbcz != z) {
                this.motionZ = 0.0;
            }

            double multip7 = this.posX - var7;
            double multip6 = this.posZ - var9;
            if (this.canTriggerWalking() && this.ridingEntity == null) {
                if ((Entity)this instanceof EntityPlayerSP) {
                    EntityPlayerSP player = (EntityPlayerSP)(Entity)this;
                    if (!player.capabilities.isFlying && this.fallDistance == 0.0F) {
                        this.distanceWalkedModified = (float)(
                                (double)this.distanceWalkedModified
                                        + (double)MathHelper.sqrt_double(multip7 * multip7 + multip6 * multip6) * 0.6
                        );
                    }
                } else {
                    this.distanceWalkedModified = (float)(
                            (double)this.distanceWalkedModified
                                    + (double)MathHelper.sqrt_double(multip7 * multip7 + multip6 * multip6) * 0.6
                    );
                }

                if (this.worldObj.getBlockId(multip2, multip3 - (!isUpsideDown() ? 1 : -1), multip4) == Block.fence.blockID) {
                    multip5 = this.worldObj.getBlockId(multip2, multip3 - (!isUpsideDown() ? 1 : -1), multip4);
                }

                Block block = Block.blocksList[multip5];
                if (block != null && !(block instanceof BlockAir)) {
                    if (this.isCollidedVertically) {
                        block.onEntityCollidedWithBlock(this.worldObj, multip2, multip3, multip4, this);
                    }

                    if (this.distanceWalkedModified > (float)this.nextStepDistance) {
                        ++this.nextStepDistance;
                        StepSound sound = Block.blocksList[multip5].stepSound;
                        if (this.worldObj.getBlockId(multip2, multip3 + (!isUpsideDown() ? 1 : -1), multip4) == Block.snowPile.blockID) {
                            sound = Block.snowPile.stepSound;
                            this.worldObj
                                    .playSoundAtEntity(this, sound.blockSound(), sound.getVolume() * 0.15F, sound.getPitch());
                        } else if (this.worldObj.getBlockId(multip2, multip3, multip4) == Block.ashPile.blockID) {
                            sound = Block.snowPile.stepSound;
                            this.worldObj
                                    .playSoundAtEntity(this, sound.blockSound(), sound.getVolume() * 0.15F, sound.getPitch());
                            Block.blocksList[this.worldObj.getBlockId(multip2, multip3, multip4)]
                                    .onEntityWalking(this.worldObj, multip2, multip3, multip4, this);
                        } else if (!Block.blocksList[multip5].blockMaterial.getIsLiquid()) {
                            this.worldObj
                                    .playSoundAtEntity(this, sound.blockSound(), sound.getVolume() * 0.15F, sound.getPitch());
                        }

                        Block.blocksList[multip5].onEntityWalking(this.worldObj, multip2, multip3, multip4, this);
                        this.onEntityWalking();
                    }
                }
            }

            multip2 = MathHelper.floor_double(this.boundingBox.minX + 0.001);
            multip3 = MathHelper.floor_double(this.boundingBox.minY + 0.001);
            multip4 = MathHelper.floor_double(this.boundingBox.minZ + 0.001);
            multip5 = MathHelper.floor_double(this.boundingBox.maxX - 0.001);
            int bbmaxy = MathHelper.floor_double(this.boundingBox.maxY - 0.001);
            int bbmaxz = MathHelper.floor_double(this.boundingBox.maxZ - 0.001);
            if (this.worldObj.checkChunksExist(multip2, multip3, multip4, multip5, bbmaxy, bbmaxz)) {
                for(int xRad = multip2; xRad <= multip5; ++xRad) {
                    for(int yRad = multip3; yRad <= bbmaxy; ++yRad) {
                        for(int zRad = multip4; zRad <= bbmaxz; ++zRad) {
                            int id = this.worldObj.getBlockId(xRad, yRad, zRad);
                            if (id > 0) {
                                Block.blocksList[id].onEntityCollidedWithBlock(this.worldObj, xRad, yRad, zRad, this);
                            }
                        }
                    }
                }
            }

            boolean wet = this.isWet();
            if (this.worldObj.isBoundingBoxBurning(this.boundingBox.func_28195_e(0.001, 0.001, 0.001))) {
                this.dealFireDamage(1);
                if (!wet) {
                    ++this.fire;
                    if (this.fire == 0) {
                        this.fire = 300;
                    }
                }
            } else if (this.fire <= 0) {
                this.fire = -this.fireResistance;
            }

            if (wet && this.fire > 0) {
                this.worldObj
                        .playSoundAtEntity(
                                this, "random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F
                        );
                this.fire = -this.fireResistance;
            }
        }
    }

    @Override
    public void func_346_d(float arg1, float arg2) {
        float var3 = this.rotationPitch;
        float var4 = this.rotationYaw;
        if (!isUpsideDown()) {
            this.rotationYaw = (float) ((double) this.rotationYaw + (double) arg1 * 0.15);
            this.rotationPitch = (float) ((double) this.rotationPitch - (double) arg2 * 0.15);
        } else {
            this.rotationYaw = (float) ((double) this.rotationYaw + (double) arg1 * -0.15);
            this.rotationPitch = (float) ((double) this.rotationPitch - (double) arg2 * -0.15);
        }
        if (this.rotationPitch < -90.0F) {
            this.rotationPitch = -90.0F;
        }

        if (this.rotationPitch > 90.0F) {
            this.rotationPitch = 90.0F;
        }

        this.prevRotationPitch += this.rotationPitch - var3;
        this.prevRotationYaw += this.rotationYaw - var4;
    }

    @Redirect(method = "moveEntityWithHeading", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/entity/EntityLiving;moveEntity(DDD)V"))
    public void moveEntityWithHeadingMIXIN(EntityLiving entity, double x, double y, double z) {
        if (!isUpsideDown()) {
            entity.moveEntity(x,y, z);
        } else {
            entity.moveEntity(x, y * -1, z);
        }
    }

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
