package com.elytradev.carpentrycubes.common.item;

import com.elytradev.carpentrycubes.common.CarpentryMod;
import com.elytradev.carpentrycubes.common.block.BlockCarpentry;
import com.elytradev.concrete.resgen.EnumResourceType;
import com.elytradev.concrete.resgen.IResourceHolder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCarpentryHammer extends Item implements IResourceHolder {

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        EnumToolMode toolMode = EnumToolMode.byId(stack.getMetadata());

        return I18n.format("item.carpentrycubes.hammer.name") + " - " + toolMode.getLocalizedName();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltips, ITooltipFlag flag) {
        if (GuiScreen.isShiftKeyDown()) {
            EnumToolMode toolMode = EnumToolMode.byId(stack.getMetadata());

            tooltips.add(toolMode.getLocalizedDescription());
        } else {
            tooltips.add(I18n.format("carpentrycubes.tooltip.itemhint"));
        }
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
        EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.getBlockState(pos).getBlock() instanceof BlockCarpentry) {
            return EnumActionResult.SUCCESS;
        }

        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking() && !world.isRemote) {
            EnumToolMode mode;
            if (stack.getMetadata() + 1 < EnumToolMode.values().length) {
                mode = EnumToolMode.byId(stack.getMetadata() + 1);
            } else {
                mode = EnumToolMode.byId(0);
            }
            stack.setItemDamage(mode.id);
        }

        if (player.isSneaking()) {
            return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
        }

        return super.onItemRightClick(world, player, hand);
    }

    @Nullable
    @Override
    public ResourceLocation getResource(EnumResourceType resourceType, int meta) {
        if (resourceType == EnumResourceType.TEXTURE) {
            return new ResourceLocation(CarpentryMod.MOD_ID, "blocks/foursectionframe");
        }
        return null;
    }

    public enum EnumToolMode {
        ROTATE(0, "rotate"),
        TWEAK(1, "tweak"),
        SCRAPE(2, "scrape"),
        ORIENT(3, "orient");

        private final static EnumToolMode[] META_LOOKUP = Arrays.stream(EnumToolMode.values())
            .sorted(Comparator.comparingInt(o -> o.id))
            .toArray(EnumToolMode[]::new);

        private final int id;
        private final String name;

        EnumToolMode(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public static EnumToolMode byId(int id) {
            return META_LOOKUP[id];
        }

        @SideOnly(Side.CLIENT)
        public String getLocalizedName() {
            return I18n.format(String.format("carpentrycubes.tooltip.%s.name", this.name));
        }

        @SideOnly(Side.CLIENT)
        public String getLocalizedDescription() {
            return I18n.format(String.format("carpentrycubes.tooltip.%s.description", this.name));
        }
    }
}
