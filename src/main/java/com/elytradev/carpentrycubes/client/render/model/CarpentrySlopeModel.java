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

import javax.vecmath.Vector3f;
import java.util.ArrayList;

public class CarpentrySlopeModel implements ICarpentryModel<BlockCarpentrySlope> {

    private static CarpentrySlopeModel INSTANCE;
    private static CarpentryModelData straightSlopeModelData;
    private static CarpentryModelData outterSlopeModelData;
    private static CarpentryModelData innerSlopeModelData;


    static {
        INSTANCE = new CarpentrySlopeModel();

        straightSlopeModelData = new CarpentryModelData(getInstance());

        straightSlopeModelData.addInstruction(EnumFacing.DOWN, 0, 0, 0, 0, 16);
        straightSlopeModelData.addInstruction(EnumFacing.DOWN, 1, 0, 0, 16, 16);
        straightSlopeModelData.addInstruction(EnumFacing.DOWN, 1, 0, 1, 16, 0);
        straightSlopeModelData.addInstruction(EnumFacing.DOWN, 0, 0, 1, 0, 0);

        straightSlopeModelData.addInstruction(EnumFacing.UP, 1, 0, 0, 0, 16);
        straightSlopeModelData.addInstruction(EnumFacing.UP, 0, 0, 0, 16, 16);
        straightSlopeModelData.addInstruction(EnumFacing.UP, 0, 1, 1, 16, 0);
        straightSlopeModelData.addInstruction(EnumFacing.UP, 1, 1, 1, 0, 0);

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

        outterSlopeModelData = new CarpentryModelData(getInstance());

        outterSlopeModelData.addInstruction(EnumFacing.DOWN, 0, 0, 0, 0, 16);
        outterSlopeModelData.addInstruction(EnumFacing.DOWN, 1, 0, 0, 16, 16);
        outterSlopeModelData.addInstruction(EnumFacing.DOWN, 1, 0, 1, 16, 0);
        outterSlopeModelData.addInstruction(EnumFacing.DOWN, 0, 0, 1, 0, 0);

        outterSlopeModelData.addInstruction(EnumFacing.NORTH, 0, 0, 0, 16, 16);
        outterSlopeModelData.addInstruction(EnumFacing.NORTH, 0, 0, 0, 16, 16);
        outterSlopeModelData.addInstruction(EnumFacing.NORTH, 1, 1, 1, 0, 0);
        outterSlopeModelData.addInstruction(EnumFacing.NORTH, 1, 0, 0, 0, 16);

        outterSlopeModelData.addInstruction(EnumFacing.SOUTH, 0, 0, 1, 0, 16);
        outterSlopeModelData.addInstruction(EnumFacing.SOUTH, 0, 0, 1, 0, 16);
        outterSlopeModelData.addInstruction(EnumFacing.SOUTH, 1, 0, 1, 16, 16);
        outterSlopeModelData.addInstruction(EnumFacing.SOUTH, 1, 1, 1, 16, 0);

        outterSlopeModelData.addInstruction(EnumFacing.WEST, 0, 0, 0, 0, 16);
        outterSlopeModelData.addInstruction(EnumFacing.WEST, 0, 0, 0, 0, 16);
        outterSlopeModelData.addInstruction(EnumFacing.WEST, 0, 0, 1, 16, 16);
        outterSlopeModelData.addInstruction(EnumFacing.WEST, 1, 1, 1, 16, 0);

        outterSlopeModelData.addInstruction(EnumFacing.EAST, 1, 0, 0, 16, 16);
        outterSlopeModelData.addInstruction(EnumFacing.EAST, 1, 0, 0, 16, 0);
        outterSlopeModelData.addInstruction(EnumFacing.EAST, 1, 1, 1, 0, 0);
        outterSlopeModelData.addInstruction(EnumFacing.EAST, 1, 0, 1, 0, 16);

        innerSlopeModelData = new CarpentryModelData(getInstance());

        innerSlopeModelData.addInstruction(EnumFacing.DOWN, 0, 0, 0, 0, 16);
        innerSlopeModelData.addInstruction(EnumFacing.DOWN, 1, 0, 0, 16, 16);
        innerSlopeModelData.addInstruction(EnumFacing.DOWN, 1, 0, 1, 16, 0);
        innerSlopeModelData.addInstruction(EnumFacing.DOWN, 0, 0, 1, 0, 0);

        innerSlopeModelData.addInstruction(EnumFacing.NORTH, 0, 0, 0, 16, 16);
        innerSlopeModelData.addInstruction(EnumFacing.NORTH, 0, 0, 0, 16, 16);
        innerSlopeModelData.addInstruction(EnumFacing.NORTH, 0, 1, 1, 16, 0);
        innerSlopeModelData.addInstruction(EnumFacing.NORTH, 1, 1, 1, 0, 0);

        innerSlopeModelData.addInstruction(EnumFacing.NORTH, 0, 0, 0, 16, 16);
        innerSlopeModelData.addInstruction(EnumFacing.NORTH, 0, 0, 0, 16, 16);
        innerSlopeModelData.addInstruction(EnumFacing.NORTH, 1, 1, 0, 0, 0);
        innerSlopeModelData.addInstruction(EnumFacing.NORTH, 1, 0, 0, 0, 16);

        innerSlopeModelData.addInstruction(EnumFacing.SOUTH, 0, 0, 1, 0, 16);
        innerSlopeModelData.addInstruction(EnumFacing.SOUTH, 1, 0, 1, 16, 16);
        innerSlopeModelData.addInstruction(EnumFacing.SOUTH, 1, 1, 1, 16, 0);
        innerSlopeModelData.addInstruction(EnumFacing.SOUTH, 0, 1, 1, 0, 0);

        innerSlopeModelData.addInstruction(EnumFacing.WEST, 0, 0, 0, 0, 16);
        innerSlopeModelData.addInstruction(EnumFacing.WEST, 0, 0, 0, 0, 16);
        innerSlopeModelData.addInstruction(EnumFacing.WEST, 1, 1, 1, 16, 0);
        innerSlopeModelData.addInstruction(EnumFacing.WEST, 1, 1, 0, 0, 0);

        innerSlopeModelData.addInstruction(EnumFacing.WEST, 0, 0, 0, 0, 16);
        innerSlopeModelData.addInstruction(EnumFacing.WEST, 0, 0, 0, 0, 16);
        innerSlopeModelData.addInstruction(EnumFacing.WEST, 0, 0, 1, 16, 16);
        innerSlopeModelData.addInstruction(EnumFacing.WEST, 0, 1, 1, 16, 0);

        innerSlopeModelData.addInstruction(EnumFacing.EAST, 1, 0, 0, 16, 16);
        innerSlopeModelData.addInstruction(EnumFacing.EAST, 1, 1, 0, 16, 0);
        innerSlopeModelData.addInstruction(EnumFacing.EAST, 1, 1, 1, 0, 0);
        innerSlopeModelData.addInstruction(EnumFacing.EAST, 1, 0, 1, 0, 16);
    }

