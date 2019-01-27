package com.elytradev.carpentrycubes.common.block;

import com.elytradev.carpentrycubes.client.render.model.ICarpentryModel;
import com.elytradev.carpentrycubes.client.render.model.data.CarpentryDoorModel;
import com.elytradev.carpentrycubes.common.CarpentryContent;
import com.elytradev.carpentrycubes.common.block.prop.UnlistedEnumProperty;
import com.elytradev.carpentrycubes.common.item.ItemCarpentryHammer;
import com.elytradev.carpentrycubes.common.network.TileUpdateMessage;
import com.elytradev.carpentrycubes.common.tile.TileCarpentry;
import com.elytradev.carpentrycubes.common.tile.TileCarpentryDoor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;

public class BlockCarpentryDoor extends BlockDoor implements IBlockCarpentry, ITileEntityProvider {

    public static final IUnlistedProperty<TileCarpentry> CARPENTRY_TILE = BlockCarpentry.CARPENTRY_TILE;
    public static IUnlistedProperty<EnumDoorStyle> DOOR_STYLE = UnlistedEnumProperty.create("door_style", EnumDoorStyle.class);

    public BlockCarpentryDoor(Material materialIn) {
        super(materialIn);
    }

    @Override
    public ICarpentryModel<? extends IBlockCarpentry> getModel() {
        return CarpentryDoorModel.getInstance();
    }

    protected IProperty[] getProperties() {
        return new IProperty[]{HALF, FACING, OPEN, HINGE, POWERED};
    }

    protected IUnlistedProperty[] getUnlistedProperties() {
        return new IUnlistedProperty[]{DOOR_STYLE};
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        EnumDoorHalf half = state.getValue(HALF);
        BlockPos oPos = pos;
        if (half == EnumDoorHalf.UPPER) {
            pos = pos.offset(EnumFacing.DOWN);
        }

        if (worldIn.getTileEntity(pos) instanceof TileCarpentryDoor) {
            TileCarpentryDoor carpentryDoor = (TileCarpentryDoor) worldIn.getTileEntity(pos);
            ItemStack heldItem = playerIn.getHeldItem(hand);
            ItemCarpentryHammer.EnumToolMode toolMode = null;
            if (heldItem.getItem().equals(CarpentryContent.itemHammer)) {
                toolMode = ItemCarpentryHammer.EnumToolMode.byId(heldItem.getMetadata());
            }
            // Use tool if present.
            if (toolMode != null) {
                if (toolMode == ItemCarpentryHammer.EnumToolMode.SCRAPE) {
                    if (!worldIn.isRemote) {
                        carpentryDoor.removeCoverState(true);
                        new TileUpdateMessage(carpentryDoor).sendToAllWatching(carpentryDoor);
                    }
                    return true;
                } else if (toolMode == ItemCarpentryHammer.EnumToolMode.TWEAK) {
                    if (!worldIn.isRemote) {
                        int styleCount = EnumDoorStyle.values().length;
                        carpentryDoor.setStyle(EnumDoorStyle.values()[carpentryDoor.getStyle().ordinal() == styleCount - 1 ? 0 : carpentryDoor.getStyle().ordinal() + 1]);
                        new TileUpdateMessage(carpentryDoor).sendToAllWatching(carpentryDoor);
                    }
                    return true;
                } else {
                    return false;
                }
            }
            if (!carpentryDoor.hasCoverState() && Block.getBlockFromItem(heldItem.getItem()) != Blocks.AIR && !playerIn.isSneaking()) {
                if (worldIn.isRemote) {
                    return true;
                }
                Block block = Block.getBlockFromItem(heldItem.getItem());
                IBlockState coverState = block.getStateFromMeta(heldItem.getMetadata());
                if (block instanceof BlockCarpentry || !coverState.isFullBlock())
                    return false;
                carpentryDoor.setCoverState(coverState);
                new TileUpdateMessage(carpentryDoor).sendToAllWatching(carpentryDoor);
                return true;
            } else {
                return super.onBlockActivated(worldIn, oPos, state, playerIn, hand, facing, hitX, hitY, hitZ);
            }
        }

        return !playerIn.isSneaking();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, getProperties(), ArrayUtils.addAll(new IUnlistedProperty[]{CARPENTRY_TILE}, getUnlistedProperties()));
    }

    @Override
    public BlockStateContainer getBlockState() {
        return super.getBlockState();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        EnumDoorHalf half = state.getValue(HALF);
        if (half == EnumDoorHalf.UPPER) {
            pos = pos.offset(EnumFacing.DOWN);
        }

        if (state instanceof IExtendedBlockState && world.getTileEntity(pos) instanceof TileCarpentryDoor) {
            IExtendedBlockState extendedState = (IExtendedBlockState) state;
            TileCarpentryDoor tileCarpentry = (TileCarpentryDoor) world.getTileEntity(pos);
            extendedState = extendedState.withProperty(CARPENTRY_TILE, tileCarpentry).withProperty(DOOR_STYLE, tileCarpentry.getStyle());
            state = extendedState;
        }
        return super.getExtendedState(state, world, pos);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        IBlockState stateFromMeta = getStateFromMeta(meta);
        if (stateFromMeta.getValue(HALF) == EnumDoorHalf.LOWER) {
            return new TileCarpentryDoor();
        } else {
            return null;
        }
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return this.getBlockLightValue(state, world, pos);
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        return this.getBlockLightOpacity(state, world, pos);
    }

    @Override
    public boolean addLandingEffects(IBlockState state, WorldServer world, BlockPos pos, IBlockState state2, EntityLivingBase entity, int numberOfParticles) {
        return this.spawnLandingEffects(state, world, pos, state2, entity, numberOfParticles);
    }

    @Override
    public boolean addRunningEffects(IBlockState state, World world, BlockPos pos, Entity entity) {
        return this.spawnRunningEffects(state, world, pos, entity);
    }

    @Override
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        return this.spawnHitEffects(state, world, target, manager);
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        return this.spawnDestroyEffects(world, pos, manager);
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return this.renderBlockInLayer(state, layer);
    }

    @Override
    public boolean isTranslucent(IBlockState state) {
        return this.isBlockTranslucent(state);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }

    public enum EnumDoorStyle implements IStringSerializable {
        FOUR_WINDOW("four_window"),
        SOLID("solid"),
        SLATTED("slatted");

        private final String name;

        EnumDoorStyle(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }
    }

}
