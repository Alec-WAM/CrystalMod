package alec_wam.CrystalMod.tiles.pipes.estorage.panel.display;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import alec_wam.CrystalMod.api.estorage.IInsertListener;
import alec_wam.CrystalMod.tiles.pipes.estorage.FluidStorage.FluidStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.ItemStorage.ItemStackData;
import alec_wam.CrystalMod.tiles.pipes.estorage.autocrafting.CraftingPattern;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.GuiPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.TileEntityPanel;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.FluidUtil;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.tool.ToolUtil;

import com.enderio.core.common.util.ChatUtil;
import com.google.common.base.Strings;

public class TileEntityPanelItem extends TileEntityPanel implements IInsertListener {

	public ItemStack displayItem;
	public FluidStack displayFluid;
	public String displayText = "";
	public boolean isLocked = false;
	public boolean update = false;
	
	public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack held, EnumFacing side){
		if(network == null || !connected) return false;
		if(worldObj.isRemote) return true;
		ItemStack stack = held;
		if(stack !=null){
			
			if(isLocked){
				if(displayItem != null && ItemUtil.canCombine(stack, displayItem)){
					if(network !=null){
						ItemStack insert = network.getItemStorage().addItem(stack, false);
						stack = insert;
						if(stack !=null && stack.stackSize <=0){
							stack = null;
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
					displayItem = null;
					displayFluid = null;
					update = true;
					return true;
				}
				
				FluidStack itemFluid = FluidUtil.getFluidTypeFromItem(stack);
				if(itemFluid !=null && displayItem == null){
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
					if(displayItem == null || displayItem != null && !ItemUtil.canCombine(stack, displayItem)){
						ItemStack copy = stack.copy();
						copy.stackSize = 1;
						displayItem = copy;
						update = true;
						return true;
					}
					
					if(network !=null){
						ItemStack insert = network.getItemStorage().addItem(stack, false);
						stack = insert;
						if(stack !=null && stack.stackSize <=0){
							stack = null;
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
				BlockUtil.markBlockForUpdate(worldObj, pos);
				return true;
			}
			
			if(displayFluid !=null){
				ChatUtil.sendNoSpam(player, displayFluid.getLocalizedName());
				return true;
			}
			
			if(displayItem !=null && network !=null){
				boolean changed = false;
				for(int s = 0; s < player.inventory.mainInventory.length; s++){
					ItemStack invStack = player.inventory.mainInventory[s];
					if(invStack !=null && ItemUtil.canCombine(invStack, displayItem)){
						ItemStack insert = network.getItemStorage().addItem(stack, false);
						stack = insert;
						if(stack !=null && stack.stackSize <=0){
							stack = null;
						}
						player.inventory.mainInventory[s]=stack;
						changed = true;
					}
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
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		if(displayItem !=null)nbt.setTag("DisplayStack", displayItem.writeToNBT(new NBTTagCompound()));
		if(displayFluid !=null)nbt.setTag("DisplayFluid", displayFluid.writeToNBT(new NBTTagCompound()));
		if(!Strings.isNullOrEmpty(displayText))nbt.setString("DisplayString", displayText);
		nbt.setBoolean("isLocked", isLocked);
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		if(nbt.hasKey("DisplayStack")){
			this.displayItem = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("DisplayStack"));
		}else{
			this.displayItem = null;
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
		if(worldObj !=null && !this.worldObj.isRemote){
			update = true;
		}
	}
	
	public void update(){
		super.update();
		
		if(update){
			if(this.displayItem !=null){
				if(this.network !=null){
					ItemStackData data = network.getItemStorage().getItemData(displayItem);
					if(data !=null){
						if(data.isCrafting){
							displayText = "0";
						} else if(data.stack !=null) {
							if(data.stack.stackSize == 1){
								displayText = "1";
							}
							else displayText = GuiPanel.getStackSize(data.stack);
						}
					}else{
						boolean found = false;
						search : for(CraftingPattern data2 : network.getPatterns()){
							if(data2 !=null){
								for(ItemStack out : data2.getOutputs()){
									if(out !=null && ItemUtil.canCombine(out, displayItem)){
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
		if(stack !=null && this.displayItem !=null){
			if(ItemUtil.canCombine(stack, displayItem)){
				if(worldObj !=null && !worldObj.isRemote)
				update = true;
			}
		}
	}

	@Override
	public void onItemExtracted(ItemStack stack, int amount) {
		if(stack !=null && this.displayItem !=null && amount > 0){
			if(ItemUtil.canCombine(stack, displayItem)){
				if(worldObj !=null && !worldObj.isRemote)
				update = true;
			}
		}
	}
	
	@Override
	public void onFluidInserted(FluidStack stack) {
		if(stack !=null && this.displayFluid !=null){
			if(FluidUtil.canCombine(stack, displayFluid)){
				if(worldObj !=null && !worldObj.isRemote)
				update = true;
			}
		}
	}

	@Override
	public void onFluidExtracted(FluidStack stack, int amount) {
		if(stack !=null && this.displayFluid !=null && amount > 0){
			if(FluidUtil.canCombine(stack, displayFluid)){
				if(worldObj !=null && !worldObj.isRemote)
				update = true;
			}
		}
	}
	
}
