package com.elytradev.carpentrycubes.client.render.model;

import com.elytradev.carpentrycubes.common.block.BlockCarpentry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.vecmath.Vector3f;
import java.util.ArrayList;

public interface ICarpentryModel<B extends BlockCarpentry> {

    CarpentryModelData.ModelDataQuads getQuads(BlockCarpentry block, IBlockAccess access, BlockPos pos,
                                               ArrayList<Integer>[] tintIndices,
                                               ArrayList<TextureAtlasSprite>[] faceSprites,
                                               ArrayList<Vector3f>[] quadOffsets);

    TextureAtlasSprite getDefaultSprite();

}
