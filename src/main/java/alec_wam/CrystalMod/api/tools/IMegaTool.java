package alec_wam.CrystalMod.api.tools;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMegaTool {

	public ImmutableList<BlockPos> getAOEBlocks(ItemStack tool, World world, EntityPlayer player, BlockPos pos);
	
}
