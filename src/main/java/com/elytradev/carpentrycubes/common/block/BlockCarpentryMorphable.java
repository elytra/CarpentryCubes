package com.elytradev.carpentrycubes.common.block;

import com.elytradev.carpentrycubes.client.render.model.ICarpentryModel;
import com.elytradev.carpentrycubes.client.render.model.data.CarpentryMorphableModel;
import com.elytradev.carpentrycubes.common.CarpentryContent;
import com.elytradev.carpentrycubes.common.item.ItemCarpentryHammer;
import com.elytradev.carpentrycubes.common.network.TileUpdateMessage;
import com.elytradev.carpentrycubes.common.tile.TileCarpentry;
import com.elytradev.carpentrycubes.common.tile.TileCarpentryMorphable;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Objects;

public class BlockCarpentryMorphable extends BlockCarpentry {

    public static PropertyDirection FACING = PropertyDirection.create("facing");

    public BlockCarpentryMorphable(Material materialIn) {
        super(materialIn);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, facing);
    }


    @Override
    protected IProperty[] getProperties() {
        return new IProperty[]{FACING};
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public ICarpentryModel<? extends BlockCarpentry> getModel() {
        return CarpentryMorphableModel.getInstance();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        boolean result = super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
        if (!worldIn.isRemote) {
            ItemStack heldItem = playerIn.getHeldItem(hand);
            if (blockState.getBlock() instanceof BlockCarpentryMorphable
                    && Objects.equals(heldItem.getItem(), CarpentryContent.itemHammer)) {

                ItemCarpentryHammer.EnumToolMode toolMode = ItemCarpentryHammer.EnumToolMode.byId(heldItem.getMetadata());
                TileEntity tileEntity = worldIn.getTileEntity(pos);
                TileCarpentryMorphable carpentryMorphable =
                        tileEntity instanceof TileCarpentryMorphable ? (TileCarpentryMorphable) tileEntity : null;
                if (carpentryMorphable != null) {
                    switch (toolMode) {
                        case ROTATE:
                            int newRotation = state.getValue(FACING).getIndex();

                            if (playerIn.isSneaking()) {
                                newRotation = newRotation == 0 ? 5 : newRotation - 1;
                            } else {
                                newRotation = newRotation == 5 ? 0 : newRotation + 1;
                            }
                            worldIn.setBlockState(pos, state.withProperty(FACING, EnumFacing.byIndex(newRotation)));
                            result = true;
                            break;
                        case TWEAK:
                            int heightDelta = (playerIn.isSneaking() ? -1 : 1);

                            if (hitX >= 0F && hitX < 0.5F) {
                                //WEST
                                if (hitZ >= 0F && hitZ < 0.5F) {
                                    //SOUTH EAST
                                    carpentryMorphable.applyDeltaNorthWest(heightDelta);
                                } else {
                                    //NORTH EAST
                                    carpentryMorphable.applyDeltaSouthWest(heightDelta);
                                }
                            } else {
                                //EAST
                                if (hitZ >= 0F && hitZ < 0.5F) {
                                    //SOUTH EAST
                                    carpentryMorphable.applyDeltaNorthEast(heightDelta);
                                } else {
                                    //NORTH EAST
                                    carpentryMorphable.applyDeltaSouthEast(heightDelta);
                                }
                            }
                            result = true;
                            break;
                        default:
                            break;
                    }
                    new TileUpdateMessage(carpentryMorphable).sendToAllWatching(carpentryMorphable);
                }
            }

            if (result) {
                worldIn.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1F, 2);
            }
        }

        return result;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        if (state instanceof IExtendedBlockState) {
            TileCarpentry stateTile = ((IExtendedBlockState) state).getValue(BlockCarpentry.CARPENTRY_TILE);
            if (stateTile instanceof TileCarpentryMorphable) {
                TileCarpentryMorphable tile = (TileCarpentryMorphable) stateTile;
                int northWestHeight = tile.getNorthWestHeight();
                int northEastHeight = tile.getNorthEastHeight();
                int southWestHeight = tile.getSouthWestHeight();
                int southEastHeight = tile.getSouthEastHeight();
                return northWestHeight == 16 && northEastHeight == 16 && southWestHeight == 16 && southEastHeight == 16;
            }
        }

        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        if (state instanceof IExtendedBlockState) {
            TileCarpentry stateTile = ((IExtendedBlockState) state).getValue(BlockCarpentry.CARPENTRY_TILE);
            if (stateTile instanceof TileCarpentryMorphable) {
                return ((TileCarpentryMorphable) stateTile).isNormalCube();
            }
        }

        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        if (state instanceof IExtendedBlockState) {
            TileCarpentry stateTile = ((IExtendedBlockState) state).getValue(BlockCarpentry.CARPENTRY_TILE);
            if (stateTile instanceof TileCarpentryMorphable) {
                boolean isCoverOpaque = stateTile.hasCoverState() && stateTile.getCoverState().isOpaqueCube();
                return ((TileCarpentryMorphable) stateTile).isNormalCube() && isCoverOpaque;
            }
        }
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


    @Override
    public boolean isTopSolid(IBlockState state) {
        return super.isTopSolid(state);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCarpentryMorphable();
    }
}
