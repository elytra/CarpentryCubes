package com.elytradev.carpentrycubes.client.render;

import com.elytradev.carpentrycubes.common.block.BlockCarpentry;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class CarpentersBakedModel implements IBakedModel {

    public static CarpentersModelData slopeModelData;

    static {
        slopeModelData = new CarpentersModelData();

        slopeModelData.addInstruction(EnumFacing.DOWN, 0, 0, 0, true, false);
        slopeModelData.addInstruction(EnumFacing.DOWN, 1, 0, 0, false, false);
        slopeModelData.addInstruction(EnumFacing.DOWN, 1, 0, 1, false, true);
        slopeModelData.addInstruction(EnumFacing.DOWN, 0, 0, 1, true, true);

        slopeModelData.addInstruction(EnumFacing.UP, 0, 0, 0, true, true);
        slopeModelData.addInstruction(EnumFacing.UP, 0, 1, 1, true, false);
        slopeModelData.addInstruction(EnumFacing.UP, 1, 1, 1, false, false);
        slopeModelData.addInstruction(EnumFacing.UP, 1, 0, 0, false, true);

        slopeModelData.addInstruction(EnumFacing.SOUTH, 0, 0, 1, false, false);
        slopeModelData.addInstruction(EnumFacing.SOUTH, 1, 0, 1, true, false);
        slopeModelData.addInstruction(EnumFacing.SOUTH, 1, 1, 1, true, true);
        slopeModelData.addInstruction(EnumFacing.SOUTH, 0, 1, 1, false, true);

        slopeModelData.addInstruction(EnumFacing.WEST, 0, 0, 0, false, false);
        slopeModelData.addInstruction(EnumFacing.WEST, 0, 0, 1, true, false);
        slopeModelData.addInstruction(EnumFacing.WEST, 0, 1, 1, true, true);
        slopeModelData.addInstruction(EnumFacing.WEST, 0, 0.5F, 0.5F, false, true);

        slopeModelData.addInstruction(EnumFacing.EAST, 1, 0, 0, false, false);
        slopeModelData.addInstruction(EnumFacing.EAST, 1, 0.5F, 0.5F, false, true);
        slopeModelData.addInstruction(EnumFacing.EAST, 1, 1, 1, true, true);
        slopeModelData.addInstruction(EnumFacing.EAST, 1, 0, 1, true, false);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quads = Lists.newArrayList();
        if (state == null) return Lists.newArrayList();
        if (state.getBlock() instanceof BlockCarpentry) {
            IExtendedBlockState extendedState = (IExtendedBlockState) state;
            IBlockState coverState = extendedState.getValue(BlockCarpentry.COVERSTATE);
            TextureAtlasSprite[] sprites = new TextureAtlasSprite[EnumFacing.values().length];
            BlockPartFace[] partFaces = new BlockPartFace[EnumFacing.values().length];
            IBakedModel modelForState = getBlockModelShapes().getModelForState(coverState);
            for (EnumFacing facing : EnumFacing.values()) {
                List<BakedQuad> coverStateQuads = modelForState.getQuads(coverState, facing, rand);
                if (coverStateQuads.stream().anyMatch(q -> q.getSprite() != null)) {
                    coverStateQuads = coverStateQuads.stream().filter(q -> q.getSprite() != null)
                            .collect(Collectors.toList());

                    if (!coverStateQuads.isEmpty()) {
                        BakedQuad selectedQuad;
                        if (coverStateQuads.stream().allMatch(q -> q.getFace() == null || q.getFace() != facing)) {
                            selectedQuad = coverStateQuads.get(0);
                        } else {
                            selectedQuad = coverStateQuads.stream().filter(q -> q.getFace() == facing).findAny().get();
                        }
                        sprites[facing.getIndex()] = selectedQuad.getSprite();
                        partFaces[facing.getIndex()] = new BlockPartFace(facing, selectedQuad.getTintIndex(), selectedQuad.getSprite().getIconName(), getBlockFaceUV());
                        continue;
                    }
                }

                sprites[facing.getIndex()] = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("missingno");
            }

            for (int i = 0; i < EnumFacing.values().length; i++) {
                EnumFacing facing = EnumFacing.values()[i];
                slopeModelData.setFaceData(facing, sprites[i], 0);
            }

            CarpentersModelData.ModelDataQuads modelDataQuads = slopeModelData.buildModel();
            return side == null ? modelDataQuads.getGeneralQuads() : modelDataQuads.getFaceQuads().get(side);
        }

        return quads;
    }

    public BlockModelShapes getBlockModelShapes() {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
    }

    public BlockFaceUV getBlockFaceUV() {
        return new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("missingno");

        return null;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}
