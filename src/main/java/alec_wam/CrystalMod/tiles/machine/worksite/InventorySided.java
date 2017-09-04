package alec_wam.CrystalMod.tiles.machine.worksite;

import java.util.EnumSet;
import java.util.HashMap;

import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class InventorySided implements IInventory, ISidedInventory
{

	private EnumSet<RelativeSide> validSides = EnumSet.of(RelativeSide.NONE);

	/**
	 * Block side to Inventory Side
	 * inventorySide should only contain validSides
	 */
	private HashMap<RelativeSide, RelativeSide> accessMap = new HashMap<RelativeSide, RelativeSide>();

	private HashMap<RelativeSide, int[]> slotsByInventorySide = new HashMap<RelativeSide, int[]>();
	private HashMap<RelativeSide, boolean[]> extractInsertFlags = new HashMap<RelativeSide, boolean[]>();//inventoryside x boolean[2]; [0]=extract, [1]=insert
	public final IRotatableTile te;
	public final RotationType rType;
	private NonNullList<ItemStack> inventorySlots;
	private ItemSlotFilter[] filtersByInventorySlot;

	public InventorySided(IRotatableTile te, RotationType rType, int inventorySize)
	{
		if(te==null || rType==null || inventorySize<=0){throw new IllegalArgumentException("te and rotation type may not be null, inventory size must be greater than 0");}
		this.te = te;
		this.rType = rType;
		inventorySlots= NonNullList.<ItemStack>withSize(inventorySize, ItemStack.EMPTY);
		filtersByInventorySlot = new ItemSlotFilter[inventorySize];
		for(RelativeSide rSide : rType.getValidSides())
		{    
			setAccessibleSideDefault(rSide, RelativeSide.NONE, new int[]{});
		}
	}

	/**
	 * Should be called to configure the default access directly after construction of the inventory
	 * @param rSide
	 * @param iSide
	 */
	public void setAccessibleSideDefault(RelativeSide rSide, RelativeSide iSide, int[] indices)
	{
		if(rSide==null || iSide==null || indices==null){throw new IllegalArgumentException("sides or indices may not be null!");}
		if(rSide==RelativeSide.NONE){throw new IllegalArgumentException("base side may not be NONE");}
		addValidSide(iSide);
		accessMap.put(rSide, iSide);
		setInventoryIndices(iSide, indices);
	}

	public int[] getRawIndices(RelativeSide side)
	{
		return slotsByInventorySide.get(side);
	}

	public int[] getRawIndicesCombined(RelativeSide... sides)
	{
		int len = 0;
		int [] indices, combindedIndices;
		for(RelativeSide side :sides)
		{
			indices = getRawIndices(side);
			if(indices!=null){len+=indices.length;}
		}
		combindedIndices = new int[len];
		int index = 0;
		for(RelativeSide side :sides)
		{
			indices = getRawIndices(side);
			if(indices!=null)
			{
				for(int i : indices)
				{
					combindedIndices[index] = i;
					index++;
				}
			}    
		}  
		return combindedIndices;
	}

	private void addValidSide(RelativeSide side)
	{
		validSides.add(side);
	}

	public void remapSideAccess(RelativeSide baseSide, RelativeSide remappedSide)
	{
		boolean baseValid = rType.getValidSides().contains(baseSide);
		boolean remapValid = baseValid && getValidSides().contains(remappedSide);
		if(baseValid && remapValid)
		{
			accessMap.put(baseSide, remappedSide);
			markDirty();
		}
		else
		{
			throw new IllegalArgumentException("could not remap: "+baseSide+" to: "+remappedSide);
		}
	}

	public RelativeSide getRemappedSide(RelativeSide accessSide)
	{
		if(!accessMap.containsKey(accessSide))
		{
			throw new IllegalArgumentException("no mapping exists for: "+accessSide);
		}
		return accessMap.get(accessSide);
	}

	private void setInventoryIndices(RelativeSide inventorySide, int[] indices)
	{
		slotsByInventorySide.put(inventorySide, indices);  
		markDirty();
	}

	public void setFilterForSlots(ItemSlotFilter filter, int[] indices)
	{
		for(int i : indices)
		{
			filtersByInventorySlot[i]=filter;
		}
	}

	public void setExtractInsertFlags(RelativeSide inventorySide, boolean[] flags)
	{
		if(inventorySide==null || inventorySide==RelativeSide.NONE || flags==null){throw new IllegalArgumentException("inventory side must not be null or NONE, flags must not be null");}
		extractInsertFlags.put(inventorySide, flags);
	}

	public RelativeSide getInventorySide(EnumFacing mcSide)
	{
		EnumFacing meta = te.getPrimaryFacing();
		RelativeSide rSide = RelativeSide.getSideViewed(rType, meta, mcSide);
		rSide = accessMap.get(rSide);
		return rSide;
	}

	public ItemSlotFilter getFilterForSlot(int slot)
	{
		return filtersByInventorySlot[slot];
	}

	@Override
	public int[] getSlotsForFace(EnumFacing var1)
	{
		RelativeSide iSide = getInventorySide(var1);
		int[] slots = slotsByInventorySide.get(iSide);
		return slots==null ? new int[]{} : slots;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack var2, EnumFacing mcSide)
	{
		RelativeSide iSide = getInventorySide(mcSide);
		if(iSide==null){return false;}
		boolean[] flags = extractInsertFlags.get(iSide);
		if(flags!=null && !flags[1]){return false;}
		return isItemValidForSlot(slot, var2);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack var2, EnumFacing mcSide)
	{
		RelativeSide iSide = getInventorySide(mcSide);
		if(iSide==null){return false;}
		boolean[] flags = extractInsertFlags.get(iSide);
		return flags!=null ? flags[0] : true;
	}

	@Override
	public int getSizeInventory()
	{
		return inventorySlots.size();
	}

	@Override
	public ItemStack getStackInSlot(int var1)
	{
		return inventorySlots.get(var1);
	}

	@Override
	public ItemStack decrStackSize(int var1, int var2)
	{
		ItemStack itemStack = getStackInSlot(var1);

	    if(ItemStackTools.isNullStack(itemStack)) {
	      return ItemStackTools.getEmptyStack();
	    }

	    if(ItemStackTools.getStackSize(itemStack) <= var2) {
	      setInventorySlotContents(var1, ItemStackTools.getEmptyStack());
	      markDirty();
	      return itemStack;
	    }

	    itemStack = itemStack.splitStack(var2);
	    if(ItemStackTools.isEmpty(getStackInSlot(var1))) {
	      setInventorySlotContents(var1, ItemStackTools.getEmptyStack());
	    }
	    markDirty();
	    return itemStack;
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2)
	{
		inventorySlots.set(var1, var2);  
		markDirty();
	}

	@Override
	public String getName()
	{
	  return "cm_inventory_sided";
	}

	@Override
	public boolean hasCustomName()
	{  
	  return false;
	}
	
	@Override
	public ITextComponent getDisplayName()
	{
	  return new TextComponentString(getName());
	}

	@Override
	public int getInventoryStackLimit()
	{
	  return 64;
	}

	@Override
	public void markDirty()
	{
	  ((net.minecraft.tileentity.TileEntity)te).markDirty();
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer var1)
	{
	  return true;
	}
	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public void openInventory(EntityPlayer player)
	{
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
	}

	@Override
	public boolean isItemValidForSlot(int var1, ItemStack var2)
	{
	  ItemSlotFilter filter = filtersByInventorySlot[var1];
	  if(filter!=null){return filter.isItemValid(var2);}
	  return true;
	}

	public void readFromNBT(NBTTagCompound tag)
	{
	  readInventoryFromNBT(this, tag);
	  NBTTagCompound accessTag = tag.getCompoundTag("accessTag");
	  int[] rMap = accessTag.getIntArray("rMap");
	  int[] rMap2 = accessTag.getIntArray("iMap");
	  RelativeSide rSide;
	  RelativeSide iSide;
	  for(int i = 0; i <rMap.length && i<rMap2.length; i++)
	  {
	    rSide = RelativeSide.values()[rMap[i]];
	    iSide = RelativeSide.values()[rMap2[i]];
	    accessMap.put(rSide, iSide);
	  }
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
	  writeInventoryToNBT(this, tag);
	  int l = accessMap.size();
	  int rMap[] = new int[l];
	  int iMap[] = new int[l];  
	  int index = 0;
	  RelativeSide iSide;
	  for(RelativeSide rSide : accessMap.keySet())
	  {
	    iSide = accessMap.get(rSide);
	    rMap[index]=rSide.ordinal();
	    iMap[index]=iSide.ordinal();
	    index++;
	  }
	  NBTTagCompound accessTag = new NBTTagCompound();
	  accessTag.setIntArray("rMap", rMap);
	  accessTag.setIntArray("iMap", iMap);
	  tag.setTag("accessTag", accessTag);  
	  return tag;
	}

	public static NBTTagCompound writeInventoryToNBT(IInventory inventory, NBTTagCompound tag)
	{
		NBTTagList itemList = new NBTTagList();
		NBTTagCompound itemTag;  
		ItemStack item = ItemStackTools.getEmptyStack();
		for(int i = 0; i < inventory.getSizeInventory(); i++)
		{
			item = inventory.getStackInSlot(i);
			if(ItemStackTools.isNullStack(item)){continue;}
			itemTag = item.writeToNBT(new NBTTagCompound());
			itemTag.setShort("slot", (short)i);
			itemList.appendTag(itemTag);
		}  
		tag.setTag("itemList", itemList);
		return tag;
	}

	/**
	* Reads an inventory contents into the input inventory from the given nbt-tag.<br>
	* Should only be passed nbt-tags / inventories that have been saved using
	*  {@link #InventoryTools.writeInventoryToNBT(IInventory, NBTTagCompound)} 
	* @param inventory
	* @param tag
	*/
	public static void readInventoryFromNBT(IInventory inventory, NBTTagCompound tag)
	{
		NBTTagList itemList = tag.getTagList("itemList", 10);  
		NBTTagCompound itemTag;  
		ItemStack item;
		int slot;
		for(int i = 0; i < itemList.tagCount(); i++)
		{
			itemTag = itemList.getCompoundTagAt(i);
			slot = itemTag.getShort("slot");
			item = ItemStackTools.loadFromNBT(itemTag);
			inventory.setInventorySlotContents(slot, item);
		}
	}

	public EnumFacing getAccessDirectionFor(RelativeSide blockSide)
	{
		return EnumFacing.getFront(RelativeSide.getMCSideToAccess(rType, te.getPrimaryFacing().ordinal(), blockSide));
	}

	public EnumSet<RelativeSide> getValidSides()
	{
		return validSides;
	}

	public static enum RotationType
	{
		/**
		 * Can have 6 textures / inventories.<br>
		 * Top, Bottom, Front, Rear, Left, Right<br>
		 * Can only face in one of four-directions - N/S/E/W
		 */
			FOUR_WAY(EnumSet.of(RelativeSide.TOP, RelativeSide.BOTTOM, RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.FRONT, RelativeSide.REAR)),
		/**
		 * Can have 3 textures / inventories<br>
		 * Top, Bottom, Sides<br>
		 * Can face in any orientation - U/D/N/S/E/W
		 */
			SIX_WAY(EnumSet.of(RelativeSide.TOP, RelativeSide.BOTTOM, RelativeSide.ANY_SIDE)),
		/**
		 * No rotation, can still have relative sides, but FRONT always == NORTH
		 */
		NONE(EnumSet.of(RelativeSide.TOP, RelativeSide.BOTTOM, RelativeSide.LEFT, RelativeSide.RIGHT, RelativeSide.FRONT, RelativeSide.REAR));
		private RotationType(EnumSet<RelativeSide> sides){validSides=sides;}
		EnumSet<RelativeSide> validSides;
		public EnumSet<RelativeSide> getValidSides()
		{
			return validSides;
		}
	}

	public static enum RelativeSide
	{
		TOP("guistrings.inventory.side.top"),
		BOTTOM("guistrings.inventory.side.bottom"),
		FRONT("guistrings.inventory.side.front"),
		REAR("guistrings.inventory.side.rear"),
		LEFT("guistrings.inventory.side.left"),
		RIGHT("guistrings.inventory.side.right"),
		ANY_SIDE("guistrings.inventory.side.all_sides"), 
		NONE("guistrings.inventory.side.none");
		
		private static final int DOWN = 0;
		private static final int UP = 1;
		private static final int NORTH = 2;
		private static final int SOUTH = 3;
		private static final int WEST = 4;
		private static final int EAST = 5;
		//[side-viewed][block-facing]=relative side viewed
		public static final RelativeSide[][] sixWayMap = new RelativeSide[6][6];
		//[side-viewed][block-facing]=relative side viewed
		public static final RelativeSide[][] fourWayMap = new RelativeSide[6][6];
		//[block_meta][relative_side.ordinal] = mcSide output
		public static final int[][] accessMapFourWay = new int[6][6];
		
		static
		{
			//D,U,N,S,W,E
			//[side-viewed][block-facing]=relative side viewed
			//fourWayMap[X][0-1] SHOULD BE NEVER REFERENCED AS BLOCK CAN NEVER POINT U/D
			sixWayMap[DOWN][0]=TOP;
			sixWayMap[DOWN][1]=BOTTOM;
			sixWayMap[DOWN][2]=ANY_SIDE;
			sixWayMap[DOWN][3]=ANY_SIDE;
			sixWayMap[DOWN][4]=ANY_SIDE;
			sixWayMap[DOWN][5]=ANY_SIDE;
			
			sixWayMap[UP][0]=BOTTOM;
			sixWayMap[UP][1]=TOP;
			sixWayMap[UP][2]=ANY_SIDE;
			sixWayMap[UP][3]=ANY_SIDE;
			sixWayMap[UP][4]=ANY_SIDE;
			sixWayMap[UP][5]=ANY_SIDE;
			
			sixWayMap[NORTH][0]=ANY_SIDE;
			sixWayMap[NORTH][1]=ANY_SIDE;
			sixWayMap[NORTH][2]=TOP;
			sixWayMap[NORTH][3]=BOTTOM;
			sixWayMap[NORTH][4]=ANY_SIDE;
			sixWayMap[NORTH][5]=ANY_SIDE;
			
			sixWayMap[SOUTH][0]=ANY_SIDE;
			sixWayMap[SOUTH][1]=ANY_SIDE;
			sixWayMap[SOUTH][2]=BOTTOM;
			sixWayMap[SOUTH][3]=TOP;
			sixWayMap[SOUTH][4]=ANY_SIDE;
			sixWayMap[SOUTH][5]=ANY_SIDE;
			
			sixWayMap[WEST][0]=ANY_SIDE;
			sixWayMap[WEST][1]=ANY_SIDE;
			sixWayMap[WEST][2]=ANY_SIDE;
			sixWayMap[WEST][3]=ANY_SIDE;
			sixWayMap[WEST][4]=TOP;
			sixWayMap[WEST][5]=BOTTOM;
			
			sixWayMap[EAST][0]=ANY_SIDE;
			sixWayMap[EAST][1]=ANY_SIDE;
			sixWayMap[EAST][2]=ANY_SIDE;
			sixWayMap[EAST][3]=ANY_SIDE;
			sixWayMap[EAST][4]=BOTTOM;
			sixWayMap[EAST][5]=TOP;
			
			fourWayMap[DOWN][0] = ANY_SIDE;
			fourWayMap[DOWN][1] = ANY_SIDE;
			fourWayMap[DOWN][2] = BOTTOM;
			fourWayMap[DOWN][3] = BOTTOM;
			fourWayMap[DOWN][WEST] = BOTTOM;
			fourWayMap[DOWN][EAST] = BOTTOM;
			
			fourWayMap[UP][0] = ANY_SIDE;
			fourWayMap[UP][1] = ANY_SIDE;
			fourWayMap[UP][2] = TOP;
			fourWayMap[UP][3] = TOP;
			fourWayMap[UP][WEST] = TOP;
			fourWayMap[UP][EAST] = TOP;
			
			fourWayMap[NORTH][0] = ANY_SIDE;
			fourWayMap[NORTH][1] = ANY_SIDE;
			fourWayMap[NORTH][NORTH] = FRONT;
			fourWayMap[NORTH][SOUTH] = REAR;
			fourWayMap[NORTH][WEST] = RIGHT;
			fourWayMap[NORTH][EAST] = LEFT;
			
			fourWayMap[SOUTH][0] = ANY_SIDE;
			fourWayMap[SOUTH][1] = ANY_SIDE;
			fourWayMap[SOUTH][NORTH] = REAR;
			fourWayMap[SOUTH][SOUTH] = FRONT;
			fourWayMap[SOUTH][WEST] = LEFT;
			fourWayMap[SOUTH][EAST] = RIGHT;
			
			fourWayMap[WEST][0] = ANY_SIDE;
			fourWayMap[WEST][1] = ANY_SIDE;
			fourWayMap[WEST][NORTH] = LEFT;
			fourWayMap[WEST][SOUTH] = RIGHT;
			fourWayMap[WEST][WEST] = FRONT;
			fourWayMap[WEST][EAST] = REAR;
			
			fourWayMap[EAST][0] = ANY_SIDE;
			fourWayMap[EAST][1] = ANY_SIDE;
			fourWayMap[EAST][NORTH] = RIGHT;
			fourWayMap[EAST][SOUTH] = LEFT;
			fourWayMap[EAST][WEST] = REAR;
			fourWayMap[EAST][EAST] = FRONT;
		}

		private String key;
		private RelativeSide(String key)
		{
			this.key = key;
		}

		public String getTranslationKey(){return key;}

		public static RelativeSide getSideViewed(RotationType t, EnumFacing meta, EnumFacing side)
		{
			if(t==RotationType.FOUR_WAY)
			{
				return fourWayMap[side.getIndex()][meta.getIndex()];
			}
			else if(t==RotationType.SIX_WAY)
			{
				return sixWayMap[side.getIndex()][meta.getIndex()];
			}
			return ANY_SIDE;
		}

		public static int getMCSideToAccess(RotationType t, int meta, RelativeSide access)
		{
			RelativeSide[][] map = t==RotationType.FOUR_WAY ? fourWayMap : sixWayMap;
			for(int x = 0; x<map.length;x++)
			{
				if(map[x][meta]==access)
				{
					return x;
				}
			}
			return -1;
		}
	}

	public interface IRotatableTile
	{
		public EnumFacing getPrimaryFacing();
		public void setPrimaryFacing(EnumFacing face);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		this.setInventorySlotContents(index, ItemStackTools.getEmptyStack());
		return ItemStackTools.getEmptyStack();
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		inventorySlots.clear();
		markDirty();
	}
}
