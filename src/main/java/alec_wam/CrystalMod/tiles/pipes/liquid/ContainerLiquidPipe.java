package alec_wam.CrystalMod.tiles.pipes.liquid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class ContainerLiquidPipe extends Container {

	public ContainerLiquidPipe(InventoryPlayer inventoryPlayer, TileEntityPipeLiquid pipe){
		for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 39 + j * 18, 130 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++)
        {
            addSlotToContainer(new Slot(inventoryPlayer, i, 39 + i * 18, 188));
        }
	}
	
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

}
