package com.elytradev.carpentrycubes.client.render.model.data;

import com.elytradev.carpentrycubes.client.render.model.ICarpentryModel;
import com.elytradev.carpentrycubes.client.render.model.builder.CarpentryModelData;
import com.elytradev.carpentrycubes.client.render.model.builder.CarpentryTransformData;
import com.elytradev.carpentrycubes.common.block.BlockCarpentrySlope;
import com.elytradev.carpentrycubes.common.block.BlockCarpentrySlope.EnumOrientation;
import com.elytradev.carpentrycubes.common.block.BlockCarpentrySlope.EnumShape;
import com.elytradev.carpentrycubes.common.tile.TileCarpentrySlope;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.ArrayList;
import java.util.List;

public class CarpentrySlopeModel implements ICarpentryModel<BlockCarpentrySlope> {

    private static CarpentrySlopeModel INSTANCE = new CarpentrySlopeModel();
    private CarpentryModelData straightSlopeModelData;
    private CarpentryModelData outerSlopeModelData;
    private CarpentryModelData innerSlopeModelData;

    private CarpentrySlopeModel() {
        this.straightSlopeModelData = new CarpentryModelData(getInstance());

        this.straightSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 0, 0, 0);
        this.straightSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 1, 0, 0);
        this.straightSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 1, 0, 1);
        this.straightSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 0, 0, 1);

        this.straightSlopeModelData.addQuadInstruction(EnumFacing.NORTH, 1, 0, 0);
        this.straightSlopeModelData.addQuadInstruction(EnumFacing.NORTH, 0, 0, 0);
        this.straightSlopeModelData.addQuadInstruction(EnumFacing.NORTH, 0, 1, 1);
        this.straightSlopeModelData.addQuadInstruction(EnumFacing.NORTH, 1, 1, 1);

        this.straightSlopeModelData.addQuadInstruction(EnumFacing.SOUTH, 0, 0, 1);
        this.straightSlopeModelData.addQuadInstruction(EnumFacing.SOUTH, 1, 0, 1);
        this.straightSlopeModelData.addQuadInstruction(EnumFacing.SOUTH, 1, 1, 1);
        this.straightSlopeModelData.addQuadInstruction(EnumFacing.SOUTH, 0, 1, 1);

        this.straightSlopeModelData.addTriInstruction(EnumFacing.WEST, 0, 0, 0);
        this.straightSlopeModelData.addTriInstruction(EnumFacing.WEST, 0, 0, 1);
        this.straightSlopeModelData.addTriInstruction(EnumFacing.WEST, 0, 1, 1);

        this.straightSlopeModelData.addTriInstruction(EnumFacing.EAST, 1, 0, 0);
        this.straightSlopeModelData.addTriInstruction(EnumFacing.EAST, 1, 1, 1);
        this.straightSlopeModelData.addTriInstruction(EnumFacing.EAST, 1, 0, 1);

        this.outerSlopeModelData = new CarpentryModelData(getInstance());

        this.outerSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 0, 0, 0);
        this.outerSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 1, 0, 0);
        this.outerSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 1, 0, 1);
        this.outerSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 0, 0, 1);

        this.outerSlopeModelData.addTriInstruction(EnumFacing.NORTH, 0, 0, 0);
        this.outerSlopeModelData.addTriInstruction(EnumFacing.NORTH, 1, 1, 1);
        this.outerSlopeModelData.addTriInstruction(EnumFacing.NORTH, 1, 0, 0);

        this.outerSlopeModelData.addTriInstruction(EnumFacing.SOUTH, 0, 0, 1);
        this.outerSlopeModelData.addTriInstruction(EnumFacing.SOUTH, 1, 0, 1);
        this.outerSlopeModelData.addTriInstruction(EnumFacing.SOUTH, 1, 1, 1);

        this.outerSlopeModelData.addTriInstruction(EnumFacing.WEST, 0, 0, 0);
        this.outerSlopeModelData.addTriInstruction(EnumFacing.WEST, 0, 0, 1);
        this.outerSlopeModelData.addTriInstruction(EnumFacing.WEST, 1, 1, 1);

        this.outerSlopeModelData.addTriInstruction(EnumFacing.EAST, 1, 0, 0);
        this.outerSlopeModelData.addTriInstruction(EnumFacing.EAST, 1, 1, 1);
        this.outerSlopeModelData.addTriInstruction(EnumFacing.EAST, 1, 0, 1);

        this.innerSlopeModelData = new CarpentryModelData(getInstance());

        this.innerSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 0, 0, 0);
        this.innerSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 1, 0, 0);
        this.innerSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 1, 0, 1);
        this.innerSlopeModelData.addQuadInstruction(EnumFacing.DOWN, 0, 0, 1);

        this.innerSlopeModelData.addQuadInstruction(EnumFacing.SOUTH, 0, 0, 1);
        this.innerSlopeModelData.addQuadInstruction(EnumFacing.SOUTH, 1, 0, 1);
        this.innerSlopeModelData.addQuadInstruction(EnumFacing.SOUTH, 1, 1, 1);
        this.innerSlopeModelData.addQuadInstruction(EnumFacing.SOUTH, 0, 1, 1);

        this.innerSlopeModelData.addQuadInstruction(EnumFacing.EAST, 1, 0, 0);
        this.innerSlopeModelData.addQuadInstruction(EnumFacing.EAST, 1, 1, 0);
        this.innerSlopeModelData.addQuadInstruction(EnumFacing.EAST, 1, 1, 1);
        this.innerSlopeModelData.addQuadInstruction(EnumFacing.EAST, 1, 0, 1);

        this.innerSlopeModelData.addTriInstruction(EnumFacing.NORTH, 0, 0, 0);
        this.innerSlopeModelData.addTriInstruction(EnumFacing.NORTH, 0, 1, 1);
        this.innerSlopeModelData.addTriInstruction(EnumFacing.NORTH, 1, 1, 1);

        this.innerSlopeModelData.addTriInstruction(EnumFacing.NORTH, 0, 0, 0);
        this.innerSlopeModelData.addTriInstruction(EnumFacing.NORTH, 1, 1, 0);
        this.innerSlopeModelData.addTriInstruction(EnumFacing.NORTH, 1, 0, 0);

        this.innerSlopeModelData.addTriInstruction(EnumFacing.WEST, 0, 0, 0);
        this.innerSlopeModelData.addTriInstruction(EnumFacing.WEST, 1, 1, 1);
        this.innerSlopeModelData.addTriInstruction(EnumFacing.WEST, 1, 1, 0);

        this.innerSlopeModelData.addTriInstruction(EnumFacing.WEST, 0, 0, 0);
        this.innerSlopeModelData.addTriInstruction(EnumFacing.WEST, 0, 0, 1);
        this.innerSlopeModelData.addTriInstruction(EnumFacing.WEST, 0, 1, 1);
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
                    return this.straightSlopeModelData;
                case INNER:
                    return this.innerSlopeModelData;
                case OUTER:
                    return this.outerSlopeModelData;
            }
        }
        return this.straightSlopeModelData;
    }

    @Override
    public TextureAtlasSprite getDefaultSprite() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("carpentrycubes:blocks/foursectionframe");
    }

    @Override
    public List<BakedQuad> getDefaultModel() {
        CarpentryModelData modelData = this.straightSlopeModelData;
        modelData.setState(null);
        modelData.setTransform(EnumFacing.UP, TRSRTransformation.identity());
        for (int i = 0; i < EnumFacing.values().length; i++) {
            EnumFacing side = EnumFacing.values()[i];
            TextureAtlasSprite sprite = getDefaultSprite();
            int tintIndex = -1;

            modelData.setFaceData(0, side, sprite, tintIndex);
        }

        return modelData.buildModel().getGeneralQuads();
    }
}
