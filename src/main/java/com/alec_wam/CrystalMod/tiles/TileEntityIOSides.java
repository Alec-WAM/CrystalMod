package com.alec_wam.CrystalMod.tiles;

import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

import com.alec_wam.CrystalMod.network.IMessageHandler;
import com.alec_wam.CrystalMod.util.BlockUtil;
import com.google.common.collect.Maps;

public class TileEntityIOSides extends TileEntityMod implements IMessageHandler {



	public static enum IOType implements IStringSerializable {
		BLOCKED, IN, OUT;

		@Override
		public String getName() {
			return name().toLowerCase();
		}
		
	}
	
	//TM (Apple Computers Inc.) ;)
	private final Map<EnumFacing, IOType> ios = Maps.newHashMap();
		

	
	public void setIO(EnumFacing face, IOType io){
		this.ios.put(face, io);
		if(this.getWorld() !=null && this.getBlockType() !=null)this.getWorld().notifyNeighborsOfStateChange(getPos(), getBlockType());
	}
	
	public IOType getIO(EnumFacing face){
		IOType io = ios.get(face);
		if(io == null){
			this.ios.put(face, io = IOType.IN);
		}
		return io;
	}
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);

		for(EnumFacing face : EnumFacing.VALUES){
			if(!ios.containsKey(face)){
				ios.put(face, IOType.IN);
			}
			nbt.setByte("io."+face.name().toLowerCase(), (byte)ios.get(face).ordinal());
		}
	}
	
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
			this.ios.put(ioFace, type);
			BlockUtil.markBlockForUpdate(getWorld(), getPos());
		}
	}

}
