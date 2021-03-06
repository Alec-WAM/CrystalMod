package alec_wam.CrystalMod.tiles.cauldron;

import java.util.List;

import alec_wam.CrystalMod.fluids.ModFluids;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.cauldron.CauldronRecipeManager.InfusionRecipe;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityCrystalCauldron extends TileEntityMod {

	public FluidStack crystalStack;
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		if(crystalStack !=null)nbt.setTag("CrystalStack", crystalStack.writeToNBT(new NBTTagCompound()));
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		crystalStack = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("CrystalStack"));
	}
	
	@Override
	public void update(){
		super.update();
		List<EntityItem> items = getWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(getPos().getX(), getPos().getY(), getPos().getZ(), getPos().getX()+1f, getPos().getY()+1f, getPos().getZ()+1f));
		for(EntityItem item : items){
			if(item !=null && ItemStackTools.isValid(item.getEntityItem())){
				ItemStack stack = item.getEntityItem();
				ItemStack spawn = ItemStackTools.getEmptyStack();
				int decAmt = 0;
				InfusionRecipe recipe = crystalStack == null ? null : CauldronRecipeManager.getRecipe(stack, crystalStack);
				if(recipe != null){
					spawn = recipe.getOutput();
					if(recipe.getFluidInput() !=null){
						decAmt = recipe.getFluidInput().amount;
					} else {
						decAmt = 1;
					}
				}
				if(ItemStackTools.isValid(spawn)){
					if(!getWorld().isRemote){
						ItemStackTools.incStackSize(item.getEntityItem(), -1);
						if(ItemStackTools.isEmpty(item.getEntityItem())){
							item.setDead();
						}
						EntityItem entItem = new EntityItem(getWorld(), getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5, spawn);
						entItem.motionX = entItem.motionY = entItem.motionZ = 0;
						entItem.motionY = 0.3D;
						entItem.setDefaultPickupDelay();
						getWorld().spawnEntity(entItem);
						if(crystalStack !=null){
							SoundEvent soundevent = crystalStack.getFluid().getEmptySound(crystalStack);
							getWorld().playSound(null, getPos(), soundevent, SoundCategory.BLOCKS, 1f, 1f);
							
							this.crystalStack.amount-=decAmt;
							if(this.crystalStack.amount <=0){
								crystalStack = null;
							}
						}
						BlockUtil.markBlockForUpdate(getWorld(), getPos());
					}
					break;
				}
				
				if(stack.getItem() == ModItems.crystals && (this.crystalStack == null || this.crystalStack.amount < Fluid.BUCKET_VOLUME)){
					
					FluidStack shardFluid = ModFluids.getCrystalFluid(stack);
					
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
							ItemStackTools.incStackSize(item.getEntityItem(), -1);
							if(ItemStackTools.isEmpty(item.getEntityItem()))item.setDead();
							SoundEvent soundevent = shardFluid.getFluid().getFillSound(shardFluid);
							getWorld().playSound(null, getPos(), soundevent, SoundCategory.BLOCKS, 1f, 1f);
							
							BlockUtil.markBlockForUpdate(getWorld(), getPos());
						}
					}
				}
			}
		}
	}
	
}
