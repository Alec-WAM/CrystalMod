package alec_wam.CrystalMod.tiles.machine.elevator.floor;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.machine.elevator.TileEntityElevator;

import com.google.common.base.Strings;

public class TileEntityElevatorFloor extends TileEntityMod {

	private String floorName;
	
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		if(!Strings.isNullOrEmpty(floorName))nbt.setString("FloorName", floorName);
	}
	
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
					TileEntity tile = worldObj.getTileEntity(getPos().offset(side));
					if(tile !=null && tile instanceof TileEntityElevator){
						TileEntityElevator elevator = (TileEntityElevator)tile;
						elevator.updateFloors(false);
					}
				}
			}
		}
	}
	
}
