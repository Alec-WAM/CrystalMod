package com.alec_wam.CrystalMod.tiles.machine.elevator.caller;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.alec_wam.CrystalMod.tiles.TileEntityMod;
import com.alec_wam.CrystalMod.tiles.machine.elevator.TileEntityElevator;

public class TileEntityElevatorCaller extends TileEntityMod {

	public static class ElevatorButton {
		public double posX, posY, width, height;
        public String buttonText = "";
        public int floorNumber;
        public int floorHeight;
        public int dye;

        public ElevatorButton(double posX, double posY, double width, double height, int floorNumber, int floorHeight){
            this.posX = posX;
            this.posY = posY;
            this.width = width;
            this.height = height;
            this.floorNumber = floorNumber;
            this.floorHeight = floorHeight;
            buttonText = floorNumber + 1 + "";
        }

        public ElevatorButton(){}

        public void setColor(int dye){
        	this.dye = dye;
        }

        public void writeToNBT(NBTTagCompound tag){
            tag.setDouble("posX", posX);
            tag.setDouble("posY", posY);
            tag.setDouble("width", width);
            tag.setDouble("height", height);
            tag.setString("buttonText", buttonText);
            tag.setInteger("floorNumber", floorNumber);
            tag.setInteger("floorHeight", floorHeight);
            tag.setInteger("dye", dye);
        }

        public void readFromNBT(NBTTagCompound tag){
            posX = tag.getDouble("posX");
            posY = tag.getDouble("posY");
            width = tag.getDouble("width");
            height = tag.getDouble("height");
            buttonText = tag.getString("buttonText");
            floorNumber = tag.getInteger("floorNumber");
            floorHeight = tag.getInteger("floorHeight");
            dye = tag.getInteger("dye");
        }

	}

	public ElevatorButton[] buttons = new ElevatorButton[0];
	
	
	public ElevatorButton[] getFloors(){
        return buttons;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox(){
        return new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1);
    }
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("floors", buttons.length);
        for(ElevatorButton floor : buttons) {
            NBTTagCompound buttonTag = new NBTTagCompound();
            floor.writeToNBT(buttonTag);
            nbt.setTag("floor" + floor.floorNumber, buttonTag);
        }
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		int floorAmount = nbt.getInteger("floors");
		buttons = new ElevatorButton[floorAmount];
        for(int i = 0; i < floorAmount; i++) {
            NBTTagCompound buttonTag = nbt.getCompoundTag("floor" + i);
            buttons[i] = new ElevatorButton();
            buttons[i].readFromNBT(buttonTag);
        }
	}

	public void updateFloors() {
		
		if(!getWorld().isRemote){
			EnumFacing[] sides = EnumFacing.HORIZONTALS;
			if(sides !=null){
				for(EnumFacing side : sides){
					TileEntity tile = worldObj.getTileEntity(getPos().offset(side));
					if(tile !=null && tile instanceof TileEntityElevator){
						TileEntityElevator elevator = (TileEntityElevator)tile;
						BlockPos pos = elevator.findBottomElevator();
						if(pos !=null){
							TileEntity tile2 = worldObj.getTileEntity(pos);
							if(tile2 !=null && tile2 instanceof TileEntityElevator){
								TileEntityElevator elevator2 = (TileEntityElevator)tile2;
								elevator2.updateFloors(false);
							}
						}
					}
				}
			}
		}
	}
	
}
