package com.elytradev.carpentrycubes.client.render.model.data;

import com.elytradev.carpentrycubes.client.render.model.ICarpentryModel;
import com.elytradev.carpentrycubes.client.render.model.builder.CarpentryModelData;
import com.elytradev.carpentrycubes.client.render.model.builder.CarpentryTransformData;
import com.elytradev.carpentrycubes.common.CarpentryLog;
import com.elytradev.carpentrycubes.common.block.BlockCarpentry;
import com.elytradev.carpentrycubes.common.block.BlockCarpentryDoor;
import com.elytradev.carpentrycubes.common.tile.TileCarpentry;
import com.elytradev.carpentrycubes.common.tile.TileCarpentryDoor;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CarpentryDoorModel implements ICarpentryModel<BlockCarpentryDoor> {
    private static CarpentryDoorModel INSTANCE = new CarpentryDoorModel();
    private CarpentryModelData fullLowerData;
    private CarpentryModelData fourWindowUpperData;
    private CarpentryModelData slattedLowerData;
    private CarpentryModelData slattedUpperData;

    public static CarpentryDoorModel getInstance() {
        return INSTANCE;
    }

    public void loadModels() {
        this.fullLowerData = new CarpentryModelData(this, this.loadModel(new ResourceLocation("carpentrycubes", "block/door_full_lower")));
        this.fourWindowUpperData = new CarpentryModelData(this, this.loadModel(new ResourceLocation("carpentrycubes", "block/door_fourwindow_upper")));
        this.slattedLowerData = new CarpentryModelData(this, this.loadModel(new ResourceLocation("carpentrycubes", "block/door_slatted_lower")));
        this.slattedUpperData = new CarpentryModelData(this, this.loadModel(new ResourceLocation("carpentrycubes", "block/door_slatted_upper")));
    }

    private IBakedModel loadModel(ResourceLocation resourceLocation) {
        try {
            IModel model = ModelLoaderRegistry.getModel(resourceLocation);
            return model.bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
        } catch (Exception exception) {
            CarpentryLog.error("Failed to load carpentry door models.", exception);
        }
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel();
    }

    public CarpentryModelData getUpperDataFor(BlockCarpentryDoor.EnumDoorStyle style) {
        switch (style) {
            case FOUR_WINDOW:
                return fourWindowUpperData;
            case SOLID:
                return fullLowerData;
            case SLATTED:
                return slattedUpperData;
            default:
                return fullLowerData;
        }
    }

    public CarpentryModelData getLowerDataFor(BlockCarpentryDoor.EnumDoorStyle style) {
        switch (style) {
            case FOUR_WINDOW:
                return fullLowerData;
            case SOLID:
                return fullLowerData;
            case SLATTED:
                return slattedLowerData;
            default:
                return fullLowerData;
        }
    }

    @Override
    public CarpentryModelData.ModelDataQuads getQuads(IBlockState state, IBlockAccess access, BlockPos pos, ArrayList<Integer>[] tintIndices, ArrayList<TextureAtlasSprite>[] faceSprites, int[] quadCount) {
        this.loadModels();
        CarpentryModelData modelData = fullLowerData;
        if (state instanceof IExtendedBlockState) {
            TileCarpentry tile = ((IExtendedBlockState) state).getValue(BlockCarpentry.CARPENTRY_TILE);
            if (tile instanceof TileCarpentryDoor) {
                EnumFacing blockFacing = state.getValue(BlockDoor.FACING);
                CarpentryTransformData transform = getTransform(blockFacing);
                BlockDoor.EnumDoorHalf half = state.getValue(BlockDoor.HALF);
                if (half == BlockDoor.EnumDoorHalf.UPPER) {
                    modelData = getUpperDataFor(((TileCarpentryDoor) tile).getStyle());
                } else {
                    modelData = getLowerDataFor(((TileCarpentryDoor) tile).getStyle());
                }
                modelData.setTransform(blockFacing, transform.asTRSRTransform());
                modelData.setState(state);
                for (int i = 0; i < EnumFacing.values().length; i++) {
                    for (int q = 0; q < quadCount[i]; q++) {
                        EnumFacing side = EnumFacing.values()[i];
                        TextureAtlasSprite sprite = faceSprites[i].get(q);
                        int tintIndex = tintIndices[i].get(q);

                        modelData.setFaceData(q, side, sprite, tintIndex);
                    }
                }
            }
        }

        return modelData.buildModel();
    }

    private CarpentryTransformData getTransform(EnumFacing facing) {
        CarpentryTransformData transformOut = new CarpentryTransformData();
        switch (facing) {
            //case NORTH:
            //    transformOut.setRotation(0, 0, 0);
            //    break;
            //case SOUTH:
            //    transformOut.setRotation(0, 180, 0);
            //    break;
            //case WEST:
            //    transformOut.setRotation(0, 90, 0);
            //    break;
            //case EAST:
            //    transformOut.setRotation(0, 270, 0);
            //    break;
        }
        return transformOut;
    }

    @Override
    public TextureAtlasSprite getDefaultSprite() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("carpentrycubes:blocks/foursectionframe");
    }

    @Override
    public List<BakedQuad> getDefaultModel() {
        return Collections.emptyList();
    }
}
