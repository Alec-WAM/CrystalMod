package alec_wam.CrystalMod.tiles.cluster;

import java.util.Random;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.Config.RegenType;
import alec_wam.CrystalMod.api.energy.CapabilityCrystalEnergy;
import alec_wam.CrystalMod.api.energy.ICEnergyStorage;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.machine.IFacingTile;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.CrystalColors;
import alec_wam.CrystalMod.util.TimeUtil;
import alec_wam.CrystalMod.util.data.watchable.WatchableInteger;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

public class TileCrystalCluster extends TileEntityMod implements IMessageHandler, IFacingTile {

	public static class ClusterData implements INBTSerializable<NBTTagCompound>{
		private int powerOutput, regenSpeed;
		
		public ClusterData(int output, int regenSpeed){
			this.powerOutput = output;
			this.regenSpeed = regenSpeed;
		}
		
		public int getPowerOutput(){
			return this.powerOutput;
		}
		
		public void setPowerOutput(int output){
			this.powerOutput = output;
		}

		public int getRegenSpeed() {
			return regenSpeed;
		}

		public void setRegenSpeed(int regenSpeed) {
			this.regenSpeed = regenSpeed;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("PowerOutput", powerOutput);
			nbt.setInteger("RegenSpeed", regenSpeed);
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			this.powerOutput = nbt.getInteger("PowerOutput");
			this.regenSpeed = nbt.getInteger("RegenSpeed");
		}
	}
	
	private WatchableInteger health = new WatchableInteger();
	private int drainDelay;
	private ClusterData clusterData = new ClusterData(22, 1);
	private EnumFacing facing = EnumFacing.UP;
	private boolean canRegen;
	
	public TileCrystalCluster(){
		health.setValue(TimeUtil.MINECRAFT_DAY_TICKS);
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("Health", health.getValue());
		nbt.setInteger("DrainDelay", drainDelay);
		if(clusterData !=null)nbt.setTag("ClusterData", clusterData.serializeNBT());
		nbt.setInteger("Facing", facing.getIndex());
		nbt.setBoolean("CanRegen", canRegen);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		health.setValue(nbt.getInteger("Health"));
		drainDelay = nbt.getInteger("DrainDelay");
		clusterData = new ClusterData(22, 1);
		if(nbt.hasKey("ClusterData"))clusterData.deserializeNBT(nbt.getCompoundTag("ClusterData"));
		this.facing = nbt.hasKey("Facing") ? EnumFacing.getFront(nbt.getInteger("Facing")) : EnumFacing.UP;
		this.canRegen = nbt.getBoolean("CanRegen");
		updateAfterLoad();
	}
	
