package com.elytradev.carpentrycubes.common.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;

@ReceivedOn(Side.CLIENT)
public class TileUpdateMessage extends Message {

    public NBTTagCompound updateTag;
    public BlockPos pos;

    public TileUpdateMessage(NetworkContext ctx) {
        super(ctx);
    }

    public TileUpdateMessage() {
        super(CarpentryNetworking.NETWORK);
    }

    public TileUpdateMessage(@Nonnull TileEntity tile) {
        this();

        this.updateTag = tile.getUpdateTag();
        this.pos = tile.getPos();
    }

    @Override
    protected void handle(EntityPlayer player) {
        TileEntity tileEntity = player.getEntityWorld().getTileEntity(pos);
        if (tileEntity != null && updateTag != null) {
            tileEntity.handleUpdateTag(updateTag);
            tileEntity.getWorld().markBlockRangeForRenderUpdate(pos, pos);
        }
    }
}
