package com.elytradev.carpentrycubes.common.tile;

import com.elytradev.carpentrycubes.common.CarpentryLog;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;

public class TileCarpentry extends TileEntity {

    private IBlockState coverState = Blocks.AIR.getDefaultState();

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        ResourceLocation coverStateID = new ResourceLocation(tag.getString("coverStateID"));
        int coverStateMeta = tag.getInteger("coverStateMeta");

        try {
            this.setCoverState(Block.REGISTRY.getObject(coverStateID).getStateFromMeta(coverStateMeta));
        } catch (Exception e) {
            CarpentryLog.error("Failed to obtain block with id {} from registry, defaulting to air.", coverStateID);
            this.setCoverState(Blocks.AIR.getDefaultState());
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag = super.writeToNBT(tag);
        tag.setString("coverStateID", getCoverState().getBlock().getRegistryName().toString());
        tag.setInteger("coverStateMeta", getCoverState().getBlock().getMetaFromState(getCoverState()));

        return tag;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return super.shouldRefresh(world, pos, oldState, newState) && oldState.getBlock() != newState.getBlock();
    }

    public IBlockState getCoverState() {
        return coverState;
    }

    public void setCoverState(IBlockState coverState) {
        this.coverState = coverState;
    }
}
