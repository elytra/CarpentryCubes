package com.elytradev.carpentrycubes.common.block;

import com.elytradev.carpentrycubes.client.render.model.ICarpentryModel;
import com.elytradev.carpentrycubes.client.render.model.data.CarpentryCubeModel;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class BlockCarpentryCube extends BlockCarpentry {
    public BlockCarpentryCube(Material materialIn) {
        super(materialIn);
    }

    @Override
    public ICarpentryModel<? extends BlockCarpentry> getModel() {
        return CarpentryCubeModel.getInstance();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
}
