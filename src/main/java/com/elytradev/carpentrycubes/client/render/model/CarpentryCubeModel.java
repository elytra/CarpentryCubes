package com.elytradev.carpentrycubes.client.render.model;

import com.elytradev.carpentrycubes.client.render.model.builder.CarpentryModelData;
import com.elytradev.carpentrycubes.client.render.model.builder.ICarpentryModel;
import com.elytradev.carpentrycubes.common.block.BlockCarpentry;
import java.util.ArrayList;
import javax.vecmath.Vector3f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class CarpentryCubeModel implements ICarpentryModel<BlockCarpentry> {

    @Override
    public CarpentryModelData.ModelDataQuads getQuads(IBlockState state, IBlockAccess access, BlockPos pos,
                                                      ArrayList<Integer>[] tintIndices, ArrayList<TextureAtlasSprite>[] faceSprites,
                                                      ArrayList<Vector3f>[] quadOffsets) {
        return null;
    }

    @Override
    public float[] getUVs(EnumFacing oldFace, EnumFacing facing, IBlockState state, float oU,
                          float oV) {
        return new float[0];
    }

    @Override
    public TextureAtlasSprite getDefaultSprite() {
        return null;
    }
}
