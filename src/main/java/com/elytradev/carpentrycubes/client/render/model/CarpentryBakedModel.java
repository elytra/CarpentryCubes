package com.elytradev.carpentrycubes.client.render.model;

import com.elytradev.carpentrycubes.client.render.model.ICarpentryModel;
import com.elytradev.carpentrycubes.client.render.model.builder.CarpentryModelData;
import com.elytradev.carpentrycubes.client.render.model.builder.QuadEligibilityTester;
import com.elytradev.carpentrycubes.common.block.BlockCarpentry;
import com.elytradev.carpentrycubes.common.tile.TileCarpentry;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CarpentryBakedModel implements IBakedModel {

    private final ICarpentryModel defaultModel;
    private final ItemCameraTransforms transforms;

    public CarpentryBakedModel(ICarpentryModel defaultModel) {
        ItemTransformVec3f gui = new ItemTransformVec3f(new Vector3f(30F, 90+45F, 0F),
                new Vector3f(0F, 0F, 0F),
                new Vector3f(0.625F, 0.625F, 0.625F));

        ItemTransformVec3f ground = new ItemTransformVec3f(new Vector3f(0F, 0F, 0F),
                new Vector3f(0F, 0.1875F, 0F),
                new Vector3f(0.25F, 0.25F, 0.25F));

        ItemTransformVec3f fixed = new ItemTransformVec3f(new Vector3f(0F, 0F, 0F),
                new Vector3f(0F, 0F, 0F),
                new Vector3f(0.5F, 0.5F, 0.5F));

        ItemTransformVec3f thirdpersonLeft = new ItemTransformVec3f(new Vector3f(75F, 45F, 0F),
                new Vector3f(0F, 0.15625F, 0F),
                new Vector3f(0.375F, 0.375F, 0.375F));

        ItemTransformVec3f thirdpersonRight = new ItemTransformVec3f(new Vector3f(75F, 45F, 0F),
                new Vector3f(0F, 0.15625F, 0F),
                new Vector3f(0.375F, 0.375F, 0.375F));

        ItemTransformVec3f firstpersonLeft = new ItemTransformVec3f(new Vector3f(0F, 225F, 0F),
                new Vector3f(0F, 0F, 0F),
                new Vector3f(0.4F, 0.4F, 0.4F));

        ItemTransformVec3f firstpersonRight = new ItemTransformVec3f(new Vector3f(0F, 45F, 0F),
                new Vector3f(0F, 0F, 0F),
                new Vector3f(0.4F, 0.4F, 0.4F));

        this.defaultModel = defaultModel;
        this.transforms = new ItemCameraTransforms(thirdpersonLeft, thirdpersonRight, firstpersonLeft, firstpersonRight, ItemTransformVec3f.DEFAULT, gui, ground, fixed);
    }

    public CarpentryBakedModel(ICarpentryModel defaultModel, ItemCameraTransforms transforms) {
        this.defaultModel = defaultModel;
        this.transforms = transforms;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (state != null && state.getBlock() instanceof BlockCarpentry) {
            BlockCarpentry block = (BlockCarpentry) state.getBlock();
            ICarpentryModel<? extends BlockCarpentry> carpentryModel = block.getModel();
            IExtendedBlockState extendedState = (IExtendedBlockState) state;
            TileCarpentry tile = extendedState.getValue(BlockCarpentry.CARPENTRY_TILE);
            IBlockState coverState = tile.getCoverState();
            IBlockAccess access = tile.getWorld();
            BlockPos pos = tile.getPos();
            ArrayList<Integer>[] tintIndices = new ArrayList[EnumFacing.values().length];
            ArrayList<TextureAtlasSprite>[] faceSprites = new ArrayList[EnumFacing.values().length];
            int[] quadCount = new int[EnumFacing.values().length];
            for (int i = 0; i < tintIndices.length; i++) {
                tintIndices[i] = Lists.newArrayList();
                faceSprites[i] = Lists.newArrayList();
                quadCount[i] = 0;
            }

            IBakedModel modelForState = getBlockModelShapes().getModelForState(coverState);
            gatherQuadData(rand, carpentryModel, coverState, modelForState, tintIndices, faceSprites, quadCount);

            // Build the model and return the quads for the side.
            CarpentryModelData.ModelDataQuads modelDataQuads = carpentryModel.getQuads(state, access, pos, tintIndices, faceSprites, quadCount);
            return side == null ? modelDataQuads.getGeneralQuads() : modelDataQuads.getFaceQuads().get(side);
        } else {
            return this.defaultModel.getDefaultModel();
        }
    }

    private void gatherQuadData(long rand, ICarpentryModel<? extends BlockCarpentry> carpentryModel,
                                IBlockState coverState, IBakedModel modelForState,
                                ArrayList<Integer>[] tintIndices,
                                ArrayList<TextureAtlasSprite>[] faceSprites,
                                int[] quadCount) {
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
                    quadCount[facing.getIndex()]++;
                }
                if (!eligibleQuads.isEmpty())
                    continue;
            }

            // Fallback with default texture.
            addOrSet(faceSprites[facing.getIndex()], 0, carpentryModel.getDefaultSprite());
            addOrSet(tintIndices[facing.getIndex()], 0, -1);
            quadCount[facing.getIndex()]++;
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
            this.putQuadInTester(tester, bakedQuad);
            if (tester.isValid()) {
                eligibleQuads.add(bakedQuad);
            }
        }
        return eligibleQuads;
    }

    private void putQuadInTester(QuadEligibilityTester tester, BakedQuad quad) {
        float[] data = new float[4];
        VertexFormat format = quad.getFormat();

        for (int v = 0; v < 4; v++) {
            for (VertexFormatElement element : format.getElements()) {
                if (Objects.equals(element.getUsage(), VertexFormatElement.EnumUsage.POSITION)
                        && Objects.equals(element.getType(), VertexFormatElement.EnumType.FLOAT)) {
                    unpackVertexData(quad.getVertexData(), data, element, quad.getFormat(), v, element.getIndex());
                    tester.put(data);
                }
            }
        }
    }

    private void unpackVertexData(int[] from, float[] to, VertexFormatElement element, VertexFormat formatFrom, int v, int e) {
        int length = 4 < to.length ? 4 : to.length;
        int vertexStart = v * formatFrom.getSize() + formatFrom.getOffset(e);
        int count = element.getElementCount();
        int size = element.getType().getSize();
        int mask = (256 << (8 * (size - 1))) - 1;
        for (int i = 0; i < length; i++) {
            if (i < count) {
                int pos = vertexStart + size * i;
                int index = pos >> 2;
                int offset = pos & 3;
                int bits = from[index];
                bits = bits >>> (offset * 8);
                if ((pos + size - 1) / 4 != index) {
                    bits |= from[index + 1] << ((4 - offset) * 8);
                }
                bits &= mask;
                to[i] = Float.intBitsToFloat(bits);
            } else {
                to[i] = 0;
            }
        }
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
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("carpentrycubes:blocks/foursectionframe");
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return transforms;
    }
}
