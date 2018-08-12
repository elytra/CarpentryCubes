package com.elytradev.carpentrycubes.common.block;

import com.elytradev.carpentrycubes.client.render.model.CarpentrySlopeModel;
import com.elytradev.carpentrycubes.client.render.model.builder.ICarpentryModel;
import com.elytradev.carpentrycubes.common.CarpentryContent;
import com.elytradev.carpentrycubes.common.block.prop.UnlistedEnumProperty;
import com.elytradev.carpentrycubes.common.item.ItemCarpentryHammer.EnumToolMode;
import com.elytradev.carpentrycubes.common.network.TileUpdateMessage;
import com.elytradev.carpentrycubes.common.tile.TileCarpentry;
import com.elytradev.carpentrycubes.common.tile.TileCarpentrySlope;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Objects;

public class BlockCarpentrySlope extends BlockCarpentry {

    public static PropertyDirection PRIMARY_DIRECTION = PropertyDirection.create("facing");
    public static IUnlistedProperty<EnumFacing> SECONDARY_DIRECTION = UnlistedEnumProperty.create("secondary_facing", EnumFacing.class);
    public static IUnlistedProperty<EnumShape> SHAPE = UnlistedEnumProperty.create("shape", EnumShape.class);
    public static IUnlistedProperty<EnumOrientation> ORIENTATION = UnlistedEnumProperty.create("orientation", EnumOrientation.class);

    public BlockCarpentrySlope(Material materialIn) {
        super(materialIn);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer) {
        // TODO: Check neighbouring positions for any other slopes.

        return this.getDefaultState().withProperty(PRIMARY_DIRECTION, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    protected IUnlistedProperty[] getUnlistedProperties() {
        return new IUnlistedProperty[]{SECONDARY_DIRECTION, SHAPE, ORIENTATION};
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        boolean result = super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
        if (!worldIn.isRemote) {
            ItemStack heldItem = playerIn.getHeldItem(hand);
            IBlockState blockState = worldIn.getBlockState(pos);
            if (blockState.getBlock() instanceof BlockCarpentrySlope
                    && Objects.equals(heldItem.getItem(), CarpentryContent.itemHammer)) {

                EnumToolMode toolMode = EnumToolMode.byId(heldItem.getMetadata());
                TileEntity tileEntity = worldIn.getTileEntity(pos);
                TileCarpentrySlope carpentrySlope =
                        tileEntity instanceof TileCarpentrySlope ? (TileCarpentrySlope) tileEntity : null;
                if (carpentrySlope != null) {
                    switch (toolMode) {
                        case TWEAK:
                            int shape = carpentrySlope.getShape().ordinal();
                            shape = shape >= EnumShape.values().length - 1 ? 0 : shape + 1;
                            carpentrySlope.setShape(EnumShape.values()[shape]);
                            break;
                        case ORIENT:
                            int orientation = carpentrySlope.getOrientation().ordinal();
                            orientation = orientation >= EnumOrientation.values().length - 1 ? 0 : orientation + 1;
                            carpentrySlope.setOrientation(EnumOrientation.values()[orientation]);
                            break;
                        case ROTATE:
                            if (playerIn.isSneaking()) {
                                int rotation = carpentrySlope.getSecondaryDirection().getHorizontalIndex();
                                rotation = rotation >= EnumFacing.HORIZONTALS.length - 1 ? 0 : rotation + 1;
                                carpentrySlope.setSecondaryDirection(EnumFacing.HORIZONTALS[rotation]);
                            } else {
                                int rotation = state.getValue(PRIMARY_DIRECTION).getHorizontalIndex();
                                rotation = rotation >= EnumFacing.HORIZONTALS.length - 1 ? 0 : rotation + 1;
                                worldIn.setBlockState(pos, state.withProperty(PRIMARY_DIRECTION, EnumFacing.HORIZONTALS[rotation]));
                            }
                            break;
                        default:
                            break;
                    }
                    new TileUpdateMessage(carpentrySlope).sendToAllWatching(carpentrySlope);
                }
            }

            if (result) {
                worldIn.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1F, 2);
            }
        }

        return result;
    }

    @Override
    public ICarpentryModel<? extends BlockCarpentry> getModel() {
        return CarpentrySlopeModel.getInstance();
    }

    @Override
    protected IProperty[] getProperties() {
        return new IProperty[]{PRIMARY_DIRECTION};
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(PRIMARY_DIRECTION, EnumFacing.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PRIMARY_DIRECTION).getIndex();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        state = super.getExtendedState(state, world, pos);
        TileEntity tile = world.getTileEntity(pos);
        if (state instanceof IExtendedBlockState && tile instanceof TileCarpentrySlope) {
            TileCarpentrySlope tileCarpentrySlope = (TileCarpentrySlope) tile;
            IExtendedBlockState extendedState = (IExtendedBlockState) state;
            state = extendedState.withProperty(SECONDARY_DIRECTION, tileCarpentrySlope.getSecondaryDirection())
                    .withProperty(SHAPE, tileCarpentrySlope.getShape())
                    .withProperty(ORIENTATION, tileCarpentrySlope.getOrientation());
        }
        return state;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
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

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCarpentrySlope();
    }

    public enum EnumShape implements IStringSerializable {
        STRAIGHT("straight"),
        INNER("inner"),
        OUTER("outer");

        private final String name;

        EnumShape(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }
    }

    public enum EnumOrientation implements IStringSerializable {
        GROUND("ground", 0),
        CEILING("ceiling", 180),
        WALL("wall", 90);

        private final String name;
        private final int zRotation;

        EnumOrientation(String name, int zRotation) {
            this.name = name;
            this.zRotation = zRotation;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        public int getZRotation() {
            return zRotation;
        }
    }
}
