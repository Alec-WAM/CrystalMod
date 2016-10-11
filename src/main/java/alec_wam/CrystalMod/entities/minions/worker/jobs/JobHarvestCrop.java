package alec_wam.CrystalMod.entities.minions.worker.jobs;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.ForgeEventFactory;
import alec_wam.CrystalMod.entities.minions.MinionConstants;
import alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import alec_wam.CrystalMod.entities.minions.worker.WorkerJob;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;
import alec_wam.CrystalMod.tiles.machine.worksite.WorksiteUpgrade;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.RelativeSide;
import alec_wam.CrystalMod.tiles.machine.worksite.imp.WorksiteCropFarm;
import alec_wam.CrystalMod.util.FarmUtil;
import alec_wam.CrystalMod.util.fakeplayer.FakePlayerUtil;
import alec_wam.CrystalMod.world.DropCapture;
import alec_wam.CrystalMod.world.DropCapture.CaptureContext;

public class JobHarvestCrop extends WorkerJob {

	public static final BlockPos NULL_BLOCKPOS = new BlockPos(0, -1, 0);
	
	public BlockPos cropPos;
	
	public JobHarvestCrop(BlockPos pos){
		this.cropPos = pos;
	}
	
	@Override
	public boolean run(EntityMinionWorker worker, TileWorksiteBase worksite) {
		if(worker.worldObj.isRemote) return false;
		if(cropPos == null || cropPos == BlockPos.ORIGIN) return true;
		if(worksite == null || !(worksite instanceof WorksiteCropFarm)) return true;
		WorksiteCropFarm cFarm = (WorksiteCropFarm)worksite;
		if(worker.worldObj.isAirBlock(cropPos)){ 
			return true;
		}
		
		if(!FarmUtil.isGrownCrop(worker.worldObj, cropPos)) return true;
		IBlockState cropState = worker.worldObj.getBlockState(cropPos);
		worker.getLookHelper().setLookPosition(cropPos.getX() + 0.5, cropPos.getY() + 0.5, cropPos.getZ() + 0.5, 10, 40);
		double d = worker.getDistance(cropPos.getX() + 0.5, cropPos.down().getY() + 0.5, cropPos.getZ() + 0.5);
		if(d <= 1.5D){
			
			if(FarmUtil.isClickableCrop(worker.worldObj, cropPos)){
				final CaptureContext dropsCapturer = DropCapture.instance.start(cropPos);

				final List<EntityItem> drops;
				try {
					if(FakePlayerUtil.rightClickBlock(worker.worldObj, cropPos, EnumFacing.UP, null)){
						worker.swingArm(EnumHand.MAIN_HAND);
					}
				} finally {
					drops = dropsCapturer.stop();
				}
				for(EntityItem item : drops){
					if(item !=null && !item.isDead && item.getEntityItem() !=null){
						cFarm.addStackToInventory(item.getEntityItem(), RelativeSide.FRONT, RelativeSide.TOP);
						item.setDead();
					}
				}
				cropPos = BlockPos.ORIGIN;
				return true;
			} else {
				int fortune = cFarm.getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_1)? 1 : cFarm.getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_2)? 2 : cFarm.getUpgrades().contains(WorksiteUpgrade.ENCHANTED_TOOLS_3) ? 3 : 0;
				worker.swingArm(EnumHand.MAIN_HAND);
				EntityPlayer player = FakePlayerUtil.getPlayer((WorldServer)worker.worldObj);
				float chance = 1.0f;
				List<ItemStack> drops = cropState.getBlock().getDrops(worker.worldObj, cropPos, cropState, fortune);
				chance = ForgeEventFactory.fireBlockHarvesting(drops, worker.worldObj, cropPos, cropState, fortune, chance, false, player);
				
				worker.worldObj.playEvent(player, 2001, cropPos, Block.getStateId(cropState));
				worker.worldObj.setBlockToAir(cropPos);
				drop : for(ItemStack stack : drops)
				{
					if(worker.worldObj.rand.nextFloat() <= chance){
						if(worker.getHeldItemMainhand() == null){
							if (stack.getItem() instanceof IPlantable) {
								IPlantable plantable = (IPlantable) stack.getItem();
								if(FarmUtil.canPlant(worker.worldObj, cropPos, plantable)){
									if(worker.addCommand(new JobPlantCrop(cropPos))){
										worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack);
										continue drop;
									}
								}
							}
						}
						cFarm.addStackToInventory(stack, RelativeSide.FRONT, RelativeSide.TOP);
					}
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
