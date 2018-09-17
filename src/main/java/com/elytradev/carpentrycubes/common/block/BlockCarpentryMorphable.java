package com.elytradev.carpentrycubes.common.block;

import com.elytradev.carpentrycubes.client.render.model.ICarpentryModel;
import com.elytradev.carpentrycubes.client.render.model.data.CarpentryMorphableModel;
import com.elytradev.carpentrycubes.common.CarpentryContent;
import com.elytradev.carpentrycubes.common.item.ItemCarpentryHammer;
import com.elytradev.carpentrycubes.common.network.TileUpdateMessage;
import com.elytradev.carpentrycubes.common.tile.TileCarpentry;
import com.elytradev.carpentrycubes.common.tile.TileCarpentryMorphable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Objects;

public class BlockCarpentryMorphable extends BlockCarpentry {
    public BlockCarpentryMorphable(Material materialIn) {
        super(materialIn);
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
            IBlockState blockState = worldIn.getBlockState(pos);
            if (blockState.getBlock() instanceof BlockCarpentryMorphable
                    && Objects.equals(heldItem.getItem(), CarpentryContent.itemHammer)) {

                ItemCarpentryHammer.EnumToolMode toolMode = ItemCarpentryHammer.EnumToolMode.byId(heldItem.getMetadata());
                TileEntity tileEntity = worldIn.getTileEntity(pos);
                TileCarpentryMorphable carpentryMorphable =
                        tileEntity instanceof TileCarpentryMorphable ? (TileCarpentryMorphable) tileEntity : null;
                if (carpentryMorphable != null) {
                    switch (toolMode) {
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
        return new TileCarpentryMorphable();
    }
}
