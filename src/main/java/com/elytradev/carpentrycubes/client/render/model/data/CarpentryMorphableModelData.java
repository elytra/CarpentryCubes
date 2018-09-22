package com.elytradev.carpentrycubes.client.render.model.data;

import com.elytradev.carpentrycubes.client.render.model.ICarpentryModel;
import com.elytradev.carpentrycubes.client.render.model.builder.CarpentryModelData;
import com.elytradev.carpentrycubes.client.render.model.builder.CarpentryTransformData;
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
                    BakedQuad builtQuad = quad.bake(transform.compose(quadTransform), side,
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
                    TRSRTransformation quadTransform = TRSRTransformation.identity();
                    TopHeightData heightData = getHeightDataForTop(topSide);
                    if (topSide == EnumFacing.SOUTH) {
                        quadTransform = TRSRTransformation.from(EnumFacing.SOUTH);
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
                    if (requiresTriFlip()) {
                        quadTransform = quadTransform.compose(TRSRTransformation.from(EnumFacing.EAST));
                    }
                    quadsOut.add(new MutablePair<>(quad, quadTransform));
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
            if (sideHeightData.getHeight0() == 0 && sideHeightData.getHeight1() == 0) {
                return Lists.newArrayList();
            }
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
            TRSRTransformation quadTransform = TRSRTransformation.from(side);
            return Lists.newArrayList(new ImmutablePair<>(quadOut, quadTransform));
        }
    }

    public TopHeightData getHeightDataForTop(EnumFacing side) {
        TileCarpentryMorphable.BlockCorner[] selectedCorners = new TileCarpentryMorphable.BlockCorner[3];

        if (side == EnumFacing.NORTH) {
            selectedCorners[0] = TileCarpentryMorphable.BlockCorner.NW;
            selectedCorners[1] = TileCarpentryMorphable.BlockCorner.SE;
            selectedCorners[2] = TileCarpentryMorphable.BlockCorner.NE;
        } else {
            selectedCorners[0] = TileCarpentryMorphable.BlockCorner.SE;
            selectedCorners[1] = TileCarpentryMorphable.BlockCorner.NW;
            selectedCorners[2] = TileCarpentryMorphable.BlockCorner.SW;
        }

        EnumFacing flipFacing = EnumFacing.EAST;
        if (this.facing == EnumFacing.DOWN) {
            selectedCorners[0] = selectedCorners[0].getFlipped();
            selectedCorners[1] = selectedCorners[1].getFlipped();
            selectedCorners[2] = selectedCorners[2].getFlipped();
            flipFacing = EnumFacing.WEST;
        }

        if (requiresTriFlip()) {
            selectedCorners[0] = selectedCorners[0].rotate(TRSRTransformation.from(flipFacing));
            selectedCorners[1] = selectedCorners[1].rotate(TRSRTransformation.from(flipFacing));
            selectedCorners[2] = selectedCorners[2].rotate(TRSRTransformation.from(flipFacing));
        }

        return new TopHeightData(tile.getHeightFromCorner(selectedCorners[0]),
                tile.getHeightFromCorner(selectedCorners[1]),
                tile.getHeightFromCorner(selectedCorners[2]));
    }


    public SideHeightData getHeightDataForSide(EnumFacing side) {
        SideHeightData heightData;
        if (this.facing == EnumFacing.DOWN) {
            side = this.transform.rotate(side);
        }

        switch (side) {
            case NORTH:
                heightData = new SideHeightData(tile.getNorthEastHeight(), tile.getNorthWestHeight());
                break;
            case WEST:
                heightData = new SideHeightData(tile.getNorthWestHeight(), tile.getSouthWestHeight());
                break;
            case SOUTH:
                heightData = new SideHeightData(tile.getSouthWestHeight(), tile.getSouthEastHeight());
                break;
            case EAST:
                heightData = new SideHeightData(tile.getSouthEastHeight(), tile.getNorthEastHeight());
                break;
            default:
                heightData = new SideHeightData(16, 16);
                break;
        }

        if (this.facing == EnumFacing.DOWN) {
            heightData = new SideHeightData(heightData.getHeight1(), heightData.getHeight0());
        }

        return heightData;
    }

    public boolean requiresTriFlip() {
        TileCarpentryMorphable.BlockCorner corner0 = TileCarpentryMorphable.BlockCorner.NW;
        TileCarpentryMorphable.BlockCorner corner1 = TileCarpentryMorphable.BlockCorner.NE;
        TileCarpentryMorphable.BlockCorner corner2 = TileCarpentryMorphable.BlockCorner.SW;
        TileCarpentryMorphable.BlockCorner corner3 = TileCarpentryMorphable.BlockCorner.SE;

        if (this.facing == EnumFacing.DOWN) {
            corner0 = corner0.getFlipped();
            corner1 = corner1.getFlipped();
            corner2 = corner2.getFlipped();
            corner3 = corner3.getFlipped();
        }

        int cornerValue0 = this.tile.getHeightFromCorner(corner0);
        int cornerValue1 = this.tile.getHeightFromCorner(corner1);
        int cornerValue2 = this.tile.getHeightFromCorner(corner2);
        int cornerValue3 = this.tile.getHeightFromCorner(corner3);

        if (cornerValue2 > cornerValue0
                || cornerValue2 > cornerValue1
                || cornerValue2 > cornerValue3) {
            return true;
        } else if (cornerValue1 > cornerValue0
                || cornerValue1 > cornerValue2
                || cornerValue1 > cornerValue3) {
            return true;
        }

        return false;
    }

    private CarpentryTransformData getTransform(EnumFacing facing) {
        CarpentryTransformData transformOut = new CarpentryTransformData();
        switch (facing) {
            case DOWN:
                transformOut.setRotation(180, 180, 0);
                break;
            case NORTH:
                transformOut.setRotation(-90, 0, 0);
                break;
            case SOUTH:
                transformOut.setRotation(90, 0, 0);
                break;
            case WEST:
                transformOut.setRotation(0, 0, 90);
                break;
            case EAST:
                transformOut.setRotation(0, 0, -90);
                break;
        }
        return transformOut;
    }

    public void setTile(TileCarpentryMorphable tile) {
        this.tile = tile;
    }
}
