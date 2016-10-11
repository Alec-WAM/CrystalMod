package alec_wam.CrystalMod.entities.minions.warrior;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerMinionWarrior extends Container {

	public ContainerMinionWarrior(EntityPlayer player, EntityMinionWarrior warrior){
		
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

}
