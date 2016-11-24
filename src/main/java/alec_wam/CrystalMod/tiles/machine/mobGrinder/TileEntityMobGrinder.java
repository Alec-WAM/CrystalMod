package alec_wam.CrystalMod.tiles.machine.mobGrinder;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.IItemHandler;
import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.api.energy.ICEnergyReceiver;
import alec_wam.CrystalMod.fluids.XpUtil;
import alec_wam.CrystalMod.fluids.xp.ExperienceContainer;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.machine.IMachineTile;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.world.DropCapture;
import alec_wam.CrystalMod.world.DropCapture.CaptureContext;

import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;

public class TileEntityMobGrinder extends TileEntityMod implements IMessageHandler, ICEnergyReceiver, IMachineTile {

	
	public static class ZombieBlocker {

		private Set<EntityZombie> cache = Sets.newHashSet();

		@SubscribeEvent
		public void onSummonAid(SummonAidEvent event) {
			if (!cache.isEmpty() && cache.remove(event.getSummoner())) {
				event.setResult(Result.DENY);
			}
		}
	}

	//TM Z-Blocker Kills on contact
	public static ZombieBlocker zBlocker;
	
	public Vec3d centerPos;
	public int facing = EnumFacing.NORTH.getHorizontalIndex();
	
	List<EntityLiving> killList;
	int tick = 0;
	public int energyPerKill = 1000;
	public boolean hasPower = false;
	public boolean hasPowerCach = false;
	public boolean disabled = false;
	public boolean disabledCach = false;
	private boolean readyNext = false;
	
	public CEnergyStorage energyStorage = new CEnergyStorage(100000, 32000, 0);
	private int lastEnergyCach;
	public ExperienceContainer xpCon;
	
	public FakePlayer fakePlayer;
	private static final GameProfile GRINDER = new GameProfile(UUID.nameUUIDFromBytes("[CrystalMod-Grinder]".getBytes()), "[CrystalMod-Grinder]");
	private static final ItemStack genericSword;
	private ItemStack attackTool;
	static {
		genericSword = new ItemStack(ModItems.crystalSword, 0);
    }
	
