package com.elytradev.carpentrycubes.common.block;

import com.elytradev.carpentrycubes.common.CarpentryContent;
import com.elytradev.carpentrycubes.common.block.prop.UnlistedCarpentryTileProperty;
import com.elytradev.carpentrycubes.common.item.ItemCarpentryHammer.EnumToolMode;
import com.elytradev.carpentrycubes.common.network.TileUpdateMessage;
import com.elytradev.carpentrycubes.common.tile.TileCarpentry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
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
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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

public abstract class BlockCarpentry extends BlockContainer implements IBlockCarpentry {

    public static final IUnlistedProperty<TileCarpentry> CARPENTRY_TILE = UnlistedCarpentryTileProperty.create("tile");

    public BlockCarpentry(Material materialIn) {
        super(materialIn);
    }

    protected IProperty[] getProperties() {
        return new IProperty[0];
    }

    protected IUnlistedProperty[] getUnlistedProperties() {
        return new IUnlistedProperty[0];
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.getTileEntity(pos) instanceof TileCarpentry) {
            TileCarpentry tileCarpentry = (TileCarpentry) worldIn.getTileEntity(pos);
            ItemStack heldItem = playerIn.getHeldItem(hand);
            EnumToolMode toolMode = null;
            if (heldItem.getItem().equals(CarpentryContent.itemHammer))
                toolMode = EnumToolMode.byId(heldItem.getMetadata());

            if (tileCarpentry.hasCoverState()) {
                if (toolMode == EnumToolMode.SCRAPE) {
                    if (!worldIn.isRemote)
                        tileCarpentry.removeCoverState(true);
                    return true;
                } else {
                    return false;
                }
            }
            if (!worldIn.isRemote && Block.getBlockFromItem(heldItem.getItem()) != Blocks.AIR
                    && !playerIn.isSneaking()) {
                Block block = Block.getBlockFromItem(heldItem.getItem());
                IBlockState coverState = block.getStateFromMeta(heldItem.getMetadata());
                if (block instanceof BlockCarpentry || !coverState.isFullBlock())
                    return false;
                tileCarpentry.setCoverState(coverState);
                new TileUpdateMessage(tileCarpentry).sendToAllWatching(tileCarpentry);
                return true;
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
        if (state instanceof IExtendedBlockState && world.getTileEntity(pos) instanceof TileCarpentry) {
            TileCarpentry tileCarpentry = (TileCarpentry) world.getTileEntity(pos);
            state = ((IExtendedBlockState) state).withProperty(CARPENTRY_TILE, tileCarpentry);
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
        return new TileCarpentry();
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

}
