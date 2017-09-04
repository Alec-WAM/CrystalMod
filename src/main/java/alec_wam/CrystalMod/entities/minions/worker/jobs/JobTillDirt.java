package alec_wam.CrystalMod.entities.minions.worker.jobs;

import alec_wam.CrystalMod.entities.minions.MinionConstants;
import alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import alec_wam.CrystalMod.entities.minions.worker.WorkerJob;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;
import alec_wam.CrystalMod.tiles.machine.worksite.imp.WorksiteCropFarm;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.fakeplayer.FakePlayerUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

public class JobTillDirt extends WorkerJob {

	public static final BlockPos NULL_BLOCKPOS = new BlockPos(0, -1, 0);
	
	public BlockPos dirtPos;
	
	public JobTillDirt(BlockPos pos){
		this.dirtPos = pos;
	}
	
	@Override
	public boolean run(EntityMinionWorker worker, TileWorksiteBase worksite) {
		if(worker.getEntityWorld().isRemote) return false;
		if(dirtPos == null || dirtPos == BlockPos.ORIGIN) return true;
		if(worksite == null || !(worksite instanceof WorksiteCropFarm)) return true;
		WorksiteCropFarm tFarm = (WorksiteCropFarm)worksite;
		if(worker.getEntityWorld().isAirBlock(dirtPos)){ 
			return true;
		}
		IBlockState state = worker.getEntityWorld().getBlockState(dirtPos);
		if(state == null || state.getBlock() == Blocks.FARMLAND || !(state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.DIRT)){
			return true;
		}

		
		destroyTool(worker);
		tFarm.giveHoe(worker);
		ItemStack held = worker.getHeldItemMainhand();
		if(ItemStackTools.isEmpty(held)){
			return true;
		}
		
		worker.getLookHelper().setLookPosition(dirtPos.getX() + 0.5, dirtPos.getY() + 0.5, dirtPos.getZ() + 0.5, 10, 40);
		double d = worker.getDistance(dirtPos.getX() + 0.5, dirtPos.down().getY() + 0.5, dirtPos.getZ() + 0.5);
		if(d <= 2.5D){
			worker.swingArm(EnumHand.MAIN_HAND);
			EntityPlayer player = FakePlayerUtil.getPlayer((WorldServer)worker.getEntityWorld());
			player.setHeldItem(EnumHand.MAIN_HAND, held);
			held.getItem().onItemUse(player, worker.getEntityWorld(), dirtPos, EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 0.5f, 0.5f);
			destroyTool(worker);
			dirtPos = BlockPos.ORIGIN;
			return true;
		} else {
			if(worker.getNavigator().noPath()){
				worker.getNavigator().tryMoveToXYZ(dirtPos.getX() + 0.5, dirtPos.down().getY() + 0.5, dirtPos.getZ() + 0.5, MinionConstants.SPEED_WALK);
			}
		}
		
		return false;
	}
	
	public void destroyTool(EntityMinionWorker worker){
		ItemStack tool = worker.getHeldItemMainhand();
		if(!ItemStackTools.isValid(tool)) return;
		boolean canDamage = tool.isItemStackDamageable() && tool.getItem().isDamageable();
		if(ItemStackTools.isEmpty(tool) || (canDamage && tool.getItemDamage() >= tool.getMaxDamage())) {
			worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStackTools.getEmptyStack());
	    }
	}

	@Override
	public boolean isSame(WorkerJob job) {
		if(!(job instanceof JobTillDirt)) return false;
		return dirtPos == ((JobTillDirt)job).dirtPos;
	}

}
