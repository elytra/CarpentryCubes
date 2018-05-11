package com.elytradev.carpentrycubes.common.handlers;

import com.elytradev.carpentrycubes.common.tile.IElementProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class CarpentryGuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te;
        if (ID == ElementType.ELEMENT_PROVIDER.caseNumber) {
            te = world.getTileEntity(pos);
            if (te instanceof IElementProvider) {
                return ((IElementProvider) te).getServerElement(player);
            }
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te;
        if (ID == ElementType.ELEMENT_PROVIDER.caseNumber) {
            te = world.getTileEntity(pos);
            if (te instanceof IElementProvider) {
                return ((IElementProvider) te).getClientElement(player);
            }
        }

        return null;
    }

    public enum ElementType {
        ELEMENT_PROVIDER(0);

        public final int caseNumber;

        ElementType(int caseNumber) {
            this.caseNumber = caseNumber;
        }
    }

}