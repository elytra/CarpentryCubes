package com.elytradev.carpentrycubes.client.proxy;

import com.elytradev.carpentrycubes.client.render.model.CarpentryCubeModel;
import com.elytradev.carpentrycubes.client.render.model.CarpentrySlopeModel;
import com.elytradev.carpentrycubes.client.render.model.builder.CarpentryBakedModel;
import com.elytradev.carpentrycubes.client.render.model.builder.CarpentryModelLoader;
import com.elytradev.carpentrycubes.common.CarpentryContent;
import com.elytradev.carpentrycubes.common.CarpentryMod;
import com.elytradev.carpentrycubes.common.block.BlockCarpentry;
import com.elytradev.carpentrycubes.common.proxy.CommonProxy;
import com.elytradev.carpentrycubes.common.tile.TileCarpentry;
import com.elytradev.concrete.resgen.ConcreteResourcePack;
import com.elytradev.concrete.resgen.IResourceHolder;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collection;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerRenderers(LoaderState.ModState state) {
        if (state == LoaderState.ModState.PREINITIALIZED) {
            new ConcreteResourcePack(CarpentryMod.MOD_ID);
        }

        if (state == LoaderState.ModState.INITIALIZED) {
            registerBlockColours();
            registerItemRenderers();
        }
    }

    private void registerBlockColours() {
        BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();
        Collection<Block> values = CarpentryContent.registeredBlocks.values();
        Block[] acceptedBlocks = new Block[values.size()];
        values.toArray(acceptedBlocks);
        blockColors.registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
            if (worldIn == null || pos == null)
                return -1;
            if (state.getBlock() instanceof BlockCarpentry) {
                TileEntity tile = worldIn.getTileEntity(pos);
                if (tile instanceof TileCarpentry && ((TileCarpentry) tile).hasCoverState()) {
                    TileCarpentry carpentryTile = (TileCarpentry) tile;

                    int color = blockColors.colorMultiplier(carpentryTile.getCoverState(), worldIn, pos, tintIndex);
                    return color;
                }
            }

            return -1;
        }, acceptedBlocks);
    }


    public void registerItemRenderers() {
        Item itemToRegister = null;
        ModelResourceLocation modelResourceLocation = null;

        // Do some general render registrations for OBJECTS, not considering meta.
        ItemModelMesher modelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        for (int i = 0; i < CarpentryContent.registeredBlocks.size(); i++) {
            modelResourceLocation = new ModelResourceLocation(
                    CarpentryMod.RESOURCE_DOMAIN + CarpentryContent.registeredBlocks.keySet().toArray()[i], "inventory");
            itemToRegister = Item.getItemFromBlock((Block) CarpentryContent.registeredBlocks.values().toArray()[i]);

            modelMesher.register(itemToRegister, 0, modelResourceLocation);
        }

        for (int i = 0; i < CarpentryContent.registeredItems.size(); i++) {
            modelResourceLocation = new ModelResourceLocation(
                    CarpentryMod.RESOURCE_DOMAIN + CarpentryContent.registeredItems.keySet().toArray()[i], "inventory");
            itemToRegister = (Item) CarpentryContent.registeredItems.values().toArray()[i];
            if (CarpentryContent.skipItemMesh.contains(itemToRegister) || itemToRegister instanceof IResourceHolder)
                continue;
            modelMesher.register(itemToRegister, 0, modelResourceLocation);
        }
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent e) {
        e.getMap().registerSprite(new ResourceLocation(CarpentryMod.MOD_ID, "blocks/foursectionframe"));
    }

    @SubscribeEvent
    public void onModelBakeEvent(ModelBakeEvent e) {
    }

    @SubscribeEvent
    public void onModelRegistryEvent(ModelRegistryEvent event) {
        CarpentryModelLoader carpentryModelLoader = new CarpentryModelLoader();

        carpentryModelLoader.registerModel((resourceLocation -> resourceLocation.getPath().startsWith("carpentryslope")),
                (state, format, textureGetter) -> new CarpentryBakedModel(CarpentrySlopeModel.getInstance()));
        carpentryModelLoader.registerModel((resourceLocation -> resourceLocation.getPath().startsWith("carpentrycube")),
                (state, format, textureGetter) -> new CarpentryBakedModel(CarpentryCubeModel.getInstance()));

        ModelLoaderRegistry.registerLoader(carpentryModelLoader);
    }

}
