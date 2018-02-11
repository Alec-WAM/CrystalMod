package alec_wam.CrystalMod.tiles.machine.power.battery;

import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.api.energy.CapabilityCrystalEnergy;
import alec_wam.CrystalMod.api.energy.ICEnergyStorage;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityIOSides;
import alec_wam.CrystalMod.tiles.machine.power.battery.BlockBattery.BatteryType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;

public class TileEntityBattery extends TileEntityIOSides implements IMessageHandler, ITickable {
	public static final int[] MAX_SEND = { 80, 400, 2000, 10000, 50000, 50000};
	public static final int[] MAX_RECEIVE = { 80, 400, 2000, 10000, 50000, 0};
	public static final int[] MAX_ENERGY = new int[]{400000, 2000000, 10000000, 30000000, 50000000, 100000000};
	
	public int facing = EnumFacing.NORTH.ordinal();
	
	public CEnergyStorage energyStorage;
	public int sendAmount;
	public int receiveAmount; 
	
	public boolean loadedUpdate = false;
	
	public TileEntityBattery() {
		this(0);
	}
	
	public TileEntityBattery(int meta) {
		super();
		energyStorage = new CEnergyStorage(MAX_ENERGY[meta], MAX_RECEIVE[meta], MAX_SEND[meta]);
		this.sendAmount = MAX_SEND[meta];
		this.receiveAmount = MAX_RECEIVE[meta];
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("Type", getBlockMetadata());
		nbt.setInteger("Facing", facing);
		nbt.setInteger("Send", sendAmount);
		nbt.setInteger("Receive", receiveAmount);
		this.energyStorage.writeToNBT(nbt);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		facing = nbt.getInteger("Facing");
		int meta = nbt.getInteger("Type");
		energyStorage = new CEnergyStorage(MAX_ENERGY[meta], MAX_RECEIVE[meta], MAX_SEND[meta]);
		if(nbt.hasKey("Send")){
			this.sendAmount = Math.min(MAX_SEND[meta], nbt.getInteger("Send"));
		} else {
			this.sendAmount = MAX_SEND[meta];
		}
		if(nbt.hasKey("Receive")){
			this.receiveAmount = Math.min(MAX_RECEIVE[meta], nbt.getInteger("Receive"));
		}else{
			this.receiveAmount =MAX_RECEIVE[meta];
		}
		this.energyStorage.readFromNBT(nbt);
		updateAfterLoad();
	}

	public int getScaledEnergyStored(int paramInt)
	{
		return this.energyStorage.getCEnergyStored() * paramInt / this.energyStorage.getMaxCEnergyStored();
	}
	
	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData,	boolean client) {
		super.handleMessage(messageId, messageData, client);
		if(messageId.equalsIgnoreCase("UpdateEnergy")){
			energyStorage.setEnergyStored(messageData.getInteger("Energy"));
		}
		if(messageId.equalsIgnoreCase("UpdateSend")){
			this.sendAmount = messageData.getInteger("Amount");
		}
		if(messageId.equalsIgnoreCase("UpdateReceive")){
			this.receiveAmount = messageData.getInteger("Amount");
		}
	}
	
	public int getEnergySend(){
		return Math.min(MAX_SEND[getBlockMetadata()], sendAmount);
	}
	
	public int getEnergyReceive(){
		return Math.min(MAX_RECEIVE[getBlockMetadata()], receiveAmount);
	}

	@Override
	public void update(){
		if(!getWorld().isRemote){
			for(EnumFacing face : EnumFacing.VALUES){
				EnumFacing fix = fixFace(face);
				IOType io = getIO(fix);
				if(io == IOType.OUT){
					TileEntity tile = this.getWorld().getTileEntity(getPos().offset(face));
					if(tile !=null && tile.hasCapability(CapabilityCrystalEnergy.CENERGY, face.getOpposite())){
						ICEnergyStorage rec = tile.getCapability(CapabilityCrystalEnergy.CENERGY, face.getOpposite());
						boolean creative = BlockBattery.fromMeta(getBlockMetadata()) == BatteryType.CREATIVE;
						int drain = !rec.canReceive() ? 0 : rec.fillCEnergy(creative ? getEnergySend() : Math.min(getEnergySend(), energyStorage.getCEnergyStored()), false);
						if(!creative){
							this.energyStorage.modifyEnergyStored(-drain);
							if(drain > 0){
								this.markDirty();
								NBTTagCompound nbt = new NBTTagCompound();
								nbt.setInteger("Energy", this.energyStorage.getCEnergyStored());
								CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateEnergy", nbt), this);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public EnumFacing fixFace(EnumFacing side){
		EnumFacing fixedDir = side;
		
		if(facing == EnumFacing.SOUTH.ordinal()){
			if(side !=EnumFacing.UP && side !=EnumFacing.DOWN)fixedDir = side.getOpposite();
		}
		
		if(facing == EnumFacing.WEST.ordinal()){
			if(side !=EnumFacing.UP && side !=EnumFacing.DOWN)fixedDir = side.rotateAround(Axis.Y);
		}
		
		if(facing == EnumFacing.EAST.ordinal()){
			if(side !=EnumFacing.UP && side !=EnumFacing.DOWN)fixedDir = side.getOpposite().rotateAround(Axis.Y);
		}
		
		if(facing == EnumFacing.UP.ordinal()){
			fixedDir = side.rotateAround(Axis.X);
		}
		
		if(facing == EnumFacing.DOWN.ordinal()){
			if(side == EnumFacing.WEST || side == EnumFacing.EAST)fixedDir = side.rotateAround(Axis.X);
			else fixedDir = side.getOpposite().rotateAround(Axis.X);
		}
		return fixedDir;
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
					if(!canReceive())return 0;
					
					boolean creative = BlockBattery.fromMeta(getBlockMetadata()) == BatteryType.CREATIVE;
					if(creative){
						return 0;
					}
					
					int fill = energyStorage.fillCEnergy(Math.min(getEnergyReceive(), maxReceive), simulate);
					if(fill > 0 && !simulate){
						if(!getWorld().isRemote){
							NBTTagCompound nbt = new NBTTagCompound();
							nbt.setInteger("Energy", energyStorage.getCEnergyStored());
							CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateEnergy", nbt), TileEntityBattery.this);
						}
					}
					return fill;
				}

				@Override
				public int drainCEnergy(int maxExtract, boolean simulate) {
					return 0;
				}

				@Override
				public int getCEnergyStored() {
					return energyStorage.getCEnergyStored();
				}

				@Override
				public int getMaxCEnergyStored() {
					return energyStorage.getMaxCEnergyStored();
				}

				@Override
				public boolean canExtract() {
					return false;
				}

				@Override
				public boolean canReceive() {
					return !(getIO(fixFace(facing)) == IOType.BLOCKED || getIO(fixFace(facing)) == IOType.OUT);
				}
            	
            };
        }
        return super.getCapability(capability, facing);
    }

}
