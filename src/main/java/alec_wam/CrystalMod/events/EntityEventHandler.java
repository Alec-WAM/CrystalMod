package alec_wam.CrystalMod.events;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.ModConfig;
import alec_wam.CrystalMod.util.ProfileUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CrystalMod.MODID)
public class EntityEventHandler {

	@SubscribeEvent
    public static void addLivingDrops(LivingDropsEvent event)
    {
		addMobHeads(event);
		addAxeWitherHeads(event);
    }
	
	@SubscribeEvent
    public static void addPlayerDrops(LivingDeathEvent event)
    {
		addPlayerHead(event);
    }
	
	public static enum ItemDropType{
    	NONE, KILLED, ALL;
    }

    public static int fixLooting(int looting){
    	return looting == 0 ? 1 : looting;
    }
	
	public static void addMobHeads(LivingDropsEvent event){
		ItemDropType configDropType = ModConfig.Entities.getDropType(ModConfig.ENTITIES.Mob_Heads);
		if(configDropType == ItemDropType.NONE)return;
    	Entity source = event.getSource().getTrueSource();
        if(configDropType == ItemDropType.KILLED){
        	if(source == null)
	            return;
	        if(!(source instanceof PlayerEntity))
	            return;
        }
        
        Entity mob = event.getEntityLiving();
        int configChance = ModConfig.ENTITIES.Mob_Heads_Drop.get();
        if(configChance < 0)return;
        int chance = configChance / fixLooting(event.getLootingLevel());
        if(source !=null && source instanceof PlayerEntity){
        	PlayerEntity player = (PlayerEntity)source;
        	ItemStack heldItem = player.getHeldItemMainhand();
        	if(heldItem.getItem() instanceof AxeItem){
    			chance /= ModConfig.ENTITIES.Mob_Heads_Axe_Bonus.get();
    			int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, heldItem);
    			if(fortune > 0){
    				chance /= fortune + 1;
    			}
        	}
        }
        int rand = mob.getEntityWorld().rand.nextInt(Math.max(chance, 1));
        if(!mob.getEntityWorld().getGameRules().getBoolean(GameRules.DO_MOB_LOOT) || rand != 0)
            return;
        
        Item skullItem = null;
        
        if (mob instanceof SkeletonEntity) {
            skullItem = Items.SKELETON_SKULL;
        }
        else if (mob instanceof ZombieEntity) {
            skullItem = Items.ZOMBIE_HEAD;
        }
        else if (mob instanceof CreeperEntity) {
            skullItem = Items.CREEPER_HEAD;
        }
        
        // no skull found?
        if(skullItem == null)
            return;

        // drop it like it's hot
        ItemEntity entityitemSkull = new ItemEntity(mob.getEntityWorld(), mob.posX, mob.posY, mob.posZ, new ItemStack(skullItem, 1));
        entityitemSkull.setDefaultPickupDelay();
        event.getDrops().add(entityitemSkull);
    }
	
	public static void addAxeWitherHeads(LivingDropsEvent event){
		Entity mob = event.getEntityLiving();
		if(!(mob instanceof WitherSkeletonEntity))return;
		Entity source = event.getSource().getTrueSource();
		if(source == null || !(source instanceof PlayerEntity))
            return;
		
		PlayerEntity player = (PlayerEntity)source;
		ItemStack heldItem = player.getHeldItemMainhand();
    	if(!(heldItem.getItem() instanceof AxeItem)) return;
    	
        int configChance = ModConfig.ENTITIES.Wither_Heads_Axe_Bonus.get();
        if(configChance <= 0)return;
        
        int rand = mob.getEntityWorld().rand.nextInt(Math.max(configChance, 1));
        if(!mob.getEntityWorld().getGameRules().getBoolean(GameRules.DO_MOB_LOOT) || rand != 0)
            return;
        
        Item skullItem = Items.WITHER_SKELETON_SKULL;
        // drop it like it's hot
        ItemEntity entityitemSkull = new ItemEntity(mob.getEntityWorld(), mob.posX, mob.posY, mob.posZ, new ItemStack(skullItem, 1));
        entityitemSkull.setDefaultPickupDelay();
        event.getDrops().add(entityitemSkull);
    }
	
	public static void addPlayerHead(LivingDeathEvent event){
		if(!(event.getEntity() instanceof PlayerEntity))return;
		ItemDropType configDropType = ModConfig.Entities.getDropType(ModConfig.ENTITIES.Player_Heads);
    	if(configDropType == ItemDropType.NONE)return;
    	Entity source = event.getSource().getTrueSource();
        if(configDropType == ItemDropType.KILLED){
	        if(source == null)
	            return;
	        if(!(source instanceof PlayerEntity))
	            return;
        }

        PlayerEntity player = (PlayerEntity) event.getEntity();
        int configChance = ModConfig.ENTITIES.Player_Heads_Drop.get();
        if(configChance < 0)return;
        Entity attacker = event.getSource().getTrueSource();
        int looting = net.minecraftforge.common.ForgeHooks.getLootingLevel(player, attacker, event.getSource());
        int chance = configChance / fixLooting(looting);
        if(source !=null && source instanceof PlayerEntity){
        	PlayerEntity otherPlayer = (PlayerEntity)source;
        	if(otherPlayer.getHeldItem(Hand.MAIN_HAND).getItem() instanceof AxeItem){
        		chance /= ModConfig.ENTITIES.Player_Heads_Axe_Bonus.get();
    		}
        }
        int rand = player.getEntityWorld().rand.nextInt(Math.max(chance, 1));
        if(rand == 0){
	        // drop it like it's hot
	        ItemEntity entityitemSkull = new ItemEntity(player.getEntityWorld(), player.posX, player.posY, player.posZ, ProfileUtil.createPlayerSkull(player.getGameProfile()));
	        entityitemSkull.setDefaultPickupDelay();
	        if(!player.getEntityWorld().isRemote){
	        	player.getEntityWorld().addEntity(entityitemSkull);
	        }
	        //event.getDrops().add(entityitemSkull);
        }
    }
	
}
