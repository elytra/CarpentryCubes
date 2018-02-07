package com.elytradev.carpentrycubes.common.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Used by CarpentryGuiHandler to handle tile guis.
 */
public interface IElementProvider {

    Object getServerElement(EntityPlayer player);

    @SideOnly(Side.CLIENT)
    Object getClientElement(EntityPlayer player);

}
