package com.elytradev.carpentrycubes.client.render.model;

import com.elytradev.carpentrycubes.client.render.QuadBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Stores quad info that can be modified with given transforms, tintindices, and face sprites.
 */
public class CarpentryModelData {

    private final ICarpentryModel<?> carpentryModel;
    private final Map<EnumFacing, float[][][]> masterData = Arrays.stream(EnumFacing.values()).collect(Collectors.toMap(e -> e, e -> new float[0][0][0]));

    private IBlockState state;
    private EnumFacing facing = EnumFacing.NORTH;
    private TRSRTransformation transform = TRSRTransformation.identity();
    private ArrayList<Integer>[] tintIndices = new ArrayList[EnumFacing.values().length];
    private ArrayList<TextureAtlasSprite>[] faceSprites = new ArrayList[EnumFacing.values().length];
    private ArrayList<Vector3f>[] quadOffsets = new ArrayList[EnumFacing.values().length];

    public CarpentryModelData(ICarpentryModel<?> carpentryModel) {
        this.carpentryModel = carpentryModel;
        for (int i = 0; i < tintIndices.length; i++) {
            tintIndices[i] = Lists.newArrayList();
            faceSprites[i] = Lists.newArrayList();
            quadOffsets[i] = Lists.newArrayList();
        }
    }

    public void setFaceData(int quadNumber, EnumFacing side, TextureAtlasSprite sprite, int tintIndex,
                            Vector3f offset) {
        addOrSet(this.faceSprites[side.getIndex()], quadNumber, sprite);
        addOrSet(this.tintIndices[side.getIndex()], quadNumber, tintIndex);
        addOrSet(this.quadOffsets[side.getIndex()], quadNumber, offset);
    }

    private void addOrSet(ArrayList list, int index, Object element) {
        if (index >= list.size()) {
            list.add(index, element);
        } else {
            list.set(index, element);
        }
    }

    public void setTransform(EnumFacing facing, TRSRTransformation transform) {
        this.facing = facing;
        this.transform = transform;
    }

    public void setState(IBlockState state) {
        this.state = state;
    }

    public ModelDataQuads buildModel() {
        List<BakedQuad> generalQuads = Lists.newArrayList();
        Map<EnumFacing, List<BakedQuad>> faceQuads = Maps.newHashMap();
        for (int faceIndex = 0; faceIndex < faceSprites.length; faceIndex++) {
            EnumFacing face = EnumFacing.values()[faceIndex];
            EnumFacing newFace = transform.rotate(face);

            int size = faceSprites[newFace.getIndex()].size();
            for (int sideQuad = 0; sideQuad < size; sideQuad++) {
                TextureAtlasSprite sprite = faceSprites[newFace.getIndex()].get(sideQuad);
                int tintIndex = tintIndices[newFace.getIndex()].get(sideQuad);
                float[][][] rawQuads = masterData.get(face);
                faceQuads.putIfAbsent(newFace, Lists.newArrayList());

                for (int quad = 0; quad < rawQuads.length; quad++) {
                    QuadBuilder quadBuilder = new QuadBuilder(DefaultVertexFormats.ITEM, transform, sprite, newFace, tintIndex);
                    float[][] steps = rawQuads[quad];
                    Vector3f normals = genNormals(steps);
                    for (int i = 0; i < steps.length; i++) {
                        float[] instructions = steps[i];
                        float[] uVs = carpentryModel.getUVs(face, newFace, facing, state, instructions[3], instructions[4]);
                        float u, v;
                        u = sprite.getInterpolatedU(uVs[0]);
                        v = sprite.getInterpolatedV(uVs[1]);

                        quadBuilder.putVertex(instructions[0], instructions[1], instructions[2], u, v,
                                normals.x, normals.y, normals.z);
                    }
                    BakedQuad builtQuad = quadBuilder.build();
                    generalQuads.add(builtQuad);
                    faceQuads.get(newFace).add(builtQuad);
                }
            }
        }

        setup();
        return new ModelDataQuads(generalQuads, faceQuads);
    }

    public Vector3f genNormals(float[][] points) {
        int prevPoint = 3;
        int curPoint = 0;
        int nextPoint = 1;

        Vector3f prevVertex = new Vector3f(points[prevPoint]);
        Vector3f curVertex = new Vector3f(points[curPoint]);
        Vector3f nextVertex = new Vector3f(points[nextPoint]);

        // Sanity checks for irregular quads.
        if (prevVertex.equals(curVertex)) {
            prevPoint = 2;
            prevVertex = new Vector3f(points[prevPoint]);
        }
        if (nextVertex.equals(curVertex)) {
            nextPoint = 2;
            nextVertex = new Vector3f(points[nextPoint]);
        }

        prevVertex.sub(curVertex);
        nextVertex.sub(curVertex);

        Vector3f normals = new Vector3f();
        normals.cross(prevVertex, nextVertex);
        normals.normalize();
        return normals;
    }

    private void setup() {
        // reset the model data for a new draw request.
        this.state = Blocks.AIR.getDefaultState();
        this.facing = EnumFacing.NORTH;
        this.transform = TRSRTransformation.identity();
        this.tintIndices = new ArrayList[EnumFacing.values().length];
        this.faceSprites = new ArrayList[EnumFacing.values().length];
        this.quadOffsets = new ArrayList[EnumFacing.values().length];

        for (int i = 0; i < tintIndices.length; i++) {
            tintIndices[i] = Lists.newArrayList();
            faceSprites[i] = Lists.newArrayList();
            quadOffsets[i] = Lists.newArrayList();
        }
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
            float[][] stepsArray = new float[steps.size()][];
            steps.toArray(stepsArray);
            quads.add(stepsArray);
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
