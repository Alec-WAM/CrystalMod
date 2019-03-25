package alec_wam.CrystalMod.items.tools.blockholder;

import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemDoor;
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

public class ItemBlockHolder extends Item implements ICustomModel {
	//TODO Possibly add void option (Use Backpack upgrades)
	public static final String NBT_BLOCK_STACK = "BlockStack";
	public static final String NBT_BLOCK_COUNT = "BlockCount";
	public static final String NBT_SETTING_PICKUP = "AutoPickup";
	public static final int MAX_STACK_COUNT = 64;
	
	public ItemBlockHolder(){
		super();
		this.setCreativeTab(CrystalMod.tabTools);
		this.setMaxStackSize(1);
		ModItems.registerItem(this, "blockholder");
	}
	
	
	@SideOnly(Side.CLIENT)
	@Override
	public void initModel() {
		ModItems.initBasicModel(this);
		ClientProxy.registerItemRenderCustom(getRegistryName().toString(), ItemRenderBlockHolder.INSTANCE);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        super.getSubItems(itemIn, tab, subItems);
        
        for(Block block : new Block[]{Blocks.STONE}){
	        ItemStack stoneStack = new ItemStack(this);
	        setBlockStack(stoneStack, new ItemStack(block));
	        setBlockCount(stoneStack, 64);
	        subItems.add(stoneStack);
        }
        
        ItemStack stoneStack = new ItemStack(this);
        setBlockStack(stoneStack, new ItemStack(Blocks.STONE));
        setBlockCount(stoneStack, 64 * MAX_STACK_COUNT);
        subItems.add(stoneStack);
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
		if(ItemStackTools.isValid(stack)){
			ItemStack blockStack = getBlockStack(stack);
			if(ItemStackTools.isValid(blockStack)){
				tooltip.add(blockStack.getDisplayName() + " ("+getBlockCount(stack)+")");
			} else {
				tooltip.add(Lang.localize("gui.empty"));
			}
			
			tooltip.add("Auto Pickup: "+ (isAutoPickupEnabled(stack) ? TextFormatting.GREEN + "Enabled" : TextFormatting.RED + "Disabled"));			
		}
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
		ItemStack held = player.getHeldItem(hand);
		if(ItemStackTools.isValid(held)){
			if(player.isSneaking()){
				if(hand == EnumHand.OFF_HAND)return new ActionResult<ItemStack>(EnumActionResult.PASS, held);
				player.openGui(CrystalMod.instance, GuiHandler.GUI_ID_ITEM, world, 0, 0, 0);
		        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, held);
			} else {
				ItemStack blockStack = getBlockStack(held);
				if(ItemStackTools.isValid(blockStack)){
					if(canBePlaced(blockStack)){
						int count = getBlockCount(held);
						if(count > 0){
							ItemStack placeStack = ItemUtil.copy(blockStack, 1);
							ItemUtil.setPlayerHandSilently(player, hand, placeStack);
							ActionResult<ItemStack> result = blockStack.getItem().onItemRightClick(world, player, hand);
							placeStack = result.getResult();
							if(ItemStackTools.isEmpty(placeStack)){
								removeBlocks(held, 1);
							}
							ItemUtil.setPlayerHandSilently(player, hand, held);
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
		ItemStack held = player.getHeldItem(hand);
		if(ItemStackTools.isValid(held)){
			ItemStack blockStack = getBlockStack(held);
			if(ItemStackTools.isValid(blockStack)){
				if(canBePlaced(blockStack)){
					int count = getBlockCount(held);
					if(count > 0){
						ItemStack placeStack = ItemUtil.copy(blockStack, 1);
						ItemUtil.setPlayerHandSilently(player, hand, placeStack);
						EnumActionResult response = blockStack.getItem().onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
						placeStack = player.getHeldItem(hand);
						if(ItemStackTools.isEmpty(placeStack) && !player.capabilities.isCreativeMode){
							removeBlocks(held, 1);
						}
						ItemUtil.setPlayerHandSilently(player, hand, held);
						return response;
					}
				}
			}
		}
        return EnumActionResult.PASS;
    }
	
	public static boolean canBePlaced(ItemStack stack){
		//TODO Look into having covers work in the advanced block holder
		if(ItemStackTools.isValid(stack)){
			Item item = stack.getItem();
			if(item instanceof ItemBlock || item instanceof ItemBlockSpecial || item instanceof ItemDoor){
				return true;
			}
			if(item == Items.REDSTONE || item == Items.SIGN || item == Items.SKULL){
				return true;
			}
		}
		return false;
	}
	
	//TODO Add Ore-Dictionary Setting
	public static void setBlockStack(ItemStack stack, ItemStack block){
		if(ItemStackTools.isValid(stack)){
			if(ItemStackTools.isValid(block)){
				NBTTagCompound blockNBT = block.writeToNBT(new NBTTagCompound());
				ItemNBTHelper.getCompound(stack).setTag(NBT_BLOCK_STACK, blockNBT);				
			} else {
				ItemNBTHelper.getCompound(stack).removeTag(NBT_BLOCK_STACK);
			}
		}
	}
	
	public static ItemStack getBlockStack(ItemStack stack){
		if(ItemStackTools.isValid(stack)){
			if(stack.hasTagCompound()){
				NBTTagCompound blockNBT = ItemNBTHelper.getCompound(stack).getCompoundTag(NBT_BLOCK_STACK);
				if(blockNBT !=null){
					return ItemStackTools.loadFromNBT(blockNBT);
				}
			}
		}
		return ItemStackTools.getEmptyStack();
	}
	
	public static int addBlocks(ItemStack stack, int amt){
		ItemStack blockStack = getBlockStack(stack);
		int current = getBlockCount(stack);
		int filled = (blockStack.getMaxStackSize() * MAX_STACK_COUNT) - current;

        if (amt < filled)
        {
            setBlockCount(stack, current + amt);
            filled = amt;
        }
        else
        {
        	setBlockCount(stack, (blockStack.getMaxStackSize() * MAX_STACK_COUNT));
        }		
		return filled;
	}
	
	public static int removeBlocks(ItemStack stack, int amt){
		int current = getBlockCount(stack);
		int removed = Math.min(current, amt);
		if(removed > 0){
			setBlockCount(stack, current - removed);
		}
		return removed;
	}
	
	public static void setBlockCount(ItemStack stack, int value){
		ItemStack blockStack = getBlockStack(stack);
		ItemNBTHelper.setInteger(stack, NBT_BLOCK_COUNT, Math.min(value, (blockStack.getMaxStackSize() * MAX_STACK_COUNT)));
	}
	
	public static int getBlockCount(ItemStack stack){
		return ItemNBTHelper.getInteger(stack, NBT_BLOCK_COUNT, 0);
	}
	
	public static boolean isAutoPickupEnabled(ItemStack stack){
		return ItemNBTHelper.getBoolean(stack, NBT_SETTING_PICKUP, false);
	}
	
	public static void setAutoPickup(ItemStack stack, boolean value){
		ItemNBTHelper.setBoolean(stack, NBT_SETTING_PICKUP, value);
	}
}
