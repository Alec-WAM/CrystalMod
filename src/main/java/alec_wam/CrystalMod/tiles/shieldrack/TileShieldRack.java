package alec_wam.CrystalMod.tiles.shieldrack;

import alec_wam.CrystalMod.tiles.TileEntityModStatic;
import alec_wam.CrystalMod.tiles.machine.IFacingTile;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class TileShieldRack extends TileEntityModStatic implements IFacingTile {

	private ItemStack leftStack = ItemStackTools.getEmptyStack();
	private ItemStack rightStack = ItemStackTools.getEmptyStack();
	private ItemStack shieldStack = ItemStackTools.getEmptyStack();
	private EnumFacing facing = EnumFacing.NORTH;
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		if(ItemStackTools.isValid(leftStack))nbt.setTag("LeftStack", leftStack.writeToNBT(new NBTTagCompound()));
		if(ItemStackTools.isValid(rightStack))nbt.setTag("RightStack", rightStack.writeToNBT(new NBTTagCompound()));
		if(ItemStackTools.isValid(shieldStack))nbt.setTag("ShieldStack", shieldStack.writeToNBT(new NBTTagCompound()));
		nbt.setInteger("Facing", getFacing());
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		if(nbt.hasKey("LeftStack")){
			leftStack = ItemStackTools.loadFromNBT(nbt.getCompoundTag("LeftStack"));
		} else {
			leftStack = ItemStackTools.getEmptyStack();
		}
		if(nbt.hasKey("RightStack")){
			rightStack = ItemStackTools.loadFromNBT(nbt.getCompoundTag("RightStack"));
		} else {
			rightStack = ItemStackTools.getEmptyStack();
		}
		if(nbt.hasKey("ShieldStack")){
			shieldStack = ItemStackTools.loadFromNBT(nbt.getCompoundTag("ShieldStack"));
		} else {
			shieldStack = ItemStackTools.getEmptyStack();
		}
		setFacing(nbt.getInteger("Facing"));
	}
	
	/**
	 * @return the leftStack
	 */
	public ItemStack getLeftStack() {
		return leftStack;
	}

	/**
	 * @param leftStack the leftStack to set
	 */
	public void setLeftStack(ItemStack leftStack) {
		this.leftStack = leftStack;
	}

	/**
	 * @return the rightStack
	 */
	public ItemStack getRightStack() {
		return rightStack;
	}

	/**
	 * @param rightStack the rightStack to set
	 */
	public void setRightStack(ItemStack rightStack) {
		this.rightStack = rightStack;
	}

	/**
	 * @return the shieldStack
	 */
	public ItemStack getShieldStack() {
		return shieldStack;
	}

	/**
	 * @param shieldStack the shieldStack to set
	 */
	public void setShieldStack(ItemStack shieldStack) {
		this.shieldStack = shieldStack;
	}

	@Override
	public void setFacing(int facing) {
		this.facing = EnumFacing.getHorizontal(facing);
	}
	
	@Override
	public int getFacing() {
		return facing.getHorizontalIndex();
	}
	
}
