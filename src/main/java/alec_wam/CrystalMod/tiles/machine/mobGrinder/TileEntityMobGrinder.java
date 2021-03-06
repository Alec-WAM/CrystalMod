package alec_wam.CrystalMod.tiles.machine.mobGrinder;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;

import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.api.energy.CapabilityCrystalEnergy;
import alec_wam.CrystalMod.fluids.XpUtil;
import alec_wam.CrystalMod.fluids.xp.ExperienceContainer;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.machine.IMachineTile;
import alec_wam.CrystalMod.tiles.xp.TileEntityXPVacuum;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
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

public class TileEntityMobGrinder extends TileEntityMod implements IMessageHandler, IMachineTile {

	
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
	
	public CEnergyStorage energyStorage = new CEnergyStorage(100000, 32000, 0) {
		@Override
		public boolean canExtract(){
			return false;
		}
	};
	private int lastEnergyCach;
	public ExperienceContainer xpCon;
	
	public FakePlayer fakePlayer;
	private static final GameProfile GRINDER = new GameProfile(UUID.nameUUIDFromBytes("[CrystalMod-Grinder]".getBytes()), "[CrystalMod-Grinder]");
	
	public TileEntityMobGrinder(){
		xpCon = new ExperienceContainer(XpUtil.getExperienceForLevel(30));
	    xpCon.setCanFill(false);
	    
	    if (zBlocker == null) {
	    	zBlocker = new ZombieBlocker();
	        MinecraftForge.EVENT_BUS.register(zBlocker);
	    }
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("Facing", facing);
		nbt.setBoolean("Disabled", disabled);
		nbt.setTag("EnergyStorage", energyStorage.writeToNBT(new NBTTagCompound()));
		nbt.setTag("XPStorage", xpCon.writeToNBT(new NBTTagCompound()));
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		facing = nbt.getInteger("Facing");
		disabled = nbt.getBoolean("Disabled");
		energyStorage.readFromNBT(nbt.getCompoundTag("EnergyStorage"));
		xpCon.readFromNBT(nbt.getCompoundTag("XPStorage"));
	}
	
	@Override
	public void update(){
		super.update();
		updateCenterPos();
		TileEntityXPVacuum.vacuumXP(getWorld(), getPos(), xpCon, getKillBox(), 4.5, 1.5);
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
		if (getWorld().isRemote) return false;
		
		if (fakePlayer == null){
			fakePlayer = FakePlayerFactory.get((WorldServer) getWorld(), GRINDER);
		}

		killList = getWorld().getEntitiesWithinAABB(EntityLiving.class, getKillBox());

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
	  List<EntityItem> items = getWorld().getEntitiesWithinAABB(EntityItem.class, getKillBox());
	  for(EntityItem item : items)
	  {
	    stack = item.getEntityItem();
	    if(ItemStackTools.isNullStack(stack)){continue;}
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
		if(capability == CapabilityCrystalEnergy.CENERGY){
			return facingIn.getHorizontalIndex() !=facing;
		}
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
            	
            	@Override
				public int fill(FluidStack resource, boolean doFill) {
                   return 0;
                }

                @Override
				public FluidStack drain(int maxEmpty, boolean doDrain) {
                	return getTank().drain(maxEmpty, doDrain);
                }

                @Override
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
        if(capability == CapabilityCrystalEnergy.CENERGY){
        	if(facing.getHorizontalIndex() == this.facing)return null;
        	return (T) energyStorage;
        }
        return super.getCapability(capability, facing);
    }
	
}
