package alec_wam.CrystalMod.util.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;


public interface ITool {
  
  boolean canUse(ItemStack stack, EntityPlayer player, BlockPos pos);

  void used(ItemStack stack, EntityPlayer player, BlockPos pos);

}
