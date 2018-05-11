package com.elytradev.carpentrycubes.client.render.model;

import com.elytradev.carpentrycubes.common.block.BlockCarpentry;
import java.util.ArrayList;
import javax.vecmath.Vector3f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface ICarpentryModel<B extends BlockCarpentry> {

    CarpentryModelData.ModelDataQuads getQuads(IBlockState state, IBlockAccess access, BlockPos pos,
        ArrayList<Integer>[] tintIndices,
        ArrayList<TextureAtlasSprite>[] faceSprites,
        ArrayList<Vector3f>[] quadOffsets);

    float[] getUVs(EnumFacing oldFace, EnumFacing newFace, EnumFacing facing, IBlockState state, float oU, float oV);

    TextureAtlasSprite getDefaultSprite();
}
