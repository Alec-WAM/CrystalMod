package alec_wam.CrystalMod.tiles.machine.worksite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.vecmath.Vector4f;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.api.energy.ICEnergyReceiver;
import alec_wam.CrystalMod.entities.minions.worker.EntityMinionWorker;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.IRotatableTile;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemUtil;

public abstract class TileWorksiteBase extends TileEntityMod implements IWorkSite, IChunkLoaderTile, ICEnergyReceiver, IRotatableTile {

	private double efficiencyBonusFactor = 0.f;

    private EnumSet<WorksiteUpgrade> upgrades = EnumSet.noneOf(WorksiteUpgrade.class);
	
	private EnumFacing orientation = EnumFacing.NORTH;
	
	private int workRetryDelay = 20;
	
	private Ticket chunkTicket = null;
	
	//*************************************** UPGRADE HANDLING METHODS ***************************************//

	@Override
	public final EnumSet<WorksiteUpgrade> getUpgrades(){return upgrades;}

	@Override
	public EnumSet<WorksiteUpgrade> getValidUpgrades()
	{
	  return EnumSet.of(
	      WorksiteUpgrade.ENCHANTED_TOOLS_1,
	      WorksiteUpgrade.ENCHANTED_TOOLS_2,
	      WorksiteUpgrade.ENCHANTED_TOOLS_3,
	      WorksiteUpgrade.TOOL_QUALITY_1,
	      WorksiteUpgrade.TOOL_QUALITY_2,
	      WorksiteUpgrade.TOOL_QUALITY_3
	      );
	}

	@Override
	public void onBlockBroken()
	{
		for(WorksiteUpgrade ug : this.upgrades)
		{
			ItemUtil.spawnItemInWorldWithoutMotion(worldObj, ItemWorksiteUpgrade.getStack(ug), getPos());
	    }
		efficiencyBonusFactor = 0;
		upgrades.clear();
		if(this.chunkTicket!=null)
	    {
			ForgeChunkManager.releaseTicket(chunkTicket);
			this.chunkTicket = null;
	    }
	}

	@Override
	public void addUpgrade(WorksiteUpgrade upgrade)
	{
		upgrades.add(upgrade);
		updateEfficiency();
		BlockUtil.markBlockForUpdate(getWorld(), getPos());
		markDirty();
		if(upgrade==WorksiteUpgrade.BASIC_CHUNK_LOADER || upgrade==WorksiteUpgrade.QUARRY_CHUNK_LOADER)
	    {
			setupInitialTicket();//setup chunkloading for the worksite
	    }
	}

	@Override
	public final void removeUpgrade(WorksiteUpgrade upgrade)
	{
	  upgrades.remove(upgrade);
	  updateEfficiency();
	  BlockUtil.markBlockForUpdate(getWorld(), getPos());
	  markDirty();
	  if(upgrade==WorksiteUpgrade.BASIC_CHUNK_LOADER || upgrade==WorksiteUpgrade.QUARRY_CHUNK_LOADER)
	  {
	    setTicket(null);//release any existing ticket
	  }
	}

	//*************************************** TILE UPDATE METHODS ***************************************//
	
	protected abstract boolean processWork();

	protected abstract boolean hasWorksiteWork();

	protected abstract void updateWorksite();

    public Set<EntityMinionWorker> workers = new HashSet<EntityMinionWorker>();
	
	public abstract boolean isWorkerOkay(EntityMinionWorker minion);
	
	public EntityMinionWorker getRandomWorker(boolean ignoreWorking){
		return getRandomWorker(ignoreWorking, null);
	}
	
	public EntityMinionWorker getRandomWorker(WorkerFilter...filters){
		EntityMinionWorker worker = null;
		if(filters == null){
			return getRandomWorker(false);
		}
		for(WorkerFilter fil : filters){
			worker = getRandomWorker(false, fil);
			if(worker !=null){
				break;
			}
		}
		return worker;
	}
	
	public EntityMinionWorker getRandomWorker(boolean ignoreWorking, WorkerFilter filter){
		List<EntityMinionWorker> curWorkers = new ArrayList<EntityMinionWorker>();
		Iterator<EntityMinionWorker> it = workers.iterator();
		while(it.hasNext()){
			EntityMinionWorker worker = it.next();
			if(filter == null || filter.matches(worker)){
				curWorkers.add(worker);
			}
		}
		if(curWorkers.isEmpty())return null;
		if(curWorkers.size() > 1)Collections.shuffle(curWorkers);
		if(!curWorkers.isEmpty()){
			if(ignoreWorking || !curWorkers.get(0).isWorking()){
				return curWorkers.get(0);
			}
		}
		return null;
	}
	
