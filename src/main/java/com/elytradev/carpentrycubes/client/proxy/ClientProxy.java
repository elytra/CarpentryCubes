package com.elytradev.carpentrycubes.client.proxy;

import com.elytradev.carpentrycubes.client.render.model.CarpentryModelLoader;
import com.elytradev.carpentrycubes.common.CarpentryContent;
import com.elytradev.carpentrycubes.common.CarpentryMod;
import com.elytradev.carpentrycubes.common.proxy.CommonProxy;
import com.elytradev.concrete.resgen.ConcreteResourcePack;
import com.elytradev.concrete.resgen.IResourceHolder;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerRenderers(LoaderState.ModState state) {
        if (state == LoaderState.ModState.PREINITIALIZED) {
            new ConcreteResourcePack(CarpentryMod.MOD_ID);
        }

        if (state == LoaderState.ModState.INITIALIZED) {
            registerTileEntitySpecialRenderers();
            registerItemRenderers();
        }
    }

    public void registerTileEntitySpecialRenderers() {
    }

    public void registerItemRenderers() {
        Item itemToRegister = null;
        ModelResourceLocation modelResourceLocation = null;

        // Do some general render registrations for OBJECTS, not considering meta.
        ItemModelMesher modelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        for (int i = 0; i < CarpentryContent.registeredBlocks.size(); i++) {
            modelResourceLocation = new ModelResourceLocation(CarpentryMod.RESOURCE_DOMAIN + CarpentryContent.registeredBlocks.keySet().toArray()[i], "inventory");
            itemToRegister = Item.getItemFromBlock((Block) CarpentryContent.registeredBlocks.values().toArray()[i]);

            modelMesher.register(itemToRegister, 0, modelResourceLocation);
        }

        for (int i = 0; i < CarpentryContent.registeredItems.size(); i++) {
            modelResourceLocation = new ModelResourceLocation(CarpentryMod.RESOURCE_DOMAIN + CarpentryContent.registeredItems.keySet().toArray()[i], "inventory");
            itemToRegister = (Item) CarpentryContent.registeredItems.values().toArray()[i];
            if (CarpentryContent.skipItemMesh.contains(itemToRegister) || itemToRegister instanceof IResourceHolder)
                continue;
            modelMesher.register(itemToRegister, 0, modelResourceLocation);
        }
    }

    @SubscribeEvent
    public void onModelBakeEvent(ModelBakeEvent e) {

    }

    @SubscribeEvent
    public void onModelRegistryEvent(ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(new CarpentryModelLoader());
    }

}
