package com.elytradev.carpentrycubes.client.render;

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

public class CarpentersModelData {

    private final Map<EnumFacing, float[][][]> masterData = Arrays.stream(EnumFacing.values()).collect(Collectors.toMap(e -> e, e -> new float[0][0][0]));

    private TRSRTransformation transform = TRSRTransformation.identity();
    private int[] tintIndices = new int[EnumFacing.values().length];
    private TextureAtlasSprite[] faceSprites = new TextureAtlasSprite[EnumFacing.values().length];

    public void setFaceData(EnumFacing side, TextureAtlasSprite sprite, int tintIndex) {
        this.faceSprites[side.getIndex()] = sprite;
        this.tintIndices[side.getIndex()] = tintIndex;
    }

    public void setTransform(TRSRTransformation transform) {
        this.transform = transform;
    }

    public ModelDataQuads buildModel() {
        List<BakedQuad> generalQuads = Lists.newArrayList();
        Map<EnumFacing, List<BakedQuad>> faceQuads = Maps.newHashMap();
        for (int faceIndex = 0; faceIndex < faceSprites.length; faceIndex++) {
            EnumFacing face = EnumFacing.values()[faceIndex];
            TextureAtlasSprite sprite = faceSprites[faceIndex];
            int tintIndex = tintIndices[faceIndex];
            float[][][] rawQuads = masterData.get(face);
            faceQuads.putIfAbsent(face, Lists.newArrayList());

            for (int quad = 0; quad < rawQuads.length; quad++) {
                QuadBuilder quadBuilder = new QuadBuilder(DefaultVertexFormats.ITEM, transform, sprite, face, tintIndex);
                float[][] steps = rawQuads[quad];

                for (int i = 0; i < steps.length; i++) {
                    float[] instructions = steps[i];
                    float u, v;
                    u = instructions[3] == 0 ? sprite.getMinU() : sprite.getMaxU();
                    v = instructions[4] == 0 ? sprite.getMinV() : sprite.getMaxV();
                    quadBuilder.putVertex(instructions[0], instructions[1], instructions[2], u, v);
                }
                BakedQuad builtQuad = quadBuilder.build();
                generalQuads.add(builtQuad);
                faceQuads.get(face).add(builtQuad);
            }
        }

        setup();
        return new ModelDataQuads(generalQuads, faceQuads);
    }

    private void setup() {
        this.transform = TRSRTransformation.identity();
        this.tintIndices = new int[EnumFacing.values().length];
        this.faceSprites = new TextureAtlasSprite[EnumFacing.values().length];
    }

    public void addInstruction(EnumFacing facing, float x, float y, float z, boolean minU, boolean minV) {
        float[] instructions = new float[]{x, y, z, minU ? 0 : 1, minV ? 0 : 1};

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

}
