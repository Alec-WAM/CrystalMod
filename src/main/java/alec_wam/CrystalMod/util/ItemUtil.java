package alec_wam.CrystalMod.util;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class ItemUtil {

	public static boolean canCombine(ItemStack stack1, ItemStack stack2)
	{
		if (ItemStackTools.isEmpty(stack1) || stack1.getItem() == null || (ItemStackTools.isValid(stack2) && stack2.getItem() == null))
		{
			return false;
		}

		if (ItemStackTools.isEmpty(stack2))
		{
			return true;
		}

		if (stack1.isDamageable() ^ stack2.isDamageable())
		{
			return false;
		}

		return stack1.getItem() == stack2.getItem() && stack1.getDamage() == stack2.getDamage() && ItemStack.areItemStackTagsEqual(stack1, stack2);
	}

	public static boolean matchUsingTags(ItemStack stack1, ItemStack stack2){
		List<ResourceLocation> tags1 = ItemTagHelper.getTags(stack1.getItem());
		List<ResourceLocation> tags2 = ItemTagHelper.getTags(stack2.getItem());
		for(ResourceLocation res : tags2){
			if(tags1.contains(res)){
				return true;
			}
		}
		return false;
	}
	
	public static ItemStack copy(ItemStack stack, int size) {
		ItemStack copy = stack.copy();
		ItemStackTools.setStackSize(copy, size);
		return copy;
	}

	public static ItemStack consumeItem(ItemStack stack) {
		if(ItemStackTools.isEmpty(stack))return ItemStackTools.getEmptyStack();
		if (ItemStackTools.getStackSize(stack) == 1) {
			if (stack.getItem().hasContainerItem(stack)) {
				return stack.getItem().getContainerItem(stack);
			} else {
				return ItemStackTools.getEmptyStack();
			}
		} else {
			stack.split(1);

			return stack;
		}
	}
	
	public static void setPlayerHandSilently(EntityPlayer player, EnumHand hand, ItemStack stack){
		if(hand == EnumHand.OFF_HAND){
			player.inventory.offHandInventory.set(0, stack);
		} else {
			player.inventory.mainInventory.set(player.inventory.currentItem, stack);			
		}
	}

	public static boolean matches(Object object, ItemStack stack2)
	{
		if(object instanceof ItemStack){
			return canCombine((ItemStack)object, stack2);
		}
		if(object instanceof Item){
			if(stack2.getItem() == (Item)object)return true;
		}
		if(object instanceof Block){
			if(stack2.getItem() == ((Block)object).asItem())return true;
		}
		if(object instanceof List){
			for(Object obj : (List<?>)object){
				if(obj instanceof ItemStack){
					if(canCombine((ItemStack)obj, stack2))return true;
				}
				if(obj instanceof Item){
					if(stack2.getItem() == (Item)obj)return true;
				}
				if(obj instanceof Block){
					if(stack2.getItem() == ((Block)obj).asItem())return true;
				}
			}
		}
		return false;
	}

	public static IItemHandler getExternalItemHandler(IWorld world, BlockPos pos, EnumFacing face){
		if (world == null || pos == null || face == null) {
			return null;
		}
		TileEntity te = world.getTileEntity(pos);
		return getItemHandler(te, face);
	}

	public static IItemHandler getItemHandler(TileEntity tile, EnumFacing side) {
		if (tile == null) {
			return null;
		}

		IItemHandler handler = null;
        if (tile != null)
        {
        	handler = (IItemHandler) tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).orElse(null);
        }

		if (handler == null) {
			if (side != null && tile instanceof ISidedInventory) {
				handler = new SidedInvWrapper((ISidedInventory) tile, side);
			} else if (tile instanceof IInventory) {
				handler = new InvWrapper((IInventory) tile);
			}
		}

		return handler;
	}

	public static int getFurnaceFuelValue(ItemStack stack) {
		if (stack.isEmpty()) {
			return 0;
		} else {
			Item item = stack.getItem();
			int ret = stack.getBurnTime();
			return net.minecraftforge.event.ForgeEventFactory.getItemBurnTime(stack, ret == -1 ? TileEntityFurnace.getBurnTimes().getOrDefault(item, 0) : ret);
		}
	}

	public static boolean isItemFuel(ItemStack stack) {
		return getFurnaceFuelValue(stack) > 0;
	}

	public static String getDyeName(EnumDyeColor dye){
		if(dye == null) return "null";
		return Lang.translateToLocal("item.minecraft.firework_star." + dye.getName());
	}

	public static EnumDyeColor getDyeColor(ItemStack stack) {
		if(stack.getItem() instanceof ItemDye){
			return ((ItemDye)stack.getItem()).getDyeColor();
		}
		return null;
	}

	public static void dropItemOnSide(World worldIn, BlockPos pos, ItemStack itemStack, EnumFacing sideHit) {
		double x = pos.getX() + 0.5;
		double y = pos.getY() + 0.5;
		double z = pos.getZ() + 0.5;
		switch(sideHit){
			case UP : {
				y+=0.75D;
			}
			case DOWN : {
				y-=0.75D;
			}
			case NORTH : {
				z-=1.5D;
			}
			case SOUTH : {
				z+=0.75D;
				x+=0.75D;
			}
			case WEST : {
				x-=1.5D;
			}
			case EAST : {
				x+=0.75D;
			}
		}

		InventoryHelper.spawnItemStack(worldIn, x, y, z, itemStack);
	}
	
}
