package alec_wam.CrystalMod.client.container;

import java.util.List;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketGuiMessage;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class ContainerMessageBase extends Container implements IMessageHandler {

	public EntityPlayer player;
	IContainerGuiCallback gui;

	public ContainerMessageBase(EntityPlayer player) {
		this.player = player;
	}

	/**
	 * set the gui for this container for callback (refreshGui) purposes
	 * 
	 * @param gui
	 */
	public final void setGui(IContainerGuiCallback gui) {
		this.gui = gui;
	}

	/**
	 * @param player
	 *            the player to add hotbar from
	 * @param tx
	 *            the upper-left X coordinate of the 9x3 inventory block
	 * @param ty
	 *            the upper-left Y coordinate of the 9x3 inventory block
	 * @param gap
	 *            the gap size between upper (9x3) and lower(9x1) inventory
	 *            blocks, in pixels
	 */
	protected int addPlayerSlots(EntityPlayer player, int tx, int ty, int gap) {
		int y;
		int x;
		int slotNum;
		int xPos;
		int yPos;
		for (x = 0; x < 9; ++x)// add player hotbar slots
		{
			slotNum = x;
			xPos = tx + x * 18;
			yPos = ty + gap + 3 * 18;
			this.addSlotToContainer(new Slot(player.inventory, x, xPos, yPos));
		}
		for (y = 0; y < 3; ++y) {
			for (x = 0; x < 9; ++x) {
				slotNum = y * 9 + x + 9;// +9 is to increment past hotbar slots
				xPos = tx + x * 18;
				yPos = ty + y * 18;
				this.addSlotToContainer(new Slot(player.inventory, slotNum,
						xPos, yPos));
			}
		}
		return ty + (4 * 18) + gap;
	}

	/**
	 * server side method to send a data-packet to the client-side GUI attached
	 * to the client-side verison of this container
	 * 
	 * @param data
	 */
	protected final void sendDataToGui(NBTTagCompound data) {
		if (!player.worldObj.isRemote) {
			PacketGuiMessage pkt = new PacketGuiMessage("Data");
			pkt.data.setTag("gui", data);
			CrystalModNetwork.sendTo(pkt, (EntityPlayerMP) player);
		}
	}

	/**
	 * send data from client-container to server container
	 * 
	 * @param data
	 */
	protected void sendDataToServer(NBTTagCompound data) {
		if (player.worldObj.isRemote) {
			PacketGuiMessage pkt = new PacketGuiMessage("Data", data);
			CrystalModNetwork.sendToServer(pkt);
		}
	}

	/**
	 * send data from server container to client container
	 * 
	 * @param data
	 */
	protected void sendDataToClient(NBTTagCompound data) {
		if (!player.worldObj.isRemote) {
			PacketGuiMessage pkt = new PacketGuiMessage("Data", data);
			CrystalModNetwork.sendTo(pkt, (EntityPlayerMP) player);
		}
	}

	/**
	 * client/server side method to receive packet data from PacketGui
	 * 
	 * @param data
	 */
	public final void onPacketData(NBTTagCompound data) {
		if (data.hasKey("gui")) {
			if (this.gui != null) {
				this.gui.handlePacketData(data.getCompoundTag("gui"));
			}
		} else {
			handlePacketData(data);
		}
	}

	/**
	 * subclasses should override this method to send any data from server to
	 * the client-side container. This method is called immediately after the
	 * container has been constructed and set as the active container. The data
	 * is received client-side immediately after the GUI has been constructed,
	 * initialized, and opened.
	 */
	public void sendInitData() {

	}

	/**
	 * sub-classes should override this method to handle any packet data they
	 * are expecting to receive. packets destined to the GUI or for slot-click
	 * have already been filtered out
	 * 
	 * @param tag
	 */
	public void handlePacketData(NBTTagCompound tag) {

	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return true;
	}

	/**
	 * Causes the GUI to be re-setup on its next update tick
	 */
	public void refreshGui() {
		if (this.gui != null) {
			this.gui.refreshGui();
		}
	}

	/**
	 * remove the inventory slots from view on the screen, effectively disabling
	 * them
	 */
	public void removeSlots() {
		for (Slot s : ((List<Slot>) this.inventorySlots)) {
			if (s.yDisplayPosition >= 0) {
				s.yDisplayPosition -= 10000;
			}
		}
	}

	/**
	 * add any removed from screen slots back into view
	 */
	public void addSlots() {
		for (Slot s : ((List<Slot>) this.inventorySlots)) {
			if (s.yDisplayPosition < 0) {
				s.yDisplayPosition += 10000;
			}
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotClickedIndex) {
		return ItemStackTools.getEmptyStack();
	}

	/**
	 * merges provided ItemStack with the first avaliable one in the
	 * container/player inventory<br>
	 * overriden to clean up the mess of the code that was the vanilla code.
	 * 
	 * @return true if item-stack was fully-consumed/merged
	 */
	@Override
	protected boolean mergeItemStack(ItemStack incomingStack, int startIndex,
			int endBeforeIndex, boolean iterateBackwards) {
		Slot slotFromContainer;
		ItemStack stackFromSlot;
		int currentIndex, start, stop, transferAmount;
		int iterator = iterateBackwards ? -1 : 1;
		start = iterateBackwards ? endBeforeIndex : startIndex;
		stop = iterateBackwards ? startIndex : endBeforeIndex;
		if (incomingStack.isStackable()) {
			for (currentIndex = start; !ItemStackTools.isEmpty(incomingStack)
					&& currentIndex != stop; currentIndex += iterator) {
				slotFromContainer = (Slot) this.inventorySlots
						.get(currentIndex);
				if (!slotFromContainer.isItemValid(incomingStack)) {
					continue;
				}
				stackFromSlot = slotFromContainer.getStack();
				if (ItemStackTools.isNullStack(stackFromSlot)
						|| !ItemUtil.canCombine(incomingStack, stackFromSlot)) {
					continue;
				}
				transferAmount = stackFromSlot.getMaxStackSize()
						- ItemStackTools.getStackSize(stackFromSlot);
				if (transferAmount > ItemStackTools.getStackSize(incomingStack)) {
					transferAmount = ItemStackTools.getStackSize(incomingStack);
				}
				if (transferAmount > 0) {
					ItemStackTools.incStackSize(incomingStack, -transferAmount);
					ItemStackTools.incStackSize(stackFromSlot, transferAmount);
					slotFromContainer.onSlotChanged();
				}
			}
		}
		if (!ItemStackTools.isEmpty(incomingStack)) {
			for (currentIndex = start; !ItemStackTools.isEmpty(incomingStack)
					&& currentIndex != stop; currentIndex += iterator) {
				slotFromContainer = (Slot) this.inventorySlots
						.get(currentIndex);
				if (!slotFromContainer.isItemValid(incomingStack)) {
					continue;
				}
				stackFromSlot = slotFromContainer.getStack();
				if (stackFromSlot == null) {
					slotFromContainer.putStack(incomingStack.copy());
					slotFromContainer.onSlotChanged();
					ItemStackTools.makeEmpty(incomingStack);
					break;
				}
			}
		}
		return ItemStackTools.isEmpty(incomingStack);
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		onPacketData(messageData);
	}

}
