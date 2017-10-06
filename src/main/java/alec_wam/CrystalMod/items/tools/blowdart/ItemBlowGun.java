package alec_wam.CrystalMod.items.tools.blowdart;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlowGun extends Item implements ICustomModel {

	public ItemBlowGun(){
		super();
		setCreativeTab(CrystalMod.tabTools);
		setMaxStackSize(1);
		setMaxDamage(250);
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
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn)
    {
		ItemStack stack = player.getHeldItem(handIn);
		boolean creativeAmmo = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
        ItemStack ammo = handIn == EnumHand.MAIN_HAND ? player.getHeldItem(EnumHand.OFF_HAND) : player.getHeldItem(EnumHand.MAIN_HAND);
		if(creativeAmmo || ItemStackTools.isValid(ammo)){
			if(ItemStackTools.isEmpty(ammo)){
				ammo = new ItemStack(Items.ARROW);
			}
			float f = (float)(player.getAir()) / 300.0F;

			if ((double)f >= 0.1D)
			{
				boolean infiniteArrow = player.capabilities.isCreativeMode || (ammo.getItem() instanceof ItemArrow && ((ItemArrow) ammo.getItem()).isInfinite(ammo, stack, player));

				if (!worldIn.isRemote)
				{
					ItemArrow itemarrow = (ItemArrow)((ItemArrow)(ammo.getItem() instanceof ItemArrow ? ammo.getItem() : Items.ARROW));
					EntityArrow entityarrow = itemarrow.createArrow(worldIn, ammo, player);
					entityarrow.setAim(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 3.0F, 1.0F);

					if (f == 1.0F)
					{
						entityarrow.setIsCritical(true);
					}

					int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);

					if (j > 0)
					{
						entityarrow.setDamage(entityarrow.getDamage() + (double)j * 0.5D + 0.5D);
					}

					int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);

					if (k > 0)
					{
						entityarrow.setKnockbackStrength(k);
					}

					if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0)
					{
						entityarrow.setFire(100);
					}

					stack.damageItem(1, player);

					if (infiniteArrow || player.capabilities.isCreativeMode && (ammo.getItem() == Items.SPECTRAL_ARROW || ammo.getItem() == Items.TIPPED_ARROW))
					{
						entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
					}

					worldIn.spawnEntity(entityarrow);
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
				
		        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			}
		}
		
        return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
    }
}
