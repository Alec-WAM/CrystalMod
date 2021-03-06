package alec_wam.CrystalMod.tiles.machine.elevator.floor;

import com.google.common.base.Strings;

import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.machine.elevator.TileEntityElevator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileEntityElevatorFloor extends TileEntityMod {

	private String floorName;
	
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		if(!Strings.isNullOrEmpty(floorName))nbt.setString("FloorName", floorName);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		if(nbt.hasKey("FloorName"))floorName = nbt.getString("FloorName");
	}
	
	public void setName(String name){
		this.floorName = name;
		updateFloors();
	}
	
	public String getName(){
		return floorName;
	}

	public void updateFloors() {
		
		if(!getWorld().isRemote){
			EnumFacing[] sides = EnumFacing.HORIZONTALS;
			if(sides !=null){
				for(EnumFacing side : sides){
					TileEntity tile = getWorld().getTileEntity(getPos().offset(side));
					if(tile !=null && tile instanceof TileEntityElevator){
						TileEntityElevator elevator = (TileEntityElevator)tile;
						elevator.updateFloors(false);
					}
				}
			}
		}
	}
	
}