	public TileEntityMobGrinder(){
		xpCon = new ExperienceContainer(XpUtil.getExperienceForLevel(30));
	    xpCon.setCanFill(false);
	    
	    if (zBlocker == null) {
	    	zBlocker = new ZombieBlocker();
	        MinecraftForge.EVENT_BUS.register(zBlocker);
	    }
	}
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("Facing", facing);
		nbt.setBoolean("Disabled", disabled);
		nbt.setTag("EnergyStorage", energyStorage.writeToNBT(new NBTTagCompound()));
		nbt.setTag("XPStorage", xpCon.writeToNBT(new NBTTagCompound()));
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		facing = nbt.getInteger("Facing");
		disabled = nbt.getBoolean("Disabled");
		energyStorage.readFromNBT(nbt.getCompoundTag("EnergyStorage"));
		xpCon.readFromNBT(nbt.getCompoundTag("XPStorage"));
	}
	
	public void update(){
		super.update();
		updateCenterPos();
		pickupXP();
		if (getWorld().isRemote) return;
		hasPower = energyStorage.getCEnergyStored() >= energyPerKill;
		disabled = getWorld().isBlockIndirectlyGettingPowered(getPos()) > 0;
		if (readyNext && !disabled) {
			if (energyStorage.getCEnergyStored() >= energyPerKill && killNextEntity()) {
				energyStorage.modifyEnergyStored(-energyPerKill);
				pickupItems();
			}
		}
		
		if (tick % 100 == 0) {
			readyNext = true;
		}
		detectAndSendChanges(tick % 500 == 0);
		tick++;
	}
	
	public void detectAndSendChanges(boolean force){
		
		if(shouldDoWorkThisTick(10)){
			if(xpCon.isDirty()){
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("XP", xpCon.getExperienceTotal());
				PacketTileMessage packet = new PacketTileMessage(getPos(), "UpdateXP", nbt);
				CrystalModNetwork.sendToAllAround(packet, this);
				xpCon.setDirty(false);
			}
			
			if(lastEnergyCach !=energyStorage.getCEnergyStored()){
				lastEnergyCach = energyStorage.getCEnergyStored();
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("Energy", energyStorage.getCEnergyStored());
				PacketTileMessage packet = new PacketTileMessage(getPos(), "UpdateEnergy", nbt);
				CrystalModNetwork.sendToAllAround(packet, this);
			}
		}
		
		if(hasPowerCach !=hasPower || disabledCach !=disabled || force){
			hasPowerCach = hasPower;
			disabledCach = disabled;
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setBoolean("hasPower", hasPower);
			nbt.setBoolean("disabled", disabled);
			PacketTileMessage packet = new PacketTileMessage(getPos(), "UpdateVars", nbt);
			CrystalModNetwork.sendToAllAround(packet, this);
		}
		
	}
	
	public AxisAlignedBB getKillBox(){
		return new AxisAlignedBB(centerPos.xCoord - 4.5, centerPos.yCoord - 4.5, centerPos.zCoord - 4.5, centerPos.xCoord + 4.5, centerPos.yCoord + 4.5, centerPos.zCoord + 4.5);
	}
	
	public boolean killNextEntity() {
		if (worldObj.isRemote) return false;
		
		if (fakePlayer == null){
			fakePlayer = FakePlayerFactory.get((WorldServer) worldObj, GRINDER);
		}

		killList = worldObj.getEntitiesWithinAABB(EntityLiving.class, getKillBox());

		DamageSource playerSource = DamageSource.causePlayerDamage(fakePlayer);
		
		if (killList.size() > 0) {
			for(EntityLiving mob : killList){
				
				boolean riddenByPlayer = mob.getRecursivePassengersByType(EntityPlayer.class).size() > 0;
				
				if (!mob.isDead && mob.deathTime <= 0 && !mob.isEntityInvulnerable(playerSource) && mob.hurtResistantTime == 0 && !riddenByPlayer) {
					if (mob instanceof EntityZombie) {
			            zBlocker.cache.add((EntityZombie) mob);
					}   
					
					//if (this.attackTool == null) {
			            //this.attackTool = new ItemStack(Items.DIAMOND_SWORD);
			            /*final ItemStack enchantsStack = this.enchants.getStack();
			            if (enchantsStack != null) {
			                EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(enchantsStack), this.attackTool);
			            }*/
			        //}
					
					/*ItemStack prev = fakePlayer.getHeldItemMainhand();
					
					fakePlayer.setHeldItem(EnumHand.MAIN_HAND, attackTool);
					if(prev !=null)fakePlayer.getAttributeMap().removeAttributeModifiers(prev.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
					if(attackTool !=null)fakePlayer.getAttributeMap().applyAttributeModifiers(attackTool.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
			        //fakePlayer.setPosition(mob.posX, mob.posY, mob.posZ);
			        float oldHealth = mob.getHealth();
			        this.fakePlayer.attackTargetEntityWithCurrentItem(mob);
			        fakePlayer.resetCooldown();
			        ModLogger.info("Attack2: "+(oldHealth-mob.getHealth()) + " "+fakePlayer.getHeldItemMainhand());
			        this.fakePlayer.setHeldItem(EnumHand.MAIN_HAND, (ItemStack)null);
			        */
					
					mob.attackEntityFrom(playerSource, 50000F);
					
					readyNext = true;
					return mob.isDead || mob.getHealth() <= 0;
				}
			}
		}
		readyNext = false;
		return false;
	}

	
	private void pickupItems()
	{
	  EnumFacing face = EnumFacing.getHorizontal(facing);
	  IItemHandler inv = ItemUtil.getExternalItemHandler(getWorld(), getPos().offset(face.getOpposite()), face);
	  if(inv == null){
		  return;	
	  }
	  ItemStack stack;
	  List<EntityItem> items = this.worldObj.getEntitiesWithinAABB(EntityItem.class, getKillBox());
	  for(EntityItem item : items)
	  {
	    stack = item.getEntityItem();
	    if(stack==null){continue;}
	    int added = ItemUtil.doInsertItem(inv, stack, face);
	    ItemStackTools.incStackSize(stack, -added);
	    if(ItemStackTools.isEmpty(stack)){
	    	item.setDead();
	    }
	  }
	  final InventoryPlayer inventory = this.fakePlayer.inventory;
      for (int i = 0; i < inventory.getSizeInventory(); ++i) {
          final ItemStack stack2 = inventory.getStackInSlot(i);
          if (ItemStackTools.isValid(stack2)) {
        	  int added = ItemUtil.doInsertItem(inv, stack2, face);
        	  ItemStackTools.incStackSize(stack2, -added);
          }
      }
      this.fakePlayer.inventory.clear();
	}
	
	private void pickupItem(ItemStack item)
	{
	  EnumFacing face = EnumFacing.getHorizontal(facing);
	  IItemHandler inv = ItemUtil.getExternalItemHandler(getWorld(), getPos().offset(face.getOpposite()), face);
	  if(inv == null || item == null){
		  return;	
	  }
	  int added = ItemUtil.doInsertItem(inv, item, face);
	  item.stackSize-=added;
	  if(item.stackSize <= 0){
		  item = null;
	  }
	}
	
	
	private void pickupXP() {

	    double maxDist = 4.5*2;

	    List<EntityXPOrb> xp = worldObj.getEntitiesWithinAABB(EntityXPOrb.class, getKillBox());

	    for (EntityXPOrb entity : xp) {
	      double xDist = (getPos().getX() + 0.5D - entity.posX);
	      double yDist = (getPos().getY() + 0.5D - entity.posY);
	      double zDist = (getPos().getZ() + 0.5D - entity.posZ);

	      double totalDistance = Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);

	      if (totalDistance < 1.5) {
	        pickupXP(entity);
	      } else {
	        double d = 1 - (Math.max(0.1, totalDistance) / maxDist);
	        double speed = 0.01 + (d * 0.02);

	        entity.motionX += xDist / totalDistance * speed;
	        entity.motionZ += zDist / totalDistance * speed;
	        entity.motionY += yDist / totalDistance * speed;
	        if (yDist > 0.5) {
	          entity.motionY = 0.12;
	        }

	      }
	    }
	}

	private void pickupXP(EntityXPOrb entity) {
	    if (!worldObj.isRemote) {
	      if (!entity.isDead) {
	        int xpValue = entity.getXpValue();
	        if (xpValue > 0) {
	          xpCon.addExperience(xpValue);
	        }
	        entity.setDead();
	      }
	    }
  	}
	
	public void updateCenterPos(){
		switch(facing){
			case 2 /*NORTH*/ :{
				double X = getPos().getX() + 0.5;
				double Y = getPos().getY() + 0.5;
				double Z = getPos().getZ() + 0.5 - 5;
				this.centerPos = new Vec3d(X, Y, Z);
				break;
			}
			case 0 /*SOUTH*/ :{
				double X = getPos().getX() + 0.5;
				double Y = getPos().getY() + 0.5;
				double Z = getPos().getZ() + 0.5 + 5;
				this.centerPos = new Vec3d(X, Y, Z);
				break;
			}
			case 1 /*WEST*/ :{
				double X = getPos().getX() + 0.5 - 5;
				double Y = getPos().getY() + 0.5;
				double Z = getPos().getZ() + 0.5;
				this.centerPos = new Vec3d(X, Y, Z);
				break;
			}
			case 3 /*EAST*/ :{
				double X = getPos().getX() + 0.5 + 5;
				double Y = getPos().getY() + 0.5;
				double Z = getPos().getZ() + 0.5;
				this.centerPos = new Vec3d(X, Y, Z);
				break;
			}
		}
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equalsIgnoreCase("UpdateVars")){
			if(messageData.hasKey("hasPower")){
				hasPower = messageData.getBoolean("hasPower");
			}
			if(messageData.hasKey("disabled")){
				disabled = messageData.getBoolean("disabled");
			}
		}
		if(messageId.equalsIgnoreCase("UpdateEnergy")){
			if(messageData.hasKey("Energy")){
				energyStorage.setEnergyStored(messageData.getInteger("Energy"));
			}
		}
		if(messageId.equalsIgnoreCase("UpdateXP")){
			if(messageData.hasKey("XP")){
				xpCon.setExperience(messageData.getInteger("XP"));
			}
		}
	}

	@Override
	public int getCEnergyStored(EnumFacing from) {
		return energyStorage.getCEnergyStored();
	}

	@Override
	public int getMaxCEnergyStored(EnumFacing from) {
		return energyStorage.getMaxCEnergyStored();
	}

	@Override
	public boolean canConnectCEnergy(EnumFacing from) {
		return from !=EnumFacing.getHorizontal(facing);
	}

	@Override
	public int fillCEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return energyStorage.fillCEnergy(maxReceive, simulate);
	}

	@Override
	public void setFacing(int facing) {
		this.facing = facing;
	}

	@Override
	public int getFacing() {
		return facing;
	}

	@Override
	public boolean isActive() {
		return !disabled && hasPower;
	}
	
	@Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
      return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facingIn);
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            //noinspection unchecked
            return (T) new IFluidHandler() {
            	
            	public FluidTank getTank(){
            		return xpCon;
            	}
            	
            	public int fill(FluidStack resource, boolean doFill) {
                   return 0;
                }

                public FluidStack drain(int maxEmpty, boolean doDrain) {
                	return getTank().drain(maxEmpty, doDrain);
                }

                public FluidStack drain(FluidStack resource, boolean doDrain) {
                    if (resource == null) {
                        return null;
                    }
                    if (!resource.isFluidEqual(getTank().getFluid())) {
                        return null;
                    }
                    return drain(resource.amount, doDrain);
                }

				@Override
				public IFluidTankProperties[] getTankProperties() {
					return getTank().getTankProperties();
				}
                
            };
        }
        return super.getCapability(capability, facing);
    }
	
}
