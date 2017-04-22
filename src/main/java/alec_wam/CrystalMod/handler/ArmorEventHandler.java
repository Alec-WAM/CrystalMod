package alec_wam.CrystalMod.handler;

import java.lang.reflect.Method;
import java.util.Iterator;

import alec_wam.CrystalMod.crafting.recipes.UpgradeItemRecipe;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.armor.ItemCrystalArmor;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentThorns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;

public class ArmorEventHandler {

	public static boolean clearPotionError = false;
	
	@SubscribeEvent
	public void playerUpdate(PlayerTickEvent event){
		EntityPlayer player = event.player;
		if(event.side == Side.CLIENT){
			return;
		}
		if(event.phase != TickEvent.Phase.END)return;
		//BLUE
		int pureCount = getArmorCount(player, "pure");
		
		if(pureCount == 4){
			if(!clearPotionError){
				Iterator<PotionEffect> iterator = player.getActivePotionEffects().iterator();

				while (iterator.hasNext() && !clearPotionError)
				{
					PotionEffect effect = (PotionEffect)iterator.next();

					if(effect.getPotion().isBadEffect()){
						try {
							Method method = ReflectionHelper.findMethod(EntityLivingBase.class, "onFinishedPotionEffect", "func_70688_c", PotionEffect.class);
							if(method !=null){
								method.invoke(player, effect);
								iterator.remove();
							} else {
								clearPotionError = true;
							}
						} catch (Exception e) {
							e.printStackTrace();
							ModLogger.warning("An error occured when trying to clear your bad potions!");
							clearPotionError = true;
							break;
						} 
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void addMoreMobXP(LivingExperienceDropEvent event){
		if(event.getAttackingPlayer() == null)return;
		EntityPlayer player = event.getAttackingPlayer();
		if(getArmorCount(player, "green") == 4 || getArmorCount(player, "pure") == 4){
			final int oldXP = event.getDroppedExperience();
			float dev = (float)((float)EntityUtil.rand.nextInt(50) / (float)50);
			int add = (int) (oldXP * dev);
			ModLogger.info("Added Mob XP: "+add+" / "+dev+" "+oldXP);
			event.setDroppedExperience(add + oldXP);
		}
	}
	
	@SubscribeEvent
	public void addMoreBlockXP(BreakEvent event){
		EntityPlayer player = event.getPlayer();
		if(getArmorCount(player, "green") == 4 || getArmorCount(player, "pure") == 4){
			final int oldXP = event.getExpToDrop();
			
			if(oldXP <= 0){
				int add = MathHelper.getInt(EntityUtil.rand, 0, 2);
				//ModLogger.info("Added Special Block XP: "+add+" / "+0);
				event.setExpToDrop(add);
				return;
			}
			
			float dev = (float)((float)EntityUtil.rand.nextInt(50) / (float)50);
			int add = (int) (oldXP * dev);
			//ModLogger.info("Added Block XP: "+add+" / "+dev+" "+oldXP);
			event.setExpToDrop(add + oldXP);
		}
	}
	
	@SubscribeEvent
	public void addWaterBounds(GetCollisionBoxesEvent event){
		Entity entity = event.getEntity();
		if(entity == null || !(entity instanceof EntityLivingBase))return;
		EntityLivingBase living = (EntityLivingBase)entity;
		int blueCount = getArmorCount(living, "blue");
		int redCount = getArmorCount(living, "red");
		int pureCount = getArmorCount(living, "pure");
		
		if(blueCount == 4 || pureCount == 4 || (UpgradeItemRecipe.isWaterWalking(living.getItemStackFromSlot(EntityEquipmentSlot.FEET)))){
			World world = event.getWorld();
			BlockPos pos = new BlockPos(living).down();
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof BlockLiquid && living.posY > pos.getY() + 0.9 && !(world.getBlockState(pos.up()).getBlock().getMaterial(world.getBlockState(pos.up())) == Material.WATER))
			{
				if (!living.isSneaking() && living.fallDistance <= 4D)
				{
					if (state.getBlock().getMaterial(state) == Material.WATER)
					{
						AxisAlignedBB bb = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), (double) pos.getX() + 1, (double) pos.getY() + 1, (double) pos.getZ() + 1);
						if (event.getAabb().intersectsWith(bb))
						{
							event.getCollisionBoxesList().add(bb);
						}
					}
				}
			}
		}
		if(redCount == 4 || pureCount == 4){
			World world = event.getWorld();
			BlockPos pos = new BlockPos(living).down();
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof BlockLiquid && living.posY > pos.getY() + 0.9 && !(world.getBlockState(pos.up()).getBlock().getMaterial(world.getBlockState(pos.up())) == Material.LAVA))
			{
				if (state.getBlock().getMaterial(state) == Material.LAVA && living.fallDistance <= 4D)
				{
					AxisAlignedBB bb = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), (double) pos.getX() + 1, (double) pos.getY() + 1, (double) pos.getZ() + 1);
					if (event.getAabb().intersectsWith(bb))
					{
						event.getCollisionBoxesList().add(bb);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerDamaged(LivingAttackEvent event){
		World world = event.getEntityLiving().getEntityWorld();
		EntityLivingBase attacked = event.getEntityLiving();
		DamageSource source = event.getSource();
		int pureCount = getArmorCount(attacked, "pure");
		if(getArmorCount(attacked, "red") == 4 || pureCount == 4){
			if(source == DamageSource.HOT_FLOOR){
				event.setCanceled(true);
			}
		}
		if(getArmorCount(attacked, "dark") == 4 || pureCount == 4){
			if(source.getSourceOfDamage() !=null){
				Entity toAttack = source.getSourceOfDamage();
				toAttack.attackEntityFrom(DamageSource.causeThornsDamage(attacked), (float)EnchantmentThorns.getDamage(5, EntityUtil.rand));
			}
		}
		
		/*if(source == DamageSource.FALL){
			BlockPos pos = new BlockPos(attacked).down();
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof BlockLiquid && attacked.posY > pos.getY() + 0.9 && !(world.getBlockState(pos.up()).getBlock().getMaterial(world.getBlockState(pos.up())) == Material.WATER))
			{
				if(UpgradeItemRecipe.isWaterWalking(attacked.getItemStackFromSlot(EntityEquipmentSlot.FEET))){
					world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_SPLASH, SoundCategory.PLAYERS, 1.0F, 1.0F);
					event.setCanceled(true);
				}
			}
		}*/
	}
	
	public static int getArmorCount(EntityLivingBase entity, String type){
		if(entity == null)return 0;
		ItemStack head = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		ItemStack chest = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		ItemStack legs = entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
		ItemStack feet = entity.getItemStackFromSlot(EntityEquipmentSlot.FEET);
		int count = 0;
		if(ItemStackTools.isValid(head) && head.getItem() == ModItems.crystalHelmet && ItemNBTHelper.getString(head, ItemCrystalArmor.NBT_COLOR, "").equalsIgnoreCase(type)){
			count++;
		}
		if(ItemStackTools.isValid(chest) && chest.getItem() == ModItems.crystalChestplate && ItemNBTHelper.getString(chest, ItemCrystalArmor.NBT_COLOR, "").equalsIgnoreCase(type)){
			count++;
		}
		if(ItemStackTools.isValid(legs) && legs.getItem() == ModItems.crystalLeggings && ItemNBTHelper.getString(legs, ItemCrystalArmor.NBT_COLOR, "").equalsIgnoreCase(type)){
			count++;
		}
		if(ItemStackTools.isValid(feet) && feet.getItem() == ModItems.crystalBoots && ItemNBTHelper.getString(feet, ItemCrystalArmor.NBT_COLOR, "").equalsIgnoreCase(type)){
			count++;
		}
		return count;
	}
	
}
