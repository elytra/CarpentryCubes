package com.elytradev.carpentrycubes.common;

import static com.elytradev.carpentrycubes.common.CarpentryMod.MOD_ID;

import com.elytradev.carpentrycubes.common.block.BlockCarpentrySlope;
import com.elytradev.carpentrycubes.common.item.ItemCarpentryHammer;
import com.elytradev.carpentrycubes.common.tile.TileCarpentry;
import com.elytradev.carpentrycubes.common.tile.TileCarpentrySlope;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

public class CarpentryContent {

    public static HashMap<String, Block> registeredBlocks;
    public static HashMap<String, Item> registeredItems;
    public static List<Object> skipItemMesh;
    public static String REGISTRY_PREFIX = MOD_ID.toLowerCase();
    public static BlockCarpentrySlope blockSlope;
    public static ItemCarpentryHammer itemHammer;
    private static List<Item> itemBlocksToRegister;
    public int recipeID = 0;
    private CreativeTabs creativeTab = new CreativeTabs(CarpentryMod.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return Items.BAKED_POTATO.getDefaultInstance();
        }
    };

    public CarpentryContent() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void preInit(FMLPreInitializationEvent e) {
    }

    public void init(FMLInitializationEvent e) {
        // register tiles
        GameRegistry.registerTileEntity(TileCarpentry.class, REGISTRY_PREFIX + ":" + "tileCarpentry");
        GameRegistry.registerTileEntity(TileCarpentrySlope.class, REGISTRY_PREFIX + ":" + "tileCarpentrySlope");
    }

    public void postInit(FMLPostInitializationEvent e) {
    }

    @SubscribeEvent
    public void onRecipeRegisterEvent(RegistryEvent.Register<IRecipe> event) {
        IForgeRegistry<IRecipe> registry = event.getRegistry();
    }

    @SubscribeEvent
    public void onBlockRegisterEvent(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        registeredBlocks = new HashMap<>();
        itemBlocksToRegister = new ArrayList<>();

        blockSlope = new BlockCarpentrySlope(Material.WOOD);
        registerBlock(registry, "carpentrySlope", blockSlope, true);
    }

    @SubscribeEvent
    public void onItemRegisterEvent(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        registeredItems = new HashMap<>();
        skipItemMesh = new ArrayList<>();
        itemBlocksToRegister.forEach(registry::register);

        itemHammer = new ItemCarpentryHammer();
        registerItem(registry, "carpentryTool", itemHammer);
    }

    private void registerShapedRecipe(IForgeRegistry<IRecipe> registry, ItemStack out, Object... input) {
        ResourceLocation resourceLocation = new ResourceLocation(MOD_ID, out.getTranslationKey() + recipeID++);
        registry.register(new ShapedOreRecipe(resourceLocation, out, input).setRegistryName(resourceLocation));
    }

    private void registerShapelessRecipe(IForgeRegistry<IRecipe> registry, ItemStack out, Object... input) {
        ResourceLocation resourceLocation = new ResourceLocation(MOD_ID, out.getTranslationKey() + recipeID++);
        registry.register(new ShapelessOreRecipe(resourceLocation, out, input).setRegistryName(resourceLocation));
    }

    private void registerBlock(IForgeRegistry<Block> registry, String id, Block block) {
        registerBlock(registry, id, block, true);
    }

    private void registerBlock(IForgeRegistry<Block> registry, String id, Block block, boolean withItemBlock) {
        block.setTranslationKey("carpentrycubes." + id);
        block.setRegistryName(REGISTRY_PREFIX, id);
        block.setCreativeTab(creativeTab);
        registry.register(block);
        if (withItemBlock)
            itemBlocksToRegister.add(new ItemBlock(block).setRegistryName(block.getRegistryName()));
        CarpentryContent.registeredBlocks.put(id, block);
    }

    private void registerBlock(IForgeRegistry<Block> registry, String id, Block block,
        Class<? extends ItemBlock> itemBlockClass) {
        try {
            block.setTranslationKey("carpentrycubes." + id);
            block.setRegistryName(REGISTRY_PREFIX, id);
            registry.register(block);

            ItemBlock itemBlock = itemBlockClass.getDeclaredConstructor(Block.class).newInstance(block);
            itemBlock.setRegistryName(REGISTRY_PREFIX, id);
            itemBlock.setCreativeTab(creativeTab);
            itemBlocksToRegister.add(itemBlock);
            CarpentryContent.registeredBlocks.put(id, block);
        } catch (Exception e) {
            CarpentryLog.error("Caught exception while registering " + block, e);
        }
    }

    private void registerItem(IForgeRegistry<Item> registry, String id, Item item) {
        item.setTranslationKey("carpentrycubes." + id);
        item.setRegistryName(REGISTRY_PREFIX, id);
        item.setCreativeTab(creativeTab);
        registry.register(item);
        CarpentryContent.registeredItems.put(id, item);
    }


}