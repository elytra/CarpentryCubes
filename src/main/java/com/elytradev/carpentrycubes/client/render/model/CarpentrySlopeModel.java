package com.elytradev.carpentrycubes.client.render.model;

import com.elytradev.carpentrycubes.common.block.BlockCarpentry;
import com.elytradev.carpentrycubes.common.block.BlockCarpentrySlope;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelRotation;
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
        straightSlopeModelData = new CarpentryModelData(getInstance());

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
        straightSlopeModelData.addInstruction(EnumFacing.WEST, 0, 0, 0, 0, 16);
        straightSlopeModelData.addInstruction(EnumFacing.WEST, 0, 0, 1, 16, 16);
        straightSlopeModelData.addInstruction(EnumFacing.WEST, 0, 1, 1, 16, 0);

        straightSlopeModelData.addInstruction(EnumFacing.EAST, 1, 0, 0, 16, 16);
        straightSlopeModelData.addInstruction(EnumFacing.EAST, 1, 0, 0, 16, 0);
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
        IBlockState state = access.getBlockState(pos);
        EnumFacing facing = state.getValue(BlockCarpentrySlope.FACING);
        TRSRTransformation transform;

        int xRot = 0;
        int yRot = 0;
        switch (facing) {
            case NORTH:
                yRot = 0;
                break;
            case SOUTH:
                yRot = 180;
                break;
            case WEST:
                yRot = 270;
                break;
            case EAST:
                yRot = 90;
                break;
        }
        if (state.getValue(BlockCarpentrySlope.CEILING)) {
            yRot += 180;
            xRot = 180;
        }
        transform = new TRSRTransformation(ModelRotation.getModelRotation(xRot, yRot));

        straightSlopeModelData.setTransform(facing, transform);
        straightSlopeModelData.setState(state);
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
    public float[] getUVs(EnumFacing oldFace, EnumFacing newFace, EnumFacing facing, IBlockState state, float oU, float oV) {
        if (oldFace == EnumFacing.DOWN) {
            Boolean ceiling = state.getValue(BlockCarpentrySlope.CEILING);
            float angle = ceiling ? facing.getHorizontalAngle() : -facing.getOpposite().getHorizontalAngle();
            double uPoint = (oU / 16) - 0.5;
            double vPoint = (oV / 16) - 0.5;
            double cos = Math.cos(Math.toRadians(angle));
            double sin = Math.sin(Math.toRadians(angle));
            float u = (float) (((cos * uPoint) - (sin * vPoint)) + 0.5F) * 16F;
            float v = (float) (((sin * uPoint) + (cos * vPoint)) + 0.5F) * 16F;
            return new float[]{u, v};
        }

        return new float[]{oU, oV};
    }

    @Override
    public TextureAtlasSprite getDefaultSprite() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("carpentrycubes:blocks/foursectionframe");
    }
}
