package com.elytradev.carpentrycubes.client.render.model.builder;

import com.elytradev.carpentrycubes.client.render.model.ICarpentryModel;
import com.elytradev.carpentrycubes.client.render.model.quad.CarpentryQuad;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Stores quad info that can be modified with given transforms, tintindices, and face sprites.
 */
public class CarpentryModelData {

    private final ICarpentryModel<?> carpentryModel;
    private final Map<EnumFacing, List<CarpentryQuad>> quads = Maps.newHashMap();

    protected IBlockState state;
    protected EnumFacing facing = EnumFacing.NORTH;
    protected TRSRTransformation transform = TRSRTransformation.identity();
    protected ArrayList<Integer>[] tintIndices = new ArrayList[EnumFacing.values().length];
    protected ArrayList<TextureAtlasSprite>[] faceSprites = new ArrayList[EnumFacing.values().length];

    public CarpentryModelData(ICarpentryModel<?> carpentryModel) {
        this.carpentryModel = carpentryModel;
        for (int i = 0; i < tintIndices.length; i++) {
            tintIndices[i] = Lists.newArrayList();
            faceSprites[i] = Lists.newArrayList();
        }
    }

    public void setFaceData(int quadNumber, EnumFacing side, TextureAtlasSprite sprite, int tintIndex) {
        addOrSet(this.faceSprites[side.getIndex()], quadNumber, sprite);
        addOrSet(this.tintIndices[side.getIndex()], quadNumber, tintIndex);
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
        Arrays.stream(EnumFacing.values()).forEach((o) -> faceQuads.put(o, Lists.newArrayList()));

        for (Map.Entry<EnumFacing, List<CarpentryQuad>> entry : this.quads.entrySet()) {
            EnumFacing oldFace = entry.getKey();
            EnumFacing newFace = transform.rotate(oldFace);
            List<CarpentryQuad> quads = entry.getValue();

            for (CarpentryQuad quad : quads) {
                faceQuads.putIfAbsent(newFace, Lists.newArrayList());

                int spritesForFace = this.faceSprites[newFace.getIndex()].size();
                for (int spriteIndex = 0; spriteIndex < spritesForFace; spriteIndex++) {
                    BakedQuad builtQuad = quad.bake(transform, newFace,
                            this.faceSprites[newFace.getIndex()].get(spriteIndex),
                            this.tintIndices[newFace.getIndex()].get(spriteIndex));
                    generalQuads.add(builtQuad);
                    faceQuads.get(newFace).add(builtQuad);
                }
            }
        }
        setup();
        return new ModelDataQuads(generalQuads, faceQuads);
    }

    protected void setup() {
        // reset the model data for a new draw request.
        this.state = Blocks.AIR.getDefaultState();
        this.facing = EnumFacing.NORTH;
        this.transform = TRSRTransformation.identity();
        this.tintIndices = new ArrayList[EnumFacing.values().length];
        this.faceSprites = new ArrayList[EnumFacing.values().length];

        for (int i = 0; i < tintIndices.length; i++) {
            tintIndices[i] = Lists.newArrayList();
            faceSprites[i] = Lists.newArrayList();
        }
    }

    public void addQuadInstruction(EnumFacing facing, float x, float y, float z) {
        float[] data = new float[]{x, y, z};

        if (!this.quads.containsKey(facing))
            this.quads.put(facing, new ArrayList<>());

        List<CarpentryQuad> faceQuads = this.quads.get(facing);
        if (faceQuads.isEmpty() || faceQuads.get(faceQuads.size() - 1).isComplete())
            faceQuads.add(new CarpentryQuad(facing));

        CarpentryQuad selectedQuad = faceQuads.get(faceQuads.size() - 1);
        selectedQuad.setVertex(selectedQuad.getNextVertex(), data);
    }

    public void addTriInstruction(EnumFacing facing, float x, float y, float z) {
        float[] data = new float[]{x, y, z};

        if (!this.quads.containsKey(facing))
            this.quads.put(facing, new ArrayList<>());

        List<CarpentryQuad> faceQuads = this.quads.get(facing);
        if (faceQuads.isEmpty() || faceQuads.get(faceQuads.size() - 1).isComplete())
            faceQuads.add(new CarpentryQuad(facing));

        CarpentryQuad selectedQuad = faceQuads.get(faceQuads.size() - 1);

        // If this is the first vertex being added to the quad then add it twice so the tri creates a full quad.
        if (selectedQuad.getNextVertex() == 0)
            selectedQuad.setVertex(selectedQuad.getNextVertex(), data);
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
