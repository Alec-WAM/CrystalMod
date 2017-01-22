package alec_wam.CrystalMod.integration.enderio;

/*import com.enderio.core.common.util.BlockCoord;

import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import crazypants.enderio.machine.farm.FarmNotification;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.enderio.machine.farm.farmers.PickableFarmer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;*/

//TODO Add when enderio updates
public class MaterialCropFarmer{ /*extends PickableFarmer {

	public MaterialCropFarmer(Block plantedBlock, int plantedBlockMeta, int grownBlockMeta, ItemStack seeds) {
		super(plantedBlock, plantedBlockMeta, grownBlockMeta, seeds);
	}

	public MaterialCropFarmer(Block plantedBlock, int grownBlockMeta, ItemStack seeds) {
		super(plantedBlock, grownBlockMeta, seeds);
	}

	public MaterialCropFarmer(Block plantedBlock, ItemStack seeds) {
		super(plantedBlock, seeds);
	}
	
	@Override
    public boolean prepareBlock(TileFarmStation farm, BlockCoord bc, Block block, IBlockState state)
    {
		if (!farm.isOpen(bc.getBlockPos())) {
			return false;
		}
		int slot = farm.getSupplySlotForCoord(bc.getBlockPos());
		ItemStack seedStack = farm.getSeedTypeInSuppliesFor(slot);
	    if (ItemStackTools.isEmpty(seedStack)) {
	      farm.setNotification(FarmNotification.NO_SEEDS);
	      return false;
	    }
		if(seedStack.getItem() != ModItems.materialSeed){
			return false;
		}
		Item seed = seedStack.getItem();
		if(seed.onItemUse(seedStack, farm.getFakePlayer(), farm.getWorld(), bc.getBlockPos().down(), EnumHand.MAIN_HAND, EnumFacing.UP, 0, 0, 0) == EnumActionResult.SUCCESS){
			farm.takeSeedFromSupplies(bc);
			farm.actionPerformed(false);
			return true;
		}
		return false;
    }*/
}
