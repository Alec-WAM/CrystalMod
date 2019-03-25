package alec_wam.CrystalMod.entities.accessories.boats;

import java.util.UUID;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.tiles.chest.wireless.IWirelessChestSource;
import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestHelper;
import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestManager;
import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestManager.WirelessInventory;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class EntityBoatChest extends Entity implements IInventory, IWirelessChestSource {

	public static enum EnumBoatChestType {
		NORMAL(true), ENDER(false), WIRELESS(false);
		
		boolean hasInventory;
		EnumBoatChestType(boolean hasInv){
			this.hasInventory = hasInv;
		}
	}

	private static final DataParameter<ItemStack> CHEST_STACK = EntityDataManager.<ItemStack>createKey(EntityBoatChest.class, DataSerializers.OPTIONAL_ITEM_STACK);
	private static final DataParameter<Boolean> OPEN = EntityDataManager.<Boolean>createKey(EntityBoatChest.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Byte> TYPE = EntityDataManager.<Byte>createKey(EntityBoatChest.class, DataSerializers.BYTE);
	private EnumBoatChestType type;
	private NonNullList<ItemStack> chestItems;
	private WirelessInventory inventory;
	
	public float prevLidAngle;
    public float lidAngle;
    private boolean open;
	
	public EntityBoatChest(World world){
		this(world, EnumBoatChestType.NORMAL);
	}
	
    public EntityBoatChest(World worldIn, EnumBoatChestType type) {
		super(worldIn);
		setType(type);
		if(type.hasInventory)chestItems = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY);
		else chestItems = NonNullList.create();
	}
    
    public EntityBoatChest(World worldIn, EnumBoatChestType type, ItemStack stack) {
		this(worldIn, type);
		
		dataManager.set(CHEST_STACK, stack);
	}
    
    public boolean openChestGUI(EntityPlayer player){
    	if(getType() == EnumBoatChestType.NORMAL){
    		player.displayGUIChest(this);
    		setOpen(true);
    		//TODO Possibly close when gui is closed
    		return true;
    	}
    	if(type == EnumBoatChestType.ENDER){
    		player.displayGUIChest(player.getInventoryEnderChest());
    		setOpen(true);
    		return true;
    	}
    	if(type == EnumBoatChestType.WIRELESS){
    		if(isPrivate()){
    			if(!isOwner(player.getUniqueID()))return false;
    		}
    		BlockUtil.openWorksiteGui(player, GuiHandler.GUI_ID_ENTITY, getEntityId(), 0, 0);
    		return true;
    	}
    	return false;
    }
    
    public ItemStack getChestStack(){
    	return dataManager.get(CHEST_STACK);
    }
    
	@Override
	protected void entityInit() {
		noClip = true;
		dataManager.register(CHEST_STACK, new ItemStack(Blocks.CHEST));
        dataManager.register(TYPE, Byte.valueOf((byte)0));
        dataManager.register(OPEN, Boolean.valueOf(false));
	}
	
	public void setOpen(boolean open){
		this.dataManager.set(OPEN, Boolean.valueOf(open));
		this.open = open;
	}
	
	public boolean getOpen()
    {
        return this.getEntityWorld().isRemote ? dataManager.get(OPEN) : this.open;
    }
	
	public void setType(EnumBoatChestType type){
		this.dataManager.set(TYPE, Byte.valueOf((byte)type.ordinal()));
		this.type = type;
	}
	
	public EnumBoatChestType getType(){
		int t = this.getEntityWorld().isRemote ? dataManager.get(TYPE) : this.type.ordinal();
		return EnumBoatChestType.values()[t];
	}
    
    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
    	compound.setByte("Type", (byte)getType().ordinal());
    	if(ItemStackTools.isValid(getChestStack())){
    		compound.setTag("ChestStack", getChestStack().writeToNBT(new NBTTagCompound()));
    	}
        ItemStackHelper.saveAllItems(compound, this.chestItems);
    }

	@Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        setType(EnumBoatChestType.values()[compound.getByte("Type")]);
        if(compound.hasKey("ChestStack")){
        	ItemStack stack = new ItemStack(compound.getCompoundTag("ChestStack"));
        	if(ItemStackTools.isValid(stack)){
        		dataManager.set(CHEST_STACK, stack);
        	}
        }
        if(type.hasInventory)chestItems = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY);
		else chestItems = NonNullList.create();

        ItemStackHelper.loadAllItems(compound, this.chestItems);
    }
	
	@Override
	public void setDead()
    {
        if (!this.world.isRemote)
        {
            InventoryHelper.dropInventoryItems(this.world, this, this);
            ItemUtil.spawnItemInWorldWithRandomMotion(world, dataManager.get(CHEST_STACK), getPosition());
        }

		super.setDead();
    }
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		
		if(this.isDead){
			return;
		}
		
		if(!isRiding()) {
			if(!world.isRemote){
				setDead();
			}
			
			return;
		}
		
		Entity riding = getRidingEntity();
		rotationYaw = riding.prevRotationYaw;
		rotationPitch = 0F;
		
		if (world != null && !world.isRemote)
        {
        	boolean newOpen = getOpen();
        	if(getType() == EnumBoatChestType.WIRELESS){
	            WirelessInventory inventory = getInventory();
	            if(inventory !=null){
	            	newOpen = inventory.playerUsingCount > 0;
	            }
        	}
            if(newOpen !=getOpen()){
            	setOpen(newOpen);
            }
        }
		
		prevLidAngle = lidAngle;
        float f = 0.1F;
        if (getOpen() && lidAngle == 0.0F)
        {
            world.playSound(null, posX + 0.5D, posY + 0.5D, posZ + 0.5D, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.NEUTRAL, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
        }
        if (!getOpen() && lidAngle > 0.0F || getOpen() && lidAngle < 1.0F)
        {
            float f1 = lidAngle;
            if (getOpen())
            {
                lidAngle += f;
            } else
            {
                lidAngle -= f;
            }
            if (lidAngle > 1.0F)
            {
                lidAngle = 1.0F;
            }
            float f2 = 0.5F;
            if (lidAngle < f2 && f1 >= f2)
            {
                world.playSound(null, posX + 0.5D, posY + 0.5D, posZ + 0.5D, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.NEUTRAL, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
            }
            if (lidAngle < 0.0F)
            {
                lidAngle = 0.0F;
            }
        }
	}
	
	public boolean isEmpty()
    {
        for (ItemStack itemstack : this.chestItems)
        {
            if (!itemstack.isEmpty())
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the stack in the given slot.
     */
    public ItemStack getStackInSlot(int index)
    {
        return (ItemStack)this.chestItems.get(index);
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    public ItemStack decrStackSize(int index, int count)
    {
        return ItemStackHelper.getAndSplit(this.chestItems, index, count);
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    public ItemStack removeStackFromSlot(int index)
    {
        ItemStack itemstack = (ItemStack)this.chestItems.get(index);

        if (itemstack.isEmpty())
        {
            return ItemStack.EMPTY;
        }
        else
        {
            this.chestItems.set(index, ItemStack.EMPTY);
            return itemstack;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.chestItems.set(index, stack);

        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit())
        {
            stack.setCount(this.getInventoryStackLimit());
        }
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty()
    {
    }

    /**
     * Don't rename this method to canInteractWith due to conflicts with Container
     */
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        return this.isDead ? false : player.getDistanceSqToEntity(this) <= 64.0D;
    }

    public void openInventory(EntityPlayer player)
    {
    }

    public void closeInventory(EntityPlayer player)
    {
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
     * guis use Slot.isItemValid
     */
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
     */
    public int getInventoryStackLimit()
    {
        return 64;
    }
    
    public int getField(int id)
    {
        return 0;
    }

    public void setField(int id, int value)
    {
    }

    public int getFieldCount()
    {
        return 0;
    }
    
    public net.minecraftforge.items.IItemHandler itemHandler = new net.minecraftforge.items.wrapper.InvWrapper(this);

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.util.EnumFacing facing)
    {
        if (capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return (T) itemHandler;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, @Nullable net.minecraft.util.EnumFacing facing)
    {
        return capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    public void clear()
    {
        this.chestItems.clear();
    }

	@Override
	public int getSizeInventory() {
		return this.chestItems.size();
	}

	//WirelessChest Stuff
	
	@Override
	public int getCode() {
		if(ItemStackTools.isValid(getChestStack()) && getChestStack().getItem() == Item.getItemFromBlock(ModBlocks.wirelessChest)){
			return ItemNBTHelper.getInteger(getChestStack(), WirelessChestHelper.NBT_CODE, -1);
		}
		return 0;
	}

	@Override
	public WirelessInventory getInventory() {
		if(getCode() >=0){
			if (inventory == null)
	        {
	            if (isPrivate())
	            	inventory = WirelessChestManager.get(getEntityWorld()).getPrivate(getOwner()).getInventory(getCode());
	            else
	            	inventory = WirelessChestManager.get(getEntityWorld()).getInventory(getCode());
	        }
	    	return inventory;
		}
		return null;
	}

	@Override
	public boolean isPrivate() {
		return getOwner() !=null;
	}

	@Override
	public UUID getOwner() {
		if(ItemStackTools.isValid(getChestStack()) && getChestStack().getItem() == Item.getItemFromBlock(ModBlocks.wirelessChest)){
			String owner = ItemNBTHelper.getString(getChestStack(), WirelessChestHelper.NBT_OWNER, "");
			if(UUIDUtils.isUUID(owner)){
				return UUIDUtils.fromString(owner);
			}
		}
		return null;
	}

	@Override
	public boolean isOwner(UUID uuid) {
		return getOwner() == null || UUIDUtils.areEqual(uuid, getOwner());
	}

}
