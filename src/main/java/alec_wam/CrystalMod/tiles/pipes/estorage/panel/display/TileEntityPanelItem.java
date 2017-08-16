package alec_wam.CrystalMod.tiles.pipes.estorage.panel.display;

import com.google.common.base.Strings;

import alec_wam.CrystalMod.api.estorage.IInsertListener;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage.FluidStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.GuiPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.TileEntityPanel;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.FluidUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TileEntityPanelItem extends TileEntityPanel implements IInsertListener {

	public ItemStack displayItem;
	public FluidStack displayFluid;
	public String displayText = "";
	public boolean isLocked = false;
	public boolean update = false;
	
	@Override
	public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack held, EnumFacing side){
		if(network == null || !connected) return false;
		if(getWorld().isRemote) return true;
		ItemStack stack = held;
		if(!ItemStackTools.isNullStack(stack)){
			
			if(isLocked){
				if(!ItemStackTools.isNullStack(displayItem) && ItemUtil.canCombine(stack, displayItem)){
					if(network !=null){
						ItemStack insert = network.getItemStorage().addItem(stack, false);
						stack = insert;
						if(ItemStackTools.isEmpty(stack)){
							stack = ItemStackTools.getEmptyStack();
						}
						player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);
						return true;
					}
				}
				
				FluidStack itemFluid = FluidUtil.getFluidTypeFromItem(stack);
				if(itemFluid !=null && displayFluid != null && FluidUtil.canCombine(itemFluid, displayFluid)){
					if(network !=null){
						IFluidHandler fHandler = FluidUtil.getFluidHandlerCapability(stack);
						if(fHandler !=null){
							network.getFluidStorage().addFluid(fHandler.drain(itemFluid, true), false);
						}
						return true;
					}
				}
				
			}else{
				if(ToolUtil.isToolEquipped(player, hand)){
					displayItem = ItemStackTools.getEmptyStack();
					displayFluid = null;
					update = true;
					return true;
				}
				
				FluidStack itemFluid = FluidUtil.getFluidTypeFromItem(stack);
				if(itemFluid !=null && ItemStackTools.isNullStack(displayItem)){
					if(displayFluid == null || displayFluid != null && !FluidUtil.canCombine(itemFluid, displayFluid)){
						FluidStack copy = itemFluid.copy();
						itemFluid.amount = 1;
						displayFluid = copy;
						update = true;
						return true;
					}
					
					if(network !=null){
						IFluidHandler fHandler = FluidUtil.getFluidHandlerCapability(stack);
						if(fHandler !=null){
							if(FluidUtil.canCombine(itemFluid, displayFluid)){
								return network.getFluidStorage().addFluid(fHandler.drain(itemFluid, !player.capabilities.isCreativeMode), false) > 0;
							}
						}
						return false;
					}
				} else {
					if(ItemStackTools.isNullStack(displayItem) || !ItemStackTools.isNullStack(displayItem) && !ItemUtil.canCombine(stack, displayItem)){
						ItemStack copy = stack.copy();
						ItemStackTools.setStackSize(copy, 1);
						displayItem = copy;
						update = true;
						return true;
					}
					
					if(network !=null){
						ItemStack insert = network.getItemStorage().addItem(stack, false);
						stack = insert;
						if(ItemStackTools.isEmpty(stack)){
							stack = ItemStackTools.getEmptyStack();
						}
						player.inventory.setInventorySlotContents(player.inventory.currentItem, stack);
						return true;
					}
				}
			}
		}else{
			if(player.isSneaking()){
				boolean old = isLocked;
				isLocked = !old;
				BlockUtil.markBlockForUpdate(getWorld(), pos);
				return true;
			}
			
			if(displayFluid !=null){
				ChatUtil.sendNoSpam(player, displayFluid.getLocalizedName());
				return true;
			}
			
			if(!ItemStackTools.isNullStack(displayItem) && network !=null){
				boolean changed = false;
				int s = 0;
				for(ItemStack invStack : player.inventory.mainInventory){
					if(ItemStackTools.isValid(invStack) && ItemUtil.canCombine(invStack, displayItem)){
						ItemStack insert = network.getItemStorage().addItem(stack, false);
						stack = insert;
						if(ItemStackTools.isEmpty(stack)){
							stack = ItemStackTools.getEmptyStack();
						}
						player.inventory.mainInventory.set(s, stack);
						changed = true;
					}
					s++;
				}
				if (!player.isHandActive() && changed)
                {
                    ((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
                }
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		if(displayItem !=null)nbt.setTag("DisplayStack", displayItem.writeToNBT(new NBTTagCompound()));
		if(displayFluid !=null)nbt.setTag("DisplayFluid", displayFluid.writeToNBT(new NBTTagCompound()));
		if(!Strings.isNullOrEmpty(displayText))nbt.setString("DisplayString", displayText);
		nbt.setBoolean("isLocked", isLocked);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		if(nbt.hasKey("DisplayStack")){
			this.displayItem = ItemStackTools.loadFromNBT(nbt.getCompoundTag("DisplayStack"));
		}else{
			this.displayItem = ItemStackTools.getEmptyStack();
		}
		if(nbt.hasKey("DisplayFluid")){
			this.displayFluid = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("DisplayFluid"));
		}else{
			this.displayFluid = null;
		}
		if(nbt.hasKey("DisplayString")){
			this.displayText = nbt.getString("DisplayString");
		}else{
			displayText = "";
		}
		isLocked = nbt.getBoolean("isLocked");
		if(getWorld() !=null && !this.getWorld().isRemote){
			update = true;
		}
	}
	
	@Override
	public void update(){
		super.update();
		
		if(update){
			if(!ItemStackTools.isNullStack(displayItem)){
				if(this.network !=null){
					ItemStackData data = network.getItemStorage().getItemData(displayItem);
					if(data !=null){
						if(data.isCrafting){
							displayText = "0";
						} else if(!ItemStackTools.isNullStack(displayItem)) {
							if(ItemStackTools.getStackSize(displayItem) == 1){
								displayText = "1";
							}
							else displayText = GuiPanel.getStackSize(data.stack);
						}
					}else{
						boolean found = false;
						search : for(CraftingPattern data2 : network.getPatterns()){
							if(data2 !=null){
								for(ItemStack out : data2.getOutputs()){
									if(!ItemStackTools.isNullStack(out) && ItemUtil.canCombine(out, displayItem)){
										found = true;
										break search;
									}
								}
							}
						}
						if(found){
							displayText = "Craft";
						}else displayText = "?";
					}
					BlockUtil.markBlockForUpdate(getWorld(), getPos());
					this.update = false;
				}
			} else if(this.displayFluid !=null){
				if(this.network !=null){
					FluidStackData data = network.getFluidStorage().getFluidData(displayFluid);
					if(data !=null){
						displayText = data.getAmount() + " mB";
					}else{
						displayText = "?";
					}
					BlockUtil.markBlockForUpdate(getWorld(), getPos());
					this.update = false;
				}
			} else{
				if(displayText !=""){
					displayText = "";
					BlockUtil.markBlockForUpdate(getWorld(), getPos());
					this.update = false;
				}
			}
		}
	}

	@Override
	public void onItemInserted(ItemStack stack) {
		if(!ItemStackTools.isNullStack(stack) && !ItemStackTools.isNullStack(displayItem)){
			if(ItemUtil.canCombine(stack, displayItem)){
				if(getWorld() !=null && !getWorld().isRemote)
				update = true;
			}
		}
	}

	@Override
	public void onItemExtracted(ItemStack stack, int amount) {
		if(!ItemStackTools.isNullStack(stack) && !ItemStackTools.isNullStack(displayItem) && amount > 0){
			if(ItemUtil.canCombine(stack, displayItem)){
				if(getWorld() !=null && !getWorld().isRemote)
				update = true;
			}
		}
	}
	
	@Override
	public void onFluidInserted(FluidStack stack) {
		if(stack !=null && this.displayFluid !=null){
			if(FluidUtil.canCombine(stack, displayFluid)){
				if(getWorld() !=null && !getWorld().isRemote)
				update = true;
			}
		}
	}

	@Override
	public void onFluidExtracted(FluidStack stack, int amount) {
		if(stack !=null && this.displayFluid !=null && amount > 0){
			if(FluidUtil.canCombine(stack, displayFluid)){
				if(getWorld() !=null && !getWorld().isRemote)
				update = true;
			}
		}
	}
	
}
