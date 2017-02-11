package alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd;

import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.estorage.INetworkInventory.ExtractFilter;
import alec_wam.CrystalMod.api.estorage.storage.IItemProvider;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

public class ItemHDD extends Item implements ICustomModel, IItemProvider {

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
	
	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> list){
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
		/*if(!GuiScreen.isShiftKeyDown()){
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
		}*/
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
    	return (double)(getItemLimit(stack)-getItemCount(stack)) / (double)getItemLimit(stack);
    }
	
	public static int getItemLimit(ItemStack hddStack){
		if(ItemStackTools.isValid(hddStack)){
			return ItemNBTHelper.getInteger(hddStack, NBT_ITEM_LIMIT, 0);
		}
		return 0;
	}
	
	public static int getItemCount(ItemStack hddStack){
		if(ItemStackTools.isValid(hddStack)){
			if(ItemNBTHelper.verifyExistance(hddStack, NBT_ITEM_LIST)){
				NBTTagList itemList = ItemNBTHelper.getCompound(hddStack).getTagList(NBT_ITEM_LIST, 10);
				if(itemList !=null){
					int count = 0;
					for(int t = 0; t < itemList.tagCount(); t++){
						if(ItemStackTools.isValid(getItem(hddStack, t))){
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
		if(ItemStackTools.isValid(hddStack)){
			int max = getItemLimit(hddStack);
			for(int i = 0; i < max; i++){
				if(ItemStackTools.isEmpty(getItem(hddStack, i))){
					return i;
				}
			}
		}
		return -1;
	}
	
	public static boolean hasItem(ItemStack hddStack, ItemStack filter){
		if(ItemStackTools.isValid(hddStack)){
			int itemCount = getItemLimit(hddStack);
			for(int i = 0; i < itemCount; i++){
				ItemStack foundStack = getItem(hddStack, i);
				if(ItemStackTools.isValid(foundStack) && ItemUtil.canCombine(foundStack, filter)){
					return true;
				}
			}
		}
		return false;
	}
	
	public static void setItem(ItemStack hddStack, int index, ItemStack stack){
		if(ItemStackTools.isValid(hddStack)){
			if(ItemNBTHelper.verifyExistance(hddStack, NBT_ITEM_LIST)){
				NBTTagList itemList = ItemNBTHelper.getCompound(hddStack).getTagList(NBT_ITEM_LIST, 10);
				int max = getItemLimit(hddStack);
				if(hasItem(hddStack, stack)){
					for(int t = 0; t < itemList.tagCount(); t++){
						NBTTagCompound nbt = itemList.getCompoundTagAt(t);
						int indexNBT = nbt.getInteger("Index");
						if(indexNBT == index){
							if(ItemStackTools.isEmpty(stack)){
								itemList.removeTag(t);
							}else{
								NBTTagCompound newNBT = new NBTTagCompound();
								newNBT.setInteger("Index", index);
								newNBT.setInteger("StackSize", ItemStackTools.getStackSize(stack));
								itemList.set(t, stack.writeToNBT(newNBT));
							}
						}
					}
				}else{
					if(index >= 0 && index < max){
						if(ItemStackTools.isValid(stack)){
							NBTTagCompound nbt = new NBTTagCompound();
							nbt.setInteger("Index", index);
							nbt.setInteger("StackSize", ItemStackTools.getStackSize(stack));
							itemList.appendTag(stack.writeToNBT(nbt));
						}
					}
				}
				ItemNBTHelper.getCompound(hddStack).setTag(NBT_ITEM_LIST, itemList);
			}else{
				
				int max = getItemLimit(hddStack);
				if(index >= 0 && index < max){
					if(ItemStackTools.isValid(stack)){
						NBTTagList itemList = new NBTTagList();
						NBTTagCompound nbt = new NBTTagCompound();
						nbt.setInteger("Index", index);
						nbt.setInteger("StackSize", ItemStackTools.getStackSize(stack));
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
			if(ItemStackTools.isValid(stored)){
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
			if(ItemStackTools.isValid(stored)){
				if(ItemUtil.canCombine(stored, filter)){
					return stored;
				}
			}
		}
		return ItemStackTools.getEmptyStack();
	}
	
	public static ItemStack getItem(ItemStack hddStack, int index){
		if(ItemStackTools.isValid(hddStack)){
			if(ItemNBTHelper.verifyExistance(hddStack, NBT_ITEM_LIST)){
				NBTTagList itemList = ItemNBTHelper.getCompound(hddStack).getTagList(NBT_ITEM_LIST, 10);
				for(int t = 0; t < itemList.tagCount(); t++){
					NBTTagCompound stackNBT = itemList.getCompoundTagAt(t);
					if(stackNBT !=null){
						int nbtIndex = stackNBT.getInteger("Index");
						if(index == nbtIndex){
							ItemStack stack = ItemStackTools.loadFromNBT(stackNBT);
							if(stackNBT.hasKey("StackSize")){
								ItemStackTools.setStackSize(stack, stackNBT.getInteger("StackSize"));
							}
							return stack;
						}
					}
				}
			}
		}
		return ItemStackTools.getEmptyStack();
	}

	public int getIndex(ItemStack hdd, ItemStack stack, ExtractFilter filter){
		if(!ItemStackTools.isNullStack(hdd)){
			int itemCount = ItemHDD.getItemLimit(hdd);
			for(int i = 0; i < itemCount; i++){
				ItemStack foundStack = ItemHDD.getItem(hdd, i);
				if(ItemStackTools.isValid(foundStack) && filter.canExtract(stack, foundStack)){
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	public ItemStack insert(ItemStack container, ItemStack insert, int amount, boolean sim) {
		for(int i = 0; i < getItemLimit(container); i++){
			ItemStack stored = getItem(container, i);
			if(ItemStackTools.isValid(stored)){
				if(ItemUtil.canCombine(insert, stored)){
					if(!sim){
						ItemStackTools.incStackSize(stored, amount);
						ItemHDD.setItem(container, i, stored);
					}
					return ItemStackTools.getEmptyStack();
				}
			}
		}
		
		int i = getEmptyIndex(container);
		if(i > -1){
			if(!sim){
				ItemHDD.setItem(container, i, ItemHandlerHelper.copyStackWithSize(insert, amount));
			}
			return ItemStackTools.getEmptyStack();
		}
		
		return ItemHandlerHelper.copyStackWithSize(insert, amount);
	}
	
	@Override
	public ItemStack extract(ItemStack container, ItemStack remove, int amount, ExtractFilter filter, boolean sim) {
		ItemStack received = ItemStackTools.getEmptyStack();
		if (ItemStackTools.isValid(container)) {
			int index = getIndex(container, remove, filter);
			if (index > -1) {
				ItemStack stored = getItem(container, index);
				if(ItemStackTools.isValid(stored)){
					int realCount = Math.min(amount, ItemStackTools.getStackSize(stored));
					if(ItemStackTools.isNullStack(received)){
						received = ItemHandlerHelper.copyStackWithSize(stored, realCount);
					}
					if(!sim){
						ItemStackTools.incStackSize(stored, -realCount);
						if (ItemStackTools.isEmpty(stored)) {
							stored = ItemStackTools.getEmptyStack();
						}
						setItem(container, index,	stored);
					}
				}
			}
		}
		return received;
	}
}
