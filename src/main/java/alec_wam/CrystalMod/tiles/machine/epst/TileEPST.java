package alec_wam.CrystalMod.tiles.machine.epst;

import java.util.Random;
import java.util.UUID;

import alec_wam.CrystalMod.tiles.TileEntityInventory;
import alec_wam.CrystalMod.tiles.machine.IFacingTile;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class TileEPST extends TileEntityInventory implements IFacingTile{
	public static final Random RNG = new Random();
	private EnumFacing facing = EnumFacing.NORTH;
	private boolean isTriggered;
	private UUID ownerUUID;
	public TileEPST() {
		super("EPST", 4);
	}	
	
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return ItemStackTools.isValid(stack) ? stack.getItem() == Items.ENDER_PEARL : true;
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("Facing", getFacing());
		nbt.setBoolean("Triggered", isTriggered);
		if(ownerUUID !=null)nbt.setTag("Owner", NBTUtil.createUUIDTag(ownerUUID));
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		setFacing(nbt.getInteger("Facing"));
		isTriggered = nbt.getBoolean("Triggered");
		if(nbt.hasKey("Owner"))ownerUUID = NBTUtil.getUUIDFromTag(nbt.getCompoundTag("Owner"));
		else ownerUUID = null;
		updateAfterLoad();
	}
	
	public void setOwner(UUID owner){
		this.ownerUUID = owner;
	}
	
	public UUID getOwner(){
		return ownerUUID;
	}
	
	@Override
	public void update(){
		super.update();
		if(!getWorld().isRemote){
			boolean powered = getWorld().isBlockPowered(pos) || getWorld().isBlockPowered(pos.up());
	        
			if(powered && !isTriggered){
				dispense();
				isTriggered = true;
			}
			if(!powered && isTriggered){
				isTriggered = false;
			}
		}
	}
	
	public void dispense(){
		EntityPlayer playerOwner = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(getOwner());
		if(playerOwner == null)return;
		
		double d0 = pos.getX() + 0.5 + (0.7D * (double)facing.getFrontOffsetX());
		double d1 = pos.getY() + 0.5 + (0.7D * (double)facing.getFrontOffsetY());
		double d2 = pos.getZ() + 0.5 + (0.7D * (double)facing.getFrontOffsetZ());

		if (facing.getAxis() == EnumFacing.Axis.Y)
		{
			d1 = d1 - 0.125D;
		}
		else
		{
			d1 = d1 - 0.15625D;
		}


		int slot = -1;
		int j = 1;

		for (int k = 0; k < getSizeInventory(); ++k)
		{
			if (!getStackInSlot(k).isEmpty() && RNG.nextInt(j++) == 0)
			{
				slot = k;
			}
		}

		if(slot > -1){
			ItemStack itemstack = getStackInSlot(slot);
			EntityEnderPearl entityenderpearl = new EntityEnderPearl(getWorld(), playerOwner);
			entityenderpearl.setPosition(d0, d1, d2);
			
			//entityenderpearl.setHeadingFromThrower(playerOwner, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
			float rotationYawIn = 0.0F;
			float rotationPitchIn = 0.0F;
			if(facing == EnumFacing.UP){
				rotationPitchIn = -90;
			}
			if(facing == EnumFacing.DOWN){
				rotationPitchIn = 90;
			}
			if(facing == EnumFacing.NORTH){
				rotationYawIn = 180;
			}
			if(facing == EnumFacing.WEST){
				rotationYawIn = 90.0F;
			}
			if(facing == EnumFacing.EAST){
				rotationYawIn = -90.0F;
			}
			float f = -MathHelper.sin(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
	        float f1 = -MathHelper.sin((rotationPitchIn) * 0.017453292F);
	        float f2 = MathHelper.cos(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
	        entityenderpearl.setThrowableHeading((double)f, (double)f1, (double)f2, 1.5F, 1.0F);
			
			getWorld().spawnEntity(entityenderpearl);
			setInventorySlotContents(slot, ItemUtil.consumeItem(itemstack));
			BlockUtil.markBlockForUpdate(getWorld(), getPos());
		}
	}
	
	@Override
	public void setFacing(int facing) {
		this.facing = EnumFacing.getFront(facing);
	}

	@Override
	public int getFacing() {
		return facing.getIndex();
	}
	
	@Override
	public boolean useVerticalFacing(){
		return true;
	}
}
