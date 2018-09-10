package com.elytradev.carpentrycubes.client.render.model;

import com.elytradev.carpentrycubes.client.render.model.builder.CarpentryModelData;
import com.elytradev.carpentrycubes.client.render.model.builder.CarpentryTransformData;
import com.elytradev.carpentrycubes.client.render.model.builder.ICarpentryModel;
import com.elytradev.carpentrycubes.common.block.BlockCarpentrySlope;
import com.elytradev.carpentrycubes.common.block.BlockCarpentrySlope.EnumOrientation;
import com.elytradev.carpentrycubes.common.block.BlockCarpentrySlope.EnumShape;
import com.elytradev.carpentrycubes.common.tile.TileCarpentrySlope;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.ArrayList;

public class CarpentrySlopeModel implements ICarpentryModel<BlockCarpentrySlope> {

    private static CarpentrySlopeModel INSTANCE;
    private static CarpentryModelData straightSlopeModelData;
    private static CarpentryModelData outterSlopeModelData;
    private static CarpentryModelData innerSlopeModelData;


    static {
        INSTANCE = new CarpentrySlopeModel();

        straightSlopeModelData = new CarpentryModelData(getInstance());

        straightSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 0, 0, 0);
        straightSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 1, 0, 0);
        straightSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 1, 0, 1);
        straightSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 0, 0, 1);

        straightSlopeModelData.addQuadInstruction(EnumFacing.NORTH, 1, 0, 0);
        straightSlopeModelData.addQuadInstruction(EnumFacing.NORTH, 0, 0, 0);
        straightSlopeModelData.addQuadInstruction(EnumFacing.NORTH, 0, 1, 1);
        straightSlopeModelData.addQuadInstruction(EnumFacing.NORTH, 1, 1, 1);

        straightSlopeModelData.addQuadInstruction(EnumFacing.SOUTH, 0, 0, 1);
        straightSlopeModelData.addQuadInstruction(EnumFacing.SOUTH, 1, 0, 1);
        straightSlopeModelData.addQuadInstruction(EnumFacing.SOUTH, 1, 1, 1);
        straightSlopeModelData.addQuadInstruction(EnumFacing.SOUTH, 0, 1, 1);

        straightSlopeModelData.addTriInstruction(EnumFacing.WEST, 0, 0, 0);
        straightSlopeModelData.addTriInstruction(EnumFacing.WEST, 0, 0, 1);
        straightSlopeModelData.addTriInstruction(EnumFacing.WEST, 0, 1, 1);

        straightSlopeModelData.addTriInstruction(EnumFacing.EAST, 1, 0, 0);
        straightSlopeModelData.addTriInstruction(EnumFacing.EAST, 1, 1, 1);
        straightSlopeModelData.addTriInstruction(EnumFacing.EAST, 1, 0, 1);

        outterSlopeModelData = new CarpentryModelData(getInstance());

        outterSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 0, 0, 0);
        outterSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 1, 0, 0);
        outterSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 1, 0, 1);
        outterSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 0, 0, 1);

        outterSlopeModelData.addTriInstruction(EnumFacing.NORTH, 0, 0, 0);
        outterSlopeModelData.addTriInstruction(EnumFacing.NORTH, 1, 1, 1);
        outterSlopeModelData.addTriInstruction(EnumFacing.NORTH, 1, 0, 0);

        outterSlopeModelData.addTriInstruction(EnumFacing.SOUTH, 0, 0, 1);
        outterSlopeModelData.addTriInstruction(EnumFacing.SOUTH, 1, 0, 1);
        outterSlopeModelData.addTriInstruction(EnumFacing.SOUTH, 1, 1, 1);

        outterSlopeModelData.addTriInstruction(EnumFacing.WEST, 0, 0, 0);
        outterSlopeModelData.addTriInstruction(EnumFacing.WEST, 0, 0, 1);
        outterSlopeModelData.addTriInstruction(EnumFacing.WEST, 1, 1, 1);

        outterSlopeModelData.addTriInstruction(EnumFacing.EAST, 1, 0, 0);
        outterSlopeModelData.addTriInstruction(EnumFacing.EAST, 1, 1, 1);
        outterSlopeModelData.addTriInstruction(EnumFacing.EAST, 1, 0, 1);

        innerSlopeModelData = new CarpentryModelData(getInstance());

        innerSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 0, 0, 0);
        innerSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 1, 0, 0);
        innerSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 1, 0, 1);
        innerSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 0, 0, 1);

        innerSlopeModelData.addQuadInstruction(EnumFacing.SOUTH, 0, 0, 1);
        innerSlopeModelData.addQuadInstruction(EnumFacing.SOUTH, 1, 0, 1);
        innerSlopeModelData.addQuadInstruction(EnumFacing.SOUTH, 1, 1, 1);
        innerSlopeModelData.addQuadInstruction(EnumFacing.SOUTH, 0, 1, 1);

        innerSlopeModelData.addQuadInstruction(EnumFacing.EAST, 1, 0, 0);
        innerSlopeModelData.addQuadInstruction(EnumFacing.EAST, 1, 1, 0);
        innerSlopeModelData.addQuadInstruction(EnumFacing.EAST, 1, 1, 1);
        innerSlopeModelData.addQuadInstruction(EnumFacing.EAST, 1, 0, 1);

        innerSlopeModelData.addTriInstruction(EnumFacing.NORTH, 0, 0, 0);
        innerSlopeModelData.addTriInstruction(EnumFacing.NORTH, 0, 1, 1);
        innerSlopeModelData.addTriInstruction(EnumFacing.NORTH, 1, 1, 1);

        innerSlopeModelData.addTriInstruction(EnumFacing.NORTH, 0, 0, 0);
        innerSlopeModelData.addTriInstruction(EnumFacing.NORTH, 1, 1, 0);
        innerSlopeModelData.addTriInstruction(EnumFacing.NORTH, 1, 0, 0);

        innerSlopeModelData.addTriInstruction(EnumFacing.WEST, 0, 0, 0);
        innerSlopeModelData.addTriInstruction(EnumFacing.WEST, 1, 1, 1);
        innerSlopeModelData.addTriInstruction(EnumFacing.WEST, 1, 1, 0);

        innerSlopeModelData.addTriInstruction(EnumFacing.WEST, 0, 0, 0);
        innerSlopeModelData.addTriInstruction(EnumFacing.WEST, 0, 0, 1);
        innerSlopeModelData.addTriInstruction(EnumFacing.WEST, 0, 1, 1);
    }

    public static CarpentrySlopeModel getInstance() {
        return INSTANCE;
    }

    @Override
    public CarpentryModelData.ModelDataQuads getQuads(IBlockState state, IBlockAccess access, BlockPos pos,
                                                      ArrayList<Integer>[] tintIndices, ArrayList<TextureAtlasSprite>[] faceSprites,
                                                      int[] quadCount) {
        EnumFacing facing = state.getValue(BlockCarpentrySlope.PRIMARY_DIRECTION);
        EnumOrientation orientation = state instanceof IExtendedBlockState ?
                ((IExtendedBlockState) state).getValue(BlockCarpentrySlope.ORIENTATION)
                : EnumOrientation.GROUND;
        CarpentryModelData modelData = getModelData(state);
        TRSRTransformation secondaryTransform = getSecondaryTransform(access, pos, orientation);

        int xRot = 0;
        int yRot = 0;
        int zRot = 0;
        switch (facing) {
            case NORTH:
                yRot = 0;
                break;
            case SOUTH:
                yRot = 180;
                break;
            case WEST:
                yRot = 90;
                break;
            case EAST:
                yRot = -90;
                break;
        }
        if (orientation == EnumOrientation.CEILING) {
            zRot = 180;
            TileEntity tile = access.getTileEntity(pos);
            if (tile instanceof TileCarpentrySlope) {
                TileCarpentrySlope slope = (TileCarpentrySlope) tile;
                if (slope.getShape() == EnumShape.INNER) {
                    yRot -= 90;
                } else if (slope.getShape() == EnumShape.OUTER) {
                    yRot += 90;
                }
            }
        } else if (orientation == EnumOrientation.WALL) {
            // Most of the rotation is handled in the secondaryTransform.
            yRot -= 90;
        }

        TRSRTransformation transform = new CarpentryTransformData().setRotation(xRot, yRot, zRot).asTRSRTransform().compose(secondaryTransform);
        modelData.setTransform(facing, transform);
        modelData.setState(state);
        for (int i = 0; i < EnumFacing.values().length; i++) {
            for (int q = 0; q < quadCount[i]; q++) {
                EnumFacing side = EnumFacing.values()[i];
                TextureAtlasSprite sprite = faceSprites[i].get(q);
                int tintIndex = tintIndices[i].get(q);

                modelData.setFaceData(q, side, sprite, tintIndex);
            }
        }

        return modelData.buildModel();
    }

    private TRSRTransformation getSecondaryTransform(IBlockAccess access, BlockPos pos, EnumOrientation orientation) {
        TRSRTransformation secondaryTransform;
        int xRot = 0;
        int yRot = 0;
        int zRot = 0;

        if (orientation == EnumOrientation.WALL) {
            zRot += 90;

            TileEntity tile = access.getTileEntity(pos);
            if (tile instanceof TileCarpentrySlope) {
                TileCarpentrySlope slope = (TileCarpentrySlope) tile;
                if (slope.getShape() != EnumShape.STRAIGHT) {
                    switch (slope.getSecondaryDirection()) {
                        case NORTH:
                            xRot += 0;
                            break;
                        case SOUTH:
                            xRot += 180;
                            break;
                        case WEST:
                            xRot += 90;
                            break;
                        case EAST:
                            xRot += -90;
                            break;
                    }
                }
            }
        }

        secondaryTransform = new CarpentryTransformData().setRotation(xRot, yRot, zRot).asTRSRTransform();
        return secondaryTransform;
    }

    private CarpentryModelData getModelData(IBlockState state) {
        if (state instanceof IExtendedBlockState) {
            EnumShape shape = ((IExtendedBlockState) state).getValue(BlockCarpentrySlope.SHAPE);
            switch (shape) {
                case STRAIGHT:
                    return straightSlopeModelData;
                case INNER:
                    return innerSlopeModelData;
                case OUTER:
                    return outterSlopeModelData;
            }
        }
        return straightSlopeModelData;
    }

    @Override
    public TextureAtlasSprite getDefaultSprite() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("carpentrycubes:blocks/foursectionframe");
    }
}
