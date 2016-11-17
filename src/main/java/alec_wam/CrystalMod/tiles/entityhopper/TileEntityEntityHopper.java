package alec_wam.CrystalMod.tiles.entityhopper;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.Vector3d;

public class TileEntityEntityHopper extends TileEntityMod {

	public static enum FilterType {
		NONE, ALL, PLAYER, UNDEAD, ARTHROPOD, MONSTER, ANIMAL, LIVING, WATER, BABY, PET, SLIME, VILLAGER, ITEM;
		
		public FilterType next(){
			int index = ordinal();
			if(index+1 < values().length){
				return values()[index+1];
			}
			return values()[0];
		}
		
		public FilterType previous(){
			int index = ordinal();
			if(index-1 >= 0){
				return values()[index-1];
			}
			return values()[values().length-1];
		}
	}
	
	private FilterType filter = FilterType.NONE;
	
	public void setFilter(FilterType type){
		this.filter = type;
		this.markDirty();
		BlockUtil.markBlockForUpdate(getWorld(), getPos());
	}
	
	public FilterType getFilter(){
		return filter;
	}
	
	@Override
	public void update(){
		super.update();
		if(this.worldObj == null || this.worldObj.isRemote || !worldObj.isBlockLoaded(getPos())) return;
		
		//Powered
		if(worldObj.isBlockIndirectlyGettingPowered(getPos()) > 0)return;
		BlockPos abovePos = getPos().offset(EnumFacing.UP);
		AxisAlignedBB above = new AxisAlignedBB(abovePos, abovePos.add(1, 1, 1));
		BlockPos belowPos = getPos().offset(EnumFacing.DOWN);
		AxisAlignedBB below = new AxisAlignedBB(belowPos, getPos().add(1, 0, 1));
		List<Entity> entities = worldObj.getEntitiesWithinAABB(Entity.class, above);
		for(Entity entity : entities){
			if(entity !=null && entity.isEntityAlive()){
				if(passesFilter(entity)){
					if(entity instanceof EntityItem){
						EntityItem item = (EntityItem)entity;
						IItemHandler handler = ItemUtil.getItemHandler(worldObj.getTileEntity(belowPos), EnumFacing.UP);
						if(handler !=null){
							ItemStack stack = item.getEntityItem();
							ItemStack insert = ItemHandlerHelper.insertItem(handler, stack, false);
							
							if(insert !=null){
								item.setEntityItemStack(insert);
								if(item.getEntityItem().stackSize <=0){
									item.setDead();
								}
							} else {
								item.setDead();
							}
							continue;
						}
					}
					
					Vector3d vec = new Vector3d(getPos());
					vec.y-=entity.height;
					entity.motionX = entity.motionY = entity.motionZ = 0;
					entity.setPositionAndUpdate(vec.x + 0.5, vec.y, vec.z + 0.5);
				}
			}
		}
	}
	
	public boolean passesFilter(Entity entity){
		if(filter == null || filter == FilterType.NONE) return false;
		switch(filter){
			default : case ALL : {
				return true; 
			}
			case PLAYER : {
				return entity instanceof EntityPlayer; 
			}
			case UNDEAD : {
				return (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).getCreatureAttribute() == EnumCreatureAttribute.UNDEAD);
			}
			case ARTHROPOD : {
				return (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD);
			}
			case MONSTER : {
				return (entity instanceof IMob);
			}
			case ANIMAL : {
				return (entity instanceof EntityAnimal);
			}
			case LIVING : {
				return (entity instanceof EntityLiving);
			}
			case WATER : {
				return (entity instanceof EntityWaterMob || entity instanceof EntityGuardian);
			}
			case BABY : {
				return (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isChild());
			}
			case PET : {
				return (entity instanceof IEntityOwnable);
			}
			case SLIME : {
				return (entity instanceof EntitySlime);
			}
			case VILLAGER : {
				return (entity instanceof EntityVillager);
			}
			case ITEM : {
				return (entity instanceof EntityItem);
			}
		}
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		if(filter !=null)nbt.setInteger("Filter", filter.ordinal());
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		int filterIndex = nbt.getInteger("Filter");
		
		if(filterIndex >= 0 && filterIndex < FilterType.values().length){
			setFilter(FilterType.values()[filterIndex]);
		} else setFilter(FilterType.ALL);
	}
}