	@Override
	public void update(){
		super.update();
		if(!getWorld().isRemote){
			for(EnumFacing face : EnumFacing.VALUES){
				TileEntity tile = getWorld().getTileEntity(getPos().offset(face));
				if(tile !=null && tile.hasCapability(CapabilityCrystalEnergy.CENERGY, face.getOpposite())){
					ICEnergyStorage rec = tile.getCapability(CapabilityCrystalEnergy.CENERGY, face.getOpposite());
					int multi = 25;
					for(int i = 0; i < multi; i++){
						if(rec.canReceive() && rec.fillCEnergy(getPowerOutput(), false) > 0){
							health.subSafe(1);
							drainDelay = 10;
						}
					}
				}
			}
			
			RegenType type = Config.crystalClusterRegenType;
			if(type == RegenType.IDLE){
				canRegen = true;
			} else if(type == RegenType.EMPTY){
				if(canRegen == false){
					if(this.health.getValue() <=0 && drainDelay <= 0){
						canRegen = true;
					}
				}
				
				if(canRegen && drainDelay > 0){
					canRegen = false;
				}
			} else if(type == RegenType.NEVER){
				canRegen = false;
			}
			
			if(drainDelay > 0){
				drainDelay--;
			}
			
			if(canRegen){
				if(health.getValue() < TimeUtil.MINECRAFT_DAY_TICKS && drainDelay <=0){
					health.add(clusterData.getRegenSpeed());
					if(health.getValue() > TimeUtil.MINECRAFT_DAY_TICKS){
						health.setValue(TimeUtil.MINECRAFT_DAY_TICKS);
					}
				}
			}
			
			boolean healthChanged = (health.getLastValue() != health.getValue() && shouldDoWorkThisTick(10));
		    if(healthChanged) {
		    	health.setLastValue(health.getValue());
		    	NBTTagCompound nbt = new NBTTagCompound();
		    	nbt.setInteger("Health", health.getValue());
		    	CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "Health", nbt), this);
		    }
		}
	}
	
	public int getPowerOutput(){
		return calculatePowerOutput(clusterData.getPowerOutput(), health.getValue(), TimeUtil.MINECRAFT_DAY_TICKS);
	}
	
	public static int calculatePowerOutput(int powerout, int health, int maxhealth){
		int calc = (int) (powerout * ((float)health / (float)maxhealth));
		if(calc <= 0 && health > 0){
			return 1;
		}
		return calc;
	}

	public ClusterData getClusterData() {
		return clusterData;
	}
	
	public void setClusterData(ClusterData newData) {
		clusterData = newData;
	}

	public int getHealth() {
		return health.getValue();
	}

	public void setHealth(int value) {
		health.setValue(value);
	}
	
	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equalsIgnoreCase("Health")){
			this.health.setValue(messageData.getInteger("Health"));
		}
	}

	@Override
	public void setFacing(int facing) {
		this.facing = EnumFacing.getFront(facing);
	}

	@Override
	public int getFacing() {
		return facing.getIndex();
	}
	
	public static void createRandomCluster(World world, Random rand, BlockPos pos, CrystalColors.Basic type, int minPower, int maxPower, int minRegen, int maxRegen, boolean randomHealth){
		//Randomized based on seed. If the world has the same seed it will generate the same clusters.
		world.setBlockState(pos, ModBlocks.crystalCluster.getStateFromMeta(type.getMeta()), 3);
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileCrystalCluster){
			TileCrystalCluster cluster = (TileCrystalCluster)tile;
			ClusterData data = new ClusterData(0, 0);
			data.setPowerOutput(MathHelper.getInt(rand, minPower, maxPower));
			data.setRegenSpeed(MathHelper.getInt(rand, minRegen, maxRegen));
			cluster.setClusterData(data);
			if(randomHealth){
				cluster.setHealth(rand.nextInt(TimeUtil.MINECRAFT_DAY_TICKS));
			} else {
				cluster.setHealth(TimeUtil.MINECRAFT_DAY_TICKS);
			}
			BlockUtil.markBlockForUpdate(world, pos);
		}
	}
	
	@Override
	public boolean canRenderBreaking(){
		return true;
	}
	
	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing)
    {
		return capability == CapabilityCrystalEnergy.CENERGY || super.hasCapability(capability, facing);
	}
	
	@SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing)
    {
        if (facing != null && capability == CapabilityCrystalEnergy.CENERGY){
            return (T) new ICEnergyStorage(){

				@Override
				public int fillCEnergy(int maxReceive, boolean simulate) {
					return 0;
				}

				@Override
				public int drainCEnergy(int maxExtract, boolean simulate) {
					int drain = getPowerOutput();
					if(!simulate){
						health.subSafe(1);
						drainDelay = 10;
					}
					return drain;
				}

				@Override
				public int getCEnergyStored() {
					return clusterData.getPowerOutput() * health.getValue();
				}

				@Override
				public int getMaxCEnergyStored() {
					return clusterData.getPowerOutput() * TimeUtil.MINECRAFT_DAY_TICKS;
				}

				@Override
				public boolean canExtract() {
					return true;
				}

				@Override
				public boolean canReceive() {
					return false;
				}
            	
            };
        }
        return super.getCapability(capability, facing);
    }
	
}
