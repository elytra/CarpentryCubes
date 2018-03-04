package com.elytradev.carpentrycubes.common.item;

import com.elytradev.carpentrycubes.common.CarpentryMod;
import com.elytradev.concrete.resgen.EnumResourceType;
import com.elytradev.concrete.resgen.IResourceHolder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

public class ItemCarpentryTool extends Item implements IResourceHolder {

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    @Nullable
    @Override
    public ResourceLocation getResource(EnumResourceType resourceType, int meta) {
        if(resourceType == EnumResourceType.TEXTURE){
            return new ResourceLocation(CarpentryMod.MOD_ID, "blocks/foursectionframe");
        }
        return null;
    }
}
