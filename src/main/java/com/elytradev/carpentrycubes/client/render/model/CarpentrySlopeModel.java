package com.elytradev.carpentrycubes.client.render.model;

import com.elytradev.carpentrycubes.common.CarpentryMod;
import com.elytradev.carpentrycubes.common.block.BlockCarpentry;
import com.elytradev.carpentrycubes.common.block.BlockCarpentrySlope;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.model.TRSRTransformation;

public class CarpentrySlopeModel implements ICarpentersModel<BlockCarpentrySlope> {
    private static CarpentrySlopeModel INSTANCE = new CarpentrySlopeModel();
    private static CarpentryModelData straightSlopeModelData;

    static {
        straightSlopeModelData = new CarpentryModelData();

        straightSlopeModelData.addInstruction(EnumFacing.DOWN, 0, 0, 0, 0, 16);
        straightSlopeModelData.addInstruction(EnumFacing.DOWN, 1, 0, 0, 16, 16);
        straightSlopeModelData.addInstruction(EnumFacing.DOWN, 1, 0, 1, 16, 0);
        straightSlopeModelData.addInstruction(EnumFacing.DOWN, 0, 0, 1, 0, 0);

        straightSlopeModelData.addInstruction(EnumFacing.NORTH, 0, 0, 0, 16, 16);
        straightSlopeModelData.addInstruction(EnumFacing.NORTH, 0, 1, 1, 16, 0);
        straightSlopeModelData.addInstruction(EnumFacing.NORTH, 1, 1, 1, 0, 0);
        straightSlopeModelData.addInstruction(EnumFacing.NORTH, 1, 0, 0, 0, 16);

        straightSlopeModelData.addInstruction(EnumFacing.SOUTH, 0, 0, 1, 0, 16);
        straightSlopeModelData.addInstruction(EnumFacing.SOUTH, 1, 0, 1, 16, 16);
        straightSlopeModelData.addInstruction(EnumFacing.SOUTH, 1, 1, 1, 16, 0);
        straightSlopeModelData.addInstruction(EnumFacing.SOUTH, 0, 1, 1, 0, 0);

        straightSlopeModelData.addInstruction(EnumFacing.WEST, 0, 0, 0, 0, 16);
        straightSlopeModelData.addInstruction(EnumFacing.WEST, 0, 0, 1, 16, 16);
        straightSlopeModelData.addInstruction(EnumFacing.WEST, 0, 1, 1, 16, 0);
        straightSlopeModelData.addInstruction(EnumFacing.WEST, 0, 0.5F, 0.5F, 0, 0);

        straightSlopeModelData.addInstruction(EnumFacing.EAST, 1, 0, 0, 16, 16);
        straightSlopeModelData.addInstruction(EnumFacing.EAST, 1, 0.5F, 0.5F, 16, 0);
        straightSlopeModelData.addInstruction(EnumFacing.EAST, 1, 1, 1, 0, 0);
        straightSlopeModelData.addInstruction(EnumFacing.EAST, 1, 0, 1, 0, 16);
    }

    public static CarpentrySlopeModel getInstance() {
        return INSTANCE;
    }

    @Override
    public CarpentryModelData.ModelDataQuads getQuads(BlockCarpentry block, IBlockAccess access, BlockPos pos, TextureAtlasSprite[] sprites, int[] tintIndices) {
        EnumFacing facing = access.getBlockState(pos).getValue(BlockCarpentrySlope.FACING);
        straightSlopeModelData.setTransform(facing, new TRSRTransformation(facing));

        for (int i = 0; i < EnumFacing.values().length; i++) {
            EnumFacing side = EnumFacing.values()[i];
            TextureAtlasSprite sprite = sprites[i];
            int tintIndex = tintIndices[i];

            straightSlopeModelData.setFaceData(side, sprite, tintIndex);
        }

        return straightSlopeModelData.buildModel();
    }

    @Override
    public TextureAtlasSprite getDefaultSprite() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("carpentrycubes:blocks/foursectionframe");
    }
}
