package alec_wam.CrystalMod.entities.minions.worker.jobs;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.entities.minions.MinionConstants;
import alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import alec_wam.CrystalMod.entities.minions.worker.WorkerJob;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.RelativeSide;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;
import alec_wam.CrystalMod.tiles.machine.worksite.WorksiteUpgrade;
import alec_wam.CrystalMod.tiles.machine.worksite.imp.WorksiteCropFarm;
import alec_wam.CrystalMod.util.FarmUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.fakeplayer.FakePlayerUtil;
import alec_wam.CrystalMod.world.DropCapture;
import alec_wam.CrystalMod.world.DropCapture.CaptureContext;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.ForgeEventFactory;

public class JobHarvestCrop extends WorkerJob {

	public static final BlockPos NULL_BLOCKPOS = new BlockPos(0, -1, 0);
	
	public BlockPos cropPos;
	
	public JobHarvestCrop(BlockPos pos){
		this.cropPos = pos;
	}
	
	@Override
	public boolean run(EntityMinionWorker worker, TileWorksiteBase worksite) {
		if(worker.getEntityWorld().isRemote) return false;
		if(cropPos == null || cropPos == BlockPos.ORIGIN) return true;
		if(worksite == null || !(worksite instanceof WorksiteCropFarm)) return true;
		WorksiteCropFarm cFarm = (WorksiteCropFarm)worksite;
		if(worker.getEntityWorld().isAirBlock(cropPos)){ 
			return true;
		}
		IBlockState cropState = worker.getEntityWorld().getBlockState(cropPos);
		boolean isGoard = (cropState.getBlock() == Blocks.MELON_BLOCK || cropState.getBlock() == Blocks.PUMPKIN);
		boolean isStacked = FarmUtil.isStackedCrop(worker.getEntityWorld(), cropPos);
		if(!FarmUtil.isGrownCrop(worker.getEntityWorld(), cropPos) && !isGoard) return true;
		if(isStacked && worker.getEntityWorld().isAirBlock(cropPos.up())) return true;
		worker.getLookHelper().setLookPosition(cropPos.getX() + 0.5, cropPos.getY() + 0.5, cropPos.getZ() + 0.5, 10, 40);
		double d = worker.getDistance(cropPos.getX() + 0.5, cropPos.down().getY() + 0.5, cropPos.getZ() + 0.5);
		if(d <= 1.5D){
			BlockPos harvestPos = cropPos;
			if(isStacked){
				harvestPos = harvestPos.up();
			}
			if(FarmUtil.isClickableCrop(worker.getEntityWorld(), harvestPos)){
				final CaptureContext dropsCapturer = DropCapture.instance.start(harvestPos);

				final List<EntityItem> drops;
				try {
					if(FakePlayerUtil.rightClickBlock(worker.getEntityWorld(), harvestPos, EnumFacing.UP, ItemStackTools.getEmptyStack())){
						worker.swingArm(EnumHand.MAIN_HAND);
					}
				} finally {
					drops = dropsCapturer.stop();
				}
				for(EntityItem item : drops){
					if(item !=null && item.getEntityItem() !=null){
						ItemStack copyStack = ItemStackTools.safeCopy(item.getEntityItem());
						cFarm.addStackToInventoryNoDrop(copyStack, false, RelativeSide.FRONT, RelativeSide.TOP);
						item.setEntityItemStack(copyStack);
						if (ItemStackTools.isEmpty(copyStack)) {
							item.setDead();
						}
					}
				}
				cropPos = BlockPos.ORIGIN;
				return true;
			} else {
				int fortune = cFarm.getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_1)? 1 : cFarm.getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_2)? 2 : cFarm.getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_3) ? 3 : 0;
				EntityPlayer player = FakePlayerUtil.getPlayer((WorldServer)worker.getEntityWorld());
				List<ItemStack> finalDrops = Lists.newArrayList();
				List<BlockPos> breakList = Lists.newArrayList();
				breakList.add(harvestPos);
				
				if(isStacked){
					BlockPos abovePos = harvestPos.up();
					IBlockState aboveState = worker.getEntityWorld().getBlockState(abovePos);
					while(FarmUtil.isStackedCrop(worker.getEntityWorld(), abovePos) && aboveState.getBlock() == cropState.getBlock()){
						breakList.add(abovePos);
						abovePos = abovePos.up();
						aboveState = worker.getEntityWorld().getBlockState(abovePos);
					}
				}
				
				worker.swingArm(EnumHand.MAIN_HAND);
				for(int i = 0; i < breakList.size(); i++){
					//Reverse to make sure stacked crops don't break "early"
					BlockPos pos = breakList.get(breakList.size()-1-i);
					float chance = 1.0f;
					List<ItemStack> drops = cropState.getBlock().getDrops(worker.getEntityWorld(), pos, cropState, fortune);
					chance = ForgeEventFactory.fireBlockHarvesting(drops, worker.getEntityWorld(), pos, cropState, fortune, chance, false, player);
					
					worker.getEntityWorld().playEvent(player, 2001, pos, Block.getStateId(cropState));
					worker.getEntityWorld().setBlockToAir(pos);
					for(ItemStack stack : drops)
					{
						if(worker.getEntityWorld().rand.nextFloat() <= chance){
							finalDrops.add(stack);
						}
					}
				}
				
				
				boolean replacedSeed = false;
				drop : for(ItemStack stack : finalDrops)
				{
					if(ItemStackTools.isEmpty(worker.getHeldItemMainhand()) && !isStacked && !replacedSeed){
						if (stack.getItem() instanceof IPlantable) {
							IPlantable plantable = (IPlantable) stack.getItem();
							if(FarmUtil.canPlant(worker.getEntityWorld(), cropPos, plantable)){
								if(worker.addCommand(new JobPlantCrop(cropPos))){
									worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack);
									replacedSeed = true;
									continue drop;
								}
							}
						}
					}
					cFarm.addStackToInventory(stack, RelativeSide.FRONT, RelativeSide.TOP);
				}
				
				cropPos = BlockPos.ORIGIN;
				return true;
			}
			
		} else {
			if(worker.getNavigator().noPath()){
				worker.getNavigator().tryMoveToXYZ(cropPos.getX() + 0.5, cropPos.down().getY() + 0.5, cropPos.getZ() + 0.5, MinionConstants.SPEED_WALK);
			}
		}
		
		return false;
	}

	@Override
	public boolean isSame(WorkerJob job) {
		if(!(job instanceof JobHarvestCrop)) return false;
		return cropPos == ((JobHarvestCrop)job).cropPos;
	}

}
