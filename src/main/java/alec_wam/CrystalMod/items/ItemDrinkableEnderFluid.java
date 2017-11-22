package alec_wam.CrystalMod.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.fluids.ModFluids;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.EntityUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

public class ItemDrinkableEnderFluid extends Item {

	public ItemDrinkableEnderFluid(){
		this.setCreativeTab(CrystalMod.tabItems);
		this.setMaxStackSize(16);
		ModItems.registerItem(this, "enderfluidbottle");
	}
	
	//Fluid Compat
	@Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new FluidHandlerItemStack.SwapEmpty(stack, new ItemStack(Items.GLASS_BOTTLE), 250){
        	@Override
        	public FluidStack getFluid()
            {
        		return new FluidStack(ModFluids.fluidEnder, 250);
            }
        	
        	@Override
        	public boolean canFillFluidType(FluidStack stack){
        		return false;
        	}
        	
        	@Override
            public FluidStack drain(int maxDrain, boolean doDrain)
            {
        		if(maxDrain < 250) return null;
        		return super.drain(maxDrain, doDrain);
            }
        };
    }
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
        EntityPlayer entityplayer = entityLiving instanceof EntityPlayer ? (EntityPlayer)entityLiving : null;

        if (entityplayer == null || !entityplayer.capabilities.isCreativeMode)
        {
            stack.shrink(1);
        }        
        
        int color = 0x063931;
    	double r = (double)(color >> 16 & 255) / 255.0D;
        double g = (double)(color >> 8 & 255) / 255.0D;
        double b = (double)(color >> 0 & 255) / 255.0D;
    	for(int i = 0; i < 50; i++){
    		double x = (entityplayer.posX) + ((worldIn.rand.nextDouble() - 0.5D) * 1.3);
    		double y = (entityplayer.posY + entityplayer.getEyeHeight() / 2) + ((worldIn.rand.nextDouble() - 0.5D) * 1.3);
    		double z = (entityplayer.posZ) + ((worldIn.rand.nextDouble() - 0.5D) * 1.3);
    		worldIn.spawnParticle(EnumParticleTypes.SPELL_MOB, x, y, z, r, g, b, new int[0]);
    	}
        
        if (!worldIn.isRemote)
        {
        	for(int i = 0; i < 16; i++){
        		boolean successful = EntityUtil.randomTeleport(entityplayer, 16);
        		if(successful){
        			break;
        		}
        	}
        }
        
        if (entityplayer != null)
        {
            entityplayer.addStat(StatList.getObjectUseStats(this));
        }

        if (entityplayer == null || !entityplayer.capabilities.isCreativeMode)
        {
            if (stack.isEmpty())
            {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (entityplayer != null)
            {
                entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        return stack;
    }

	@Override
	public int getMaxItemUseDuration(ItemStack stack)
    {
        return 32;
    }

    @Override
	public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.DRINK;
    }

    @Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        playerIn.setActiveHand(handIn);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }
	
}
