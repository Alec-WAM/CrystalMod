package alec_wam.CrystalMod.entities.minions.worker.jobs;

import alec_wam.CrystalMod.entities.minions.MinionConstants;
import alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import alec_wam.CrystalMod.entities.minions.worker.PathFinderWorker;
import alec_wam.CrystalMod.entities.minions.worker.WorkerJob;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.RelativeSide;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;
import alec_wam.CrystalMod.tiles.machine.worksite.imp.WorksiteTreeFarm;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.fakeplayer.FakePlayerUtil;
import alec_wam.CrystalMod.util.tool.ChorusPlantUtil;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import alec_wam.CrystalMod.util.tool.TreeUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class JobChopTree extends WorkerJob {

	public static final BlockPos NULL_BLOCKPOS = new BlockPos(0, -1, 0);
	
	public BlockPos chopPos;
	
	public JobChopTree(BlockPos pos){
		this.chopPos = pos;
	}
	
	private int delay = -1;
    private int maxDelay = 1;
    private int mod = 1;
	
	@Override
	public boolean run(EntityMinionWorker worker, TileWorksiteBase worksite) {
		if(worker.getEntityWorld().isRemote) return false;
		if(chopPos == null || chopPos == BlockPos.ORIGIN) return true;
		if(worksite == null || !(worksite instanceof WorksiteTreeFarm)) return true;
		WorksiteTreeFarm tFarm = (WorksiteTreeFarm)worksite;
		if(worker.getEntityWorld().isAirBlock(chopPos)){ 
			return true;
		}
		IBlockState state = worker.getEntityWorld().getBlockState(chopPos);
		if(state == null || (!TreeUtil.isLog(state) && !ChorusPlantUtil.isChorusPlant(state))){
			FakePlayerUtil.destroyBlockPartially(worker.getEntityWorld(), worker.getEntityId(), chopPos, -1);
			return true;
		}

		
		destroyTool(worker);
		boolean foundAxe = false;
		if(!WorksiteTreeFarm.isAxe(worker.getHeldItemMainhand())){
			if(WorksiteTreeFarm.isAxe(worker.getBackItem())){
				ItemStack held = worker.getHeldItemMainhand();
				if(ItemStackTools.isEmpty(held) || ToolUtil.isTool(held)){
					worker.switchItems();
				} else {
					//if(ItemStackTools.isValid(held)){
						ModLogger.info("Chop Job: sending non tool into front/top ["+held.getDisplayName()+"]");
						if(tFarm.addStackToInventoryNoDrop(held, false, RelativeSide.FRONT, RelativeSide.TOP)){
							worker.setHeldItem(EnumHand.MAIN_HAND, ItemStackTools.getEmptyStack());
							worker.switchItems();
						}
					//}
				}
			}
			foundAxe = WorksiteTreeFarm.isAxe(worker.getHeldItemMainhand());
		}
		
		if(!foundAxe)tFarm.giveAxe(worker);
		
		ItemStack held = worker.getHeldItemMainhand();
		if(ItemStackTools.isEmpty(held)){
			//TODO Create Missing Axe warning
			return false;
		}
		
		worker.getLookHelper().setLookPosition(chopPos.getX() + 0.5, chopPos.getY() + 0.5, chopPos.getZ() + 0.5, 10, 40);
		double d = 0.0d;//worker.getDistance(chopPos.getX() + 0.5, worker.posY/*chopPos.down().getY() + 0.5*/, chopPos.getZ() + 0.5);
		
		double d0 = worker.posX - (chopPos.getX() + 0.5);
        double d2 = worker.posZ - (chopPos.getZ() + 0.5);
        d =MathHelper.sqrt(d0 * d0 + d2 * d2);
		
		if(d <= 1.5D && d > 0.5){
			if(state.getBlockHardness(worker.getEntityWorld(), chopPos) <= 0.0D){
				worker.getEntityWorld().playEvent(2001, chopPos, Block.getStateId(state));
				if(TreeUtil.isLog(state)){
					tFarm.chopDownTree(held, chopPos);
				}
				else if(ChorusPlantUtil.isChorusPlant(state)){
					tFarm.chopDownChorus(held, chopPos);
				}					
				worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, held);
				destroyTool(worker);
				delay = -1;
				chopPos = BlockPos.ORIGIN;
				return true;
			} else {			
				if (delay < 0)
			    {
					int str = 0;
					if(held !=null && ToolUtil.isAxe(held)){
						str = (int) held.getStrVsBlock(state) / 10;
					}
			        delay = ((int)Math.max(5.0F, (20.0F - (str) * 3.0F) * state.getBlockHardness(worker.getEntityWorld(), chopPos)));
			        maxDelay = delay;
			        int div = Math.round(delay / 12.0F);
			        mod = (delay / Math.max(1, div));
			    }
				if (delay > 0)
				{
					if ((--delay > 0) && (delay % mod == 0))
					{
						int des = 10-(int)(1.0F * ((10*delay)/maxDelay));
						FakePlayerUtil.destroyBlockPartially(worker.getEntityWorld(), worker.getEntityId(), chopPos, des);
						worker.swingArm(EnumHand.MAIN_HAND);
					}
				}

				if (delay == 0)
				{
					worker.getEntityWorld().playEvent(2001, chopPos, Block.getStateId(state));
					FakePlayerUtil.destroyBlockPartially(worker.getEntityWorld(), worker.getEntityId(), chopPos, -1);					
					if(TreeUtil.isLog(state)){
						tFarm.chopDownTree(held, chopPos);
					}
					else if(ChorusPlantUtil.isChorusPlant(state)){
						tFarm.chopDownChorus(held, chopPos);
					}					
					worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, held);
					destroyTool(worker);
					delay = -1;
					chopPos = BlockPos.ORIGIN;
					return true;
				}
			}
		} else {
			/*if(worker.getNavigator().noPath()){
				worker.getNavigator().tryMoveToXYZ(chopPos.getX() + 0.5, chopPos.down().getY() + 0.5, chopPos.getZ() + 0.5, MinionConstants.SPEED_WALK);
			}*/
			if(worker.getNavigator().noPath()){
				for(EnumFacing face : EnumFacing.HORIZONTALS){
					BlockPos otherPos = chopPos.offset(face);
					if(worker.getEntityWorld().isAirBlock(otherPos)){	
						double offsetX = face.getFrontOffsetX() * 0.5;
						double offsetZ = face.getFrontOffsetZ() * 0.5;
						Path path = PathFinderWorker.findDetailedPath(worker, worker.getNavigator(), otherPos.getX() + offsetX, otherPos.getY(), otherPos.getZ() + offsetZ);
						worker.getNavigator().setPath(path, MinionConstants.SPEED_WALK);
					}
				}
			}
		}
		
		return false;
	}
	
	public void destroyTool(EntityMinionWorker worker){
		ItemStack tool = worker.getHeldItemMainhand();
		if(ItemStackTools.isNullStack(tool)) return;
		boolean canDamage = tool.isItemStackDamageable() && tool.getItem().isDamageable();
		if(ItemStackTools.isEmpty(tool) || (canDamage && tool.getItemDamage() >= tool.getMaxDamage())) {
			worker.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStackTools.getEmptyStack());
	    }
	}

	@Override
	public boolean isSame(WorkerJob job) {
		if(!(job instanceof JobChopTree)) return false;
		return chopPos == ((JobChopTree)job).chopPos;
	}

}
