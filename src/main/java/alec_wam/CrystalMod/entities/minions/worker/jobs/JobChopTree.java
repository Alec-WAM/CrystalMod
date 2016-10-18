package alec_wam.CrystalMod.entities.minions.worker.jobs;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import alec_wam.CrystalMod.entities.minions.MinionConstants;
import alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import alec_wam.CrystalMod.entities.minions.worker.WorkerJob;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;
import alec_wam.CrystalMod.tiles.machine.worksite.imp.WorksiteTreeFarm;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.fakeplayer.FakePlayerUtil;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import alec_wam.CrystalMod.util.tool.TreeUtil;

public class JobChopTree extends WorkerJob {

	public static final BlockPos NULL_BLOCKPOS = new BlockPos(0, -1, 0);
	
	public BlockPos logPos;
	
	public JobChopTree(BlockPos pos){
		this.logPos = pos;
	}
	
	private int delay = -1;
    private int maxDelay = 1;
    private int mod = 1;
	
	@Override
	public boolean run(EntityMinionWorker worker, TileWorksiteBase worksite) {
		if(worker.worldObj.isRemote) return false;
		if(logPos == null || logPos == BlockPos.ORIGIN) return true;
		if(worksite == null || !(worksite instanceof WorksiteTreeFarm)) return true;
		WorksiteTreeFarm tFarm = (WorksiteTreeFarm)worksite;
		if(worker.worldObj.isAirBlock(logPos)){ 
			return true;
		}
		IBlockState state = worker.worldObj.getBlockState(logPos);
		if(state == null || !TreeUtil.isLog(state)){
			FakePlayerUtil.destroyBlockPartially(worker.worldObj, worker.getEntityId(), logPos, -1);
			return true;
		}

		
		destroyTool(worker);
		tFarm.giveAxe(worker);
		ItemStack held = worker.getHeldItemMainhand();
		if(held == null){
			return true;
		}
		
		worker.getLookHelper().setLookPosition(logPos.getX() + 0.5, logPos.getY() + 0.5, logPos.getZ() + 0.5, 10, 40);
		double d = worker.getDistance(logPos.getX() + 0.5, logPos.down().getY() + 0.5, logPos.getZ() + 0.5);
		if(d <= 2.5D){
			if (delay < 0)
		    {
				int str = 0;
				if(held !=null && ToolUtil.isAxe(held)){
					str = (int) held.getStrVsBlock(state) / 10;
				}
		        delay = ((int)Math.max(5.0F, (20.0F - (str) * 3.0F) * state.getBlockHardness(worker.worldObj, logPos)));
		        maxDelay = delay;
		        mod = (delay / Math.round(delay / 12.0F));
		    }
			if (delay > 0)
			{
				if ((--delay > 0) && (delay % mod == 0))
				{
					int des = 10-(int)(1.0F * ((10*delay)/maxDelay));
					FakePlayerUtil.destroyBlockPartially(worker.worldObj, worker.getEntityId(), logPos, des);
					worker.swingArm(EnumHand.MAIN_HAND);
				}
				if (delay == 0)
				{
					worker.worldObj.playEvent(2001, logPos, Block.getStateId(state));
					FakePlayerUtil.destroyBlockPartially(worker.worldObj, worker.getEntityId(), logPos, -1);
					tFarm.chopDownTree(held, logPos);
					worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, held);
					destroyTool(worker);
					delay = -1;
					logPos = BlockPos.ORIGIN;
					return true;
				}
			}
		} else {
			if(worker.getNavigator().noPath()){
				worker.getNavigator().tryMoveToXYZ(logPos.getX() + 0.5, logPos.getY() + 0.5, logPos.getZ() + 0.5, MinionConstants.SPEED_WALK);
			}
		}
		
		return false;
	}
	
	public void destroyTool(EntityMinionWorker worker){
		ItemStack tool = worker.getHeldItemMainhand();
		if(tool == null) return;
		boolean canDamage = tool.isItemStackDamageable() && tool.getItem().isDamageable();
		if(tool.stackSize == 0 || (canDamage && tool.getItemDamage() >= tool.getMaxDamage())) {
			worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, null);
	    }
	}

	@Override
	public boolean isSame(WorkerJob job) {
		if(!(job instanceof JobChopTree)) return false;
		return logPos == ((JobChopTree)job).logPos;
	}

}
