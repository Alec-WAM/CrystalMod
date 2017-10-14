package alec_wam.CrystalMod.items.tools.blowdart;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.blowdart.ItemDart.DartType;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlowGun extends Item implements ICustomModel {

	public ItemBlowGun(){
		super();
		setCreativeTab(CrystalMod.tabTools);
		setMaxStackSize(1);
		setMaxDamage(250);
		MinecraftForge.EVENT_BUS.register(this);
		ModItems.registerItem(this, "blowgun");
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void initModel() {
		ModItems.initBasicModel(this);
		ClientProxy.registerItemRenderCustom(getRegistryName().toString(), new ItemRenderBlowGun());
	}
	
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
    {
		return repair.getItem() == Item.getItemFromBlock(ModBlocks.bamboo) ? true : super.getIsRepairable(toRepair, repair);
    }
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack)
    {
        return 72000;
    }
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.NONE;
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn)
    {
		ItemStack stack = player.getHeldItem(handIn);
		boolean creativeAmmo = player.capabilities.isCreativeMode;
        ItemStack ammo = handIn == EnumHand.MAIN_HAND ? player.getHeldItem(EnumHand.OFF_HAND) : player.getHeldItem(EnumHand.MAIN_HAND);
		if(creativeAmmo || ItemStackTools.isValid(ammo)){
			player.setActiveHand(handIn);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
        return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
    }
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
    {
        if (entityLiving instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)entityLiving;
            boolean creativeAmmo = player.capabilities.isCreativeMode;
            ItemStack ammo = player.getActiveHand() == EnumHand.MAIN_HAND ? player.getHeldItem(EnumHand.OFF_HAND) : player.getHeldItem(EnumHand.MAIN_HAND);
    		if(creativeAmmo || ItemStackTools.isValid(ammo)){
    			if(ItemStackTools.isEmpty(ammo)){
    				ammo = new ItemStack(ModItems.dart, 1, DartType.BASIC.getMetadata());
    			}
    			int i = this.getMaxItemUseDuration(stack) - timeLeft;
    			if (i < 0) return;
    			
    			float f = ItemBow.getArrowVelocity(i) * (float)(player.getAir()) / 300.0F;

    			if ((double)f >= 0.1D)
    			{
    				boolean infiniteArrow = player.capabilities.isCreativeMode || (ammo.getItem() instanceof ItemDart && ((ItemDart) ammo.getItem()).isInfinite(ammo, stack, player));

    				if (!worldIn.isRemote)
    				{
    					ItemDart itemdart = (ItemDart)((ItemDart)(ammo.getItem() instanceof ItemDart ? ammo.getItem() : ModItems.dart));
    					EntityDart entitydart = itemdart.createArrow(worldIn, ammo, player);
    					entitydart.setAim(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 3.0F, 1.0F);

    					if (f == 1.0F)
    					{
    						entitydart.setIsCritical(true);
    					}

    					int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);

    					if (j > 0)
    					{
    						entitydart.setDamage(entitydart.getDamage() + (double)j * 0.5D + 0.5D);
    					}

    					int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);

    					if (k > 0)
    					{
    						entitydart.setKnockbackStrength(k);
    					}

    					if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0)
    					{
    						entitydart.setFire(100);
    					}

    					stack.damageItem(1, player);

    					if (infiniteArrow || player.capabilities.isCreativeMode && (ammo.getItem() == Items.SPECTRAL_ARROW || ammo.getItem() == Items.TIPPED_ARROW))
    					{
    						entitydart.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
    					}

    					worldIn.spawnEntity(entitydart);
    				}

    				worldIn.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

    				if (!infiniteArrow && !player.capabilities.isCreativeMode)
    				{
    					ItemStackTools.incStackSize(ammo, -1);

    					if (ItemStackTools.isEmpty(ammo))
    					{
    						player.inventory.deleteStack(ammo);
    					}
    				}

    				player.addStat(StatList.getObjectUseStats(this));
    			}
    		}
        }
    }
		
	
	@Override
	public int getItemEnchantability()
    {
        return 1;
    }
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onFovUpdateEvent(FOVUpdateEvent fovEvt) {
		ItemStack currentItem = fovEvt.getEntity().getActiveItemStack();
		if (ItemStackTools.isNullStack(currentItem) || currentItem.getItem() != this || fovEvt.getEntity().getItemInUseCount() <= 0) {
			return;
		}

		int drawDuration = getMaxItemUseDuration(currentItem) - fovEvt.getEntity().getItemInUseCount();
		float ratio = drawDuration / (float) 20.0F;

		if (ratio > 1.0F) {
			ratio = 1.0F;
		} else {
			ratio *= ratio;
		}
		fovEvt.setNewfov((1.0F - ratio * 0.35f));
	}
}
