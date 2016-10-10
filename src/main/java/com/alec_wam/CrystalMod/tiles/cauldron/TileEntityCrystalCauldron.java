package com.alec_wam.CrystalMod.tiles.cauldron;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import com.alec_wam.CrystalMod.blocks.BlockCrystalIngot.CrystalIngotBlockType;
import com.alec_wam.CrystalMod.blocks.ModBlocks;
import com.alec_wam.CrystalMod.fluids.Fluids;
import com.alec_wam.CrystalMod.items.ItemCrystal.CrystalType;
import com.alec_wam.CrystalMod.items.ItemIngot.IngotType;
import com.alec_wam.CrystalMod.items.ModItems;
import com.alec_wam.CrystalMod.tiles.TileEntityMod;
import com.alec_wam.CrystalMod.tiles.cauldron.CauldronRecipeManager.InfusionRecipe;
import com.alec_wam.CrystalMod.tiles.cauldron.TileEntityCrystalCauldron.LiquidCrystalColor;
import com.alec_wam.CrystalMod.util.ItemUtil;

public class TileEntityCrystalCauldron extends TileEntityMod {

	public static enum LiquidCrystalColor{
		BLUE, RED, GREEN, DARK, PURE;
	}
	
	public FluidStack crystalStack;
	
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		if(crystalStack !=null)nbt.setTag("CrystalStack", crystalStack.writeToNBT(new NBTTagCompound()));
	}
	
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		crystalStack = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("CrystalStack"));
	}
	
	public void update(){
		super.update();
		
		List<EntityItem> items = getWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB((float)getPos().getX(), (float)getPos().getY(), (float)getPos().getZ(), (float)getPos().getX()+1f, (float)getPos().getY()+1f, (float)getPos().getZ()+1f));
		for(EntityItem item : items){
			if(item !=null && item.getEntityItem() !=null){
				ItemStack stack = item.getEntityItem();
				ItemStack spawn = null;
				int decAmt = 0;
				InfusionRecipe recipe = crystalStack == null ? null : CauldronRecipeManager.getRecipe(stack, crystalStack);
				if(recipe == null){
					//CONVERTING FREE RECIPES
					if(stack.getItem() == Items.ROTTEN_FLESH){
						spawn = new ItemStack(Items.LEATHER);
					}
					if(stack.getItem() == Item.getItemFromBlock(Blocks.SNOW)){
						spawn = new ItemStack(Blocks.ICE);
					}
				}else{
					spawn = recipe.getOutput();
					if(recipe.getFluidInput() !=null)decAmt = recipe.getFluidInput().amount;
				}
				if(spawn !=null){
					if(!getWorld().isRemote){
						item.getEntityItem().stackSize--;
						if(item.getEntityItem().stackSize <=0){
							item.setDead();
						}
						EntityItem entItem = new EntityItem(getWorld(), (double)(getPos().getX() + 0.5), (double)(getPos().getY() + 0.5), (double)(getPos().getZ() + 0.5), spawn);
						entItem.motionX = entItem.motionY = entItem.motionZ = 0;
						entItem.motionY = 0.3D;
						entItem.setDefaultPickupDelay();
						getWorld().spawnEntityInWorld(entItem);
						if(crystalStack !=null){
							this.crystalStack.amount-=decAmt;
							if(this.crystalStack.amount <=0){
								crystalStack = null;
							}
						}
						markDirty();
					}else getWorld().spawnParticle(EnumParticleTypes.REDSTONE, getPos().getX(), getPos().getY()+1.5d, getPos().getZ(), 0, 0, 0, new int[0]);
					break;
				}
				
				if(stack.getItem() == ModItems.crystals && (this.crystalStack == null || this.crystalStack.amount < Fluid.BUCKET_VOLUME)){
					
					FluidStack shardFluid = Fluids.getCrystalFluid(stack);
					
					if(shardFluid !=null && (this.crystalStack == null || this.crystalStack.isFluidEqual(shardFluid)) && !getWorld().isRemote){
						boolean pass = false;
						if(this.crystalStack == null){
							crystalStack = shardFluid;
							pass = true;
						}else {
							if(crystalStack.amount+shardFluid.amount <= Fluid.BUCKET_VOLUME){
								crystalStack.amount+=shardFluid.amount;
								pass = true;
							}
						}
						if(pass){
							item.getEntityItem().stackSize--;
							if(item.getEntityItem().stackSize <=0)
							item.setDead();
						}
					}
				}
			}
		}
	}
	
}
