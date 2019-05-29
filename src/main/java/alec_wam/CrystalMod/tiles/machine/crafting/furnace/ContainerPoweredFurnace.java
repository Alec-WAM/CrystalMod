package alec_wam.CrystalMod.tiles.machine.crafting.furnace;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerPoweredFurnace extends ContainerFurnace
{
    public TileEntityPoweredFurnace furnace;

    public ContainerPoweredFurnace(EntityPlayer player, TileEntityPoweredFurnace tileEntity)
    {
    	super(player.inventory, tileEntity);
    	this.furnace = (tileEntity);
    	this.inventorySlots.clear();
    	this.addSlot(new Slot(furnace, 0, 56, 26) {
    		@Override
    		public boolean isItemValid(ItemStack stack){
    			return TileEntityPoweredFurnace.canSmelt(stack, furnace);
    		}
    	});
    	this.addSlot(new SlotPoweredFurnaceOutput(player, furnace, 1, 116, 35));
        this.addPlayerInventory(player.inventory);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return (this.furnace != null && this.furnace.isUsableByPlayer(player));
    }

    protected void addPlayerInventory(InventoryPlayer paramInventoryPlayer)
    {
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                this.addSlot(new Slot(paramInventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++)
            this.addSlot(new Slot(paramInventoryPlayer, i, 8 + i * 18, 142));
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int i)
    {
    	ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(i);
        if (slot != null && slot.getHasStack()) {
           ItemStack itemstack1 = slot.getStack();
           itemstack = itemstack1.copy();
           if (i == 1) {
              if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
                 return ItemStack.EMPTY;
              }

              slot.onSlotChange(itemstack1, itemstack);
           } else if (i != 0) {
              if (TileEntityPoweredFurnace.canSmelt(itemstack1, furnace)) {
                 if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                 }
              }
              else if (i >= 1 && i < 29) {
                 if (!this.mergeItemStack(itemstack1, 29, 38, false)) {
                    return ItemStack.EMPTY;
                 }
              } else if (i >= 29 && i < 38 && !this.mergeItemStack(itemstack1, 2, 29, false)) {
                 return ItemStack.EMPTY;
              }
           } else if (!this.mergeItemStack(itemstack1, 2, 38, false)) {
              return ItemStack.EMPTY;
           }

           if (itemstack1.isEmpty()) {
              slot.putStack(ItemStack.EMPTY);
           } else {
              slot.onSlotChanged();
           }

           if (itemstack1.getCount() == itemstack.getCount()) {
              return ItemStack.EMPTY;
           }

           slot.onTake(player, itemstack1);
        }
		return itemstack;
    }

    
    //RecipeBook Stuff
	@Override
	public void func_201771_a(RecipeItemHelper p_201771_1_) {
		if (this.furnace instanceof IRecipeHelperPopulator) {
			((IRecipeHelperPopulator)this.furnace).fillStackedContents(p_201771_1_);
		}
	}

	@Override
	public void clear() {
		this.furnace.clear();
	}

	@Override
	public boolean matches(IRecipe p_201769_1_) {
		return p_201769_1_.matches(this.furnace, this.furnace.getWorld());
	}

	@Override
	public int getOutputSlot() {
		return 1;
	}

	@Override
	public int getWidth() {
		return 1;
	}

	@Override
	public int getHeight() {
		return 1;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getSize() {
		return 2;
	}
	
	@Override
	public void detectAndSendChanges() {
		for(int i = 0; i < this.inventorySlots.size(); ++i) {
			ItemStack itemstack = this.inventorySlots.get(i).getStack();
			ItemStack itemstack1 = this.inventoryItemStacks.get(i);
			if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
				boolean clientStackChanged = !itemstack1.equals(itemstack, true);
				itemstack1 = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();
				this.inventoryItemStacks.set(i, itemstack1);

				if (clientStackChanged){
					for(int j = 0; j < this.listeners.size(); ++j) {
						this.listeners.get(j).sendSlotContents(this, i, itemstack1);
					}
				}
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void updateProgressBar(int id, int data) {
		
	}
}
