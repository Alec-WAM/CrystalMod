package alec_wam.CrystalMod.items.tools.blockholder.advanced;

import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.blockholder.ItemBlockHolder;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAdvancedBlockHolder extends Item implements ICustomModel {
	public static final String NBT_BLOCK_LIST = "BlockList";
	public static final String NBT_SELECTION = "BlockSelection";
	public static final int MAX_BLOCKS = 5;
	
	public ItemAdvancedBlockHolder(){
		super();
		this.setCreativeTab(CrystalMod.tabTools);
		this.setMaxStackSize(1);
		ModItems.registerItem(this, "advblockholder");
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void initModel() {
		ModItems.initBasicModel(this);
		ClientProxy.registerItemRenderCustom(getRegistryName().toString(), ItemRenderAdvancedBlockHolder.INSTANCE);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        super.getSubItems(itemIn, tab, subItems);
        
        for(Block block : new Block[]{Blocks.STONE}){
	        ItemStack stoneStack = new ItemStack(this);
	        BlockStackData data = new BlockStackData(new ItemStack(block), 64);
	        setBlockData(stoneStack, 0, data);
	        subItems.add(stoneStack);
        }
        
        ItemStack stoneStack = new ItemStack(this);
        BlockStackData data = new BlockStackData(new ItemStack(Blocks.STONE), 64 * ItemBlockHolder.MAX_STACK_COUNT);
        setBlockData(stoneStack, 0, data);
        subItems.add(stoneStack);
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
		if(ItemStackTools.isValid(stack)){
			BlockStackData data = getSelectedData(stack);
			ItemStack blockStack = data.stack;
			if(ItemStackTools.isValid(blockStack)){
				tooltip.add(blockStack.getDisplayName() + " ("+data.count+")");
			} else {
				tooltip.add(Lang.localize("gui.empty"));
			}
			
			tooltip.add("Auto Pickup: "+ (ItemBlockHolder.isAutoPickupEnabled(stack) ? TextFormatting.GREEN + "Enabled" : TextFormatting.RED + "Disabled"));			
		}
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
		ItemStack held = player.getHeldItem(hand);
		if(hand == EnumHand.OFF_HAND)return new ActionResult<ItemStack>(EnumActionResult.PASS, held);
		if(ItemStackTools.isValid(held)){
			if(player.isSneaking()){
				player.openGui(CrystalMod.instance, GuiHandler.GUI_ID_ITEM, world, 0, 0, 0);
		        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, held);
			}else {
				int selection = getSelection(held);
				BlockStackData data = getBlockData(held, selection);
				ItemStack blockStack = data.stack;
				if(ItemStackTools.isValid(blockStack)){
					if(ItemBlockHolder.canBePlaced(blockStack)){
						int count = data.count;
						if(count > 0){
							ItemStack placeStack = ItemUtil.copy(blockStack, 1);
							player.inventory.mainInventory.set(player.inventory.currentItem, placeStack);
							ActionResult<ItemStack> result = blockStack.getItem().onItemRightClick(world, player, hand);
							placeStack = result.getResult();
							if(ItemStackTools.isEmpty(placeStack)){
								removeBlocks(held, selection, 1);
							}
							player.inventory.mainInventory.set(player.inventory.currentItem, held);
							return new ActionResult<ItemStack>(result.getType(), held);
						}
					}
				}
			}
		}
        return new ActionResult<ItemStack>(EnumActionResult.PASS, held);
    }
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		if(hand == EnumHand.OFF_HAND)return EnumActionResult.PASS;
		ItemStack held = player.getHeldItem(hand);
		if(ItemStackTools.isValid(held)){
			int selection = getSelection(held);
			BlockStackData data = getBlockData(held, selection);
			ItemStack blockStack = data.stack;
			if(ItemStackTools.isValid(blockStack)){
				if(ItemBlockHolder.canBePlaced(blockStack)){
					int count = data.count;
					if(count > 0){
						ItemStack placeStack = ItemUtil.copy(blockStack, 1);
						player.inventory.mainInventory.set(player.inventory.currentItem, placeStack);
						EnumActionResult response = blockStack.getItem().onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
						placeStack = player.getHeldItem(hand);
						if(ItemStackTools.isEmpty(placeStack) && !player.capabilities.isCreativeMode){
							removeBlocks(held, selection, 1);
						}
						player.inventory.mainInventory.set(player.inventory.currentItem, held);
						return response;
					}
				}
			}
		}
        return EnumActionResult.PASS;
    }
	
	public static void setSelection(ItemStack stack, int value){
		ItemNBTHelper.setInteger(stack, NBT_SELECTION, Math.min(value, MAX_BLOCKS));
	}
	
	public static int getSelection(ItemStack stack){
		return ItemNBTHelper.getInteger(stack, NBT_SELECTION, 0);
	}
	
	
	public static final BlockStackData EMPTY_DATA = new BlockStackData(ItemStackTools.getEmptyStack(), 0);
	public static class BlockStackData {
		public ItemStack stack;
		public int count;
		public BlockStackData(ItemStack stack, int count){
			this.stack = stack;
			this.count = count;
		}
		
		public static BlockStackData loadFromNBT(NBTTagCompound nbt){
			return new BlockStackData(ItemStackTools.loadFromNBT(nbt.getCompoundTag("Stack")), nbt.getInteger("Count"));
		}
		
		public NBTTagCompound saveToNBT(){
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setTag("Stack", stack.writeToNBT(new NBTTagCompound()));
			nbt.setInteger("Count", count);
			return nbt;
		}
	}
	
	public static void setBlockList(ItemStack stack, NonNullList<BlockStackData> blocks){
		if(ItemStackTools.isValid(stack)){
			NBTTagCompound nbt = ItemNBTHelper.getCompound(stack);
			NBTTagCompound listNBT = new NBTTagCompound();
			for(int i = 0; i < blocks.size(); i++){
				listNBT.setTag("#"+i, blocks.get(i).saveToNBT());
			}
			nbt.setTag(NBT_BLOCK_LIST, listNBT);
		}
	}
	
	public static NonNullList<BlockStackData> getBlockList(ItemStack stack){
		if(ItemStackTools.isValid(stack)){
			NBTTagCompound nbt = ItemNBTHelper.getCompound(stack);
			NonNullList<BlockStackData> dataList = NonNullList.withSize(MAX_BLOCKS, EMPTY_DATA);
			if(nbt.hasKey(NBT_BLOCK_LIST)){
				NBTTagCompound listNBT = nbt.getCompoundTag(NBT_BLOCK_LIST);
				for(int i = 0; i < MAX_BLOCKS; i++){
					BlockStackData data = BlockStackData.loadFromNBT(listNBT.getCompoundTag("#"+i));
					dataList.set(i, data);
				}
			}
			return dataList;
		}
		return null;
	}
	
	public static void setBlockData(ItemStack stack, int selection, BlockStackData data){
		NonNullList<BlockStackData> dataList = getBlockList(stack);
		dataList.set(selection, data);    
		setBlockList(stack, dataList);   
	}
	
	public static BlockStackData getBlockData(ItemStack stack, int selection){
		NonNullList<BlockStackData> dataList = getBlockList(stack);
		return dataList.get(selection);    
	}
	
	public static void setSelectedData(ItemStack stack, BlockStackData data){
		setBlockData(stack, getSelection(stack), data);
	}
	
	public static BlockStackData getSelectedData(ItemStack stack){
		return getBlockData(stack, getSelection(stack));
	}
	
	public static int addBlocks(ItemStack stack, int selection, int amt){
		NonNullList<BlockStackData> dataList = getBlockList(stack);
		BlockStackData data = dataList.get(selection);
		if(data == null)return 0;
		ItemStack blockStack = data.stack;
		int current = data.count;
		int filled = (blockStack.getMaxStackSize() * ItemBlockHolder.MAX_STACK_COUNT) - current;

        if (amt < filled)
        {
        	data.count +=amt;
            filled = amt;
        }
        else
        {
        	data.count = (blockStack.getMaxStackSize() * ItemBlockHolder.MAX_STACK_COUNT);
        }	
        dataList.set(selection, data);    
		setBlockList(stack, dataList);    
		return filled;
	}
	
	public static int removeBlocks(ItemStack stack, int selection, int amt){
		NonNullList<BlockStackData> dataList = getBlockList(stack);
		BlockStackData data = dataList.get(selection);
		if(data == null)return 0;
		int current = data.count;
		int removed = Math.min(current, amt);
		if(removed > 0){
			data.count-=removed;
			dataList.set(selection, data); 
			setBlockList(stack, dataList);
		}
		return removed;
	}
	
	public static int getValidBlockSize(ItemStack stack) {
		int count = 0;
		for(BlockStackData data : getBlockList(stack)){
			if(ItemStackTools.isValid(data.stack)){
				count++;
			}
		}
		return count;
	}
	
	public static NonNullList<BlockStackData> clearSlot(NonNullList<BlockStackData> orginal, int slot){
		NonNullList<BlockStackData> newList = NonNullList.withSize(MAX_BLOCKS, EMPTY_DATA);
		int index = 0;
		for(int i = 0; i < MAX_BLOCKS; i++){
			if(i != slot){
				newList.set(index, orginal.get(i));
				index++;
			}
		}
		return newList;
	}
}
