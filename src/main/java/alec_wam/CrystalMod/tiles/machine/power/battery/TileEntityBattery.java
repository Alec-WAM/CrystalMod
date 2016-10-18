package alec_wam.CrystalMod.tiles.machine.power.battery;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.api.energy.ICEnergyProvider;
import alec_wam.CrystalMod.api.energy.ICEnergyReceiver;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityIOSides;
import alec_wam.CrystalMod.tiles.machine.power.battery.BlockBattery.BatteryType;

public class TileEntityBattery extends TileEntityIOSides implements IMessageHandler, ITickable, ICEnergyReceiver, ICEnergyProvider {
	public static final int[] MAX_SEND = { 80, 400, 2000, 10000, 50000, 50000};
	public static final int[] MAX_RECEIVE = { 80, 400, 2000, 10000, 50000, 0};
	public static final int[] MAX_ENERGY = new int[]{400000, 2000000, 10000000, 30000000, 50000000, 100000000};
	
	public int facing = EnumFacing.NORTH.ordinal();
	
	public CEnergyStorage energyStorage;
	
	public boolean loadedUpdate = false;
	
	public TileEntityBattery() {
		this(0);
	}
	
	public TileEntityBattery(int meta) {
		super();
		energyStorage = new CEnergyStorage(MAX_ENERGY[meta], MAX_RECEIVE[meta], MAX_SEND[meta]);
	}
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("Facing", facing);
		this.energyStorage.writeToNBT(nbt);
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		facing = nbt.getInteger("Facing");
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
	}

	public void update(){
		if(!getWorld().isRemote){
			for(EnumFacing face : EnumFacing.VALUES){
				EnumFacing fix = fixFace(face);
				IOType io = getIO(fix);
				if(io == IOType.OUT){
					TileEntity tile = this.getWorld().getTileEntity(getPos().offset(face));
					if(tile !=null && tile instanceof ICEnergyReceiver){
						ICEnergyReceiver rec = (ICEnergyReceiver)tile;
						boolean creative = BlockBattery.fromMeta(getBlockMetadata()) == BatteryType.CREATIVE;
						int drain = rec.fillCEnergy(face.getOpposite(), creative ? energyStorage.getMaxExtract() : Math.min(energyStorage.getMaxExtract(), energyStorage.getCEnergyStored()), false);
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
	public int getCEnergyStored(EnumFacing from) {
		return energyStorage.getCEnergyStored();
	}

	@Override
	public int getMaxCEnergyStored(EnumFacing from) {
		return energyStorage.getMaxCEnergyStored();
	}

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
	public boolean canConnectCEnergy(EnumFacing from) {
		return getIO(fixFace(from)) !=IOType.BLOCKED;
	}

	@Override
	public int fillCEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if(getIO(fixFace(from)) == IOType.BLOCKED || getIO(fixFace(from)) == IOType.OUT)return 0;
		
		boolean creative = BlockBattery.fromMeta(getBlockMetadata()) == BatteryType.CREATIVE;
		if(creative){
			return 0;
		}
		
		int fill = energyStorage.fillCEnergy(Math.min(MAX_RECEIVE[0], maxReceive), simulate);
		if(fill > 0 && !simulate){
			if(!getWorld().isRemote){
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("Energy", energyStorage.getCEnergyStored());
				CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateEnergy", nbt), this);
			}
			//getWorld().notifyBlockOfStateChange(getPos(), ModBlocks.battery);
		}
		return fill;
	}

	@Override
	public int drainCEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return 0;
	}

}
