package alec_wam.CrystalMod.util;

import alec_wam.CrystalMod.init.ModItems;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ToolUtil {

	public static boolean isHoldingWrench(EntityPlayer player, EnumHand hand){
		return isWrench(player.getHeldItem(hand));
	}

	public static boolean isWrench(ItemStack heldItem) {
		if(heldItem.getItem() == ModItems.wrench){
			return true;
		}
		return false;
	}

	public static boolean breakBlockWithWrench(World world, BlockPos pos, EntityPlayer entityPlayer, EnumHand hand) {
		return breakBlockWithWrench(world, pos, entityPlayer, entityPlayer.getHeldItem(hand));
	}

	//TODO Make this work even in Creative
	public static boolean breakBlockWithWrench(World world, BlockPos pos, EntityPlayer entityPlayer, ItemStack heldItem) {
		if (entityPlayer.isSneaking() && isWrench(heldItem)) {
			IBlockState bs = world.getBlockState(pos);
			SoundType sound = bs.getBlock().getSoundType(bs, world, pos, entityPlayer);
			IFluidState ifluidstate = world.getFluidState(pos);
			if(bs.removedByPlayer(world, pos, entityPlayer, true, ifluidstate)) {
				bs.getBlock().harvestBlock(world, entityPlayer, pos, bs, world.getTileEntity(pos), heldItem);
				world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, sound.getBreakSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F, false);
				return true;
			}
		}
		return false;
	}

}