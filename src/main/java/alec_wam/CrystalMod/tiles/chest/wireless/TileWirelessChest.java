package alec_wam.CrystalMod.tiles.chest.wireless;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.chest.wireless.WirelessChestManager.WirelessInventory;
import alec_wam.CrystalMod.tiles.pipes.CollidableComponent;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.PlayerUtil;
import alec_wam.CrystalMod.util.Vector3d;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;

public class TileWirelessChest extends TileEntityMod implements IMessageHandler {

	public int code = WirelessChestHelper.getDefaultCode(EnumDyeColor.WHITE);
	private UUID boundToPlayer;
	private WirelessInventory inventory;
	//Chest Feilds
	private byte facing;
	private int ticksSinceSync = -1;
    public float prevLidAngle;
    public float lidAngle;
    private boolean open;
    
	
	@Override
	public void update(){
		super.update();
		// Resynchronize clients with the server state
        if (getWorld() != null && !getWorld().isRemote)
        {
        	//First Init
            if(ticksSinceSync < 0){
            	dirtyCollison = true;
            	getWorld().addBlockEvent(pos, ModBlocks.wirelessChest, 3, (((open ? 1 : 0) << 3) & 0xF8) | (facing & 0x7));
            }
            
            boolean newOpen = open;
            WirelessInventory inventory = getInventory();
            if(inventory !=null){
            	newOpen = inventory.playerUsingCount > 0;
            }
            if(newOpen !=open){
            	open = newOpen;
            	dirtyCollison = true;
            	getWorld().addBlockEvent(pos, ModBlocks.wirelessChest, 1, open ? 1 : 0);
            }
        }

        this.ticksSinceSync++;
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
            dirtyCollison = true;
        } else if (i == 2)
        {
            facing = (byte) j;
            dirtyCollison = true;
        } else if (i == 3)
        {
            facing = (byte) (j & 0x7);
            open = ((j & 0xF8) >> 3) == 1;
            dirtyCollison = true;
        }
        return true;
    }
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("Code", code);
		nbt.setByte("Facing", facing);
		if(boundToPlayer !=null)PlayerUtil.uuidToNBT(nbt, boundToPlayer);
	}
	
    @Override
	public void readCustomNBT(NBTTagCompound nbt){
    	super.readCustomNBT(nbt);
    	facing = nbt.getByte("Facing");
    	if(nbt.hasKey("Code"))this.code = nbt.getInteger("Code");
    	boundToPlayer = PlayerUtil.uuidFromNBT(nbt);
        releasePreviousInventory();
	}
	
	public void setCode(int code) {
		this.code = code;
		releasePreviousInventory();
        markDirty();

        BlockUtil.markBlockForUpdate(getWorld(), getPos());
	}

	public UUID getPlayerBound()
    {
        return boundToPlayer;
    }

    public void bindToPlayer(UUID boundToPlayer)
    {
        this.boundToPlayer = boundToPlayer;

        releasePreviousInventory();
        markDirty();

        IBlockState state = getWorld().getBlockState(pos);
        getWorld().notifyBlockUpdate(pos, state, state, 3);
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
				this.code = messageData.getInteger("Code");
			}
		}
		if(messageId.equalsIgnoreCase("MarkDirty")){
			BlockUtil.markBlockForUpdate(getWorld(), getPos());
		}
	}
	
	//CHEST

    public void setFacing(byte facing2)
    {
        this.facing = facing2;
        dirtyCollison = true;
    }

    public int getFacing()
    {
        return this.facing;
    }

    public void rotateAround()
    {
        facing++;
        if (facing > EnumFacing.EAST.ordinal())
        {
            facing = (byte) EnumFacing.NORTH.ordinal();
        }
        setFacing(facing);
        getWorld().addBlockEvent(pos, ModBlocks.wirelessChest, 2, facing);
    }
    
    @Override
    public boolean canRenderBreaking()
    {
        return true;
    }
    
	@Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
		if(capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return getInventory() !=null;
		}
      return super.hasCapability(capability, facingIn);
    }
	
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing)
    {
        if (capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
        	if(getInventory() == null)return super.getCapability(capability, facing);
        	return (T) getInventory();
        }
        return super.getCapability(capability, facing);
    }

    private boolean dirtyCollison = true;
    private final List<CollidableComponent> cachedCollidables = new ArrayList<CollidableComponent>();
	public List<CollidableComponent> getCollidableComponents() {
		
		if(!dirtyCollison && !cachedCollidables.isEmpty()){
			return cachedCollidables;
		}
		dirtyCollison = false;
		cachedCollidables.clear();
		cachedCollidables.add(new CollidableComponent(new AxisAlignedBB(0.0625F, 0F, 0.0625F, 0.9375F, 0.875F, 0.9375F), null, 0));
		if(!open){
			for(int i = 0; i < 3; i++){
				DyeButton ebutton = buttons[i].copy();
	            ebutton.rotate(0, 0.5625, 0.0625, 1, 0, 0, 0);
	            ebutton.rotateMeta(facing+1);
	            Vector3d min = ebutton.getMin();
	            Vector3d max = ebutton.getMax();
	            cachedCollidables.add(new CollidableComponent(new AxisAlignedBB(min.x, min.y, min.z, max.x, max.y, max.z), null, i+1));
			}
		}
		return cachedCollidables;
	}
	
	public static DyeButton[] buttons;
    
    static
    {
        buttons = new DyeButton[3];
        for(int i = 0; i < 3; i++)
        {
            buttons[i] = new DyeButton(i);
        }
    }
}
