package alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting;

import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.slot.SlotDisabled;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.slot.SlotOutput;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.slot.SlotSpecimen;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.slot.SlotSpecimenLegacy;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerPatternEncoder extends Container {

	private SlotDisabled patternResultSlot;
	public TilePatternEncoder encoder;

	public ContainerPatternEncoder(EntityPlayer player, TilePatternEncoder encoder){
		this.encoder = encoder;
		if(encoder instanceof TileProcessingPatternEncoder){
			TileProcessingPatternEncoder processingPatternEncoder = (TileProcessingPatternEncoder)encoder;
			int ox = 8;
	        int x = ox;
	        int y = 20;

	        for (int i = 0; i < 9 * 2; ++i) {
	            addSlotToContainer(new SlotSpecimen(processingPatternEncoder.getConfiguration(), i, x, y));

	            x += 18;

	            if ((i + 1) % 3 == 0) {
	                if (i == 8) {
	                    ox = 90;
	                    x = ox;
	                    y = 20;
	                } else {
	                    x = ox;
	                    y += 18;
	                }
	            }
	        }

	        addSlotToContainer(new SlotItemHandler(processingPatternEncoder.getPatterns(), 0, 152, 18));
	        addSlotToContainer(new SlotOutput(processingPatternEncoder.getPatterns(), 1, 152, 58));

	        addPlayerInventory(player, 8, 90);
			return;
		}
		addPlayerInventory(player, 8, 90);
		
		int x = 8;
        int y = 20;

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotSpecimenLegacy(encoder.getMatrix(), i, x, y, false));

            x += 18;

            if ((i + 1) % 3 == 0) {
                y += 18;
                x = 8;
            }
        }

        addSlotToContainer(patternResultSlot = new SlotDisabled(encoder.getResult(), 0, 116 + 4, 38));

        addSlotToContainer(new SlotItemHandler(encoder.getPatterns(), 0, 152, 18));
        addSlotToContainer(new SlotOutput(encoder.getPatterns(), 1, 152, 58));
	}
	
	protected void addPlayerInventory(EntityPlayer player, int xInventory, int yInventory) {
        int id = 0;

        for (int i = 0; i < 9; i++) {
            Slot slot = new Slot(player.inventory, id, xInventory + i * 18, yInventory + 4 + (3 * 18));

            //playerInventorySlots.add(slot);

            addSlotToContainer(slot);

            id++;
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                Slot slot = new Slot(player.inventory, id, xInventory + x * 18, yInventory + y * 18);

                //playerInventorySlots.add(slot);

                addSlotToContainer(slot);

                id++;
            }
        }
    }
	
	@Override
    public ItemStack slotClick(int id, int clickedButton, ClickType clickType, EntityPlayer player) {
        Slot slot = id >= 0 ? getSlot(id) : null;

        if (slot instanceof SlotSpecimen) {
            if (((SlotSpecimen) slot).isWithSize()) {
                if (slot.getStack() != null) {
                    if (GuiScreen.isShiftKeyDown()) {
                        slot.putStack(null);
                    } else {
                        int amount = slot.getStack().stackSize;

                        if (clickedButton == 0) {
                            amount--;

                            if (amount < 1) {
                                amount = 1;
                            }
                        } else if (clickedButton == 1) {
                            amount++;

                            if (amount > 64) {
                                amount = 64;
                            }
                        }

                        slot.getStack().stackSize = amount;
                    }
                } else if (player.inventory.getItemStack() != null) {
                    int amount = player.inventory.getItemStack().stackSize;

                    if (clickedButton == 1) {
                        amount = 1;
                    }

                    ItemStack toPut = player.inventory.getItemStack().copy();
                    toPut.stackSize = amount;

                    slot.putStack(toPut);
                }
            } else if (player.inventory.getItemStack() == null) {
                slot.putStack(null);
            } else if (slot.isItemValid(player.inventory.getItemStack())) {
                slot.putStack(player.inventory.getItemStack().copy());
            }

            return player.inventory.getItemStack();
        } else if (slot instanceof SlotSpecimenLegacy) {
            if (player.inventory.getItemStack() == null) {
                slot.putStack(null);
            } else if (slot.isItemValid(player.inventory.getItemStack())) {
                slot.putStack(player.inventory.getItemStack().copy());
            }

            return player.inventory.getItemStack();
        } else if (slot instanceof SlotDisabled) {
            return null;
        }

        return super.slotClick(id, clickedButton, clickType, player);
    }
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	@Override
    public boolean canMergeSlot(ItemStack stack, Slot slot) {
        if (slot == patternResultSlot) {
            return false;
        }

        return super.canMergeSlot(stack, slot);
    }
	
	public ItemStack transferStackInSlot(EntityPlayer player, int slot){
		return null;
	}
}
