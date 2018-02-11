package com.elytradev.carpentrycubes.client.render.model;

import com.elytradev.carpentrycubes.common.block.BlockCarpentry;
import com.elytradev.carpentrycubes.common.block.BlockCarpentrySlope;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.model.TRSRTransformation;

public class CarpentrySlopeModel implements ICarpentersModel<BlockCarpentrySlope> {
    private static CarpentrySlopeModel INSTANCE = new CarpentrySlopeModel();
    private static CarpentryModelData straightSlopeModelData;

    static {
        straightSlopeModelData = new CarpentryModelData();

        straightSlopeModelData.addInstruction(EnumFacing.DOWN, 0, 0, 0, true, false);
        straightSlopeModelData.addInstruction(EnumFacing.DOWN, 1, 0, 0, false, false);
        straightSlopeModelData.addInstruction(EnumFacing.DOWN, 1, 0, 1, false, true);
        straightSlopeModelData.addInstruction(EnumFacing.DOWN, 0, 0, 1, true, true);

        straightSlopeModelData.addInstruction(EnumFacing.UP, 0, 0, 0, true, true);
        straightSlopeModelData.addInstruction(EnumFacing.UP, 0, 1, 1, true, false);
        straightSlopeModelData.addInstruction(EnumFacing.UP, 1, 1, 1, false, false);
        straightSlopeModelData.addInstruction(EnumFacing.UP, 1, 0, 0, false, true);

        straightSlopeModelData.addInstruction(EnumFacing.SOUTH, 0, 0, 1, false, false);
        straightSlopeModelData.addInstruction(EnumFacing.SOUTH, 1, 0, 1, true, false);
        straightSlopeModelData.addInstruction(EnumFacing.SOUTH, 1, 1, 1, true, true);
        straightSlopeModelData.addInstruction(EnumFacing.SOUTH, 0, 1, 1, false, true);

        straightSlopeModelData.addInstruction(EnumFacing.WEST, 0, 0, 0, false, false);
        straightSlopeModelData.addInstruction(EnumFacing.WEST, 0, 0, 1, true, false);
        straightSlopeModelData.addInstruction(EnumFacing.WEST, 0, 1, 1, true, true);
        straightSlopeModelData.addInstruction(EnumFacing.WEST, 0, 0.5F, 0.5F, false, true);

        straightSlopeModelData.addInstruction(EnumFacing.EAST, 1, 0, 0, false, false);
        straightSlopeModelData.addInstruction(EnumFacing.EAST, 1, 0.5F, 0.5F, false, true);
        straightSlopeModelData.addInstruction(EnumFacing.EAST, 1, 1, 1, true, true);
        straightSlopeModelData.addInstruction(EnumFacing.EAST, 1, 0, 1, true, false);
    }

    public static CarpentrySlopeModel getInstance() {
        return INSTANCE;
    }

    @Override
    public CarpentryModelData.ModelDataQuads getQuads(BlockCarpentry block, IBlockAccess access, BlockPos pos, TextureAtlasSprite[] sprites, int[] tintIndices) {
        EnumFacing facing = access.getBlockState(pos).getValue(BlockCarpentrySlope.FACING);
        straightSlopeModelData.setTransform(new TRSRTransformation(facing));

        for (int i = 0; i < EnumFacing.values().length; i++) {
            EnumFacing side = EnumFacing.values()[i];
            TextureAtlasSprite sprite = sprites[i];
            int tintIndex = tintIndices[i];

            straightSlopeModelData.setFaceData(side, sprite, tintIndex);
        }

        return straightSlopeModelData.buildModel();
    }
}
