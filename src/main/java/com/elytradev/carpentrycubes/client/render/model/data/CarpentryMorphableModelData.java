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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CarpentryMorphableModelData extends CarpentryModelData {

    private TileCarpentryMorphable tile;
    private Map<SideHeightData, CarpentryQuad> cachedSideQuads = Maps.newHashMap();
    private Map<TopHeightData, CarpentryQuad> cachedTopQuads = Maps.newHashMap();
    private Pair<CarpentryQuad, TRSRTransformation> bottomQuad;

    private class SideHeightData {
        final int height0, height1;

        public SideHeightData(int height0, int height1) {
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
            SideHeightData that = (SideHeightData) o;
            return height0 == that.height0 &&
                    height1 == that.height1;
        }

        @Override
        public int hashCode() {
            return Objects.hash(height0, height1);
        }
    }

    private class TopHeightData {
        final int height0, height1, height2;

        public TopHeightData(int height0, int height1, int height2) {
            this.height0 = height0;
            this.height1 = height1;
            this.height2 = height2;
        }

        public int getHeight0() {
            return height0;
        }

        public int getHeight1() {
            return height1;
        }

        public int getHeight2() {
            return height2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TopHeightData that = (TopHeightData) o;
            return height0 == that.height0 &&
                    height1 == that.height1 &&
                    height2 == that.height2;
        }

        @Override
        public int hashCode() {
            return Objects.hash(height0, height1, height2);
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
            List<Pair<CarpentryQuad, TRSRTransformation>> quadsForSide = this.getQuadsForSide(side);
            for (Pair<CarpentryQuad, TRSRTransformation> pair : quadsForSide) {
                CarpentryQuad quad = pair.getLeft();
                TRSRTransformation quadTransform = pair.getRight();

                faceQuads.putIfAbsent(side, Lists.newArrayList());

                int spritesForFace = this.faceSprites[side.getIndex()].size();
                for (int spriteIndex = 0; spriteIndex < spritesForFace; spriteIndex++) {
                    BakedQuad builtQuad = quad.bake(quadTransform, side,
                            this.faceSprites[side.getIndex()].get(spriteIndex),
                            this.tintIndices[side.getIndex()].get(spriteIndex));
                    generalQuads.add(builtQuad);
                    faceQuads.get(side).add(builtQuad);
                }
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

    public List<Pair<CarpentryQuad, TRSRTransformation>> getQuadsForSide(EnumFacing side) {
        if (side.getAxis() == EnumFacing.Axis.Y) {
            List<Pair<CarpentryQuad, TRSRTransformation>> quadsOut = Lists.newArrayList();
            if (side == EnumFacing.UP) {
                for (EnumFacing topSide : Lists.newArrayList(EnumFacing.NORTH, EnumFacing.SOUTH)) {
                    TRSRTransformation transform = TRSRTransformation.identity();
                    TopHeightData heightData;
                    if (topSide == EnumFacing.NORTH) {
                        heightData = new TopHeightData(tile.getNorthWestHeight(),
                                tile.getSouthEastHeight(),
                                tile.getNorthEastHeight());
                    } else {
                        transform = TRSRTransformation.from(EnumFacing.SOUTH);
                        heightData = new TopHeightData(tile.getSouthEastHeight(),
                                tile.getNorthWestHeight(),
                                tile.getSouthWestHeight());
                    }

                    CarpentryQuad quad = this.cachedTopQuads.get(heightData);
                    if (quad == null) {
                        quad = new CarpentryQuad(EnumFacing.UP);
                        quad.setVertex(quad.getNextVertex(), new float[]{0, (1F / 16F) * heightData.getHeight0(), 0});
                        quad.setVertex(quad.getNextVertex(), new float[]{0, (1F / 16F) * heightData.getHeight0(), 0});
                        quad.setVertex(quad.getNextVertex(), new float[]{1, (1F / 16F) * heightData.getHeight1(), 1});
                        quad.setVertex(quad.getNextVertex(), new float[]{1, (1F / 16F) * heightData.getHeight2(), 0});
                        this.cachedTopQuads.put(heightData, quad);
                    }
                    quadsOut.add(new MutablePair<>(quad, transform));
                }
            } else {
                if (this.bottomQuad == null) {
                    CarpentryQuad quad = new CarpentryQuad(EnumFacing.DOWN);
                    quad.setVertex(quad.getNextVertex(), new float[]{0, 0, 0});
                    quad.setVertex(quad.getNextVertex(), new float[]{1, 0, 0});
                    quad.setVertex(quad.getNextVertex(), new float[]{1, 0, 1});
                    quad.setVertex(quad.getNextVertex(), new float[]{0, 0, 1});
                    this.bottomQuad = new MutablePair<>(quad, TRSRTransformation.identity());
                }
                quadsOut.add(bottomQuad);
            }
            return quadsOut;
        } else {
            SideHeightData sideHeightData = this.getHeightDataForSide(side);
            CarpentryQuad quadOut = this.cachedSideQuads.get(sideHeightData);
            if (quadOut == null) {
                float calculatedHeight0 = (1F / 16F) * sideHeightData.getHeight0();
                float calculatedHeight1 = (1F / 16F) * sideHeightData.getHeight1();
                quadOut = new CarpentryQuad(EnumFacing.NORTH); // Always use north so the rotation calculations work properly.
                quadOut.setVertex(quadOut.getNextVertex(), new float[]{0, 0, 0});
                quadOut.setVertex(quadOut.getNextVertex(), new float[]{0, calculatedHeight1, 0});
                quadOut.setVertex(quadOut.getNextVertex(), new float[]{1, calculatedHeight0, 0});
                quadOut.setVertex(quadOut.getNextVertex(), new float[]{1, 0, 0});
                this.cachedSideQuads.put(sideHeightData, quadOut);
            }
            return Lists.newArrayList(new ImmutablePair<>(quadOut, TRSRTransformation.from(side)));
        }
    }

    public SideHeightData getHeightDataForSide(EnumFacing side) {
        switch (side) {
            case NORTH:
                return new SideHeightData(tile.getNorthEastHeight(), tile.getNorthWestHeight());
            case WEST:
                return new SideHeightData(tile.getNorthWestHeight(), tile.getSouthWestHeight());
            case SOUTH:
                return new SideHeightData(tile.getSouthWestHeight(), tile.getSouthEastHeight());
            case EAST:
                return new SideHeightData(tile.getSouthEastHeight(), tile.getNorthEastHeight());
            default:
                return new SideHeightData(16, 16);
        }
    }

    public void setTile(TileCarpentryMorphable tile) {
        this.tile = tile;
    }
}
