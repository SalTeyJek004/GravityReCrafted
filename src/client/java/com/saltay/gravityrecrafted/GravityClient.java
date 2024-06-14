package com.saltay.gravityrecrafted;

import com.fox2code.foxloader.loader.ClientMod;
import com.fox2code.foxloader.loader.Mod;
import com.fox2code.foxloader.registry.BlockBuilder;
import com.fox2code.foxloader.registry.RegisteredBlock;
import com.fox2code.foxloader.registry.RegisteredItemStack;
import net.minecraft.src.game.block.Block;
import net.minecraft.src.game.item.Item;

public class GravityClient extends Mod implements ClientMod {
    @Override
    public void onInit() {
        RegisteredItemStack gravField = registerNewBlock("gravity_field", new BlockBuilder()
                .setBlockName("gravity_field")
                .setGameBlockProvider(((id, blockBuilder, ext) -> new GravityFieldBlock(id))))
        .newRegisteredItemStack();

        gravField.setRegisteredStackSize(4);
        registerRecipe(gravField,
                " C ",
                "CDC",
                " C ",
                'C',
                Block.cloudstone.asRegisteredItem(),
                'D',
                Item.diamond);
    }
}
