package com.elytradev.carpentrycubes.common.block;

import com.elytradev.carpentrycubes.client.render.model.builder.ICarpentryModel;
import com.elytradev.carpentrycubes.common.CarpentryContent;
import com.elytradev.carpentrycubes.common.block.prop.UnlistedBlockAccessProperty;
import com.elytradev.carpentrycubes.common.block.prop.UnlistedBlockPosProperty;
import com.elytradev.carpentrycubes.common.block.prop.UnlistedBlockStateProperty;
import com.elytradev.carpentrycubes.common.item.ItemCarpentryHammer.EnumToolMode;
import com.elytradev.carpentrycubes.common.network.TileUpdateMessage;
import com.elytradev.carpentrycubes.common.tile.TileCarpentry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;

public class BlockCarpentry extends BlockContainer {

    public static final IUnlistedProperty<IBlockState> COVERSTATE = UnlistedBlockStateProperty.create("cover");
    public static final IUnlistedProperty<IBlockAccess> BLOCK_ACCESS = UnlistedBlockAccessProperty.create("access");
    public static final IUnlistedProperty<BlockPos> POS = UnlistedBlockPosProperty.create("pos");

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

    public ICarpentryModel<? extends BlockCarpentry> getModel() {
        return null;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, getProperties(), ArrayUtils.addAll(new IUnlistedProperty[]{COVERSTATE,
                BLOCK_ACCESS, POS}, getUnlistedProperties()));
    }

    @Override
    public BlockStateContainer getBlockState() {
        return super.getBlockState();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state instanceof IExtendedBlockState && world.getTileEntity(pos) instanceof TileCarpentry) {
            TileCarpentry tileCarpentry = (TileCarpentry) world.getTileEntity(pos);
            state = ((IExtendedBlockState) state).withProperty(COVERSTATE, tileCarpentry.getCoverState())
                    .withProperty(BLOCK_ACCESS, world)
                    .withProperty(POS, pos);
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
        return true;
    }

    @Override
    public boolean isTranslucent(IBlockState state) {
        if (state != null && state.getBlock() instanceof BlockCarpentry) {
            IExtendedBlockState extendedState = (IExtendedBlockState) state;
            IBlockState coverState = extendedState.getValue(BlockCarpentry.COVERSTATE);
            return coverState.isTranslucent();
        }

        return super.isTranslucent(state);
    }

    public BlockRenderLayer getRenderLayer(IBlockAccess access, BlockPos pos) {
        if (access != null && pos != null) {
            TileEntity tileEntity = access.getTileEntity(pos);
            if (tileEntity instanceof TileCarpentry && ((TileCarpentry) tileEntity).hasCoverState()) {
                return ((TileCarpentry) tileEntity).getCoverState().getBlock().getRenderLayer();
            }
        }

        return BlockRenderLayer.CUTOUT;
    }
}