    public static CarpentrySlopeModel getInstance() {
        return INSTANCE;
    }

    @Override
    public CarpentryModelData.ModelDataQuads getQuads(IBlockState state, IBlockAccess access, BlockPos pos,
                                                      ArrayList<Integer>[] tintIndices, ArrayList<TextureAtlasSprite>[] faceSprites,
                                                      ArrayList<Vector3f>[] quadOffsets) {
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
            for (int q = 0; q < quadOffsets[i].size(); q++) {
                EnumFacing side = EnumFacing.values()[i];
                TextureAtlasSprite sprite = faceSprites[i].get(q);
                int tintIndex = tintIndices[i].get(q);
                Vector3f quadOffset = quadOffsets[i].get(q);

                modelData.setFaceData(q, side, sprite, tintIndex, quadOffset);
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
    public float[] getUVs(EnumFacing oldFace, EnumFacing facing,
                          IBlockState state, float oU, float oV) {
        EnumOrientation orientation = EnumOrientation.GROUND;
        EnumShape shape = EnumShape.STRAIGHT;

        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState extendedState = (IExtendedBlockState) state;
            orientation = extendedState.getValue(BlockCarpentrySlope.ORIENTATION);
            shape = extendedState.getValue(BlockCarpentrySlope.SHAPE);
        }

        Boolean onCeiling = orientation == EnumOrientation.CEILING;
        Boolean onWall = orientation == EnumOrientation.WALL;
        if (oldFace == EnumFacing.DOWN || orientation != EnumOrientation.GROUND) {
            float angle = orientation.getZRotation();
            if (oldFace == EnumFacing.DOWN) {
                if (onCeiling) {
                    angle += 180;
                    if (shape == EnumShape.INNER) {
                        angle += 90;
                    } else if (shape == EnumShape.OUTER) {
                        angle -= 90;
                    }

                    angle += facing.getHorizontalAngle();
                } else if (!onWall) {
                    angle += -facing.getOpposite().getHorizontalAngle();
                }
            }

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
