package com.alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd;

import java.util.List;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.blocks.ICustomModel;
import com.alec_wam.CrystalMod.items.ModItems;
import com.alec_wam.CrystalMod.util.ItemNBTHelper;
import com.alec_wam.CrystalMod.util.ItemUtil;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemHDD extends Item implements ICustomModel {

	public static final String NBT_ITEM_LIMIT = "ItemLimit";
	public static final String NBT_ITEM_LIST = "ItemList";
	
	public ItemHDD(){
		super();
		setMaxStackSize(1);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(CrystalMod.tabItems);
		ModItems.registerItem(this, "harddrive");
	}
	
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list){
		for(int m = 0; m < getSizes().length; m++){
			list.add(getFromMeta(m));
		}
	}
	
	public static int[] getSizes(){
		return new int[]{8, 16, 32, 64, 128};
	}
	
	public static ItemStack getFromMeta(int meta){
		ItemStack stack = new ItemStack(ModItems.harddrive, 1, meta);
		ItemNBTHelper.setInteger(stack, NBT_ITEM_LIMIT, getSizes()[meta]);
		return stack;
	}
	
	public String getUnlocalizedName(ItemStack stack)
    {
		String[] colors = new String[]{"blue", "red", "green", "dark", "pure"};
        return super.getUnlocalizedName(stack) + (colors[stack.getMetadata()]);
    }
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
		String[] colors = new String[]{"blue", "red", "green", "dark", "pure"};
		for(int c = 0; c < colors.length; c++){
			ModelResourceLocation loc = new ModelResourceLocation("crystalmod:harddrive", "color="+colors[c]);
			ModelLoader.setCustomModelResourceLocation(this, c, loc);
		}
    }
	
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean adv){
		list.add("Items Stored: "+ItemHDD.getItemCount(stack)+"/"+getItemLimit(stack));
		if(!GuiScreen.isShiftKeyDown()){
			list.add("Hold <Shift> for item list");
		}else{
			list.add("Items: ");
			for(int i = 0; i < ItemHDD.getItemCount(stack); i++){
				ItemStack stored = ItemHDD.getItem(stack, i);
				if(stored !=null){
					String stackSize;
		    		if (stored.stackSize == 1) {
		    			stackSize = "1";
		    		} else if (stored.stackSize < 1000) {
		    			stackSize = stored.stackSize + "";
		    		} else if (stored.stackSize < 100000) {
		    			stackSize = stored.stackSize / 1000 + "K";
		    		} else if (stored.stackSize < 1000000) {
		    			stackSize = "0." + stored.stackSize / 100000+"M";
		    		} else {
		    			stackSize = stored.stackSize / 1000000 + "M";
		    		}
					list.add(stored.getDisplayName()+" ("+stackSize+")");
				}
			}
		}
	}
	
	public boolean showDurabilityBar(ItemStack stack)
    {
        return getItemCount(stack) > 0;
    }

    /**
     * Queries the percentage of the 'Durability' bar that should be drawn.
     *
     * @param stack The current ItemStack
     * @return 1.0 for 100% 0 for 0%
     */
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return ((1.0D/getItemLimit(stack)))*getItemCount(stack);
    }
	
	public static int getItemLimit(ItemStack hddStack){
		if(hddStack !=null){
			return ItemNBTHelper.getInteger(hddStack, NBT_ITEM_LIMIT, 0);
		}
		return 0;
	}
	
	public static int getItemCount(ItemStack hddStack){
		if(hddStack !=null){
			if(ItemNBTHelper.verifyExistance(hddStack, NBT_ITEM_LIST)){
				NBTTagList itemList = ItemNBTHelper.getCompound(hddStack).getTagList(NBT_ITEM_LIST, 10);
				if(itemList !=null){
					int count = 0;
					for(int t = 0; t < itemList.tagCount(); t++){
						if(getItem(hddStack, t) !=null){
							count++;
						}
					}
					return count;
				}
			}
		}
		return 0;
	}
	
	public static int getEmptyIndex(ItemStack hddStack){
		if(hddStack !=null){
			int max = getItemLimit(hddStack);
			for(int i = 0; i < max; i++){
				if(getItem(hddStack, i) == null){
					return i;
				}
			}
		}
		return -1;
	}
	
	public static boolean hasItem(ItemStack hddStack, ItemStack filter){
		if(hddStack !=null){
			int itemCount = getItemLimit(hddStack);
			for(int i = 0; i < itemCount; i++){
				ItemStack foundStack = getItem(hddStack, i);
				if(foundStack !=null && ItemUtil.canCombine(foundStack, filter)){
					return true;
				}
			}
		}
		return false;
	}
	
	public static void setItem(ItemStack hddStack, int index, ItemStack stack){
		if(hddStack !=null){
			if(ItemNBTHelper.verifyExistance(hddStack, NBT_ITEM_LIST)){
				NBTTagList itemList = ItemNBTHelper.getCompound(hddStack).getTagList(NBT_ITEM_LIST, 10);
				int max = getItemLimit(hddStack);
				if(hasItem(hddStack, stack)){
					for(int t = 0; t < itemList.tagCount(); t++){
						NBTTagCompound nbt = itemList.getCompoundTagAt(t);
						int indexNBT = nbt.getInteger("Index");
						if(indexNBT == index){
							if(stack == null){
								itemList.removeTag(t);
							}else{
								NBTTagCompound newNBT = new NBTTagCompound();
								newNBT.setInteger("Index", index);
								newNBT.setInteger("StackSize", stack.stackSize);
								itemList.set(t, stack.writeToNBT(newNBT));
							}
						}
					}
				}else{
					if(index >= 0 && index < max){
						if(stack !=null){
							NBTTagCompound nbt = new NBTTagCompound();
							nbt.setInteger("Index", index);
							nbt.setInteger("StackSize", stack.stackSize);
							itemList.appendTag(stack.writeToNBT(nbt));
						}
					}
				}
				ItemNBTHelper.getCompound(hddStack).setTag(NBT_ITEM_LIST, itemList);
			}else{
				
				int max = getItemLimit(hddStack);
				if(index >= 0 && index < max){
					if(stack !=null){
						NBTTagList itemList = new NBTTagList();
						NBTTagCompound nbt = new NBTTagCompound();
						nbt.setInteger("Index", index);
						nbt.setInteger("StackSize", stack.stackSize);
						itemList.appendTag(stack.writeToNBT(nbt));
						ItemNBTHelper.getCompound(hddStack).setTag(NBT_ITEM_LIST, itemList);
					}
				}
			}
		}
	}
	
	public static int getItemIndex(ItemStack hddStack, ItemStack filter){
		for(int i = 0; i < getItemLimit(hddStack); i++){
			ItemStack stored = getItem(hddStack, i);
			if(stored !=null){
				if(ItemUtil.canCombine(stored, filter)){
					return i;
				}
			}
		}
		return -1;
	}
	
	public static ItemStack getItem(ItemStack hddStack, ItemStack filter){
		for(int i = 0; i < getItemLimit(hddStack); i++){
			ItemStack stored = getItem(hddStack, i);
			if(stored !=null){
				if(ItemUtil.canCombine(stored, filter)){
					return stored;
				}
			}
		}
		return null;
	}
	
	public static ItemStack getItem(ItemStack hddStack, int index){
		if(hddStack !=null){
			if(ItemNBTHelper.verifyExistance(hddStack, NBT_ITEM_LIST)){
				NBTTagList itemList = ItemNBTHelper.getCompound(hddStack).getTagList(NBT_ITEM_LIST, 10);
				for(int t = 0; t < itemList.tagCount(); t++){
					NBTTagCompound stackNBT = itemList.getCompoundTagAt(t);
					if(stackNBT !=null){
						int nbtIndex = stackNBT.getInteger("Index");
						if(index == nbtIndex){
							ItemStack stack = ItemStack.loadItemStackFromNBT(stackNBT);
							if(stackNBT.hasKey("StackSize")){
								stack.stackSize = stackNBT.getInteger("StackSize");
							}
							return stack;
						}
					}
				}
			}
		}
		return null;
	}
}
