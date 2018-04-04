package com.elytradev.carpentrycubes.common.tile;

import com.elytradev.carpentrycubes.common.block.BlockCarpentrySlope;
import net.minecraft.nbt.NBTTagCompound;

public class TileCarpentrySlope extends TileCarpentry {

    private BlockCarpentrySlope.EnumShape shape = BlockCarpentrySlope.EnumShape.STRAIGHT;
    private BlockCarpentrySlope.EnumOrientation orientation = BlockCarpentrySlope.EnumOrientation.GROUND;

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.setShape(BlockCarpentrySlope.EnumShape.values()[tag.getInteger("shape")]);
        this.setOrientation(BlockCarpentrySlope.EnumOrientation.values()[tag.getInteger("orientation")]);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag = super.writeToNBT(tag);
        tag.setInteger("shape", getShape().ordinal());
        tag.setInteger("orientation", getOrientation().ordinal());
        return tag;
    }

    public BlockCarpentrySlope.EnumOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(BlockCarpentrySlope.EnumOrientation orientation) {
        this.orientation = orientation;
    }

    public BlockCarpentrySlope.EnumShape getShape() {
        return shape;
    }

    public void setShape(BlockCarpentrySlope.EnumShape shape) {
        this.shape = shape;
    }
}
