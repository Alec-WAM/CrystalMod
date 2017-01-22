package alec_wam.CrystalMod.entities.minions.worker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.entities.minions.EntityMinionBase;
import alec_wam.CrystalMod.tiles.machine.worksite.TileWorksiteBase;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityMinionWorker extends EntityMinionBase {
	
	public BlockPos wStation = null;
	public int wStationDim;
	
	public WorkerClass type;
	public List<WorkerJob> commands;
	public List<WorkerJob> commandsToAdd;
	
	public Map<String, Object> storedObjects;
	
	public EntityMinionWorker(World worldIn) {
		super(worldIn);
		type = WorkerClass.NONE;
		commands = new ArrayList<WorkerJob>();
		commandsToAdd = new ArrayList<WorkerJob>();
		storedObjects = Maps.newHashMap();
	}
	
	public String getOccupationName(){
		return "Worker";
	}

	public boolean addCommand(WorkerJob command){
		if(!commandsToAdd.isEmpty()){
			Iterator<WorkerJob> jobs = commandsToAdd.iterator();
			while(jobs.hasNext()){
				WorkerJob job = jobs.next();
				if(job !=null){
					if(job.isSame(command)){
						return false;
					}
				}
			}
		}
		commandsToAdd.add(command);
		return true;
	}
	
	public void onUpdate(){
		super.onUpdate();
		if(!commands.isEmpty()){
			Iterator<WorkerJob> jobs = commands.iterator();
			
			while(jobs.hasNext()){
				WorkerJob job = jobs.next();
				if(job !=null && job.run(this, getWorksite())){
					jobs.remove();
				}
			}
	    }
		
		if(!commandsToAdd.isEmpty() && commands.isEmpty()){
			Iterator<WorkerJob> jobs = commandsToAdd.iterator();
			
			while(jobs.hasNext()){
				WorkerJob job = jobs.next();
				if(job !=null){
					commands.add(job);
					jobs.remove();
				}
			}
	    }
		
		if(!getEntityWorld().isRemote){
			if(getWorksite() == null && this.wStation !=null){
				this.fireFromWorksite();
				return;
			}
			if(this.wStation !=null){
				if(getWorksite() !=null){
					if(!this.isWorkingAtWorksite(wStation)){
						addToWorksite(wStation);
					}
				}
			}
		}
	}
	
	public TileWorksiteBase getWorksite(){
		if(this.wStationDim !=getEntityWorld().provider.getDimension()) return null;
		if(wStation !=null){
			TileEntity tile = getEntityWorld().getTileEntity(wStation);
			if(tile !=null && tile instanceof TileWorksiteBase){
			  return (TileWorksiteBase) tile;
			}
		}
		return null;
	}
	
	public boolean isWorkingAtWorksite(BlockPos siteCoord){
		if(getWorksite() !=null){
			if(getWorksite().workers.contains(this))return true;
		}
		return false;
	}
	
	public void addToWorksite(BlockPos siteCoord){
		if(isWorkingAtWorksite(siteCoord)){
			return;
		}
		
		fireFromWorksite();
		wStation = siteCoord;
		if(getWorksite() !=null){
			this.getWorksite().addWorker(this);
		}
	}
	
	public void fireFromWorksite(){
		if(getWorksite() !=null){
			this.getWorksite().removeWorker(this);
		}
		wStation = null;
	}
	
	public boolean isWorking() {
		return !commands.isEmpty();
	}
	
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		if(wStation !=null){
			NBTTagCompound coordNBT = new NBTTagCompound();
			coordNBT.setInteger("x", wStation.getX());
			coordNBT.setInteger("y", wStation.getY());
			coordNBT.setInteger("z", wStation.getZ());
			coordNBT.setInteger("dim", wStationDim);
			nbt.setTag("WorksitePos", coordNBT);
		}
	}
	
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		if(nbt.hasKey("WorksitePos")){
			NBTTagCompound coordNBT = nbt.getCompoundTag("WorksitePos");
			int x = coordNBT.getInteger("x");
			int y = coordNBT.getInteger("y");
			int z = coordNBT.getInteger("z");
			wStationDim = coordNBT.getInteger("dim");
			wStation = new BlockPos(x, y, z);
		}
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand)
    {
		ItemStack stack = player.getHeldItem(hand);
		if(!ItemStackTools.isNullStack(stack) && isOwner(player)){
			if(ToolUtil.isAxe(stack)){
	    		if(!ItemStackTools.isNullStack(getHeldItemMainhand())){
	    			if(ItemUtil.canCombine(stack, getHeldItemMainhand())){
	    				return false;
	    			}
	    			if (!getEntityWorld().isRemote)
	                {
	    				entityDropItem(getHeldItemMainhand(), 0.0F);
	                }
	    		}
	    		setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack.copy());
	    		consumeItemFromStack(player, stack);
	    		return true;
			}
			if(stack.getItem() instanceof ItemSword){
	    		if(!ItemStackTools.isNullStack(getHeldItemMainhand())){
	    			if(ItemUtil.canCombine(stack, getHeldItemMainhand())){
	    				return false;
	    			}
	    			if (!getEntityWorld().isRemote)
	                {
	    				entityDropItem(getHeldItemMainhand(), 0.0F);
	                }
	    		}
	    		setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack.copy());
	    		consumeItemFromStack(player, stack);
	    		return true;
			}
			if(stack.getItem() == Items.STICK){
				if(!ItemStackTools.isNullStack(getHeldItemMainhand())){
	    			if(ItemUtil.canCombine(stack, getHeldItemMainhand())){
	    				return false;
	    			}
	    			if (!getEntityWorld().isRemote)
	                {
	    				entityDropItem(getHeldItemMainhand(), 0.0F);
	                }
	    		}
	    		setItemStackToSlot(EntityEquipmentSlot.MAINHAND, null);
	    		return true;
			}
    	}
		if(ItemStackTools.isNullStack(stack) && isOwner(player)){
			if(!this.isWorking()){
    			BlockPos pos = new BlockPos(this).down();
    			TileEntity tile = getEntityWorld().getTileEntity(pos);
    			if(tile !=null && tile instanceof TileWorksiteBase){
    				TileWorksiteBase tfarm = (TileWorksiteBase) tile;
    				if(tfarm.isWorkerOkay(this)){
    					BlockPos coord = tfarm.getPos();
    					if(isWorkingAtWorksite(coord)){
    						if(!getEntityWorld().isRemote)ChatUtil.sendNoSpam(player, "I already work here.");
    						return true;
    					}
    					addToWorksite(coord);
    					if(!getEntityWorld().isRemote)ChatUtil.sendNoSpam(player, (this.wStation !=null ? wStation.getX()+", "+wStation.getY()+", "+wStation.getZ() : "NULL"));
    					return true;
    				}
    				if(!getEntityWorld().isRemote)ChatUtil.sendNoSpam(player, "I can not work here");
    				return true;
    			}
    		}
    	}  
		return super.processInteract(player, hand);
	}
	
	@Override
	public void onDeath(DamageSource damageSource) 
	{
		super.onDeath(damageSource);
		fireFromWorksite();
	}

}
