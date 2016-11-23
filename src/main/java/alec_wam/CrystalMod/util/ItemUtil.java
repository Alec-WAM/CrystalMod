package alec_wam.CrystalMod.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided;
import alec_wam.CrystalMod.tiles.pipes.item.GhostItemHelper;
import alec_wam.CrystalMod.tiles.pipes.item.filters.CameraFilterInventory;
import alec_wam.CrystalMod.tiles.pipes.item.filters.FilterInventory;
import alec_wam.CrystalMod.tiles.pipes.item.filters.ItemPipeFilter.FilterType;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ItemUtil {

  //public static final List<IItemReceptor> receptors = new ArrayList<IItemReceptor>();
  private static final Random rand = new Random();

  static {
    /*try {
      Class.forName("crazypants.util.BuildcraftUtil");
    } catch (Exception e) {
      if (Loader.isModLoaded("BuildCraft|Transport")) {
        //Log.warn("ItemUtil: Could not register Build Craft pipe handler. Machines will not be able to output to BC pipes.");
      } //Don't log if BC isn't installed, but we still check in case another mod is using their API
    }*/
  }

  /**
   * Turns a String into an item that can be used in a recipe. This is one of:
   * <ul>
   * <li>String</li>
   * <li>Item</li>
   * <li>Block</li>
   * <li>ItemStack</li>
   * </ul>
   * Because this method can return a String, it is highly recommended that you
   * use the result in a {@link ShapedOreRecipe} or {@link ShapelessOreRecipe}.
   * 
   * @param string
   *          The String to parse.
   * @return An object for use in recipes.
   */
  public static Object parseStringIntoRecipeItem(String string) {
    return parseStringIntoRecipeItem(string, false);
  }

  /**
   * Turns a String into an item that can be used in a recipe. This is one of:
   * <ul>
   * <li>String</li>
   * <li>Item</li>
   * <li>Block</li>
   * <li>ItemStack</li>
   * </ul>
   * Because this method can return a String, it is highly recommended that you
   * use the result in a {@link ShapedOreRecipe} or {@link ShapelessOreRecipe}.
   * 
   * @see #parseStringIntoItemStack(String)
   * @param string
   *          The String to parse.
   * @param forceItemStack
   *          True if the result should be forced to be an ItemStack.
   * @return AN object for use in recipes.
   */
  public static Object parseStringIntoRecipeItem(String string, boolean forceItemStack) {
    if ("null".equals(string)) {
      return null;
    } else if (OreDictionary.getOres(string).isEmpty()) {
      ItemStack stack = null;

      String[] info = string.split(";");
      Object temp = null;
      int damage = OreDictionary.WILDCARD_VALUE;
      temp = Item.REGISTRY.getObject(new ResourceLocation(info[0]));
      if (info.length > 1) {
        damage = Integer.parseInt(info[1]);
      }

      if (temp instanceof Item) {
        stack = new ItemStack((Item) temp, 1, damage);
      } else if (temp instanceof Block) {
        stack = new ItemStack((Block) temp, 1, damage);
      } else if (temp instanceof ItemStack) {
        stack = ((ItemStack) temp).copy();
        stack.setItemDamage(damage);
      } else {
        throw new IllegalArgumentException(string
            + " is not a vaild string. Strings should be either an oredict name, or in the format objectname;damage (damage is optional)");
      }

      return stack;
    } else if (forceItemStack) {
      return OreDictionary.getOres(string).get(0).copy();
    } else {
      return string;
    }
  }

  /**
   * Turns a string into an ItemStack.
   * <p>
   * This is basically a convenience method that casts the result of
   * {@link #parseStringIntoRecipeItem(String, boolean)}, but with one extra
   * feature.
   * <p>
   * A '#' character may be used at the end of the string to signify stack size,
   * e.g. "minecraft:diamond#2" would be a stack of 2 diamonds.
   * <p>
   * The stack size will automatically be clamped to be below the give Item's
   * max stack size.
   * 
   * @param string
   *          The String to parse.
   * @return An ItemStack the string represents.
   */
  public static ItemStack parseStringIntoItemStack(String string) {
    int size = 1;
    int idx = string.indexOf('#');

    if (idx != -1) {
      String num = string.substring(idx + 1);

      try {
        size = Integer.parseInt(num);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(num + " is not a valid stack size");
      }

      string = string.substring(0, idx);
    }

    ItemStack stack = (ItemStack) parseStringIntoRecipeItem(string, true);
    stack.stackSize = MathHelper.clamp_int(size, 1, stack.getMaxStackSize());
    return stack;
  }

  /**
   * Returns the appropriate config string for the given {@link ItemStack}
   * <p>
   * This does not take into account ore dict.
   * 
   * @param stack
   *          The {@link ItemStack} to serialize
   * @param damage
   *          If damage should be taken into account
   * @param size
   *          If stack size should be taken into account
   * @return A string that will be the equivalent of if {@link ItemStack stack}
   *         was constructed from it using
   *         {@link #parseStringIntoItemStack(String)}
   */
  public static String getStringForItemStack(ItemStack stack, boolean damage, boolean size) {
    if (stack == null) {
      return null;
    }

    String base = Item.REGISTRY.getNameForObject(stack.getItem()).toString();

    if (damage) {
      base += ";" + stack.getItemDamage();
    }

    if (size) {
      base += "#" + stack.stackSize;
    }

    return base;
  }
  
  public static void dropContent(int newSize, IItemHandler chest, World world, BlockPos pos)
  {
	  Random random = world.rand;

      for (int l = newSize; l < chest.getSlots(); l++)
      {
          ItemStack itemstack = chest.getStackInSlot(l);
          if (itemstack == null)
          {
              continue;
          }
          float f = random.nextFloat() * 0.8F + 0.1F;
          float f1 = random.nextFloat() * 0.8F + 0.1F;
          float f2 = random.nextFloat() * 0.8F + 0.1F;
          while (itemstack.stackSize > 0)
          {
              int i1 = random.nextInt(21) + 10;
              if (i1 > itemstack.stackSize)
              {
                  i1 = itemstack.stackSize;
              }
              itemstack.stackSize -= i1;
              EntityItem entityitem = new EntityItem(world, pos.getX() + f, (float) pos.getY() + (newSize > 0 ? 1 : 0) + f1, pos.getZ() + f2, new ItemStack(itemstack.getItem(), i1, itemstack.getMetadata()));
              float f3 = 0.05F;
              entityitem.motionX = (float) random.nextGaussian() * f3;
              entityitem.motionY = (float) random.nextGaussian() * f3 + 0.2F;
              entityitem.motionZ = (float) random.nextGaussian() * f3;
              if (itemstack.hasTagCompound())
              {
                  entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
              }
              world.spawnEntityInWorld(entityitem);
          }
      }
  }
  
  public static void dropContent(int newSize, IInventory chest, World world, BlockPos pos)
  {
      Random random = world.rand;

      for (int l = newSize; l < chest.getSizeInventory(); l++)
      {
          ItemStack itemstack = chest.getStackInSlot(l);
          if (itemstack == null)
          {
              continue;
          }
          float f = random.nextFloat() * 0.8F + 0.1F;
          float f1 = random.nextFloat() * 0.8F + 0.1F;
          float f2 = random.nextFloat() * 0.8F + 0.1F;
          while (itemstack.stackSize > 0)
          {
              int i1 = random.nextInt(21) + 10;
              if (i1 > itemstack.stackSize)
              {
                  i1 = itemstack.stackSize;
              }
              itemstack.stackSize -= i1;
              EntityItem entityitem = new EntityItem(world, pos.getX() + f, (float) pos.getY() + (newSize > 0 ? 1 : 0) + f1, pos.getZ() + f2, new ItemStack(itemstack.getItem(), i1, itemstack.getMetadata()));
              float f3 = 0.05F;
              entityitem.motionX = (float) random.nextGaussian() * f3;
              entityitem.motionY = (float) random.nextGaussian() * f3 + 0.2F;
              entityitem.motionZ = (float) random.nextGaussian() * f3;
              if (itemstack.hasTagCompound())
              {
                  entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
              }
              world.spawnEntityInWorld(entityitem);
          }
      }
  }

  public static void spawnItemInWorldWithRandomMotion(World world, ItemStack item, BlockPos pos) {
    spawnItemInWorldWithRandomMotion(world, item, pos.getX(), pos.getY(), pos.getZ());
  }
  
  /**
   * Spawns an ItemStack into the world with motion that simulates a normal
   * block drop.
   * 
   * @param world
   *          The world object.
   * @param item
   *          The ItemStack to spawn.
   * @param x
   *          X coordinate of the block in which to spawn the entity.
   * @param y
   *          Y coordinate of the block in which to spawn the entity.
   * @param z
   *          Z coordinate of the block in which to spawn the entity.
   */
  public static void spawnItemInWorldWithRandomMotion(World world, ItemStack item, int x, int y, int z) {
    if (item != null) {
      spawnItemInWorldWithRandomMotion(new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, item));
    }
  }

  /**
   * Spawns an EntityItem into the world with motion that simulates a normal
   * block drop.
   * 
   * @param entity
   *          The entity to spawn.
   */
  public static void spawnItemInWorldWithRandomMotion(EntityItem entity) {
    entity.setDefaultPickupDelay();

    float f = (rand.nextFloat() * 0.1f) - 0.05f;
    float f1 = (rand.nextFloat() * 0.1f) - 0.05f;
    float f2 = (rand.nextFloat() * 0.1f) - 0.05f;

    entity.motionX += f;
    entity.motionY += f1;
    entity.motionZ += f2;

    if(!entity.worldObj.isRemote)
    entity.worldObj.spawnEntityInWorld(entity);
  }
  
  public static void spawnItemInWorldWithoutMotion(World world, ItemStack item, BlockPos pos) {
	  spawnItemInWorldWithoutMotion(world, item, pos.getX(), pos.getY(), pos.getZ());
  }
	  
  /**
   * Spawns an ItemStack into the world without motion
   * block drop.
   * 
   * @param world
   *          The world object.
   * @param item
   *          The ItemStack to spawn.
   * @param x
   *          X coordinate of the block in which to spawn the entity.
   * @param y
   *          Y coordinate of the block in which to spawn the entity.
   * @param z
   *          Z coordinate of the block in which to spawn the entity.
   */
  public static void spawnItemInWorldWithoutMotion(World world, ItemStack item, int x, int y, int z) {
    if (item != null) {
    	spawnItemInWorldWithoutMotion(new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, item));
    }
  }

  /**
   * Spawns an EntityItem into the world without motion
   * block drop.
   * 
   * @param entity
   *          The entity to spawn.
   */
  public static void spawnItemInWorldWithoutMotion(EntityItem entity) {
    entity.motionX = entity.motionY = entity.motionZ = 0.0D;
	entity.setDefaultPickupDelay();

    if(!entity.worldObj.isRemote)
    entity.worldObj.spawnEntityInWorld(entity);
  }

  
  public static boolean stackMatchUseOre(ItemStack stack, ItemStack stack2){
	  if(canCombine(stack, stack2))return true;
	  if(isOreMatch(stack, stack2))return true;
	  return false;
  }
  
  public static boolean canCombine(ItemStack stack1, ItemStack stack2)
  {
      if (stack1 == null || stack1.getItem() == null || (stack2 !=null && stack2.getItem() == null))
      {
          return false;
      }

      if (stack2 == null)
      {
          return true;
      }

      if (stack1.isItemStackDamageable() ^ stack2.isItemStackDamageable())
      {
          return false;
      }

      return stack1.getItem() == stack2.getItem() && stack1.getItemDamage() == stack2.getItemDamage() && ItemStack.areItemStackTagsEqual(stack1, stack2);
  }
  
  public static boolean isOreMatch(ItemStack stack, ItemStack stack2){
	  if(stack2 !=null){
		for(int ids : OreDictionary.getOreIDs(stack)){
			String name = OreDictionary.getOreName(ids);
			if(ItemUtil.itemStackMatchesOredict(stack2, name)){
				return true;
			}
		}
	  }
	  return false;
  }
  
  public static List<ItemStack> getMatchingOreStacks(ItemStack stack){
	  if(stack == null) return Lists.newArrayList();
	  int[] ids = OreDictionary.getOreIDs(stack);
	  if(ids == null || ids.length == 0){
		  return Collections.singletonList(stack);
	  }
	  List<ItemStack> stacks = Lists.newArrayList();
	  for(int id : ids){
		  for(ItemStack oreStack : OreDictionary.getOres(OreDictionary.getOreName(id))){
			  if(oreStack !=null && oreStack.stackSize == stack.stackSize){
				  stacks.add(oreStack);
			  }
		  }
	  }
	  
	  return stacks;
  }
  
  public static List<String> getOreNames(ItemStack stack){
	  List<String> list = Lists.newArrayList();
	  for(int ids : OreDictionary.getOreIDs(stack)){
			list.add(OreDictionary.getOreName(ids));
	  }
	  return list;
  }
  
  /**
   * Returns true if the given stack has the given Ore Dictionary name applied
   * to it.
   * 
   * @param stack
   *          The ItemStack to check.
   * @param oredict
   *          The oredict name.
   * @return True if the ItemStack matches the name passed.
   */
  public static boolean itemStackMatchesOredict(ItemStack stack, String oredict) {
    int[] ids = OreDictionary.getOreIDs(stack);
    for (int i : ids) {
      String name = OreDictionary.getOreName(i);
      if (name.equals(oredict)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Gets an NBT tag from an ItemStack, creating it if needed. The tag returned
   * will always be the same as the one on the stack.
   * 
   * @param stack
   *          The ItemStack to get the tag from.
   * @return An NBTTagCompound from the stack.
   */
  public static NBTTagCompound getNBTTag(ItemStack stack) {
    if (!stack.hasTagCompound()) {
      stack.setTagCompound(new NBTTagCompound());
    }
    return stack.getTagCompound();
  }

  /*public static String getDurabilityString(ItemStack item) {
    if (item == null) {
      return null;
    }
    return EnderCore.lang.localize("tooltip.durability") + " " + (item.getMaxDamage() - item.getItemDamage()) + "/" + item.getMaxDamage();
  }*/

  public static NBTTagCompound getOrCreateNBT(ItemStack stack) {
    if (!stack.hasTagCompound()) {
      stack.setTagCompound(new NBTTagCompound());
    }
    return stack.getTagCompound();
  }

  public static int doInsertItem(Object into, ItemStack item, EnumFacing side) {
	  return doInsertItem(into, item, side, true);
  }
  
  public static int doInsertItem(Object into, ItemStack item, EnumFacing side, boolean insert) {
    if (into == null || item == null) {
      return 0;
    }
    if (into instanceof ISidedInventory) {
      return ItemUtil.doInsertItemInv((ISidedInventory) into, item, side, insert);
    } else if (into instanceof IInventory) {
      return ItemUtil.doInsertItemInv(getInventory((IInventory) into), item, side, insert);
    } else if (into instanceof IItemHandler){
    	IItemHandler handler = (IItemHandler)into;
	    int startSize = item.stackSize;
	    ItemStack res = ItemHandlerHelper.insertItemStacked(handler, item.copy(), !insert);
	    int val = res == null ? startSize : startSize - res.stackSize;
	    return val;
    }

    /*for (IItemReceptor rec : receptors) {
      if (rec.canInsertIntoObject(into, side)) {
        return rec.doInsertItem(into, item, side);
      }
    }*/

    return 0;
  }
  
  public static int doInsertItemMatching(Object into, ItemStack item, EnumFacing side) {
    if (into == null || item == null) {
      return 0;
    }
    if (into instanceof ISidedInventory) {
      return ItemUtil.doInsertItemInvMatching((ISidedInventory) into, item, side, true);
    } else if (into instanceof IInventory) {
      return ItemUtil.doInsertItemInv(getInventory((IInventory) into), item, side, true);
    } 

    return 0;
  }

  public static int doInsertItem(IInventory inv, int startSlot, int endSlot, ItemStack item) {
    return doInsertItemInv(inv, null, invSlotter.getInstance(startSlot, endSlot), item, null, true);
  }

  public static int doInsertItem(IInventory inv, int startSlot, int endSlot, ItemStack item, boolean doInsert) {
    return doInsertItemInv(inv, null, invSlotter.getInstance(startSlot, endSlot), item, null, doInsert);
  }

  private static int doInsertItemInv(IInventory inv, ItemStack item, EnumFacing inventorySide, boolean doInsert) {
    final ISidedInventory sidedInv = inv instanceof ISidedInventory ? (ISidedInventory) inv : null;
    ISlotIterator slots;

    if (sidedInv != null) {     
      slots = sidedSlotter.getInstance(sidedInv.getSlotsForFace(inventorySide));
    } else {
      slots = invSlotter.getInstance(0, inv.getSizeInventory());
    }

    return doInsertItemInv(inv, sidedInv, slots, item, inventorySide, doInsert);
  }
  
  /**
   * Ignores ISided
   */
  public static int doInsertItemInvArray(IInventory inv, ItemStack item, int[] slotArray, boolean doInsert) {
    return doInsertItemInv(inv, null, sidedSlotter.getInstance(slotArray), item, null, doInsert);
  }

  private static int doInsertItemInv(IInventory inv, ISidedInventory sidedInv, ISlotIterator slots, ItemStack item, EnumFacing inventorySide,
      boolean doInsert) {
    int numInserted = 0;
    int numToInsert = item.stackSize;
    int firstFreeSlot = -1;

    // PASS1: Try to add to an existing stack
    while (numToInsert > 0 && slots.hasNext()) {
      final int slot = slots.nextSlot();
      if (sidedInv == null || sidedInv.canInsertItem(slot, item, inventorySide)) {
        final ItemStack contents = inv.getStackInSlot(slot);
        if (contents != null) {
          if (areStackMergable(contents, item)) {
            final int freeSpace = Math.min(inv.getInventoryStackLimit(), contents.getMaxStackSize()) - contents.stackSize; // some inventories like using itemstacks with invalid stack sizes
            if (freeSpace > 0) {
              final int noToInsert = Math.min(numToInsert, freeSpace);
              final ItemStack toInsert = item.copy();
              toInsert.stackSize = contents.stackSize + noToInsert;
              // isItemValidForSlot() may check the stacksize, so give it the number the stack would have in the end.
              // If it does something funny, like "only even numbers", we are screwed.
              if (sidedInv != null || inv.isItemValidForSlot(slot, toInsert)) {
                numInserted += noToInsert;
                numToInsert -= noToInsert;
                if (doInsert) {
                  inv.setInventorySlotContents(slot, toInsert);
                }
              }
            }
          }
        } else if (firstFreeSlot == -1) {
          firstFreeSlot = slot;
        }
      }
    }

    // PASS2: Try to insert into an empty slot
    if (numToInsert > 0 && firstFreeSlot != -1) {
      final ItemStack toInsert = item.copy();
      toInsert.stackSize = min(numToInsert, inv.getInventoryStackLimit(), toInsert.getMaxStackSize()); // some inventories like using itemstacks with invalid stack sizes
      if (sidedInv != null || inv.isItemValidForSlot(firstFreeSlot, toInsert)) {
        numInserted += toInsert.stackSize;
        numToInsert -= toInsert.stackSize;
        if (doInsert) {
          inv.setInventorySlotContents(firstFreeSlot, toInsert);
        }
      }
    }

    if (numInserted > 0 && doInsert) {
      inv.markDirty();
    }
    return numInserted;
  }
  
  private static int doInsertItemInvMatching(IInventory inv, ItemStack item, EnumFacing inventorySide, boolean doInsert) {
    final ISidedInventory sidedInv = inv instanceof ISidedInventory ? (ISidedInventory) inv : null;
    ISlotIterator slots;

    if (sidedInv != null) {     
      slots = sidedSlotter.getInstance(sidedInv.getSlotsForFace(inventorySide));
    } else {
      slots = invSlotter.getInstance(0, inv.getSizeInventory());
    }

    return doInsertItemInvMatching(inv, sidedInv, slots, item, inventorySide, doInsert);
  }
  
  private static int doInsertItemInvMatching(IInventory inv, ISidedInventory sidedInv, ISlotIterator slots, ItemStack item, EnumFacing inventorySide,
	      boolean doInsert) {
	    int numInserted = 0;
	    int numToInsert = item.stackSize;
	    int firstFreeSlot = -1;

	    // PASS1: Try to add to an existing stack
	    while (numToInsert > 0 && slots.hasNext()) {
	      final int slot = slots.nextSlot();
	      if (sidedInv == null || sidedInv.canInsertItem(slot, item, inventorySide)) {
	        final ItemStack contents = inv.getStackInSlot(slot);
	        if (contents != null) {
	          if (areStackMergable(contents, item)) {
	            final int freeSpace = Math.min(inv.getInventoryStackLimit(), contents.getMaxStackSize()) - contents.stackSize; // some inventories like using itemstacks with invalid stack sizes
	            if (freeSpace > 0) {
	              final int noToInsert = Math.min(numToInsert, freeSpace);
	              final ItemStack toInsert = item.copy();
	              toInsert.stackSize = contents.stackSize + noToInsert;
	              // isItemValidForSlot() may check the stacksize, so give it the number the stack would have in the end.
	              // If it does something funny, like "only even numbers", we are screwed.
	              if (sidedInv != null || inv.isItemValidForSlot(slot, toInsert)) {
	                numInserted += noToInsert;
	                numToInsert -= noToInsert;
	                if (doInsert) {
	                  inv.setInventorySlotContents(slot, toInsert);
	                }
	              }
	            }
	          }
	        } else if (firstFreeSlot == -1) {
	          firstFreeSlot = slot;
	        }
	      }
	    }

	    if (numInserted > 0 && doInsert) {
	      inv.markDirty();
	    }
	    return numInserted;
	  }

  private final static int min(int i1, int i2, int i3) {
    return i1 < i2 ? (i1 < i3 ? i1 : i3) : (i2 < i3 ? i2 : i3);
  }

  public static boolean isStackFull(ItemStack contents) {
    if (contents == null) {
      return false;
    }
    return contents.stackSize >= contents.getMaxStackSize();
  }

  public static IInventory getInventory(IInventory inv) {
    if (inv instanceof TileEntityChest) {
      TileEntityChest chest = (TileEntityChest) inv;
      TileEntityChest neighbour = null;
      if (chest.adjacentChestXNeg != null) {
        neighbour = chest.adjacentChestXNeg;
      } else if (chest.adjacentChestXPos != null) {
        neighbour = chest.adjacentChestXPos;
      } else if (chest.adjacentChestZNeg != null) {
        neighbour = chest.adjacentChestZNeg;
      } else if (chest.adjacentChestZPos != null) {
        neighbour = chest.adjacentChestZPos;
      }
      if (neighbour != null) {
        return new InventoryLargeChest("", (ILockableContainer)inv, (ILockableContainer)neighbour);
      }
      return inv;
    }
    return inv;
  }

  /**
   * Checks if items, damage and NBT are equal and the items are stackable.
   * 
   * @param s1
   * @param s2
   * @return True if the two stacks are mergeable, false otherwise.
   */
  public static boolean areStackMergable(ItemStack s1, ItemStack s2) {
    if (s1 == null || s2 == null || !s1.isStackable() || !s2.isStackable()) {
      return false;
    }
    if (!s1.isItemEqual(s2)) {
      return false;
    }
    return ItemStack.areItemStackTagsEqual(s1, s2);
  }

  /**
   * Checks if items, damage and NBT are equal.
   * 
   * @param s1
   * @param s2
   * @return True if the two stacks are equal, false otherwise.
   */
  public static boolean areStacksEqual(ItemStack s1, ItemStack s2) {
    if (s1 == null || s2 == null) {
      return false;
    }
    if (!s1.isItemEqual(s2)) {
      return false;
    }
    return ItemStack.areItemStackTagsEqual(s1, s2);
  }

  private interface ISlotIterator {
    int nextSlot();

    boolean hasNext();
  }

  private final static class invSlotter implements ISlotIterator {
    private static final invSlotter me = new invSlotter();
    private int end;
    private int current;

    public final static invSlotter getInstance(int start, int end) {
      me.end = end;
      me.current = start;
      return me;
    }

    @Override
    public final int nextSlot() {
      return current++;
    }

    @Override
    public final boolean hasNext() {
      return current < end;
    }
  }

  private final static class sidedSlotter implements ISlotIterator {
    private static final sidedSlotter me = new sidedSlotter();
    private int[] slots;
    private int current;

    public final static sidedSlotter getInstance(int[] slots) {
      me.slots = slots;
      me.current = 0;
      return me;
    }

    @Override
    public final int nextSlot() {
      return slots[current++];
    }

    @Override
    public final boolean hasNext() {
      return slots != null && current < slots.length;
    }
  }

  public static ItemStack consumeItem(ItemStack stack) {
		if (stack.stackSize == 1) {
			if (stack.getItem().hasContainerItem(stack)) {
				return stack.getItem().getContainerItem(stack);
			} else {
				return null;
			}
		} else {
			stack.splitStack(1);

			return stack;
		}
	}

  public static ItemStack copy(ItemStack stack, int size) {
	  ItemStack copy = stack.copy();
	  copy.stackSize = size;
	  return copy;
  }


    
    public static ItemStack getRandomEnchantedItem(Enchantment enchantment, EntityLivingBase entity)
    {
    	Iterable<ItemStack> stacks = entity.getEquipmentAndArmor();
        if (stacks == null)
        {
        	return null;
        }
        else
        {
            List<ItemStack> list = Lists.newArrayList();

            for (ItemStack itemstack : stacks)
            {
                if (itemstack != null && EnchantmentHelper.getEnchantmentLevel(enchantment, itemstack) > 0)
                {
                    list.add(itemstack);
                }
            }
            return list.isEmpty() ? null : (ItemStack)list.get(entity.getRNG().nextInt(list.size()));
        }
    }
    
    public static IItemHandler getExternalItemHandler(IBlockAccess world, BlockPos pos, EnumFacing face){
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

        IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);

        if (handler == null) {
            if (side != null && tile instanceof ISidedInventory) {
                handler = new SidedInvWrapper((ISidedInventory) tile, side);
            } else if (tile instanceof IInventory) {
                handler = new InvWrapper((IInventory) tile);
            }
        }

        return handler;
    }
    
    public static void combineMultipleItemsInTooltip(List<String> lines, boolean displayAmount, ItemStack... stacks) {
    	Set<Integer> combinedIndices = new HashSet<Integer>();

        for (int i = 0; i < stacks.length; ++i) {
            if (stacks[i] != null && !combinedIndices.contains(i)) {
                String data = stacks[i].getDisplayName();

                int amount = stacks[i].stackSize;

                for (int j = i + 1; j < stacks.length; ++j) {
                    if (stacks[j] !=null && canCombine(stacks[i], stacks[j])) {
                        amount += stacks[j].stackSize;

                        combinedIndices.add(j);
                    }
                }

                data = (displayAmount ? (TextFormatting.WHITE + String.valueOf(amount) + " ") : "") + TextFormatting.GRAY + data;

                lines.add(data);
            }
        }
    }
    
    public static boolean passesFilter(ItemStack item, ItemStack filter){
		if(item == null || item.getItem() == null || (filter !=null && filter.getItem() !=ModItems.pipeFilter))return false;
	    if(filter !=null){
	    	List<ItemStack> filteredList = new ArrayList<ItemStack>();
	    	if(filter.getMetadata() == FilterType.NORMAL.ordinal()){
		    	FilterInventory inv = new FilterInventory(filter, 10, "");
		    	for (int i = 0; i < inv.getSizeInventory(); i++)
		        {
		            ItemStack stack = inv.getStackInSlot(i);
		            if (stack == null)
		            {
		                continue;
		            }
		            ItemStack ghostStack = GhostItemHelper.getStackFromGhost(stack);
		            filteredList.add(ghostStack);
		        }
		    	boolean black = ItemNBTHelper.getBoolean(filter, "BlackList", false);
		    	
		    	if(filteredList.isEmpty()){
		    		return black ? false : true;
		    	}
		    	
		    	boolean ore = ItemNBTHelper.getBoolean(filter, "OreMatch", false);
		    	boolean meta = ItemNBTHelper.getBoolean(filter, "MetaMatch", true);
		    	boolean matchNBT = ItemNBTHelper.getBoolean(filter, "NBTMatch", true);
		    	boolean matched = false;
		    	for(ItemStack filterStack : filteredList){
		    		if(filterStack != null && Item.getIdFromItem(item.getItem()) == Item.getIdFromItem(filterStack.getItem())) {
		    	        matched = true;
		    	        if(meta && item.getItemDamage() != filterStack.getItemDamage()) {
		    	            matched = false;
		    	        } else if(matchNBT) {
		    	        	if(filterStack.getTagCompound() == null || item.getTagCompound() == null || !filterStack.getTagCompound().equals(item.getTagCompound()))
		    	        	matched = false;
		    	        }   
		    		}
		    		if(ore && !matched){
		    			int[] filterIds = OreDictionary.getOreIDs(filterStack);
		    	        int[] testIds = OreDictionary.getOreIDs(item);
	
		    	        if (filterIds.length > 0 && testIds.length > 0)
		    	        {
		    	        	oreList : for (int filterId : filterIds)
			    	        {
			    	            for (int testId : testIds)
			    	            {
			    	                if (filterId == testId)
			    	                {
			    	                	matched = true;
			    	                	break oreList;
			    	                }
			    	            }
			    	        }
		    	        }
		    		}
		    		if(matched) {
		    	        break;
		    		}
		    	}
		    	return black ? matched == false : matched;
	    	}else if(filter.getMetadata() == FilterType.MOD.ordinal()){
	    		FilterInventory inv = new FilterInventory(filter, 3, "");
		    	for (int i = 0; i < inv.getSizeInventory(); i++)
		        {
		            ItemStack stack = inv.getStackInSlot(i);
		            if (stack == null)
		            {
		                continue;
		            }
		            ItemStack ghostStack = GhostItemHelper.getStackFromGhost(stack);
		            filteredList.add(ghostStack);
		        }
		    	ResourceLocation resourceInput = Item.REGISTRY.getNameForObject(item.getItem());
    	        String modIDInput = resourceInput.getResourceDomain();
    	        boolean matched = false;
		    	for(ItemStack filterStack : filteredList){
		    		if(filterStack !=null){
		    			ResourceLocation resource = Item.REGISTRY.getNameForObject(filterStack.getItem());
		    	        String modID = resource.getResourceDomain();
		    	        if(modID.equals(modIDInput)){
		    	        	matched = true;
		    	        	break;
		    	        }
		    		}
		    	}
		    	boolean black = ItemNBTHelper.getBoolean(filter, "BlackList", false);
		    	return black ? matched == false : matched;
	    	}else if(filter.getMetadata() == FilterType.CAMERA.ordinal()){
		    	CameraFilterInventory inv = new CameraFilterInventory(filter, "");
		    	for (int i = 0; i < inv.getSizeInventory(); i++)
		        {
		            ItemStack stack = inv.getStackInSlot(i);
		            if (stack == null)
		            {
		                continue;
		            }
		            filteredList.add(stack);
		        }
		    	boolean black = ItemNBTHelper.getBoolean(filter, "BlackList", false);
		    	
		    	if(filteredList.isEmpty()){
		    		return black ? false : true;
		    	}
		    	
		    	boolean ore = ItemNBTHelper.getBoolean(filter, "OreMatch", false);
		    	boolean meta = ItemNBTHelper.getBoolean(filter, "MetaMatch", true);
		    	boolean matchNBT = ItemNBTHelper.getBoolean(filter, "NBTMatch", true);
		    	boolean matched = false;
		    	for(ItemStack filterStack : filteredList){
		    		if(filterStack != null && Item.getIdFromItem(item.getItem()) == Item.getIdFromItem(filterStack.getItem())) {
		    	        matched = true;
		    	        if(meta && item.getItemDamage() != filterStack.getItemDamage()) {
		    	            matched = false;
		    	        } else if(matchNBT) {
		    	        	if(filterStack.getTagCompound() == null || item.getTagCompound() == null || !filterStack.getTagCompound().equals(item.getTagCompound()))
		    	        	matched = false;
		    	        }   
		    		}
		    		if(ore && !matched){
		    			int[] filterIds = OreDictionary.getOreIDs(filterStack);
		    	        int[] testIds = OreDictionary.getOreIDs(item);
	
		    	        if (filterIds.length > 0 && testIds.length > 0)
		    	        {
		    	        	oreList : for (int filterId : filterIds)
			    	        {
			    	            for (int testId : testIds)
			    	            {
			    	                if (filterId == testId)
			    	                {
			    	                	matched = true;
			    	                	break oreList;
			    	                }
			    	            }
			    	        }
		    	        }
		    		}
		    		if(matched) {
		    	        break;
		    		}
		    	}
		    	return black ? matched == false : matched;
	    	}
	    }
	    return true;
	}

    public static ItemStack getItemFromBlock(
			final IBlockState state )
	{
		final Block blk = state.getBlock();

		final Item i = blk.getItemDropped( state, rand, 0 );
		final int meta = blk.getMetaFromState( state );
		final int damage = blk.damageDropped( state );
		final Item blockVarient = Item.getItemFromBlock( blk );

		if ( i == null )
		{
			return null;
		}

		if ( blockVarient == null )
		{
			return null;
		}

		if ( blockVarient != i )
		{
			return null;
		}

		if ( blockVarient instanceof ItemBlock )
		{
			final ItemBlock ib = (ItemBlock) blockVarient;
			if ( meta != ib.getMetadata( damage ) )
			{
				// this item dosn't drop itself... BAIL!
				return null;
			}
		}

		return new ItemStack( i, 1, damage );
	}

	public static boolean isDust(ItemStack stack) {
		List<String> names = getOreNames(stack);
		for(String name : names){
			if(name.startsWith("dust"))return true;
		}
		return false;
	}
	
	public static boolean isOre(ItemStack stack) {
		List<String> names = getOreNames(stack);
		for(String name : names){
			if(name.startsWith("ore"))return true;
		}
		return false;
	}
	
	public static boolean isIngot(ItemStack stack) {
		List<String> names = getOreNames(stack);
		for(String name : names){
			if(name.startsWith("ingot"))return true;
		}
		return false;
	}

	

	/** Writes the contents of the inventory to the tag */
	public static void writeInventoryToNBT(IInventory inventory, NBTTagCompound tag) {
		NBTTagList nbttaglist = new NBTTagList();

	    for(int i = 0; i < inventory.getSizeInventory(); i++) {
	      if(inventory.getStackInSlot(i) != null) {
	        NBTTagCompound itemTag = new NBTTagCompound();
	        itemTag.setByte("Slot", (byte) i);
	        inventory.getStackInSlot(i).writeToNBT(itemTag);
	        nbttaglist.appendTag(itemTag);
	      }
	    }

	    tag.setTag("Items", nbttaglist);
	}

	/** Reads a an inventory from the tag. Overwrites current content */
	public static void readInventoryFromNBT(IInventory inventory, NBTTagCompound tag) {
	    NBTTagList nbttaglist = tag.getTagList("Items", 10);

	    for(int i = 0; i < nbttaglist.tagCount(); ++i) {
	      NBTTagCompound itemTag = nbttaglist.getCompoundTagAt(i);
	      int slot = itemTag.getByte("Slot");

	      if(slot >= 0 && slot < inventory.getSizeInventory()) {
	        inventory.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(itemTag));
	      }
	    }
	}
	
	public static ItemStack removeFromPlayerInventory(Container container, ItemStack removeStack){
		final Map<Integer, ItemStack> originalSlotContents = new HashMap<Integer, ItemStack>();
		InventoryPlayer playerinv = null;
		for(Slot slot : container.inventorySlots){
			if(slot.inventory instanceof InventoryPlayer){
				playerinv = ((InventoryPlayer)slot.inventory);
				break;
			}
		}
		if(playerinv == null)return null;
		final int amount = removeStack.stackSize;
		final ItemStack requiredStack = removeStack.copy();
		boolean broke = false;
		int needed = amount;
		while(needed > 0 && !broke){
			int slot = playerinv.getSlotFor(requiredStack);
			if(slot == -1){
				broke = true;
				break;
			}
			ItemStack slotStack = playerinv.decrStackSize(slot, needed);
			if(slotStack == null){
				broke = true;
				break;
			}
			needed -=slotStack.stackSize;
			if(needed < 0){
				needed = 0;
			}
		}
		//ModLogger.info("Needed "+(needed)+"/"+amount);
		
		if(broke){
			for (Map.Entry<Integer, ItemStack> slotEntry : originalSlotContents.entrySet()) {
				ItemStack stack = slotEntry.getValue();
				playerinv.addItemStackToInventory(stack);
			}
		}
		/*while (requiredStack.stackSize > 0 && con) {
			
			final Slot slot = getSlotWithStack(container, requiredStack, playerSlots, new ArrayList<Integer>());
			if (slot == null) {
				// abort! put removed items back where the came from
				for (Map.Entry<Slot, ItemStack> slotEntry : originalSlotContents.entrySet()) {
					ItemStack stack = slotEntry.getValue();
					slotEntry.getKey().putStack(stack);
				}
				ModLogger.info("PRINT2");
				con = false;
				break;
			}

			if (!originalSlotContents.containsKey(slot) && slot.getStack() !=null) {
				originalSlotContents.put(slot, slot.getStack().copy());
			}

			ItemStack removed = slot.decrStackSize(requiredStack.stackSize);
			if(removed !=null && requiredStack !=null){
				requiredStack.stackSize -= removed.stackSize;
				retAmt+=removed.stackSize;
			}
			break;
		}*/
		int remaining = amount-needed;
		return remaining <=0 ? null : ItemUtil.copy(removeStack, remaining);
	}
	
	public static Slot getSlotWithStack(@Nonnull Container container, @Nonnull Iterable<Integer> slotNumbers, @Nonnull ItemStack stack) {
		for (Integer slotNumber : slotNumbers) {
			Slot slot = container.getSlot(slotNumber);
			if (slot != null) {
				ItemStack slotStack = slot.getStack();
				if (ItemUtil.canCombine(stack, slotStack)) {
					return slot;
				}
			}
		}
		return null;
	}

	public static boolean canInventoryHold(InventorySided inventory, int[] slots, ItemStack stack) {
		return canInventoryHold(inventory, slots, Lists.newArrayList(stack));
	}
		
	public static boolean canInventoryHold(InventorySided inventory, int[] slots, List<ItemStack> stacks) {
		int slot;
		int emptySlots = 0;
		ItemStack stack;
		ItemQuantityMap itemQuantities = new ItemQuantityMap();
		 
		for(int i = 0; i<stacks.size(); i++)
    	{
			stack = stacks.get(i);
			itemQuantities.addCount(stack, stack.stackSize);
	    }
		    
		for(int i = 0; i < slots.length; i++)
		{
		    slot = slots[i];
		    stack = inventory.getStackInSlot(slot);
	    	if(stack==null){emptySlots++;}
	    	else if(itemQuantities.contains(stack))
	    	{
	    		itemQuantities.decreaseCount(stack, stack.getMaxStackSize()-stack.stackSize);
	    	}
		}
		  
		return emptySlots >= itemQuantities.keySet().size();
	}

	public static int[] getIndiceArrayForSpread(int start, int len)
	{
		int[] array = new int[len];
		for(int i = 0, k = start; i<len; i++, k++)
		{
			array[i]=k;
		}
		return array;
	}
	
	public static ItemStack removeItems(IInventory inventory, EnumFacing side,	ItemStack filter, int quantity) {
		if (quantity > filter.getMaxStackSize()) {
			quantity = filter.getMaxStackSize();
		}
		ItemStack returnStack = null;
		if (side !=null && inventory instanceof ISidedInventory) {
			int[] slotIndices = ((ISidedInventory) inventory)
					.getSlotsForFace(side);
			if (slotIndices == null) {
				return null;
			}
			int index;
			int toMove;
			ItemStack slotStack;
			for (int i = 0; i < slotIndices.length; i++) {
				index = slotIndices[i];
				slotStack = inventory.getStackInSlot(index);
				if (slotStack == null || !canCombine(slotStack, filter)) {
					continue;
				}
				if (returnStack == null) {
					returnStack = filter.copy();
					returnStack.stackSize = 0;
				}
				toMove = slotStack.stackSize;
				if (toMove > quantity) {
					toMove = quantity;
				}
				if (toMove + returnStack.stackSize > returnStack
						.getMaxStackSize()) {
					toMove = returnStack.getMaxStackSize()
							- returnStack.stackSize;
				}
				returnStack.stackSize += toMove;
				slotStack.stackSize -= toMove;
				quantity -= toMove;
				if (slotStack.stackSize <= 0) {
					inventory.setInventorySlotContents(index, null);
				}
				inventory.markDirty();
				if (quantity <= 0) {
					break;
				}
			}
		} else {
			int toMove;
			ItemStack slotStack;
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				slotStack = inventory.getStackInSlot(i);
				if (slotStack == null || !canCombine(slotStack, filter)) {
					continue;
				}
				if (returnStack == null) {
					returnStack = filter.copy();
					returnStack.stackSize = 0;
				}
				toMove = slotStack.stackSize;
				if (toMove > quantity) {
					toMove = quantity;
				}
				if (toMove + returnStack.stackSize > returnStack
						.getMaxStackSize()) {
					toMove = returnStack.getMaxStackSize()
							- returnStack.stackSize;
				}
				returnStack.stackSize += toMove;
				slotStack.stackSize -= toMove;
				quantity -= toMove;
				if (slotStack.stackSize <= 0) {
					inventory.setInventorySlotContents(i, null);
				}
				inventory.markDirty();
				if (quantity <= 0) {
					break;
				}
			}
		}
		return returnStack;
	}

	public static EnumDyeColor getDyeColor(ItemStack stack) {
		if(stack !=null){
        	for(EnumDyeColor color : EnumDyeColor.values()){
        		String cap = (color.getUnlocalizedName().substring(0, 1).toUpperCase()+color.getUnlocalizedName().substring(1));
        		String oreID = "dye"+cap;
        		if(ItemUtil.itemStackMatchesOredict(stack, oreID)){
        			return color;
        		}
        	}
    	}
    	return null;
	}
	
	public static String getDyeName(EnumDyeColor dye){
		if(dye == null) return "null";
		return Lang.translateToLocal("item.fireworksCharge." + dye.getUnlocalizedName());
	}

	public static int compareNames(ItemStack stack1, ItemStack stack2) {
		int i = new ItemStack(stack1.getItem()).getDisplayName().compareTo(new ItemStack(stack2.getItem()).getDisplayName());
        if (i != 0) {
            return i;
        }
        return stack1.getDisplayName().compareTo(stack2.getDisplayName());
	}

}