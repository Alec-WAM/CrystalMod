package alec_wam.CrystalMod.tiles;

import java.util.EnumMap;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.util.BlockUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.IStringSerializable;

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

	protected final EnumMap<Direction, IOType> ioMap;

	public TileEntityIOSides(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
		ioMap = Maps.newEnumMap(Direction.class);
		for(Direction facing : Direction.values()){
			ioMap.put(facing, IOType.IN);
		}
	}
	
	public void setIO(Direction face, IOType io){
		this.ioMap.put(face, io);
		if(getWorld() !=null){
			BlockUtil.markBlockForUpdate(getWorld(), getPos());
		}
	}

	public IOType getIO(Direction face){
		return ioMap.getOrDefault(face, IOType.IN);
	}

	@Override
	public void writeCustomNBT(CompoundNBT nbt){
		super.writeCustomNBT(nbt);

		for(Direction face : Direction.values()){
			nbt.putByte("io."+face.name().toLowerCase(), (byte)getIO(face).ordinal());
		}
	}

	@Override
	public void readCustomNBT(CompoundNBT nbt){
		super.readCustomNBT(nbt);
		for(Direction face : Direction.values()){
			String nbtT = "io."+face.name().toLowerCase();
			this.ioMap.put(face, IOType.values()[nbt.getByte(nbtT)]);
		}
		updateAfterLoad();
	}

	@Override
	public void handleMessage(String messageId, CompoundNBT messageData, boolean client) {
		Direction ioFace = null;
		if(messageId.equalsIgnoreCase("IO.UP"))ioFace = Direction.UP;
		if(messageId.equalsIgnoreCase("IO.DOWN"))ioFace = Direction.DOWN;
		if(messageId.equalsIgnoreCase("IO.NORTH"))ioFace = Direction.NORTH;
		if(messageId.equalsIgnoreCase("IO.SOUTH"))ioFace = Direction.SOUTH;
		if(messageId.equalsIgnoreCase("IO.WEST"))ioFace = Direction.WEST;
		if(messageId.equalsIgnoreCase("IO.EAST"))ioFace = Direction.EAST;
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
	
	public abstract Direction getFacing();
	
	public Direction fixFace(Direction side){
		Direction fixedDir = side;
		Direction facing = getFacing();
		if(facing == Direction.SOUTH){
			if(side !=Direction.UP && side !=Direction.DOWN)fixedDir = side.getOpposite();
		}
		
		if(facing == Direction.WEST){
			if(side !=Direction.UP && side !=Direction.DOWN)fixedDir = side.rotateAround(Axis.Y);
		}
		
		if(facing == Direction.EAST){
			if(side !=Direction.UP && side !=Direction.DOWN)fixedDir = side.getOpposite().rotateAround(Axis.Y);
		}
		
		if(facing == Direction.UP){
			fixedDir = side.rotateAround(Axis.X);
		}
		
		if(facing == Direction.DOWN){
			if(side == Direction.WEST || side == Direction.EAST)fixedDir = side.rotateAround(Axis.X);
			else fixedDir = side.getOpposite().rotateAround(Axis.X);
		}
		return fixedDir;
	}

}
