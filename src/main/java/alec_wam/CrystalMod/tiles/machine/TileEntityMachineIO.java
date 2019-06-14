package alec_wam.CrystalMod.tiles.machine;

import java.util.EnumMap;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

public abstract class TileEntityMachineIO extends TileEntityMachine {
	
	public static enum ItemIOType implements IStringSerializable {
		BLOCKED, NOTHING, IN, OUT, BOTH;

		@Override
		public String getName() {
			return name().toLowerCase();
		}

		public ItemIOType getNext(){
			if(this == ItemIOType.BLOCKED){
				return ItemIOType.NOTHING;
			}
			if(this == ItemIOType.NOTHING){
				return ItemIOType.IN;
			}
			if(this == ItemIOType.IN){
				return ItemIOType.OUT;
			}
			if(this == ItemIOType.OUT){
				return ItemIOType.BOTH;
			}
			return ItemIOType.BLOCKED;
		}

	}

	protected final EnumMap<Direction, ItemIOType> ioMap;
	
	public TileEntityMachineIO(TileEntityType<?> tileEntityTypeIn, String name, int size) {
		super(tileEntityTypeIn, name, size);
		ioMap = Maps.newEnumMap(Direction.class);
		for(Direction facing : Direction.values()){
			ioMap.put(facing, ItemIOType.NOTHING);
		}
	}
	
	public void setIO(Direction face, ItemIOType io){
		this.ioMap.put(face, io);
		if(getWorld() !=null){
			BlockUtil.markBlockForUpdate(getWorld(), getPos());
		}
	}

	public ItemIOType getIO(Direction face){
		return ioMap.getOrDefault(face, ItemIOType.NOTHING);
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
			this.ioMap.put(face, ItemIOType.values()[nbt.getByte(nbtT)]);
		}
		updateAfterLoad();
	}

	@Override
	public void handleMessage(String messageId, CompoundNBT messageData, boolean client) {
		super.handleMessage(messageId, messageData, client);
		Direction ioFace = null;
		if(messageId.equalsIgnoreCase("IO.UP"))ioFace = Direction.UP;
		if(messageId.equalsIgnoreCase("IO.DOWN"))ioFace = Direction.DOWN;
		if(messageId.equalsIgnoreCase("IO.NORTH"))ioFace = Direction.NORTH;
		if(messageId.equalsIgnoreCase("IO.SOUTH"))ioFace = Direction.SOUTH;
		if(messageId.equalsIgnoreCase("IO.WEST"))ioFace = Direction.WEST;
		if(messageId.equalsIgnoreCase("IO.EAST"))ioFace = Direction.EAST;
		if(ioFace !=null){
			ItemIOType type = getIO(ioFace);
			for(ItemIOType t : ItemIOType.values()){
				if(t.name().equalsIgnoreCase(messageData.getString("IOType"))){
					type = t;
					break;
				}
			}
			this.setIO(ioFace, type);
		}
	}
	
	public abstract Direction getFacing();
	
	public Direction fixDirection(Direction side){
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
	
	@Override
	public void tick(){
		super.tick();
		
		if(!getWorld().isRemote){
			if(shouldDoWorkThisTick(10)){
				search : for(Direction face : Direction.values()){
					Direction fix = fixDirection(face);
					ItemIOType io = getIO(fix);
					BlockPos otherPos = getPos().offset(face);
					TileEntity tile = getWorld().getTileEntity(otherPos);
					
					if(io == ItemIOType.OUT || io == ItemIOType.BOTH){
						if(tile instanceof TileEntityMachineIO){
							TileEntityMachineIO machine = (TileEntityMachineIO)tile;
							ItemIOType otherIO = machine.getIO(face.getOpposite());
							if(otherIO == ItemIOType.OUT || otherIO == ItemIOType.BLOCKED)continue search;
						}
						
						IItemHandler handler = ItemUtil.getExternalItemHandler(getWorld(), otherPos, face.getOpposite());
						if(handler !=null){
							if(pushOutputItems(handler, fix)){
								continue search;
							}
						}
					}
					if(io == ItemIOType.IN || io == ItemIOType.BOTH){
						if(tile instanceof TileEntityMachineIO){
							TileEntityMachineIO machine = (TileEntityMachineIO)tile;
							ItemIOType otherIO = machine.getIO(face.getOpposite());
							if(otherIO == ItemIOType.IN || otherIO == ItemIOType.BLOCKED)continue search;
						}
						IItemHandler handler = ItemUtil.getExternalItemHandler(getWorld(), getPos().offset(face), face.getOpposite());
						if(handler !=null){
							if(pullItemsIn(handler, fix)){
								continue search;
							}
						}
					}
				}
			}
		}
	}
	
	public abstract boolean pullItemsIn(IItemHandler handler, Direction from);
	
	public abstract boolean pushOutputItems(IItemHandler handler, Direction to);

	
	
}
