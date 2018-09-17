package com.elytradev.carpentrycubes.client.render.model.data;

import com.elytradev.carpentrycubes.client.render.model.ICarpentryModel;
import com.elytradev.carpentrycubes.client.render.model.builder.CarpentryModelData;
import com.elytradev.carpentrycubes.client.render.model.quad.CarpentryQuad;
import com.elytradev.carpentrycubes.common.tile.TileCarpentryMorphable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CarpentryMorphableModelData extends CarpentryModelData {

    private TileCarpentryMorphable tile;
    private Map<HeightData, CarpentryQuad> cachedQuads = Maps.newHashMap();

    private class HeightData {
        final int height0, height1;

        public HeightData(int height0, int height1) {
            this.height0 = height0;
            this.height1 = height1;
        }

        public int getHeight0() {
            return height0;
        }

        public int getHeight1() {
            return height1;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HeightData that = (HeightData) o;
            return height0 == that.height0 &&
                    height1 == that.height1;
        }

        @Override
        public int hashCode() {
            return Objects.hash(height0, height1);
        }
    }

    public CarpentryMorphableModelData(ICarpentryModel<?> carpentryModel) {
        super(carpentryModel);
    }

    public ModelDataQuads buildModel() {
        List<BakedQuad> generalQuads = Lists.newArrayList();
        Map<EnumFacing, List<BakedQuad>> faceQuads = Maps.newHashMap();
        Arrays.stream(EnumFacing.values()).forEach((o) -> faceQuads.put(o, Lists.newArrayList()));

        for (EnumFacing side : EnumFacing.values()) {
            if (side.getAxis() == EnumFacing.Axis.Y)
                continue;

            CarpentryQuad quad = this.getQuadForSide(side);
            faceQuads.putIfAbsent(side, Lists.newArrayList());

            int spritesForFace = this.faceSprites[side.getIndex()].size();
            for (int spriteIndex = 0; spriteIndex < spritesForFace; spriteIndex++) {
                BakedQuad builtQuad = quad.bake(TRSRTransformation.from(side), side,
                        this.faceSprites[side.getIndex()].get(spriteIndex),
                        this.tintIndices[side.getIndex()].get(spriteIndex));
                generalQuads.add(builtQuad);
                faceQuads.get(side).add(builtQuad);
            }
        }

        setup();
        return new ModelDataQuads(generalQuads, faceQuads);
    }

    @Override
    protected void setup() {
        this.tile = null;
        super.setup();
    }

    public CarpentryQuad getQuadForSide(EnumFacing side) {
        HeightData heightData = this.getHeightDataForSide(side);

        CarpentryQuad quadOut = this.cachedQuads.get(heightData);
        if (quadOut == null) {
            float calculatedHeight0 = (1F / 16F) * heightData.getHeight0();
            float calculatedHeight1 = (1F / 16F) * heightData.getHeight1();
            quadOut = new CarpentryQuad(EnumFacing.NORTH); // Always use north so the rotation calculations work properly.
            quadOut.setVertex(quadOut.getNextVertex(), new float[]{0, 0, 0});
            quadOut.setVertex(quadOut.getNextVertex(), new float[]{0, calculatedHeight1, 0});
            quadOut.setVertex(quadOut.getNextVertex(), new float[]{1, calculatedHeight0, 0});
            quadOut.setVertex(quadOut.getNextVertex(), new float[]{1, 0, 0});
            this.cachedQuads.put(heightData, quadOut);
        }
        return quadOut;
    }

    public HeightData getHeightDataForSide(EnumFacing side) {
        switch (side) {
            case NORTH:
                return new HeightData(tile.getNorthEastHeight(), tile.getNorthWestHeight());
            case WEST:
                return new HeightData(tile.getNorthWestHeight(), tile.getSouthWestHeight());
            case SOUTH:
                return new HeightData(tile.getSouthWestHeight(), tile.getSouthEastHeight());
            case EAST:
                return new HeightData(tile.getSouthEastHeight(), tile.getNorthEastHeight());
            default:
                return new HeightData(16, 16);
        }
    }

    public void setTile(TileCarpentryMorphable tile) {
        this.tile = tile;
    }
}
