package com.elytradev.carpentrycubes.client.render.model.builder;

import com.elytradev.carpentrycubes.client.render.QuadBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Stores quad info that can be modified with given transforms, tintindices, and face sprites.
 */
public class CarpentryModelData {

    private final ICarpentryModel<?> carpentryModel;
    private final Map<EnumFacing, List<CarpentryQuad>> quads = Maps.newHashMap();

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

        for (Map.Entry<EnumFacing, List<CarpentryQuad>> entry : this.quads.entrySet()) {
            EnumFacing oldFace = entry.getKey();
            EnumFacing newFace = transform.rotate(oldFace);
            List<CarpentryQuad> quads = entry.getValue();

            for (CarpentryQuad quad : quads) {
                faceQuads.putIfAbsent(newFace, Lists.newArrayList());

                int spritesForFace = this.faceSprites[newFace.getIndex()].size();
                for (int spriteIndex = 0; spriteIndex < spritesForFace; spriteIndex++) {
                    TextureAtlasSprite sprite = this.faceSprites[newFace.getIndex()].get(spriteIndex);
                    int tintIndex = this.tintIndices[newFace.getIndex()].get(spriteIndex);

                    QuadBuilder quadBuilder = new QuadBuilder(transform, sprite, newFace, tintIndex);
                    for (CarpentryVertex vertex : quad.getVertices()) {
                        float[] UVs = vertex.getUVs();
                        //UVs = carpentryModel.getUVs(face, facing, state, UVs[0], UVs[1]);
                        float u, v;
                        u = sprite.getInterpolatedU(UVs[0]);
                        v = sprite.getInterpolatedV(UVs[1]);

                        quadBuilder.putVertex(vertex.getX(), vertex.getY(), vertex.getZ(), u, v,
                                vertex.getNormalX(), vertex.getNormalY(), vertex.getNormalZ());
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
        float[] data = new float[]{x, y, z, u, v};

        if (!this.quads.containsKey(facing))
            this.quads.put(facing, new ArrayList<>());

        List<CarpentryQuad> faceQuads = this.quads.get(facing);
        if (faceQuads.isEmpty() || faceQuads.get(faceQuads.size() - 1).isComplete())
            faceQuads.add(new CarpentryQuad(facing));

        CarpentryQuad selectedQuad = faceQuads.get(faceQuads.size() - 1);
        selectedQuad.setVertex(selectedQuad.getNextVertex(), data);
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
