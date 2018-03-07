package alec_wam.CrystalMod.entities.misc;

import java.util.List;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.items.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.CPacketSteerBoat;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class EntityBoatChest extends EntityCustomBoat implements IInventory {
	private NonNullList<ItemStack> chestItems = NonNullList.<ItemStack>withSize(36, ItemStack.EMPTY);
    public boolean dropContentsWhenDead;
    
	public EntityBoatChest(World worldIn) {
		super(worldIn);
	}
	
	public EntityBoatChest(World worldIn, double x, double y, double z)
    {
		super(worldIn, x, y, z);
    }
	
	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
		if (player.isSneaking())
        {
	        if (!this.world.isRemote)
	        {
	        	player.displayGUIChest(this);
	        }
	        return true;
        }

        return super.processInitialInteract(player, hand);
    }
	
	@Override
	public Item getItemBoat()
    {
		return ModItems.bambooBoat;
    }
	
	@Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        ItemStackHelper.saveAllItems(compound, this.chestItems);
    }

	@Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        this.chestItems = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);

        ItemStackHelper.loadAllItems(compound, this.chestItems);
    }
	
	@Override
	public void onUpdate()
    {
        //this.previousStatus = this.status;
        ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_STATUS), FIELD_PREVSTATUS);
        //this.status = this.getBoatStatus();
        EntityBoat.Status status = getBoatStatus();
        ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, status, FIELD_STATUS);
        
        if (status != EntityCustomBoat.Status.UNDER_WATER && status != EntityCustomBoat.Status.UNDER_FLOWING_WATER)
        {
            //this.outOfControlTicks = 0.0F;

            ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, 0.0F, FIELD_OOCT);
        }
        else
        {
            //++this.outOfControlTicks;
        	float ooct = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_OOCT);
            ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, ooct + 1.0F, FIELD_OOCT);
        }
        
        float ooct = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, FIELD_OOCT);
        if (!this.world.isRemote && ooct >= 60.0F)
        {
            this.removePassengers();
        }

        if (this.getTimeSinceHit() > 0)
        {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }

        if (this.getDamageTaken() > 0.0F)
        {
            this.setDamageTaken(this.getDamageTaken() - 1.0F);
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        
        if (!this.world.isRemote)
        {
            this.setFlag(6, this.isGlowing());
        }

        this.onEntityUpdate();
        
        this.tickLerp();

        if (this.canPassengerSteer())
        {
            if (this.getPassengers().size() == 0 || !(this.getPassengers().get(0) instanceof EntityPlayer))
            {
                this.setPaddleState(false, false);
            }

            this.updateMotion();

            if (this.world.isRemote)
            {
                this.controlBoat();
                this.world.sendPacketToServer(new CPacketSteerBoat(this.getPaddleState(0), this.getPaddleState(1)));
            }

            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        }
        else
        {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
        }

        for (int i = 0; i <= 1; ++i)
        {
        	float[] paddlePositions = ObfuscationReflectionHelper.getPrivateValue(EntityBoat.class, this, 5);
            if (this.getPaddleState(i))
            {
                paddlePositions[i] = (float)((double)paddlePositions[i] + 0.01D);
            }
            else
            {
                paddlePositions[i] = 0.0F;
            }
            ObfuscationReflectionHelper.setPrivateValue(EntityBoat.class, this, paddlePositions, 5);
        }

        this.doBlockCollisions();
        List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().expand(0.20000000298023224D, -0.009999999776482582D, 0.20000000298023224D), EntitySelectors.<Entity>getTeamCollisionPredicate(this));

        if (!list.isEmpty())
        {
            boolean flag = !this.world.isRemote && !(this.getControllingPassenger() instanceof EntityPlayer);

            for (int j = 0; j < list.size(); ++j)
            {
                Entity entity = (Entity)list.get(j);

                if (!entity.isPassenger(this))
                {
                    if (flag && this.getPassengers().size() < 1 && !entity.isRiding() && entity.width < this.width && entity instanceof EntityLivingBase && !(entity instanceof EntityWaterMob) && !(entity instanceof EntityPlayer))
                    {
                        entity.startRiding(this);
                    }
                    else
                    {
                        this.applyEntityCollision(entity);
                    }
                }
            }
        }
    }
	
	@Nullable
    public Entity changeDimension(int dimensionIn)
    {
        this.dropContentsWhenDead = false;
        return super.changeDimension(dimensionIn);
    }

    /**
     * Will get destroyed next tick.
     */
    public void setDead()
    {
        if (this.dropContentsWhenDead)
        {
            InventoryHelper.dropInventoryItems(this.world, this, this);
        }

        super.setDead();
    }

    /**
     * Sets whether this entity should drop its items when setDead() is called. This applies to container minecarts.
     */
    public void setDropItemsWhenDead(boolean dropWhenDead)
    {
        this.dropContentsWhenDead = dropWhenDead;
    }
	
	@Override
	public void dropLoot(){
		if (this.world.getGameRules().getBoolean("doEntityDrops"))
        {
            InventoryHelper.dropInventoryItems(this.world, this, this);
            this.dropItemWithOffset(Item.getItemFromBlock(Blocks.CHEST), 1, 0.0F);
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
		return 27;
	}

}
