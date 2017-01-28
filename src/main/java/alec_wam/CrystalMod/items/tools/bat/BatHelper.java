package alec_wam.CrystalMod.items.tools.bat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.ItemStackList;
import alec_wam.CrystalMod.api.tools.AttackData;
import alec_wam.CrystalMod.api.tools.IBatType;
import alec_wam.CrystalMod.api.tools.IBatUpgrade;
import alec_wam.CrystalMod.api.tools.UpgradeData;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BatHelper {
	
	private static final List<IBatUpgrade> UPGRADE_REGISTRY = new ArrayList<IBatUpgrade>();
	private static final List<IBatType> TYPE_REGISTRY = Lists.newArrayList();
	
	private static final List<IBatUpgrade> CREATIVELIST_UPGRADES = new ArrayList<IBatUpgrade>();
	
	public static IBatType registerBatType(IBatType type){
		for(IBatType list : TYPE_REGISTRY){
			if(list.getID().equals(type.getID())){
				ModLogger.warning("Bat Type "+ type.getID() +" is already registered!");
				return null;
			}
		}
		TYPE_REGISTRY.add(type);
		return type;
	}
	
	public static IBatType getBat(ResourceLocation res){
		for(IBatType type : TYPE_REGISTRY){
			if(type.getID().equals(res)){
				return type;
			}
		}
		return errorType;
	}
	
	public static IBatType errorType = new BatType(new ResourceLocation(CrystalMod.resource("error")), 0, 0) {

		@SideOnly(Side.CLIENT)
		@Override
		public TextureAtlasSprite getBatTexture() {
			return RenderUtil.getMissingSprite();
		}

		@Override
		public void addCraftingRecipe() {
		}
		
	};
	
	public static IBatUpgrade registerBatUpgrade(IBatUpgrade upgrade){
		if(getUpgrade(upgrade.getID()) == null){
			UPGRADE_REGISTRY.add(upgrade);
			if(upgrade.getCreativeListData() !=null){
				CREATIVELIST_UPGRADES.add(upgrade);
			}
			return upgrade;
		} else {
			ModLogger.warning("Bat Upgrade "+ upgrade.getID() +" is already registered!");
		}
		return null;
	}
	
	public static IBatUpgrade getUpgrade(ResourceLocation res){
		for(IBatUpgrade upgrade : UPGRADE_REGISTRY){
			if(upgrade.getID().equals(res))return upgrade;
		}
		return null;
	}
	
	public static void addBatCrafting(){
		for(IBatType type : TYPE_REGISTRY){
			type.addCraftingRecipe();
		}
	}
	
	public static ItemStack getBasicBat(Item item, IBatType type){
		ItemStack bat = new ItemStack(item);
		NBTTagCompound batData = getBatData(bat);
		batData.setString("BatType", type.getID().toString());
		batData.setInteger("Damage", 0);
		batData.setInteger("TotalDurability", type.getMaxDamage());
		batData.setBoolean("Broken", false);
		ItemNBTHelper.getCompound(bat).setTag("BatData", batData);
		return bat;
	}
	
	public static List<ItemStack> getCreativeListBats(Item item){
		List<ItemStack> bats = Lists.newArrayList();
		for(IBatType type : TYPE_REGISTRY){
			ItemStack bat = getBasicBat(item, type);
			bats.add(bat);
			ItemStack allBat = ItemStack.copyItemStack(bat);
			//Seperate Upgrades 
			for(IBatUpgrade upgrade : CREATIVELIST_UPGRADES){
				ItemStack copyBat = ItemStack.copyItemStack(bat);
				UpgradeData data = upgrade.getCreativeListData();
				if(data !=null){
					setBatUpgrade(copyBat, data);
					bats.add(copyBat);
					if(upgrade.canBeAdded(allBat, Lists.newArrayList(getBatUpgrades(allBat).keySet()), data))
					setBatUpgrade(allBat, data);
				}
			}
			bats.add(allBat);
		}
		return bats;
	}
	
	@SideOnly(Side.CLIENT)
	public static List<String> getInformation(ItemStack stack, EntityPlayer player, boolean detailed){
		List<String> list = Lists.newArrayList();
		IBatType type = getBat(stack);
		ResourceLocation id = type.getID();
		list.add(Lang.translateToLocal("batType."+(id.getResourceDomain()+"."+id.getResourcePath())+".name"));
		
		if(isBroken(stack)){
			list.add("Broken");
		}
		
		Map<IBatUpgrade, UpgradeData> upgrades = getBatUpgrades(stack);
		for(Entry<IBatUpgrade, UpgradeData> entry : upgrades.entrySet()){
			entry.getKey().addInfo(list, player, stack, entry.getValue(), detailed, -1);
		}
		
		boolean shift = GuiScreen.isShiftKeyDown(), ctrl = GuiScreen.isCtrlKeyDown();
		
		if(!shift && !upgrades.isEmpty()){
        	String info = TextFormatting.YELLOW +""+ TextFormatting.ITALIC + "Shift"+TextFormatting.GRAY;
        	list.add("<Hold "+info+" for Upgrades>");
        }
		
		if(shift){
			for(Entry<IBatUpgrade, UpgradeData> entry : upgrades.entrySet()){
				entry.getKey().addInfo(list, player, stack, entry.getValue(), detailed, 0);
			}
		}
		
		if(shift && ctrl){
    		String str = Strings.repeat("~", ((String)list.get(list.size()-1)).length());
    		list.add(str);
    	}
		
		if(!ctrl && !upgrades.isEmpty()){
        	String info = TextFormatting.DARK_AQUA +""+ TextFormatting.ITALIC + "Ctrl"+TextFormatting.GRAY;
        	list.add("<Hold "+info+" for Upgrade Level>");
        }
		
		if(ctrl){
			for(Entry<IBatUpgrade, UpgradeData> entry : upgrades.entrySet()){
				entry.getKey().addInfo(list, player, stack, entry.getValue(), detailed, 1);
			}
		}
		
		boolean broken = isBroken(stack);
		
		if(getBatData(stack).hasKey("Damage")){
        	int damage = getBatData(stack).getInteger("Damage");
        	int max = getBatData(stack).getInteger("TotalDurability");
        	if((max-damage) < max && !broken)list.add("Damage: "+(max-damage)+" / "+max);
        }
		
		return list;
	}
	
	public static NBTTagCompound getBatData(ItemStack stack){
		if(ItemStackTools.isNullStack(stack) || !stack.hasTagCompound())return new NBTTagCompound();
    	NBTTagCompound batTags = stack.getTagCompound().getCompoundTag("BatData");
        return batTags;
	}
	
	public static IBatType getBat(ItemStack stack){
		NBTTagCompound batData = getBatData(stack);
		if(batData.hasKey("BatType")){
			ResourceLocation id = new ResourceLocation(batData.getString("BatType"));
			return getBat(id);
		}
		return errorType;
	}
	
	public static void setBroken(ItemStack stack, boolean broken){
		getBatData(stack).setBoolean("Broken", broken);
	}
	
	public static boolean isBroken(ItemStack stack){
		NBTTagCompound nbt = getBatData(stack);
		return nbt.hasKey("Broken") ? nbt.getBoolean("Broken") : false;
	}
	
	//For Bat Item Set NBT
	public static void setBatUpgrade(ItemStack bat, UpgradeData data){
		List<UpgradeData> upgrades = getBatUpgradeData(bat);
		setBatUpgrade(upgrades, data);
		setBatUpgradeData(bat, upgrades);
	}
	
	//For Upgrade List Checks if one already exists
	public static void setBatUpgrade(List<UpgradeData> upgrades, UpgradeData data){
		UpgradeData storedData = getUpgradeData(upgrades, data);
		if(storedData !=null){
			storedData.setAmount(data.getAmount());
		} else {
			upgrades.add(data);
		}
	}
	
	public static UpgradeData getUpgradeData(List<UpgradeData> upgrades, UpgradeData data){
		for(UpgradeData upgrade : upgrades){
			if(upgrade.getUpgradeID().equals(data.getUpgradeID())){
				return upgrade;
			}
		}
		return null;
	}
	
	public static List<UpgradeData> getBatUpgradeData(ItemStack bat){
		List<UpgradeData> upgrades = Lists.newArrayList();
		NBTTagCompound nbt = getBatData(bat);
		if(nbt.hasKey("UpgradeList")){
			NBTTagList nbtList = nbt.getTagList("UpgradeList", Constants.NBT.TAG_COMPOUND);
			for(int t = 0; t < nbtList.tagCount(); t++){
				NBTTagCompound upgradeData = nbtList.getCompoundTagAt(t);
				UpgradeData data = new UpgradeData(upgradeData);
				if(data.isValid()){
					upgrades.add(data);
				}
			}
		}
		return upgrades;
	}
	
	public static void setBatUpgradeData(ItemStack bat, List<UpgradeData> upgrades){
		NBTTagList nbtList = new NBTTagList();
		for(UpgradeData data : upgrades){
			nbtList.appendTag(data.serializeNBT());
		}
		NBTTagCompound nbt = getBatData(bat);
		nbt.setTag("UpgradeList", nbtList);
		ItemNBTHelper.getCompound(bat).setTag("BatData", nbt);
	}
	
	public static String getRomanString(UpgradeData data)
    {
        String ret = "";
        int level = 0;
        
        IBatUpgrade upgrade = BatHelper.getUpgrade(data.getUpgradeID());
        
        float value = upgrade.getValue(data);
        
        level = (int)value;
        
        if(upgrade.getMaxLevel() == 1)return "";
        
        switch (level)
        {
        case 1:
            ret += "I";
            break;
        case 2:
            ret += "II";
            break;
        case 3:
            ret += "III";
            break;
        case 4:
            ret += "IV";
            break;
        case 5:
            ret += "V";
            break;
        case 6:
            ret += "VI";
            break;
        case 7:
            ret += "VII";
            break;
        case 8:
            ret += "VIII";
            break;
        case 9:
            ret += "IX";
            break;
        case 10:
            ret += "X";
            break;
        default:
            ret += "";
            break;
        }
        return ret;
    }
	
	public static Map<IBatUpgrade, UpgradeData> getBatUpgrades(ItemStack bat){
		Map<IBatUpgrade, UpgradeData> upgrades = Maps.newHashMap();
		for(UpgradeData data : getBatUpgradeData(bat)){
			if(!data.isValid())continue;
			IBatUpgrade upgrade = getUpgrade(data.getUpgradeID());
			if(upgrade !=null){
				upgrades.put(upgrade, data);
			}
		}
		return upgrades;
	}
	
	public static UpgradeData getBatUpgradeData(ItemStack bat, IBatUpgrade upgrade){
		if(ItemStackTools.isNullStack(bat) || upgrade == null) return null;
		List<UpgradeData> upgrades = getBatUpgradeData(bat);
		for(UpgradeData up : upgrades){
			if(up.isValid() && up.getUpgradeID().equals(upgrade.getID())){
				return up;
			}
		}
		return null;
	}
	
	public static void onBatUpdate (ItemStack stack, World world, Entity entity, int slot, boolean equipped)
    {
    	if(stack == null)return;
    	if(entity instanceof EntityPlayer){
    		EntityPlayer player = (EntityPlayer)entity;
    		EnumHand hand = equipped ? EnumHand.MAIN_HAND : (player.getHeldItemOffhand() == stack) ? EnumHand.OFF_HAND : null;
    		
    		Map<IBatUpgrade, UpgradeData> upgrades = getBatUpgrades(stack);
    		for(Entry<IBatUpgrade, UpgradeData> entry : upgrades.entrySet()){
    			entry.getKey().update((EntityPlayer)entity, stack, entry.getValue(), slot, hand);
    		}
    	}
    }
	public static boolean onLeftClickEntity(ItemStack stack, EntityLivingBase attacker, Entity entity) {
		if(entity == null || !entity.canBeAttackedWithItem() || entity.hitByEntity(attacker) || !stack.hasTagCompound())return false;
		if(isBroken(stack)) return false;
		
		EntityPlayer player = null;
	    if(attacker instanceof EntityPlayer) {
	      player = (EntityPlayer) attacker;
	    }
		
		EntityLivingBase target = null;
	    if(entity instanceof EntityLivingBase) {
	      target = (EntityLivingBase) entity;
	    }
		
		float baseDamage = (float) attacker.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        float baseKnockBack = attacker.isSprinting() ? 1 : 0;
        boolean isCritical = attacker.fallDistance > 0.0F && !attacker.onGround && !attacker.isOnLadder() && !attacker.isInWater() && !attacker.isPotionActive(MobEffects.BLINDNESS) && !attacker.isRiding();

        AttackData data = new AttackData(baseDamage);
        boolean debug = false;
        float damage = data.baseDamage;
        
        Map<IBatUpgrade, UpgradeData> upgrades = getBatUpgrades(stack);
		for(Entry<IBatUpgrade, UpgradeData> entry : upgrades.entrySet()){
			entry.getKey().addAttackData(player, entity, stack, data, entry.getValue());
		}
		
		boolean doDamage = data.cancelDamage == false;
		
        float earlyUpgradeDamage = data.earlyAttackDamage;
        damage += earlyUpgradeDamage;
        if(isCritical){
        	damage*=1.5f;
        }

        if(debug && player !=null && earlyUpgradeDamage > 0){
        	if(!player.worldObj.isRemote)
        	ChatUtil.sendChat(player, "Sharpness: "+earlyUpgradeDamage+" / "+damage+" ("+earlyUpgradeDamage/2+" Hearts)");
        }
        
        float knockback = baseKnockBack;        
        knockback += data.knockback;

        
        float oldHP = 0;

        double oldVelX = entity.motionX;
        double oldVelY = entity.motionY;
        double oldVelZ = entity.motionZ;

        if(target != null) {
          oldHP = target.getHealth();
        }

        // apply cooldown damage decrease
        if(player != null) {
          float cooldown = player.getCooledAttackStrength(0.5F);
          damage *= (0.2F + cooldown * cooldown * 0.8F);
        }
        
        DamageSource attackSource = null;
        
        boolean hit = false;
        
        if(doDamage){
	        if(player !=null){
	        	attackSource = DamageSource.causePlayerDamage(player);
	        } else {
	        	attackSource = DamageSource.causeMobDamage(attacker);
	        }
        } else {
        	hit = true;
        }
        
        if(attackSource !=null)hit = entity.attackEntityFrom(attackSource, damage);
        
        if(hit && target != null) {
        	// actual damage dealt
        	float damageDealt = oldHP - target.getHealth();

            // apply knockback
            if(knockback > 0f) {
              double velX = -MathHelper.sin(attacker.rotationYaw * (float) Math.PI / 180.0F) * knockback * 0.5F;
              double velZ = MathHelper.cos(attacker.rotationYaw * (float) Math.PI / 180.0F) * knockback * 0.5F;
              entity.addVelocity(velX, 0.1d, velZ);

              // slow down player
              attacker.motionX *= 0.6f;
              attacker.motionZ *= 0.6f;
              attacker.setSprinting(false);
            }

            if(entity instanceof EntityPlayerMP && entity.velocityChanged) {
            	if(player !=null && player instanceof EntityPlayerMP && ((EntityPlayerMP) player).connection != null) {
        	      ((EntityPlayerMP) player).connection.sendPacket(new SPacketEntityVelocity(entity));
        	    }
            	entity.velocityChanged = false;
            	entity.motionX = oldVelX;
            	entity.motionY = oldVelY;
            	entity.motionZ = oldVelZ;
            }
            
            if(player != null && doDamage) {
                // vanilla critical callback
                if(isCritical) {
                  player.onCriticalHit(target);
                }

                // "magical" critical damage? (aka caused by modifiers)
                if(damage > baseDamage) {
                  // this usually only displays some particles :)
                  player.onEnchantmentCritical(entity);
                }

                // vanilla achievement support :D
                if(damage >= 18f) {
                  player.addStat(AchievementList.OVERKILL);
                }
                
                if(debug)ChatUtil.sendChat(player, "Final Damage: "+damage+" / "+(baseDamage)+" ("+damage/2+" Hearts)");
            }
            
            if(doDamage)attacker.setLastAttacker(target);
            
            List<EntityLivingBase> attackedEntities = Lists.newArrayList();
            attackedEntities.add(target);
            double range = data.rangeBoost;
        	if(range > 0){
                AxisAlignedBB bb = new AxisAlignedBB(entity.posX, entity.posY, entity.posZ, entity.posX+1, entity.posY+1, entity.posZ+1).expand(range, 1.0D, range);
            	List<EntityLivingBase> list = attacker.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, bb);                    
                list.remove(entity);
                list.remove(attacker);
                if(attacker.getRidingEntity() !=null){
                	list.remove(attacker.getRidingEntity());
                }
                
                List<EntityLivingBase> attacked = EntityUtil.attackEntitiesInArea(attacker.worldObj, list, attackSource, doDamage ? damage : 0.0f, (entity instanceof IMob));
                attackedEntities.addAll(attacked);
        	}
            
        	for(Entry<IBatUpgrade, UpgradeData> entry : upgrades.entrySet()){
    			entry.getKey().afterAttack(attacker, attackedEntities, damageDealt, stack, data, entry.getValue());
    		}
        	
            if(player !=null){
            	 
            	 if(doDamage){
            		 stack.hitEntity(target, player);
            	 }
                 if(!player.capabilities.isCreativeMode) {
                     damageTool(stack, attackedEntities.size(), player);
                 }

                 player.addStat(StatList.DAMAGE_DEALT, Math.round(damageDealt * 10f));
                 player.addExhaustion(0.3f);

                 if(player.worldObj instanceof WorldServer && damageDealt > 2f) {
                   int k = (int) (damageDealt * 0.5);
                   ((WorldServer) player.worldObj).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, target.posX, target.posY + (double) (target.height * 0.5F), target.posZ, k, 0.1D, 0.0D, 0.1D, 0.2D);
                 }

                 //if(applyCooldown) {
                   player.resetCooldown();
                 //}
            }
        }
        return true;
	}
	
	private static boolean handleAttackWithoutDamage(ItemStack stack, EntityLivingBase attacker, EntityLivingBase target, AttackData data){
		Map<IBatUpgrade, UpgradeData> upgrades = getBatUpgrades(stack);
		EntityPlayer player = null;
		if(attacker instanceof EntityPlayer){
			player = (EntityPlayer)attacker;
		}
		if(target != null) {
			
			double oldVelX = target.motionX;
	        double oldVelY = target.motionY;
	        double oldVelZ = target.motionZ;
			
	        float baseKnockBack = attacker.isSprinting() ? 1 : 0;
	        float knockback = baseKnockBack+data.knockback;
        	// apply knockback
            if(knockback > 0f) {
              double velX = -MathHelper.sin(attacker.rotationYaw * (float) Math.PI / 180.0F) * knockback * 0.5F;
              double velZ = MathHelper.cos(attacker.rotationYaw * (float) Math.PI / 180.0F) * knockback * 0.5F;
              target.addVelocity(velX, 0.1d, velZ);

              // slow down player
              attacker.motionX *= 0.6f;
              attacker.motionZ *= 0.6f;
              attacker.setSprinting(false);
            }

            if(target instanceof EntityPlayerMP && target.velocityChanged) {
            	if(player !=null && player instanceof EntityPlayerMP && ((EntityPlayerMP) player).connection != null) {
        	      ((EntityPlayerMP) player).connection.sendPacket(new SPacketEntityVelocity(target));
        	    }
            	target.velocityChanged = false;
            	target.motionX = oldVelX;
            	target.motionY = oldVelY;
            	target.motionZ = oldVelZ;
            }
            List<EntityLivingBase> attackedEntities = Lists.newArrayList();
            attackedEntities.add(target);
            double range = data.rangeBoost;
        	if(range > 0){
                AxisAlignedBB bb = new AxisAlignedBB(target.posX, target.posY, target.posZ, target.posX+1, target.posY+1, target.posZ+1).expand(range, 1.0D, range);
            	List<EntityLivingBase> list = attacker.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, bb);                    
                list.remove(target);
                list.remove(attacker);
                if(attacker.getRidingEntity() !=null)list.remove(attacker.getRidingEntity());
                
                List<EntityLivingBase> attacked = EntityUtil.attackEntitiesInArea(attacker.worldObj, list, null, 0.0f, (target instanceof IMob));
                attackedEntities.addAll(attacked);
        	}
            
        	for(Entry<IBatUpgrade, UpgradeData> entry : upgrades.entrySet()){
    			entry.getKey().afterAttack(attacker, attackedEntities, 0.0f, stack, data, entry.getValue());
    		}
        	
            if(player !=null){
            	 player.resetCooldown();
            }
        }
		return false;
	}
	
    public static void damageTool(ItemStack stack, int dam, EntityLivingBase entity)
    {
        if (entity !=null && entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode)
            return;

        boolean blockDamage = false;//getBatData(stack).hasKey("Star") && getBatData(stack).getInteger("Star") > 0;
            
        Map<IBatUpgrade, UpgradeData> upgrades = getBatUpgrades(stack);
		for(Entry<IBatUpgrade, UpgradeData> entry : upgrades.entrySet()){
			if(entry.getKey().blocksDamage(stack, entry.getValue())){
				blockDamage = true;
				break;
			}
		}
        
        if (blockDamage){
        	getBatData(stack).setInteger("Damage", 0);
        	getBatData(stack).setBoolean("Broken", false);
            return;
        }

        int damage = getBatData(stack).getInteger("Damage");
        int damageTrue = damage + dam;
        int maxDamage = getBatData(stack).getInteger("TotalDurability");
        if (damageTrue <= 0)
        {
        	getBatData(stack).setInteger("Damage", 0);
        	getBatData(stack).setBoolean("Broken", true);
        }

        else if (damageTrue > maxDamage)
        {
            breakTool(stack, entity);
        }

        else
        {
        	getBatData(stack).setInteger("Damage", damageTrue);
            int toolDamage = (damage * 100 / maxDamage) + 1;
            int stackDamage = stack.getItemDamage();
            if (toolDamage != stackDamage)
            {
                //stack.setItemDamage((damage * 100 / maxDamage) + 1);
            }
        }
    }
    
    public static void breakTool (ItemStack stack, Entity entity)
    {
    	getBatData(stack).setBoolean("Broken", true);
        if (entity != null){
        	entity.worldObj.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ITEM_BREAK, entity instanceof EntityPlayer ? SoundCategory.PLAYERS : SoundCategory.NEUTRAL, 1f, 1f, true);
        }
    }

    public static void repairTool (ItemStack stack)
    {
        getBatData(stack).setBoolean("Broken", false);
        getBatData(stack).setInteger("Damage", 0);
    }
    
    public static boolean isIngrediant(ItemStack stack){
    	for(IBatUpgrade upgrade : UPGRADE_REGISTRY){
			if(upgrade.getUpgradeValue(stack) > 0){
				return true;
			}
		}
    	return false;
    }

	public static List<IBatUpgrade> getUpgradesFromItems(ItemStack[] items) {
		List<IBatUpgrade> upgrades = Lists.newArrayList();
		ItemStackList list = new ItemStackList();
		for(ItemStack stack : items){
			if(ItemStackTools.isNullStack(stack))continue;
			list.add(stack);
		}
		for(ItemStack stack : list.getStacks()){
			for(IBatUpgrade upgrade : UPGRADE_REGISTRY){
				if(upgrade.getUpgradeValue(stack) > 0){
					upgrades.add(upgrade);
				}
			}
		}
		return upgrades;
	}

	public static String localizeName(IBatUpgrade batUpgrade) {
		ResourceLocation id = batUpgrade.getID();
		return Lang.translateToLocal("batUpgrade."+id.getResourceDomain()+"."+id.getResourcePath()+".name");
	}
	
}
