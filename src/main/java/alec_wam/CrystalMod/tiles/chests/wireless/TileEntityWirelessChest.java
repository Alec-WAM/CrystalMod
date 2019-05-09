package alec_wam.CrystalMod.tiles.chests.wireless;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alec_wam.CrystalMod.client.GuiHandler;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.INBTDrop;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.chests.wireless.WirelessChestManager.WirelessInventory;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
 
public class TileEntityWirelessChest extends TileEntityMod implements IMessageHandler, IWirelessChestSource, IInteractionObject, INBTDrop {

	public int code = WirelessChestHelper.getDefaultCode(EnumDyeColor.WHITE);
	private UUID boundToPlayer;
	private WirelessInventory inventory;
	//Chest Feilds
	public float prevLidAngle;
    public float lidAngle;
    public boolean open;
    
    public TileEntityWirelessChest() {
		super(ModBlocks.TILE_WIRELESS_CHEST);
	}
	
	@Override
	public void tick(){
		super.tick();
		// Resynchronize clients with the server state
        if (getWorld() != null && !getWorld().isRemote)
        {
        	//First Init
            boolean newOpen = open;
            WirelessInventory inventory = getInventory();
            if(inventory !=null){
            	newOpen = inventory.playerUsingCount > 0;
            }
            if(newOpen !=open){
            	open = newOpen;
            	getWorld().addBlockEvent(pos, ModBlocks.wirelessChest, 1, open ? 1 : 0);
            }
        }

        prevLidAngle = lidAngle;
        float f = 0.1F;
        if (open && lidAngle == 0.0F)
        {
            double d = pos.getX() + 0.5D;
            double d1 = pos.getZ() + 0.5D;
            getWorld().playSound(null, d, pos.getY() + 0.5D, d1, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, getWorld().rand.nextFloat() * 0.1F + 0.9F);
        }
        if (!open && lidAngle > 0.0F || open && lidAngle < 1.0F)
        {
            float f1 = lidAngle;
            if (open)
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
                double d2 = pos.getX() + 0.5D;
                double d3 = pos.getZ() + 0.5D;
                getWorld().playSound(null, d2, pos.getY() + 0.5D, d3, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, getWorld().rand.nextFloat() * 0.1F + 0.9F);
            }
            if (lidAngle < 0.0F)
            {
                lidAngle = 0.0F;
            }
        }
	}

    @Override
    public boolean receiveClientEvent(int i, int j)
    {
        if (i == 1)
        {
            open = (j == 1);
        } 
        return true;
    }
    
	@Override
	public void writeToItemNBT(ItemStack stack) {
		if(isBoundToPlayer()){
			ItemNBTHelper.setUUID(stack, WirelessChestHelper.NBT_OWNER, getOwner());
		}
		ItemNBTHelper.setInteger(stack, WirelessChestHelper.NBT_CODE, code);
	}

	@Override
	public void readFromItemNBT(ItemStack stack) {
		setCode(ItemNBTHelper.getInteger(stack, WirelessChestHelper.NBT_CODE, 0));
    	UUID owner = ItemNBTHelper.getUUID(stack, WirelessChestHelper.NBT_OWNER, null);
    	if(owner !=null){
    		bindToPlayer(owner);
    	}
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInt("Code", code);
		if(boundToPlayer !=null){
			nbt.setUniqueId("OwnerUUID", boundToPlayer);
		}
	}
	
    @Override
	public void readCustomNBT(NBTTagCompound nbt){
    	super.readCustomNBT(nbt);
    	if(nbt.hasKey("Code"))this.code = nbt.getInt("Code");
    	if(nbt.hasUniqueId("OwnerUUID")){
    		boundToPlayer = nbt.getUniqueId("OwnerUUID");
    	} else {
    		boundToPlayer = null;
    	}
        releasePreviousInventory();
	}
	
	public void setCode(int code) {
		this.code = code;
		releasePreviousInventory();
        markDirty();

        BlockUtil.markBlockForUpdate(getWorld(), getPos());
	}

    public void bindToPlayer(UUID boundToPlayer)
    {
        this.boundToPlayer = boundToPlayer;

        releasePreviousInventory();
        markDirty();

        /*IBlockState state = getWorld().getBlockState(pos);
        getWorld().notifyBlockUpdate(pos, state, state, 3);*/
    }

    public boolean isBoundToPlayer()
    {
        return boundToPlayer != null;
    }
    
	public boolean isOwner(UUID uuid) {
		return !isBoundToPlayer() ? true : boundToPlayer.equals(uuid);
	}


    private void releasePreviousInventory()
    {
    	inventory = null;
    }
    
    public boolean hasValidCode(){
    	return code >= 0;
    }
    
    public WirelessInventory getInventory(){
    	if(!hasValidCode()){
    		return null;
    	}
    	if (inventory == null)
        {
            if (isBoundToPlayer())
            	inventory = WirelessChestManager.get(getWorld()).getPrivate(boundToPlayer).getInventory(code);
            else
            	inventory = WirelessChestManager.get(getWorld()).getInventory(code);
        }
    	return inventory;
    }

	
	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equalsIgnoreCase("UpdateInfo")){
			if(messageData.hasKey("Code")){
				this.code = messageData.getInt("Code");
			}
		}
		if(messageId.equalsIgnoreCase("MarkDirty")){
			BlockUtil.markBlockForUpdate(getWorld(), getPos());
		}
		if(messageId.equalsIgnoreCase("Owner")){
			this.boundToPlayer = messageData.getUniqueId("UUID");
		}
	}
	
	//CHEST
    
    @Override
    public boolean canRenderBreaking()
    {
        return true;
    }
    
    private final LazyOptional<IItemHandler> holder = LazyOptional.of(() -> getInventory());
    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side)
    {
        if (cap == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return holder.cast();
        return super.getCapability(cap, side);
    }
    
	@Override
	public int getCode() {
		return code;
	}
	
	@Override
	public boolean isPrivate() {
		return isBoundToPlayer();
	}

	@Override
	public UUID getOwner() {
		return boundToPlayer;
	}

	@Override
	public ITextComponent getName() {
		return new TextComponentString("TileEntityWirelessChest");
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getCustomName() {
		return null;
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerWirelessChest(playerInventory, this);
	}

	@Override
	public String getGuiID() {
		return GuiHandler.TILE_NORMAL.toString();
	}
}
