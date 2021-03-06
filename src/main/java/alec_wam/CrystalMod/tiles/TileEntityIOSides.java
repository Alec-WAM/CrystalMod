package alec_wam.CrystalMod.tiles;

import java.util.EnumMap;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.util.BlockUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

public class TileEntityIOSides extends TileEntityMod implements IMessageHandler {



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
	
	//TM (Apple Computers Inc.) ;)
	private final EnumMap<EnumFacing, IOType> ios = Maps.newEnumMap(EnumFacing.class);
		

	
	public void setIO(EnumFacing face, IOType io){
		this.ios.put(face, io);
		if(this.getWorld() !=null && this.getBlockType() !=null)BlockUtil.markBlockForUpdate(getWorld(), getPos());
	}
	
	public IOType getIO(EnumFacing face){
		IOType io = ios.get(face);
		if(io == null){
			this.ios.put(face, io = IOType.IN);
		}
		return io;
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);

		for(EnumFacing face : EnumFacing.VALUES){
			if(!ios.containsKey(face)){
				ios.put(face, IOType.IN);
			}
			nbt.setByte("io."+face.name().toLowerCase(), (byte)ios.get(face).ordinal());
		}
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		for(EnumFacing face : EnumFacing.VALUES){
			String nbtT = "io."+face.name().toLowerCase();
			if(nbt.hasKey(nbtT)){
				this.ios.put(face, IOType.values()[nbt.getByte(nbtT)]);
			}else{
				ios.put(face, IOType.IN);
			}
		}
		updateAfterLoad();
	}
	


	public EnumFacing fixFace(EnumFacing side){
		EnumFacing fixedDir = side;
		return fixedDir;
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

}
