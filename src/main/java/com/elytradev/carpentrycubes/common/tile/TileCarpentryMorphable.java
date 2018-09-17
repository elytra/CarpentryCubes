package com.elytradev.carpentrycubes.common.tile;

import net.minecraft.nbt.NBTTagCompound;

public class TileCarpentryMorphable extends TileCarpentry {

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
}
