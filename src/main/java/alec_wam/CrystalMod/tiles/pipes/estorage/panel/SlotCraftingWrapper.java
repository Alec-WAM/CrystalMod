package alec_wam.CrystalMod.tiles.pipes.estorage.panel;

import alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting.ContainerPanelCrafting;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting.TileEntityPanelCrafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

public class SlotCraftingWrapper extends SlotCrafting {

  private final InventoryCrafting craftMatrix;
  private ContainerPanelCrafting container;
  private TileEntityPanelCrafting panel;
  
  public SlotCraftingWrapper(ContainerPanelCrafting con, EntityPlayer player, InventoryCrafting craftingInventory, TileEntityPanelCrafting p_i45790_3_, int slotIndex, int xPosition, int yPosition) {
    super(player, craftingInventory, (IInventory) p_i45790_3_, slotIndex, xPosition, yPosition);
    panel = p_i45790_3_;
    container = con;
    craftMatrix = craftingInventory;
  }

  @Override
  public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {

	net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(playerIn, stack, craftMatrix);
    onCrafting(stack);
    
    if(!playerIn.worldObj.isRemote){
    	panel.onCrafted(playerIn);
    	container.sendCraftingSlots();
    }
  }

  @Override
  public ItemStack decrStackSize(int p_75209_1_) {
    if (this.getHasStack()) {
      // on a right click we are asked to craft half a result. Ignore that.
      return super.decrStackSize(this.getStack().stackSize);
    }
    return super.decrStackSize(p_75209_1_);
  }
}
