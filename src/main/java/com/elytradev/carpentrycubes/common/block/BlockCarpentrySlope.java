package com.elytradev.carpentrycubes.common.block;

import com.elytradev.carpentrycubes.client.render.model.CarpentrySlopeModel;
import com.elytradev.carpentrycubes.client.render.model.ICarpentersModel;
import com.elytradev.carpentrycubes.common.block.prop.UnlistedEnumProperty;
import com.elytradev.carpentrycubes.common.tile.TileCarpentry;
import com.elytradev.carpentrycubes.common.tile.TileCarpentrySlope;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCarpentrySlope extends BlockCarpentry {

    public static PropertyDirection FACING = PropertyDirection.create("facing");
    public static PropertyBool CEILING = PropertyBool.create("ceiling");
    public static IUnlistedProperty<BlockStairs.EnumShape> SHAPE = UnlistedEnumProperty.create("shape", BlockStairs.EnumShape.class);

    public BlockCarpentrySlope(Material materialIn) {
        super(materialIn);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public ICarpentersModel<? extends BlockCarpentry> getModel() {
        return CarpentrySlopeModel.getInstance();
    }

    @Override
    protected IProperty[] getProperties() {
        return new IProperty[]{FACING, CEILING};
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing front = EnumFacing.getFront(meta & 7);
        if (front.getAxis() == EnumFacing.Axis.Y) {
            front = EnumFacing.NORTH;
        }
        return this.getDefaultState().withProperty(FACING, front).withProperty(CEILING, (meta & 8) > 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | state.getValue(FACING).getIndex();

        if (state.getValue(CEILING).booleanValue()) {
            i |= 8;
        }

        return i;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        state = super.getExtendedState(state, world, pos);
        if (state instanceof IExtendedBlockState && world.getTileEntity(pos) instanceof TileCarpentrySlope) {
            TileCarpentrySlope tileCarpentrySlope = (TileCarpentrySlope) world.getTileEntity(pos);
            state = ((IExtendedBlockState) state).withProperty(SHAPE, tileCarpentrySlope.getShape());
        }
        return state;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess access, BlockPos pos, EnumFacing side) {
        if (access != null && pos != null) {
            TileEntity tileEntity = access.getTileEntity(pos);
            if (tileEntity instanceof TileCarpentry) {
                TileCarpentry tileCarpentry = (TileCarpentry) tileEntity;
                return tileCarpentry.getCoverState().getBlock().shouldSideBeRendered(tileCarpentry.getCoverState(), access, pos, side);
            }
        }

        return super.shouldSideBeRendered(state, access, pos, side);
    }
}