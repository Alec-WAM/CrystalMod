package alec_wam.CrystalMod.items.tools.backpack.block;

import java.util.UUID;

import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.machine.IFacingTile;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityBackpack extends TileEntityMod implements IFacingTile {

	protected ItemStack backpack;
	protected UUID ownerUUID;
	private int facing;
	
	public TileEntityBackpack(){
		super();
	}
	
	public void loadFromStack(ItemStack stack){
		this.backpack = stack;
		UUID owner = BackpackUtil.getOwner(stack);
		if(owner !=null){
			this.ownerUUID = owner;
		}
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setByte("Facing", (byte)facing);
		if(ownerUUID !=null){
			nbt.setTag("OwnerUUID", NBTUtil.createUUIDTag(ownerUUID));
		}
		if(ItemStackTools.isValid(backpack)){
			nbt.setTag("Backpack", backpack.serializeNBT());
		}
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		this.facing = nbt.getByte("Facing");
		if(nbt.hasKey("OwnerUUID")){
			ownerUUID = NBTUtil.getUUIDFromTag(nbt.getCompoundTag("OwnerUUID"));
		}
		if(nbt.hasKey("Backpack")){
			backpack = ItemStackTools.loadFromNBT(nbt.getCompoundTag("Backpack"));
		}
	}
	
	public void setBackPack(ItemStack stack){
		this.backpack = stack;
	}
	
	public ItemStack getBackpack(){
		return backpack;
	}
	
	public ItemStack getDroppedBackpack() {
		return getBackpack();
	}

	public UUID getOwner() {
		return ownerUUID;
	}

	public void setOwner(UUID owner) {
		this.ownerUUID = owner;
	}

	public int getFacing() {
		return facing;
	}

	public void setFacing(int facing) {
		this.facing = facing;
	}
	
	@SideOnly(Side.CLIENT)
	public abstract Object getClientGuiElement(EntityPlayer player, World world);
	
	public abstract Object getServerGuiElement(EntityPlayer player, World world);
	
}
