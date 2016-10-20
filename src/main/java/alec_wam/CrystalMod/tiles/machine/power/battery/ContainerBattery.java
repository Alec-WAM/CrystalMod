package alec_wam.CrystalMod.tiles.machine.power.battery;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class ContainerBattery extends Container {

	public TileEntityBattery battery;
	
	public ContainerBattery(EntityPlayer player, TileEntityBattery battery){
		this.battery = battery;
		for(int i = 0; i < 9; i++){
			this.addSlotToContainer(new Slot(player.inventory, i, 8 + i*18, 142));
		}
		
		
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 9; j++){
				this.addSlotToContainer(new Slot(player.inventory, 9+j+i*9, 8+18*j, 84+i*18));
			}
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

}
