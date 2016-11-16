package alec_wam.CrystalMod.entities.minecarts.chests.wireless;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.chest.wireless.BlockWirelessChest;
import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestHelper;
import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestManager;
import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestManager.WirelessInventory;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.PlayerUtil;
import alec_wam.CrystalMod.util.ProfileUtil;
import alec_wam.CrystalMod.util.UUIDUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityWirelessChestMinecart extends EntityMinecartChest implements ISidedInventory {
	
	private static final DataParameter<Integer> CODE = EntityDataManager.<Integer>createKey(EntityWirelessChestMinecart.class, DataSerializers.VARINT);
	private int code = 0;
	private UUID boundToPlayer;
	private static final DataParameter<Optional<UUID>> OWNER = EntityDataManager.<Optional<UUID>>createKey(EntityWirelessChestMinecart.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	private WirelessInventory inventory;
	
	
	public float prevLidAngle;
    public float lidAngle;
    private boolean open;
	private static final DataParameter<Boolean> OPEN = EntityDataManager.<Boolean>createKey(EntityWirelessChestMinecart.class, DataSerializers.BOOLEAN);

	public EntityWirelessChestMinecart(World worldIn)
    {
        super(worldIn);
    }

    public EntityWirelessChestMinecart(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(CODE, Integer.valueOf(0));
        this.dataManager.register(OWNER, Optional.<UUID>absent());
        this.dataManager.register(OPEN, Boolean.valueOf(false));
    }
	
	public void setOpen(boolean open){
		this.dataManager.set(OPEN, Boolean.valueOf(open));
		this.open = open;
	}
	
	public boolean getOpen()
    {
        return this.worldObj.isRemote ? dataManager.get(OPEN) : this.open;
    }
    
    public void setOwner(UUID owner){
    	this.dataManager.set(OWNER, Optional.<UUID>fromNullable(owner));
    	this.boundToPlayer = owner;
    	this.inventory = null;
    }
    
    public UUID getOwner()
    {
        return this.worldObj.isRemote ? dataManager.get(OWNER).orNull() : this.boundToPlayer;
    }
    
    public boolean isBoundToPlayer()
    {
        return getOwner() != null;
    }
    
	public boolean isOwner(UUID uuid) {
		return !isBoundToPlayer() ? true : getOwner().equals(uuid);
	}
	
	public void setCode(int code){
    	this.dataManager.set(CODE, Integer.valueOf(code));
    	this.code = code;
    	this.inventory = null;
    }
    
    public int getCode()
    {
        return this.worldObj.isRemote ? dataManager.get(CODE) : code;
    }
    
    public boolean hasValidCode(){
    	return getCode() >= 0;
    }
    
    public WirelessInventory getInventory(){
    	if(!hasValidCode()){
    		return null;
    	}
    	if (inventory == null)
        {
            if (isBoundToPlayer())
            	inventory = WirelessChestManager.get(worldObj).getPrivate(boundToPlayer).getInventory(getCode());
            else
            	inventory = WirelessChestManager.get(worldObj).getInventory(getCode());
        }
    	return inventory;
    }
    
    @Override
    public void onUpdate(){
    	super.onUpdate();
    	// Resynchronize clients with the server state
        if (worldObj != null && !worldObj.isRemote)
        {
        	boolean newOpen = getOpen();
            WirelessInventory inventory = getInventory();
            if(inventory !=null){
            	newOpen = inventory.playerUsingCount > 0;
            }
            if(newOpen !=getOpen()){
            	setOpen(newOpen);
            }
        }
        
        prevLidAngle = lidAngle;
        float f = 0.1F;
        if (getOpen() && lidAngle == 0.0F)
        {
            double d = posX + 0.5D;
            double d1 = posZ + 0.5D;
            worldObj.playSound(null, d, posY + 0.5D, d1, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.NEUTRAL, 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
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
                double d2 = posX + 0.5D;
                double d3 = posZ + 0.5D;
                worldObj.playSound(null, d2, posY + 0.5D, d3, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.NEUTRAL, 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
            }
            if (lidAngle < 0.0F)
            {
                lidAngle = 0.0F;
            }
        }
    }
    
	public void setDead()
    {
		this.isDead = true;
    }
	
	public void killMinecart(DamageSource source)
    {
		this.setDead();

        if (this.worldObj.getGameRules().getBoolean("doEntityDrops"))
        {
            ItemStack itemstack = new ItemStack(Items.MINECART, 1);

            if (this.getName() != null)
            {
                itemstack.setStackDisplayName(this.getName());
            }

            this.entityDropItem(itemstack, 0.0F);
            entityDropItem(BlockWirelessChest.createNBTStack(getCode(), getOwner()), 0.0F);
        }
    }
	
	public ItemStack getPickedResult(RayTraceResult target)
    {
		ItemStack stack = new ItemStack(ModItems.wirelessChestMinecart);
		if(boundToPlayer !=null)ItemNBTHelper.setString(stack, WirelessChestHelper.NBT_OWNER, UUIDUtils.fromUUID(getOwner()));
		ItemNBTHelper.setInteger(stack, WirelessChestHelper.NBT_CODE, getCode());
		return stack;
    }
	
	public IBlockState getDefaultDisplayTile()
    {
        return ModBlocks.wirelessChest.getDefaultState();
    }
	
	public int getSizeInventory(){
		if(getInventory() == null)return 0;
		return getInventory().getSlots();
	}
	
	@Nullable
    public ItemStack getStackInSlot(int index)
    {
		if(getInventory() == null)return null;
        return getInventory().getStackInSlot(index);
    }
	
	@Nullable
    public ItemStack decrStackSize(int index, int count)
    {
		if(getInventory() == null)return null;
        return getInventory().extractItem(index, count, false);
    }
	
	@Nullable
    public ItemStack removeStackFromSlot(int index)
    {
        if (getInventory() != null)
        {
            ItemStack itemstack = getInventory().getStackInSlot(index);
            getInventory().setStackInSlot(index, null);
            return itemstack;
        }
        else
        {
            return null;
        }
    }
	
	public void setInventorySlotContents(int index, @Nullable ItemStack stack)
    {
		if(getInventory() == null)return;
		getInventory().setStackInSlot(index, stack);
    }

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		int[] array = new int[getSizeInventory()];
		for(int i = 0; i < array.length; i++){
			array[i] = i;
		}
		return array;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return boundToPlayer == null;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return boundToPlayer == null;
	}
	
	public boolean processInitialInteract(EntityPlayer player, @Nullable ItemStack stack, EnumHand hand)
    {
        if(net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.minecart.MinecartInteractEvent(this, player, stack, hand))) return true;
        
        if(isOwner(player.getUniqueID())){
        	if(stack !=null && ItemUtil.itemStackMatchesOredict(stack, "gemDiamond")){
        		if(!isBoundToPlayer()){
	        		if (!player.capabilities.isCreativeMode)
	                	stack.stackSize--;
	                setOwner(player.getUniqueID());
	        		return true;
        		}
        	}

            if (!this.worldObj.isRemote)
            {
                player.openGui(CrystalMod.instance, GuiHandler.GUI_ID_ENTITY, player.worldObj, getEntityId(), 0, 0);
            }
        } else {
        	if(isBoundToPlayer()){
        		if(!worldObj.isRemote)
        		ChatUtil.sendChat(player, "You do not own this chest, "+ProfileUtil.getUsername(boundToPlayer)+" does.");
        	}
        }

        return true;
    }
	
	protected void writeEntityToNBT(NBTTagCompound compound)
    {
		if (this.hasDisplayTile())
        {
            compound.setBoolean("CustomDisplayTile", true);
            IBlockState iblockstate = this.getDisplayTile();
            ResourceLocation resourcelocation = (ResourceLocation)Block.REGISTRY.getNameForObject(iblockstate.getBlock());
            compound.setString("DisplayTile", resourcelocation == null ? "" : resourcelocation.toString());
            compound.setInteger("DisplayData", iblockstate.getBlock().getMetaFromState(iblockstate));
            compound.setInteger("DisplayOffset", this.getDisplayTileOffset());
        }
		compound.setInteger("Code", getCode());
		if(boundToPlayer !=null)PlayerUtil.uuidToNBT(compound, boundToPlayer);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
    	if (compound.getBoolean("CustomDisplayTile"))
        {
            Block block;

            if (compound.hasKey("DisplayTile", 8))
            {
                block = Block.getBlockFromName(compound.getString("DisplayTile"));
            }
            else
            {
                block = Block.getBlockById(compound.getInteger("DisplayTile"));
            }

            int i = compound.getInteger("DisplayData");
            this.setDisplayTile(block == null ? Blocks.AIR.getDefaultState() : block.getStateFromMeta(i));
            this.setDisplayTileOffset(compound.getInteger("DisplayOffset"));
        }
    	setCode(compound.getInteger("Code"));
    	setOwner(PlayerUtil.uuidFromNBT(compound));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing)
    {
        if (capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
        	if(getInventory() == null)return super.getCapability(capability, facing);
            return (T) getInventory();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing)
    {
        return capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

}