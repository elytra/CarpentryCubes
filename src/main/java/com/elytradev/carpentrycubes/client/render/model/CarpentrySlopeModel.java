package com.elytradev.carpentrycubes.client.render.model;

import com.elytradev.carpentrycubes.common.block.BlockCarpentry;
import com.elytradev.carpentrycubes.common.block.BlockCarpentrySlope;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.vecmath.Vector3f;
import java.util.ArrayList;

public class CarpentrySlopeModel implements ICarpentryModel<BlockCarpentrySlope> {
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
    public CarpentryModelData.ModelDataQuads getQuads(BlockCarpentry block, IBlockAccess access, BlockPos pos,
                                                      ArrayList<Integer>[] tintIndices,
                                                      ArrayList<TextureAtlasSprite>[] faceSprites,
                                                      ArrayList<Vector3f>[] quadOffsets) {
        EnumFacing facing = access.getBlockState(pos).getValue(BlockCarpentrySlope.FACING);
        straightSlopeModelData.setTransform(new TRSRTransformation(facing));
        for (int i = 0; i < EnumFacing.values().length; i++) {
            for (int q = 0; q < quadOffsets[i].size(); q++) {
                EnumFacing side = EnumFacing.values()[i];
                TextureAtlasSprite sprite = faceSprites[i].get(q);
                int tintIndex = tintIndices[i].get(q);
                Vector3f quadOffset = quadOffsets[i].get(q);

                straightSlopeModelData.setFaceData(q, side, sprite, tintIndex, quadOffset);
            }
        }

        return straightSlopeModelData.buildModel();
    }

    @Override
    public TextureAtlasSprite getDefaultSprite() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("carpentrycubes:blocks/foursectionframe");
    }
}
