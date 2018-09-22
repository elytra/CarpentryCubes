package com.elytradev.carpentrycubes.common.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.TRSRTransformation;

public class TileCarpentryMorphable extends TileCarpentry {

    public enum BlockCorner {
        NW(0, 1, "North-West", EnumFacing.NORTH),
        NE(1, 0, "North-East", EnumFacing.EAST),
        SE(2, 3, "South-East", EnumFacing.SOUTH),
        SW(3, 2, "South-West", EnumFacing.WEST);

        static BlockCorner[] corners = new BlockCorner[]{NW, NE, SE, SW};
        static BlockCorner[] faceToCornerMap = new BlockCorner[]{SE, SW, NW, NE};

        String name;
        int index;
        int flippedIndex;
        EnumFacing associatedFace;

        BlockCorner(int index, int flippedIndex, String name, EnumFacing associatedFace) {
            this.index = index;
            this.flippedIndex = flippedIndex;
            this.name = name;
            this.associatedFace = associatedFace;
        }

        public BlockCorner rotate(TRSRTransformation transform) {
            EnumFacing newFace = transform.rotate(this.associatedFace);
            if (newFace.getHorizontalIndex() == -1) {
                return null;
            } else {
                return faceToCornerMap[newFace.getHorizontalIndex()];
            }
        }

        public BlockCorner getFlipped() {
            return corners[flippedIndex];
        }

        public static BlockCorner getCornerByIndex(int index) {
            return corners[index];
        }

        public static BlockCorner getCornerByFace(EnumFacing face) {
            return faceToCornerMap[face.getHorizontalIndex()];
        }
    }

    private int northWestHeight = 16;
    private int northEastHeight = 16;
    private int southWestHeight = 16;
    private int southEastHeight = 16;

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        this.setNorthWestHeight(tag.getByte("north_west_height"));
        this.setNorthEastHeight(tag.getByte("north_east_height"));
        this.setSouthWestHeight(tag.getByte("south_west_height"));
        this.setSouthEastHeight(tag.getByte("south_east_height"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag = super.writeToNBT(tag);

        tag.setByte("north_west_height", (byte) getNorthWestHeight());
        tag.setByte("north_east_height", (byte) getNorthEastHeight());
        tag.setByte("south_west_height", (byte) getSouthWestHeight());
        tag.setByte("south_east_height", (byte) getSouthEastHeight());

        return tag;
    }

    public int getNorthWestHeight() {
        return northWestHeight;
    }

    public void setNorthWestHeight(int northWestHeight) {
        this.northWestHeight = Math.min(Math.max(northWestHeight, 0), 16);
    }

    public void applyDeltaNorthWest(int delta) {
        this.setNorthWestHeight(this.getNorthWestHeight() + delta);
    }

    public int getNorthEastHeight() {
        return northEastHeight;
    }

    public void setNorthEastHeight(int northEastHeight) {
        this.northEastHeight = Math.min(Math.max(northEastHeight, 0), 16);
    }

    public void applyDeltaNorthEast(int delta) {
        this.setNorthEastHeight(this.getNorthEastHeight() + delta);
    }

    public int getSouthWestHeight() {
        return southWestHeight;
    }

    public void setSouthWestHeight(int southWestHeight) {
        this.southWestHeight = Math.min(Math.max(southWestHeight, 0), 16);
    }

    public void applyDeltaSouthWest(int delta) {
        this.setSouthWestHeight(this.getSouthWestHeight() + delta);
    }

    public int getSouthEastHeight() {
        return southEastHeight;
    }

    public void setSouthEastHeight(int southEastHeight) {
        this.southEastHeight = Math.min(Math.max(southEastHeight, 0), 16);
    }

    public void applyDeltaSouthEast(int delta) {
        this.setSouthEastHeight(this.getSouthEastHeight() + delta);
    }

    public boolean isNormalCube() {
        return northWestHeight == 16 && northEastHeight == 16 && southWestHeight == 16 && southEastHeight == 16;
    }

    public int getHeightFromCorner(BlockCorner corner) {
        if (corner == null)
            return 0;
        switch (corner) {
            case NW:
                return this.getNorthWestHeight();
            case NE:
                return this.getNorthEastHeight();
            case SE:
                return this.getSouthEastHeight();
            case SW:
                return this.getSouthWestHeight();
            default:
                return 0;
        }
    }

}
