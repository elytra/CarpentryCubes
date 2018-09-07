package com.elytradev.carpentrycubes.client.render.model;

import com.elytradev.carpentrycubes.client.render.model.builder.CarpentryModelData;
import com.elytradev.carpentrycubes.client.render.model.builder.ICarpentryModel;
import com.elytradev.carpentrycubes.common.block.BlockCarpentry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;

public class CarpentryCubeModel implements ICarpentryModel<BlockCarpentry> {


    @Override
    public CarpentryModelData.ModelDataQuads getQuads(IBlockState state, IBlockAccess access, BlockPos pos, ArrayList<Integer>[] tintIndices, ArrayList<TextureAtlasSprite>[] faceSprites, int[] quadCount) {
        return null;
    }

    @Override
    public TextureAtlasSprite getDefaultSprite() {
        return null;
    }
}
