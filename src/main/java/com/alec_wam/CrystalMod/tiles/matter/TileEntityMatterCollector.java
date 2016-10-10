package com.alec_wam.CrystalMod.tiles.matter;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import com.alec_wam.CrystalMod.tiles.TileEntityMod;
import com.alec_wam.CrystalMod.tiles.matter.imps.Matter;

public class TileEntityMatterCollector extends TileEntityMod{

	public MatterStack stack;
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		if(stack !=null){
			nbt.setTag("matter", stack.writeToNBT(new NBTTagCompound()));
		}
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		if(nbt.hasKey("matter")){
			stack = MatterStack.loadMatterStackFromNBT(nbt.getCompoundTag("matter"));
		}
	}
	
	public void update(){
		super.update();
		if(getWorld().isRemote){
			return;
		}
		IBlockState aboveState = getWorld().getBlockState(getPos().offset(EnumFacing.UP));
		if(this.stack == null){
			if(!getWorld().isAirBlock(getPos().offset(EnumFacing.UP))){
				ItemStack blockStack = new ItemStack(aboveState.getBlock(), 1, aboveState.getBlock().getMetaFromState(aboveState));
				Matter matter = MatterRegistry.getMatter(blockStack);
				if(matter !=null){
					stack = MatterRegistry.getMatterStack(matter, blockStack).copy();
					getWorld().setBlockToAir(getPos().offset(EnumFacing.UP));
				}
			}
		}else{
			if(!getWorld().isAirBlock(getPos().offset(EnumFacing.UP))){
				ItemStack blockStack = new ItemStack(aboveState.getBlock(), 1, aboveState.getBlock().getMetaFromState(aboveState));
				Matter matter = MatterRegistry.getMatter(blockStack);
				if(matter !=null && matter.equals(stack.getMatter())){
					MatterStack in = MatterRegistry.getMatterStack(matter, blockStack);
					if(in !=null && in.getMeta() == stack.getMeta()){
						stack.amount+=in.amount;
						getWorld().setBlockToAir(getPos().offset(EnumFacing.UP));
					}
				}
			}
		}
	}
}