	public EntityMinionWorker getClosestWorker(BlockPos pos, WorkerFilter...filters){
		EntityMinionWorker worker = null;
		if(filters == null){
			return getClosestWorker(pos, false, null);
		}
		for(WorkerFilter fil : filters){
			worker = getClosestWorker(pos, false, fil);
			if(worker !=null){
				break;
			}
		}
		return worker;
	}
	
	public EntityMinionWorker getClosestWorker(BlockPos pos, boolean ignoreWorking, WorkerFilter filter){
		List<EntityMinionWorker> curWorkers = new ArrayList<EntityMinionWorker>();
		Iterator<EntityMinionWorker> it = workers.iterator();
		while(it.hasNext()){
			EntityMinionWorker worker = it.next();
			if(filter == null || filter.matches(worker)){
				curWorkers.add(worker);
			}
		}
		if(curWorkers.isEmpty())return null;
		EntityMinionWorker worker = null;
		double d4 = -1.0D;
		double width = this.getBoundsMaxWidth();
		for(EntityMinionWorker min : curWorkers){
			double d5 = min.getDistance(pos.getX(), pos.getY(), pos.getZ());

	        if ((width < 0.0D || d5 < width * width) && (d4 == -1.0D || d5 < d4))
	        {
	            d4 = d5;
	            worker = min;
	        }
		}
		return worker;
	}
	
	public void addWorker(EntityMinionWorker minion){
		if(worldObj.isRemote){return;}
		if(!isWorkerOkay(minion))return;
		workers.add(minion);
	}
	
	public void removeWorker(EntityMinionWorker minion){
		if(worldObj.isRemote){return;}
		workers.remove(minion);
	}
	
	@Override
	public void update()
	{
	  super.update();
	  if(worldObj.isRemote){return;}  
	  worldObj.theProfiler.startSection("CMWorksite");
	  if(workers !=null){
		  Iterator<EntityMinionWorker> it = workers.iterator();
		  while(it.hasNext()){
			  EntityMinionWorker worker = it.next();
			  if(worker !=null && (!this.isWorkerOkay(worker) || worker.isDead)){
				  worker.fireFromWorksite();
			  }
		  }
	  }
	  
	  if(workRetryDelay>0)
	  {
	    workRetryDelay--;    
	  }
	  else
	  {
	    worldObj.theProfiler.endStartSection("Check For Work");
	    int ePerUse = (int) IWorkSite.WorksiteImplementation.getEnergyPerActivation(efficiencyBonusFactor);
	    boolean hasWork = hasWorksiteWork();
	    if(hasWork && powered() && getEnergyStorage() !=null)hasWork = getEnergyStorage().getCEnergyStored() >= ePerUse;
	    worldObj.theProfiler.endStartSection("Process Work");
	    if(hasWork)
	    {
	      if(processWork())
	      {
	    	 if(powered() && getEnergyStorage() !=null)getEnergyStorage().setEnergyStored(getEnergyStorage().getCEnergyStored() - ePerUse);
	      } 
	      else
	      {
	        workRetryDelay = 20;
	      }
	    }
	  }  
	  worldObj.theProfiler.endStartSection("WorksiteBaseUpdate");
	  updateWorksite();
	  worldObj.theProfiler.endSection();
	  worldObj.theProfiler.endSection();
	}
	
	protected final void updateEfficiency()
	{
	  efficiencyBonusFactor = IWorkSite.WorksiteImplementation.getEfficiencyFactor(upgrades);
	}
	
	@Override
	public boolean shouldRenderInPass(int pass)
	{
	  return pass==1;
	}
	
	public final EnumFacing getPrimaryFacing()
	{
	  return orientation;
	}

	public final void setPrimaryFacing(EnumFacing face)
	{
	  orientation = face;
	  //BlockUtil.markBlockForUpdate(getWorld(), getPos());
    }
	
