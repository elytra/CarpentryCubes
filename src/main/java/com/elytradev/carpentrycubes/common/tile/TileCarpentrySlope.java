package com.elytradev.carpentrycubes.common.tile;

import net.minecraft.block.BlockStairs;
import net.minecraft.nbt.NBTTagCompound;

public class TileCarpentrySlope extends TileCarpentry {

    private BlockStairs.EnumShape shape = BlockStairs.EnumShape.STRAIGHT;

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.setShape(BlockStairs.EnumShape.values()[tag.getInteger("shape")]);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag = super.writeToNBT(tag);
        tag.setInteger("shape", getShape().ordinal());
        return tag;
    }

    public BlockStairs.EnumShape getShape() {
        return shape;
    }

    public void setShape(BlockStairs.EnumShape shape) {
        this.shape = shape;
    }
}
