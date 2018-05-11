package com.elytradev.carpentrycubes.common.tile;

import com.elytradev.carpentrycubes.common.block.BlockCarpentrySlope.EnumOrientation;
import com.elytradev.carpentrycubes.common.block.BlockCarpentrySlope.EnumShape;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileCarpentrySlope extends TileCarpentry {

    private EnumShape shape = EnumShape.STRAIGHT;
    private EnumOrientation orientation = EnumOrientation.GROUND;
    private EnumFacing secondaryDirection = EnumFacing.NORTH;

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.setSecondaryDirection(EnumFacing.VALUES[tag.getInteger("secondary_direction")]);
        this.setShape(EnumShape.values()[tag.getInteger("shape")]);
        this.setOrientation(EnumOrientation.values()[tag.getInteger("orientation")]);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag = super.writeToNBT(tag);
        tag.setInteger("secondary_direction", getSecondaryDirection().getIndex());
        tag.setInteger("shape", getShape().ordinal());
        tag.setInteger("orientation", getOrientation().ordinal());
        return tag;
    }

    public EnumOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(EnumOrientation orientation) {
        this.orientation = orientation;
    }

    public EnumShape getShape() {
        return shape;
    }

    public void setShape(EnumShape shape) {
        this.shape = shape;
    }

    public EnumFacing getSecondaryDirection() {
        return secondaryDirection;
    }

    public void setSecondaryDirection(EnumFacing secondaryDirection) {
        this.secondaryDirection = secondaryDirection;
    }
}
