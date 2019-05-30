package alec_wam.CrystalMod.items;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.api.energy.CEnergyContainerWrapper;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemElectricBread extends ItemFood {

	//TODO Make Configurable 
	public static final int ENERGY_PER_CONSUME = 10;
	public static final int ENERGY_CAPACITY = ENERGY_PER_CONSUME * 32;
	
	public ItemElectricBread(Properties builder) {
		super(5, 0.6f, false, builder);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		int energy = ItemNBTHelper.getInteger(stack, "Energy", 0);
    	NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
    	String energyString = format.format(energy);
    	String maxEnergyString = format.format(ENERGY_CAPACITY);
    	tooltip.add(new TextComponentTranslation("crystalmod.info.battery.energy", energyString, maxEnergyString));
    }
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer)entityLiving;
			entityplayer.getFoodStats().addStats(this, stack);
			worldIn.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
			this.onFoodEaten(stack, worldIn, entityplayer);
			entityplayer.addStat(StatList.ITEM_USED.get(this));
			if (entityplayer instanceof EntityPlayerMP) {
				CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)entityplayer, stack);
			}
		}

		int energy = ItemNBTHelper.getInteger(stack, "Energy", 0);
		ItemNBTHelper.setInteger(stack, "Energy", energy - ENERGY_PER_CONSUME);
		if(energy - ENERGY_PER_CONSUME  < 0){
			ItemNBTHelper.setInteger(stack, "Energy", 0);
		}
		return stack;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		int energy = ItemNBTHelper.getInteger(itemstack, "Energy", 0);
		if (playerIn.canEat(false) && energy > ENERGY_PER_CONSUME) {
			playerIn.setActiveHand(handIn);
			return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
		} else {
			return new ActionResult<>(EnumActionResult.FAIL, itemstack);
		}
	}

	@Override
	public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @Nullable net.minecraft.nbt.NBTTagCompound nbt) {
		return new CEnergyContainerWrapper(stack, ENERGY_CAPACITY);
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack)
    {
		int energy = ItemNBTHelper.getInteger(stack, "Energy", 0);
        return energy < ENERGY_CAPACITY;
    }

    @Override
	public double getDurabilityForDisplay(ItemStack stack)
    {
		int energy = ItemNBTHelper.getInteger(stack, "Energy", 0);
        return (double) (ENERGY_CAPACITY - energy) / (double) ENERGY_CAPACITY;
    }

    @Override
	public int getRGBDurabilityForDisplay(ItemStack stack)
    {
        return 0x00ffff;
    }

}
