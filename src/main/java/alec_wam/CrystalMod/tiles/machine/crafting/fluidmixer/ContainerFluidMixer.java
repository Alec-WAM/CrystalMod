package alec_wam.CrystalMod.tiles.machine.crafting.fluidmixer;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;

public class ContainerFluidMixer extends Container
{
    public TileEntityFluidMixer tileMachine;

    public ContainerFluidMixer(EntityPlayer player, TileEntityFluidMixer tileEntity)
    {
        this.addPlayerInventory(player.inventory);
        
        this.tileMachine = (tileEntity);

        this.addSlotToContainer(new SlotFurnaceOutput(player, tileMachine, 0, 80, 34));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return (this.tileMachine != null && this.tileMachine.isUsableByPlayer(player));
    }

    protected void addPlayerInventory(InventoryPlayer paramInventoryPlayer)
    {
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                this.addSlotToContainer(new Slot(paramInventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++)
            this.addSlotToContainer(new Slot(paramInventoryPlayer, i, 8 + i * 18, 142));
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int i)
    {
    	ItemStack itemstack = ItemStackTools.getEmptyStack();
    	int par2 = i;
		Slot slot = this.inventorySlots.get(par2);

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			/*output*/
			if (par2 == 36)
			{
				if (!this.mergeItemStack(itemstack1, 0, 35, false))
				{
					return ItemStackTools.getEmptyStack();
				}

				slot.onSlotChange(itemstack1, itemstack);
			}
			else 
			{
				if (par2 >= 0 && par2 < 27)
				{
					if (!this.mergeItemStack(itemstack1, 27, 35, false))
					{
						return ItemStackTools.getEmptyStack();
					}
				}
				else if (par2 >= 27 && par2 < 35 && !this.mergeItemStack(itemstack1, 0, 27, false))
				{
					return ItemStackTools.getEmptyStack();
				}
			}

			if (ItemStackTools.isEmpty(itemstack1))
			{
				slot.putStack(ItemStackTools.getEmptyStack());
			}
			else
			{
				slot.onSlotChanged();
			}

			if (ItemStackTools.getStackSize(itemstack1) == ItemStackTools.getStackSize(itemstack))
			{
				return ItemStackTools.getEmptyStack();
			}

			slot.onTake(player, itemstack1);
		}

		return itemstack;
    }
}
