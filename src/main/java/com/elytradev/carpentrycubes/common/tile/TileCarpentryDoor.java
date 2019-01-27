package com.elytradev.carpentrycubes.common.tile;

import com.elytradev.carpentrycubes.common.block.BlockCarpentryDoor;
import net.minecraft.nbt.NBTTagCompound;

public class TileCarpentryDoor extends TileCarpentry {

    private BlockCarpentryDoor.EnumDoorStyle style = BlockCarpentryDoor.EnumDoorStyle.FOUR_WINDOW;

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.setStyle(BlockCarpentryDoor.EnumDoorStyle.values()[tag.getInteger("door_style")]);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag = super.writeToNBT(tag);
        tag.setInteger("door_style", getStyle().ordinal());
        return tag;
    }

    public BlockCarpentryDoor.EnumDoorStyle getStyle() {
        return style;
    }

    public void setStyle(BlockCarpentryDoor.EnumDoorStyle style) {
        this.style = style;
    }
}
