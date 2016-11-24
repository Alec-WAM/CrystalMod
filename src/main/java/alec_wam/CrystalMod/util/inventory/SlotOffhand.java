package alec_wam.CrystalMod.util.inventory;

import javax.annotation.Nullable;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SlotOffhand extends Slot {

	public SlotOffhand(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}
	
	@Nullable
    @SideOnly(Side.CLIENT)
    public String getSlotTexture()
    {
        return "minecraft:items/empty_armor_slot_shield";
    }

}
