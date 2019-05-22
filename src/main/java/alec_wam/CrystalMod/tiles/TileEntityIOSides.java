package alec_wam.CrystalMod.tiles;

import java.util.EnumMap;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.util.BlockUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.EnumFacing.Axis;

public abstract class TileEntityIOSides extends TileEntityMod implements IMessageHandler {

	public static enum IOType implements IStringSerializable {
		BLOCKED, IN, OUT;

		@Override
		public String getName() {
			return name().toLowerCase();
		}

		public IOType getNext(){
			if(this == IOType.BLOCKED){
				return IOType.IN;
			}
			if(this == IOType.IN){
				return IOType.OUT;
			}
			return IOType.BLOCKED;
		}

	}

	protected final EnumMap<EnumFacing, IOType> ioMap;

	public TileEntityIOSides(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
		ioMap = Maps.newEnumMap(EnumFacing.class);
		for(EnumFacing facing : EnumFacing.values()){
			ioMap.put(facing, IOType.IN);
		}
	}
	
	public void setIO(EnumFacing face, IOType io){
		this.ioMap.put(face, io);
		if(getWorld() !=null){
			BlockUtil.markBlockForUpdate(getWorld(), getPos());
		}
	}

	public IOType getIO(EnumFacing face){
		return ioMap.getOrDefault(face, IOType.IN);
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);

		for(EnumFacing face : EnumFacing.values()){
			nbt.setByte("io."+face.name().toLowerCase(), (byte)getIO(face).ordinal());
		}
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		for(EnumFacing face : EnumFacing.values()){
			String nbtT = "io."+face.name().toLowerCase();
			this.ioMap.put(face, IOType.values()[nbt.getByte(nbtT)]);
		}
		updateAfterLoad();
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		EnumFacing ioFace = null;
		if(messageId.equalsIgnoreCase("IO.UP"))ioFace = EnumFacing.UP;
		if(messageId.equalsIgnoreCase("IO.DOWN"))ioFace = EnumFacing.DOWN;
		if(messageId.equalsIgnoreCase("IO.NORTH"))ioFace = EnumFacing.NORTH;
		if(messageId.equalsIgnoreCase("IO.SOUTH"))ioFace = EnumFacing.SOUTH;
		if(messageId.equalsIgnoreCase("IO.WEST"))ioFace = EnumFacing.WEST;
		if(messageId.equalsIgnoreCase("IO.EAST"))ioFace = EnumFacing.EAST;
		if(ioFace !=null){
			IOType type = getIO(ioFace);
			for(IOType t : IOType.values()){
				if(t.name().equalsIgnoreCase(messageData.getString("IOType"))){
					type = t;
					break;
				}
			}
			this.setIO(ioFace, type);
		}
	}
	
	public abstract EnumFacing getFacing();
	
	public EnumFacing fixFace(EnumFacing side){
		EnumFacing fixedDir = side;
		EnumFacing facing = getFacing();
		if(facing == EnumFacing.SOUTH){
			if(side !=EnumFacing.UP && side !=EnumFacing.DOWN)fixedDir = side.getOpposite();
		}
		
		if(facing == EnumFacing.WEST){
			if(side !=EnumFacing.UP && side !=EnumFacing.DOWN)fixedDir = side.rotateAround(Axis.Y);
		}
		
		if(facing == EnumFacing.EAST){
			if(side !=EnumFacing.UP && side !=EnumFacing.DOWN)fixedDir = side.getOpposite().rotateAround(Axis.Y);
		}
		
		if(facing == EnumFacing.UP){
			fixedDir = side.rotateAround(Axis.X);
		}
		
		if(facing == EnumFacing.DOWN){
			if(side == EnumFacing.WEST || side == EnumFacing.EAST)fixedDir = side.rotateAround(Axis.X);
			else fixedDir = side.getOpposite().rotateAround(Axis.X);
		}
		return fixedDir;
	}

}