	@Override
	public void writeCustomNBT(NBTTagCompound tag)
	{
		super.writeCustomNBT(tag);
		if(!getUpgrades().isEmpty())
		{
			int[] ug = new int[getUpgrades().size()];
			int i = 0;
			for(WorksiteUpgrade u : getUpgrades())
			{
				ug[i] = u.ordinal();
				i++;
			}
			tag.setIntArray("Upgrades", ug);    
		}
		tag.setInteger("Facing", orientation.getHorizontalIndex());
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound tag)
	{
		super.readCustomNBT(tag);
		if(tag.hasKey("upgrades"))
		{
			NBTBase upgradeTag = tag.getTag("Upgrades");
			if(upgradeTag instanceof NBTTagIntArray)
			{
				int[] ug = tag.getIntArray("Upgrades");
				for(int i= 0; i < ug.length; i++)
				{
					upgrades.add(WorksiteUpgrade.values()[ug[i]]);
				}
			}
			else if(upgradeTag instanceof NBTTagList)//template parser reads int-arrays as a tag list for some reason
			{
				NBTTagList list = (NBTTagList)upgradeTag;
				for(int i =0; i < list.tagCount(); i++)
				{
					String st = list.getStringTagAt(i);
					try
					{
						int ug = Integer.parseInt(st);
						upgrades.add(WorksiteUpgrade.values()[ug]);
					}
					catch(NumberFormatException e){}
				}
			}
    	}

		if(tag.hasKey("Facing")){orientation = EnumFacing.getHorizontal(tag.getInteger("Facing"));}
		this.updateEfficiency();
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
		return INFINITE_EXTENT_AABB;
	}

	public abstract boolean onBlockClicked(EntityPlayer player);
	
	//*************************************** MISC METHODS ***************************************//

	@Override
	public void setTicket(Ticket tk)
	{
		if(chunkTicket!=null)
	    {
			ForgeChunkManager.releaseTicket(chunkTicket);
			chunkTicket=null;
	    }
		this.chunkTicket = tk;  
		if(this.chunkTicket==null){return;}
		writeDataToTicket(chunkTicket);
		ChunkPos ccip = new ChunkPos(getPos().getX()>>4, getPos().getZ()>>4);
		ForgeChunkManager.forceChunk(chunkTicket, ccip);  
		if(this.hasWorkBounds())
	    {
			int minX = getWorkBoundsMin().getX()>>4;
			int minZ = getWorkBoundsMin().getZ()>>4;
			int maxX = getWorkBoundsMax().getX()>>4;
			int maxZ = getWorkBoundsMax().getZ()>>4;
			for(int x = minX; x<=maxX; x++)
			{
				for(int z = minZ; z<=maxZ; z++)
				{
					ccip = new ChunkPos(x, z);
					ForgeChunkManager.forceChunk(chunkTicket, ccip);
				}
			}
    	}  
  	}

	protected final void writeDataToTicket(Ticket tk)
	{
	  WorksiteChunkLoader.writeDataToTicket(tk, getPos());
	}

	public final void setupInitialTicket()
	{
		if(chunkTicket!=null){ForgeChunkManager.releaseTicket(chunkTicket);}
	  	if(getUpgrades().contains(WorksiteUpgrade.BASIC_CHUNK_LOADER) || getUpgrades().contains(WorksiteUpgrade.QUARRY_CHUNK_LOADER))
	    {
	  		setTicket(ForgeChunkManager.requestTicket(CrystalMod.instance, worldObj, Type.NORMAL));    
	    }
	}
	
	@Override
	public void onPostBoundsAdjusted()
	{
	  setupInitialTicket();  
	}	
	
	public Vector4f boundsColor(){
		return new Vector4f(1, 1, 1, 1);
	}
	
	public boolean powered(){
		return false;
	}
	
	public CEnergyStorage getEnergyStorage(){
		return null;
	}
	
	public boolean canConnectCEnergy(EnumFacing from){
		return powered();
	}
	
	@Override
	public int getCEnergyStored(EnumFacing from) {
		return getEnergyStorage() !=null ? getEnergyStorage().getCEnergyStored() : 0;
	}
	@Override
	public int getMaxCEnergyStored(EnumFacing from) {
		return getEnergyStorage() !=null ? getEnergyStorage().getMaxCEnergyStored() : 0;
	}
	
	@Override
	public int fillCEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if(getEnergyStorage() == null || this.getCEnergyStored(from) >= this.getMaxCEnergyStored(from))return 0;
		int amt = getEnergyStorage().fillCEnergy(Math.min(maxReceive, getEnergyStorage().getMaxReceive()), simulate);
		return amt;
	}

}
