package com.elytradev.carpentrycubes.common.block;

import com.elytradev.carpentrycubes.client.render.model.ICarpentryModel;
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
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
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
import java.util.Random;

public class BlockCarpentry extends BlockContainer {

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
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (world != null) {
            TileEntity tileEntity = world.getTileEntity(pos);
            TileCarpentry tile = tileEntity instanceof TileCarpentry ? (TileCarpentry) tileEntity : null;
            if (tile != null && tile.hasCoverState()) {
                return tile.getCoverState().getLightValue();
            }
        }

        return super.getLightValue(state, world, pos);
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (world != null) {
            TileEntity tileEntity = world.getTileEntity(pos);
            TileCarpentry tile = tileEntity instanceof TileCarpentry ? (TileCarpentry) tileEntity : null;
            if (tile != null && tile.hasCoverState()) {
                return tile.getCoverState().getLightOpacity();
            }
        }

        return super.getLightOpacity(state, world, pos);
    }

    @Override
    public boolean addLandingEffects(IBlockState state, WorldServer world, BlockPos pos, IBlockState state2, EntityLivingBase entity, int numberOfParticles) {
        if (world != null) {
            TileEntity tileEntity = world.getTileEntity(pos);
            TileCarpentry tile = tileEntity instanceof TileCarpentry ? (TileCarpentry) tileEntity : null;
            if (tile != null && tile.hasCoverState()) {
                world.spawnParticle(EnumParticleTypes.BLOCK_DUST, entity.posX, entity.posY, entity.posZ,
                        numberOfParticles, 0.0D, 0.0D, 0.0D, 0.15000000596046448D,
                        Block.getStateId(tile.getCoverState()));
                return true;
            }
        }
        return super.addLandingEffects(state, world, pos, state2, entity, numberOfParticles);
    }

    @Override
    public boolean addRunningEffects(IBlockState state, World world, BlockPos pos, Entity entity) {
        if (world != null) {
            TileEntity tileEntity = world.getTileEntity(pos);
            TileCarpentry tile = tileEntity instanceof TileCarpentry ? (TileCarpentry) tileEntity : null;
            if (tile != null && tile.hasCoverState()) {
                IBlockState coverState = tile.getCoverState();
                Random rand = new Random();
                double x = entity.posX, z = entity.posZ, motionX = entity.motionX, motionZ = entity.motionZ;
                AxisAlignedBB bb = entity.getEntityBoundingBox();
                world.spawnParticle(EnumParticleTypes.BLOCK_CRACK,
                        x + ((double) rand.nextFloat() - 0.5D) * (double) entity.width, bb.minY + 0.1D,
                        z + ((double) rand.nextFloat() - 0.5D) * (double) entity.width, -motionX * 4.0D,
                        1.5D, -motionZ * 4.0D, Block.getStateId(coverState));

                return true;
            }
        }
        return super.addRunningEffects(state, world, pos, entity);
    }

    @Override
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        if (world != null) {
            TileEntity tileEntity = world.getTileEntity(target.getBlockPos());
            TileCarpentry tile = tileEntity instanceof TileCarpentry ? (TileCarpentry) tileEntity : null;
            if (tile != null && tile.hasCoverState()) {
                IBlockState coverState = tile.getCoverState();
                int particleId = EnumParticleTypes.BLOCK_CRACK.getParticleID();

                EnumFacing side = target.sideHit;
                BlockPos pos = target.getBlockPos();
                Random rand = new Random();
                AxisAlignedBB axisalignedbb = state.getBoundingBox(world, pos);
                double x = (double) pos.getX() + rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minX;
                double y = (double) pos.getY() + rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minY;
                double z = (double) pos.getZ() + rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minZ;

                if (side == EnumFacing.DOWN) {
                    y = (double) pos.getY() + axisalignedbb.minY - 0.10000000149011612D;
                }

                if (side == EnumFacing.UP) {
                    y = (double) pos.getY() + axisalignedbb.maxY + 0.10000000149011612D;
                }

                if (side == EnumFacing.NORTH) {
                    z = (double) pos.getZ() + axisalignedbb.minZ - 0.10000000149011612D;
                }

                if (side == EnumFacing.SOUTH) {
                    z = (double) pos.getZ() + axisalignedbb.maxZ + 0.10000000149011612D;
                }

                if (side == EnumFacing.WEST) {
                    x = (double) pos.getX() + axisalignedbb.minX - 0.10000000149011612D;
                }

                if (side == EnumFacing.EAST) {
                    x = (double) pos.getX() + axisalignedbb.maxX + 0.10000000149011612D;
                }

                manager.spawnEffectParticle(particleId, x, y, z, 0, 0, 0, Block.getStateId(coverState))
                        .multiplyVelocity(0.2F)
                        .multipleParticleScaleBy(0.6F);
                return true;
            }
        }
        return super.addHitEffects(state, world, target, manager);
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        if (world != null) {
            TileEntity tileEntity = world.getTileEntity(pos);
            TileCarpentry tile = tileEntity instanceof TileCarpentry ? (TileCarpentry) tileEntity : null;
            if (tile != null && tile.hasCoverState()) {
                IBlockState coverState = tile.getCoverState();
                int particleId = EnumParticleTypes.BLOCK_CRACK.getParticleID();
                for (int j = 0; j < 4; ++j) {
                    for (int k = 0; k < 4; ++k) {
                        for (int l = 0; l < 4; ++l) {
                            double d0 = ((double) j + 0.5D) / 4.0D;
                            double d1 = ((double) k + 0.5D) / 4.0D;
                            double d2 = ((double) l + 0.5D) / 4.0D;
                            ParticleDigging particle = (ParticleDigging) manager.spawnEffectParticle(particleId,
                                    (double) pos.getX() + d0,
                                    (double) pos.getY() + d1,
                                    (double) pos.getZ() + d2,
                                    d0 - 0.5D, d1 - 0.5D, d2 - 0.5D,
                                    Block.getStateId(coverState));
                            particle.setBlockPos(pos);
                        }
                    }
                }
                return true;
            }
        }
        return super.addDestroyEffects(world, pos, manager);
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

    public ICarpentryModel<? extends BlockCarpentry> getModel() {
        return null;
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
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        if (state instanceof IExtendedBlockState) {
            TileCarpentry tile = ((IExtendedBlockState) state).getValue(CARPENTRY_TILE);
            if (tile != null && tile.hasCoverState()) {
                return tile.getCoverState().getBlock().canRenderInLayer(tile.getCoverState(), layer);
            }
        }
        return layer == BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isTranslucent(IBlockState state) {
        if (state != null && state.getBlock() instanceof BlockCarpentry) {
            IExtendedBlockState extendedState = (IExtendedBlockState) state;
            TileCarpentry carpentryTile = extendedState.getValue(BlockCarpentry.CARPENTRY_TILE);
            return carpentryTile.getCoverState().isTranslucent();
        }

        return super.isTranslucent(state);
    }

}
