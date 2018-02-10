package com.elytradev.carpentrycubes.common.block;

import com.elytradev.carpentrycubes.common.block.prop.UnlistedBlockStateProperty;
import com.elytradev.carpentrycubes.common.network.TileUpdateMessage;
import com.elytradev.carpentrycubes.common.tile.TileCarpentry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nullable;

public class BlockCarpentry extends BlockContainer {

    public static final IUnlistedProperty<IBlockState> COVERSTATE = UnlistedBlockStateProperty.create("cover");

    public BlockCarpentry(Material materialIn) {
        super(materialIn);
    }

    protected IProperty[] getProperties() {
        return new IProperty[0];
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote && worldIn.getTileEntity(pos) instanceof TileCarpentry) {
            TileCarpentry tileCarpentry = (TileCarpentry) worldIn.getTileEntity(pos);
            ItemStack heldItem = playerIn.getHeldItem(hand);
            if (Block.getBlockFromItem(heldItem.getItem()) != Blocks.AIR && !playerIn.isSneaking()) {
                Block block = Block.getBlockFromItem(heldItem.getItem());
                tileCarpentry.setCoverState(block.getStateFromMeta(heldItem.getMetadata()));
                new TileUpdateMessage(tileCarpentry).sendToAllWatching(tileCarpentry);
                return true;
            }
        }

        return !playerIn.isSneaking();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, getProperties(), new IUnlistedProperty[]{COVERSTATE});
    }

    @Override
    public BlockStateContainer getBlockState() {
        return super.getBlockState();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state instanceof IExtendedBlockState && world.getTileEntity(pos) instanceof TileCarpentry) {
            TileCarpentry tileCarpentry = (TileCarpentry) world.getTileEntity(pos);
            state = ((IExtendedBlockState) state).withProperty(COVERSTATE, tileCarpentry.getCoverState());
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
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        return super.addDestroyEffects(world, pos, manager);
    }
}
