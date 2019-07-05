package alec_wam.CrystalMod.items;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.api.energy.CEnergyContainerWrapper;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemElectricBread extends Item {

	//TODO Make Configurable 
	public static final int ENERGY_PER_CONSUME = 10;
	public static final int ENERGY_CAPACITY = ENERGY_PER_CONSUME * 32;
	
	public ItemElectricBread(Properties builder) {
		super(builder);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		int energy = ItemNBTHelper.getInteger(stack, "Energy", 0);
    	NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
    	String energyString = format.format(energy);
    	String maxEnergyString = format.format(ENERGY_CAPACITY);
    	tooltip.add(new TranslationTextComponent("crystalmod.info.battery.energy", energyString, maxEnergyString));
    }
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
		if (entityLiving instanceof PlayerEntity) {
			PlayerEntity entityplayer = (PlayerEntity)entityLiving;
			entityplayer.getFoodStats().consume(stack.getItem(), stack);
			worldIn.playSound((PlayerEntity)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
			if (entityplayer instanceof ServerPlayerEntity) {
				CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity)entityplayer, stack);
			}
		}
		int energy = ItemNBTHelper.getInteger(stack, "Energy", 0);
		ItemNBTHelper.putInteger(stack, "Energy", energy - ENERGY_PER_CONSUME);
		if(energy - ENERGY_PER_CONSUME  < 0){
			ItemNBTHelper.putInteger(stack, "Energy", 0);
		}
		return stack;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		int energy = ItemNBTHelper.getInteger(itemstack, "Energy", 0);
		if (playerIn.canEat(false) && energy > ENERGY_PER_CONSUME) {
			playerIn.setActiveHand(handIn);
			return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
		} else {
			return new ActionResult<>(ActionResultType.FAIL, itemstack);
		}
	}

	@Override
	public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @Nullable net.minecraft.nbt.CompoundNBT nbt) {
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
