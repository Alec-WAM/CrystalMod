package alec_wam.CrystalMod.items.tools;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.api.energy.CEnergyContainerWrapper;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BannerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemPoweredShield extends Item {   
	//TODO Make Configurable 
	public static final int ENERGY_PER_CONSUME = 10;
	public static final int ENERGY_CAPACITY = ENERGY_PER_CONSUME * 250;
	
	public ItemPoweredShield(Item.Properties builder) {
      super(builder);
      this.addPropertyOverride(new ResourceLocation("blocking"), (p_210314_0_, p_210314_1_, p_210314_2_) -> {
         return p_210314_2_ != null && p_210314_2_.isHandActive() && p_210314_2_.getActiveItemStack() == p_210314_0_ ? 1.0F : 0.0F;
      });
      DispenserBlock.registerDispenseBehavior(this, ArmorItem.field_96605_cw);
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

   public static boolean hasCharge(ItemStack stack){
	   return ItemNBTHelper.getInteger(stack, "Energy", 0) > 0;
   }

   @Override
   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
	   BannerItem.appendHoverTextFromTileEntityTag(stack, tooltip);
	   int energy = ItemNBTHelper.getInteger(stack, "Energy", 0);
	   NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
	   String energyString = format.format(energy);
	   String maxEnergyString = format.format(ENERGY_CAPACITY);
	   tooltip.add(new TranslationTextComponent("crystalmod.info.battery.energy", energyString, maxEnergyString));
   }

   @Override   
   public UseAction getUseAction(ItemStack stack) {
	   if(!hasCharge(stack)){
		   return UseAction.NONE;
	   }
	   return UseAction.BLOCK;
   }

   @Override
   public int getUseDuration(ItemStack stack) {
	   if(!hasCharge(stack)){
		   return 0;
	   }
      return 72000;
   }

   @Override
   public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      if(hasCharge(itemstack)){
	      playerIn.setActiveHand(handIn);
	      return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
      } 
      return new ActionResult<>(ActionResultType.FAIL, itemstack);
   }

   @Override
   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return false;
   }
   
   @Override
   public boolean isShield(ItemStack stack, @Nullable LivingEntity entity)
   {
       return hasCharge(stack);
   }
   
   @Override
   public boolean canEquip(ItemStack stack, EquipmentSlotType armorType, Entity entity)
   {
       return armorType == EquipmentSlotType.OFFHAND;
   }
   
   @Override
   public int getMaxDamage(ItemStack stack)
   {
       return 1000;
   }
   
   @Override
   public boolean isDamaged(ItemStack stack)
   {
       return false;
   }
   
   @Override
   public boolean isDamageable() {
	   return true;
   }
   
   @Override
   public int getDamage(ItemStack stack)
   {
       return 0;
   }
   
   @Override
   public void setDamage(ItemStack stack, int damage)
   {
	  
   }
   
   @Override
   public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
	   int damage = ItemNBTHelper.getInteger(stack, "Damage", 0);
	   if(damage > 0){
		   //Convert Damage to Energy Drain
		   int energy = ItemNBTHelper.getInteger(stack, "Energy", 0);
		   int newAmount = energy - (ENERGY_PER_CONSUME * damage);
		   ItemNBTHelper.putInteger(stack, "Energy", newAmount);
		   if(newAmount  < 0){
			   ItemNBTHelper.putInteger(stack, "Energy", 0);
		   }
		   ItemNBTHelper.putInteger(stack, "Damage", 0);
	   }
   }
}