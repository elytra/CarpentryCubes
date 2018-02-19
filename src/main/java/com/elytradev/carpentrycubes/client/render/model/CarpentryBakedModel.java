package com.elytradev.carpentrycubes.client.render.model;

import com.elytradev.carpentrycubes.common.block.BlockCarpentry;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CarpentryBakedModel implements IBakedModel {

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quads = Lists.newArrayList();
        if (state != null && state.getBlock() instanceof BlockCarpentry) {
            BlockCarpentry block = (BlockCarpentry) state.getBlock();
            ICarpentryModel<? extends BlockCarpentry> carpentryModel = block.getModel();
            IExtendedBlockState extendedState = (IExtendedBlockState) state;
            IBlockState coverState = extendedState.getValue(BlockCarpentry.COVERSTATE);
            IBlockAccess access = extendedState.getValue(BlockCarpentry.BLOCK_ACCESS);
            BlockPos pos = extendedState.getValue(BlockCarpentry.POS);
            if (!Objects.equals(block.getRenderLayer(access, pos), MinecraftForgeClient.getRenderLayer()))
                return quads;

            ArrayList<Integer>[] tintIndices = new ArrayList[EnumFacing.values().length];
            ArrayList<TextureAtlasSprite>[] faceSprites = new ArrayList[EnumFacing.values().length];
            ArrayList<Vector3f>[] quadOffsets = new ArrayList[EnumFacing.values().length];
            for (int i = 0; i < tintIndices.length; i++) {
                tintIndices[i] = Lists.newArrayList();
                faceSprites[i] = Lists.newArrayList();
                quadOffsets[i] = Lists.newArrayList();
            }

            IBakedModel modelForState = getBlockModelShapes().getModelForState(coverState);
            gatherQuadData(rand, carpentryModel, coverState, modelForState, tintIndices, faceSprites, quadOffsets);

            // Build the model and return the quads for the side.
            CarpentryModelData.ModelDataQuads modelDataQuads = carpentryModel.getQuads(block, access, pos, tintIndices, faceSprites, quadOffsets);
            return side == null ? modelDataQuads.getGeneralQuads() : modelDataQuads.getFaceQuads().get(side);
        }

        return quads;
    }

    private void gatherQuadData(long rand, ICarpentryModel<? extends BlockCarpentry> carpentryModel, IBlockState coverState, IBakedModel modelForState,
                                ArrayList<Integer>[] tintIndices,
                                ArrayList<TextureAtlasSprite>[] faceSprites,
                                ArrayList<Vector3f>[] quadOffsets) {
        TextureAtlasSprite missingSprite = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();

        for (EnumFacing facing : EnumFacing.values()) {
            // Get the quads for the given face.
            List<BakedQuad> coverStateQuads = modelForState.getQuads(coverState, facing, rand);
            if (coverStateQuads.stream().anyMatch(q -> q.getSprite() != missingSprite)) {
                // Filter out any quads with no sprite.
                coverStateQuads = coverStateQuads.stream().filter(q -> q.getSprite() != missingSprite)
                        .collect(Collectors.toList());

                // Select a quad to use for this face.
                List<BakedQuad> eligibleQuads = findEligibleQuads(coverStateQuads);
                for (int q = 0; q < eligibleQuads.size(); q++) {
                    BakedQuad bakedQuad = eligibleQuads.get(q);
                    addOrSet(faceSprites[facing.getIndex()], q, bakedQuad.getSprite());
                    addOrSet(tintIndices[facing.getIndex()], q, bakedQuad.getTintIndex());
                    addOrSet(quadOffsets[facing.getIndex()], q, new Vector3f());
                }
                if (!eligibleQuads.isEmpty())
                    continue;
            }

            // Fallback with default texture.
            addOrSet(faceSprites[facing.getIndex()], 0, carpentryModel.getDefaultSprite());
            addOrSet(tintIndices[facing.getIndex()], 0, -1);
            addOrSet(quadOffsets[facing.getIndex()], 0, new Vector3f());
        }
    }

    private void addOrSet(ArrayList list, int index, Object element) {
        if (index >= list.size()) {
            list.add(index, element);
        } else {
            list.set(index, element);
        }
    }

    public BlockModelShapes getBlockModelShapes() {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
    }

    public List<BakedQuad> findEligibleQuads(List<BakedQuad> selection) {
        List<BakedQuad> eligibleQuads = Lists.newArrayList();

        for (BakedQuad bakedQuad : selection) {
            QuadEligibilityTester tester = new QuadEligibilityTester(bakedQuad);
            LightUtil.putBakedQuad(tester, bakedQuad);
            if (tester.isValid()) {
                eligibleQuads.add(bakedQuad);
            }
        }
        return eligibleQuads;
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
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("missingno");
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}
