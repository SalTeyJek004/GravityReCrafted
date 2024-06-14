package com.saltay.gravityrecrafted;

import com.fox2code.foxloader.registry.RegisteredBlock;
import net.minecraft.src.client.physics.AxisAlignedBB;
import net.minecraft.src.game.block.Block;
import net.minecraft.src.game.block.BlockBreakable;
import net.minecraft.src.game.block.Material;
import net.minecraft.src.game.block.texture.Face;
import net.minecraft.src.game.entity.Entity;
import net.minecraft.src.game.entity.EntityLiving;
import net.minecraft.src.game.entity.player.EntityPlayer;
import net.minecraft.src.game.level.World;

public class GravityFieldBlock extends BlockBreakable implements RegisteredBlock {
    protected GravityFieldBlock(int id) {
        super(id, Material.glass, false);
    }

    @Override
    protected void allocateTextures() {
        this.addTexture(getBlockName().replace("tile.", "").concat("_side"), Face.ALL, 0);
        this.addTexture(getBlockName().replace("tile.", "").concat("_top"), Face.TOP, 0);
        this.addTexture(getBlockName().replace("tile.", "").concat("_bottom"), Face.BOTTOM, 0);
        this.addTexture(getBlockName().replace("tile.", "").concat("_side"), Face.ALL, 1,false,true);
        this.addTexture(getBlockName().replace("tile.", "").concat("_top"), Face.TOP, 1);
        this.addTexture(getBlockName().replace("tile.", "").concat("_bottom"), Face.BOTTOM, 1);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public void doBlockDropEvent(World world, int x, int y, int z, EntityPlayer player, int metadata) {
        super.doBlockDropEvent(world, x, y, z, player, 0);
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public void onBlockPlaced(World world, int x, int y, int z, int blockFace) {
        if (blockFace < 2) {
            world.setBlockMetadata(x,y,z,1-blockFace);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (entity instanceof EntityLiving) {
            ((IMixinEntity) entity).setUpsideDown(world.getBlockMetadata(x, y, z) != 1);
        }
    }

}
