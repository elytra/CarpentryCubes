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
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CarpentryBakedModel implements IBakedModel {


    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quads = Lists.newArrayList();
        if (state != null && state.getBlock() instanceof BlockCarpentry) {
            BlockCarpentry block = (BlockCarpentry) state.getBlock();
            IExtendedBlockState extendedState = (IExtendedBlockState) state;
            IBlockState coverState = extendedState.getValue(BlockCarpentry.COVERSTATE);
            IBlockAccess access = extendedState.getValue(BlockCarpentry.BLOCK_ACCESS);
            BlockPos pos = extendedState.getValue(BlockCarpentry.POS);
            if (!Objects.equals(block.getRenderLayer(access, pos), MinecraftForgeClient.getRenderLayer()))
                return quads;

            TextureAtlasSprite missingSprite = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
            TextureAtlasSprite[] sprites = new TextureAtlasSprite[EnumFacing.values().length];
            int[] tintIndices = new int[EnumFacing.values().length];
            IBakedModel modelForState = getBlockModelShapes().getModelForState(coverState);
            for (EnumFacing facing : EnumFacing.values()) {
                // Get the quads for the given face.
                List<BakedQuad> coverStateQuads = modelForState.getQuads(coverState, facing, rand);
                if (coverStateQuads.stream().anyMatch(q -> q.getSprite() != missingSprite)) {
                    // Filter out any quads with no sprite.
                    coverStateQuads = coverStateQuads.stream().filter(q -> q.getSprite() != missingSprite)
                            .collect(Collectors.toList());

                    // Select a quad to use for this face.
                    BakedQuad selectedQuad;
                    if (coverStateQuads.stream().allMatch(q -> q.getFace() == null || q.getFace() != facing)) {
                        selectedQuad = coverStateQuads.get(0);
                    } else {
                        selectedQuad = coverStateQuads.stream().filter(q -> q.getFace() == facing).findAny().get();
                    }
                    // Gather the sprite and tint index for the face.
                    sprites[facing.getIndex()] = selectedQuad.getSprite();
                    tintIndices[facing.getIndex()] = selectedQuad.getTintIndex();
                    continue;
                }

                // Fallback with default texture.
                sprites[facing.getIndex()] = block.getModel().getDefaultSprite();
            }

            // Build the model and return the quads for the side.
            CarpentryModelData.ModelDataQuads modelDataQuads = block.getModel().getQuads(block, access, pos, sprites, tintIndices);
            return side == null ? modelDataQuads.getGeneralQuads() : modelDataQuads.getFaceQuads().get(side);
        }

        return quads;
    }

    public BlockModelShapes getBlockModelShapes() {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
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
