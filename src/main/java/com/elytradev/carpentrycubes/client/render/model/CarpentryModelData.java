package com.elytradev.carpentrycubes.client.render.model;

import com.elytradev.carpentrycubes.client.render.QuadBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Stores quad info that can be modified with given transforms, tintindices, and face sprites.
 */
public class CarpentryModelData {

    private final Map<EnumFacing, float[][][]> masterData = Arrays.stream(EnumFacing.values()).collect(Collectors.toMap(e -> e, e -> new float[0][0][0]));

    private TRSRTransformation transform = TRSRTransformation.identity();
    private EnumFacing rotation = EnumFacing.NORTH;
    private int[] tintIndices = new int[EnumFacing.values().length];
    private TextureAtlasSprite[] faceSprites = new TextureAtlasSprite[EnumFacing.values().length];

    public void setFaceData(EnumFacing side, TextureAtlasSprite sprite, int tintIndex) {
        this.faceSprites[side.getIndex()] = sprite;
        this.tintIndices[side.getIndex()] = tintIndex;
    }

    public void setTransform(EnumFacing rotation, TRSRTransformation transform) {
        this.rotation = rotation;
        this.transform = transform;
    }

    public ModelDataQuads buildModel() {
        List<BakedQuad> generalQuads = Lists.newArrayList();
        Map<EnumFacing, List<BakedQuad>> faceQuads = Maps.newHashMap();

        Map<EnumFacing, float[][][]> transformedMasterData = Maps.newHashMap();

        for (Map.Entry<EnumFacing, float[][][]> entry : masterData.entrySet()) {
            EnumFacing newFace = transform.rotate(entry.getKey());
            transformedMasterData.putIfAbsent(newFace, entry.getValue());
        }

        for (int faceIndex = 0; faceIndex < faceSprites.length; faceIndex++) {
            EnumFacing face = EnumFacing.values()[faceIndex];
            EnumFacing newFace = transform.rotate(face);
            TextureAtlasSprite sprite = faceSprites[newFace.getIndex()];
            int tintIndex = tintIndices[newFace.getIndex()];
            float[][][] rawQuads = masterData.get(face);
            faceQuads.putIfAbsent(newFace, Lists.newArrayList());

            for (int quad = 0; quad < rawQuads.length; quad++) {
                QuadBuilder quadBuilder = new QuadBuilder(DefaultVertexFormats.ITEM, transform, sprite, newFace, tintIndex);
                float[][] steps = rawQuads[quad];

                for (int i = 0; i < steps.length; i++) {
                    float[] instructions = steps[i];
                    UVData data = new UVData(sprite, new float[]{sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV()});
                    float uninterpolatedU, uninterpolatedV;

                    uninterpolatedU = instructions[3];
                    uninterpolatedV = instructions[4];

                    float u, v;
                    u = data.getInterpolatedU(uninterpolatedU);
                    v = data.getInterpolatedV(uninterpolatedV);

                    quadBuilder.putVertex(instructions[0], instructions[1], instructions[2], u, v,
                            instructions[5], instructions[6], instructions[7]);
                }
                BakedQuad builtQuad = quadBuilder.build();
                generalQuads.add(builtQuad);
                faceQuads.get(newFace).add(builtQuad);

            }
        }

        setup();
        return new ModelDataQuads(generalQuads, faceQuads);
    }

    public class ModelDataQuads {
        private final List<BakedQuad> generalQuads;
        private final Map<EnumFacing, List<BakedQuad>> faceQuads;

        public ModelDataQuads(List<BakedQuad> generalQuads, Map<EnumFacing, List<BakedQuad>> faceQuads) {
            this.generalQuads = generalQuads;
            this.faceQuads = faceQuads;
        }

        public List<BakedQuad> getGeneralQuads() {
            return generalQuads;
        }

        public Map<EnumFacing, List<BakedQuad>> getFaceQuads() {
            return faceQuads;
        }
    }

    private UVData rotateUVS(TextureAtlasSprite sprite, float angle) {
        boolean flipU, flipV;
        flipU = false;
        flipV = false;
        if (angle == 0) {
            return new UVData(sprite, new float[]{sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV()});
        }

        double minU = -0.5F;
        double minV = -0.5F;
        double maxU = 0.5F;
        double maxV = 0.5F;

        double cos = Math.cos(Math.toRadians(angle));
        double sin = Math.sin(Math.toRadians(angle));
        minU = (cos * minU) - (sin * minV);
        minV = (sin * minU) + (cos * minV);
        maxU = (cos * maxU) - (sin * maxV);
        maxV = (sin * maxU) + (cos * maxV);

        float minUOut, minVOut, maxUOut, maxVOut;

        minUOut = sprite.getInterpolatedU((minU + 0.5F) * 16F);
        minVOut = sprite.getInterpolatedV((minV + 0.5F) * 16F);
        maxUOut = sprite.getInterpolatedU((maxU + 0.5F) * 16F);
        maxVOut = sprite.getInterpolatedV((maxV + 0.5F) * 16F);

        return new UVData(sprite, new float[]{flipU ? minUOut : sprite.getMinU(), flipV ? minVOut : sprite.getMinV(),
                flipU ? maxUOut : sprite.getMaxU(), flipV ? maxVOut : sprite.getMaxV()});
    }

    private void setup() {
        // reset the model data for a new draw request.
        this.transform = TRSRTransformation.identity();
        this.rotation = EnumFacing.NORTH;
        this.tintIndices = new int[EnumFacing.values().length];
        this.faceSprites = new TextureAtlasSprite[EnumFacing.values().length];
    }

    public void addInstruction(EnumFacing facing, float x, float y, float z, float u, float v) {
        this.addInstruction(facing, x, y, z, u, v, 0, 0, 0);
    }

    public void addInstruction(EnumFacing facing, float x, float y, float z, float u, float v,
                               float normalX, float normalY, float normalZ) {
        float[] instructions = new float[]{x, y, z, u, v, normalX, normalY, normalZ};

        float[][][] quadArray = masterData.get(facing);
        ArrayList<float[][]> quads = Lists.newArrayList(quadArray);
        if (quads.isEmpty())
            quads.add(new float[0][0]);
        if (quads.get(quads.size() - 1).length != 4) {
            ArrayList<float[]> steps = Lists.newArrayList(quads.get(quads.size() - 1));
            steps.add(instructions);
            float[][] stepsArray = new float[steps.size()][];
            steps.toArray(stepsArray);
            stepsArray[steps.size() - 1] = instructions;
            quads.set(quads.size() - 1, stepsArray);
        } else {
            ArrayList<float[]> steps = Lists.newArrayList();
            steps.add(instructions);
            quads.add((float[][]) steps.toArray());
        }
        quadArray = new float[quads.size()][][];
        quads.toArray(quadArray);
        masterData.replace(facing, quadArray);
    }

    private class UVData {
        private float[] uvs = new float[]{-0.5F, -0.5F, 0.5F, 0.5F};

        public UVData(TextureAtlasSprite sprite, float[] uvs) {
            this.uvs = uvs;
        }

        public float getMinU() {
            return uvs[0];
        }

        public float getMinV() {
            return uvs[1];
        }

        public float getMaxU() {
            return uvs[2];
        }

        public float getMaxV() {
            return uvs[3];
        }

        public float getInterpolatedV(float v) {
            float f = this.getMaxV() - this.getMinV();
            return this.getMinV() + f * v / 16.0F;
        }

        public float getInterpolatedU(float u) {
            float f = this.getMaxU() - this.getMinU();
            return this.getMinU() + f * u / 16.0F;
        }
    }
}
