package alec_wam.CrystalMod.events;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.ModConfig;
import alec_wam.CrystalMod.util.ProfileUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
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
    public static void addPlayerDrops(PlayerDropsEvent event)
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
	        if(!(source instanceof EntityPlayer))
	            return;
        }
        
        Entity mob = event.getEntityLiving();
        int configChance = ModConfig.ENTITIES.Mob_Heads_Drop.get();
        if(configChance < 0)return;
        int chance = configChance / fixLooting(event.getLootingLevel());
        if(source !=null && source instanceof EntityPlayer){
        	EntityPlayer player = (EntityPlayer)source;
        	ItemStack heldItem = player.getHeldItemMainhand();
        	if(heldItem.getItem() instanceof ItemAxe){
    			chance /= ModConfig.ENTITIES.Mob_Heads_Axe_Bonus.get();
    			int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, heldItem);
    			if(fortune > 0){
    				chance /= fortune + 1;
    			}
        	}
        }
        int rand = mob.getEntityWorld().rand.nextInt(Math.max(chance, 1));
        if(!mob.getEntityWorld().getGameRules().getBoolean("doMobLoot") || rand != 0)
            return;
        
        Item skullItem = null;
        
        if (mob instanceof EntitySkeleton) {
            skullItem = Items.SKELETON_SKULL;
        }
        else if (mob instanceof EntityZombie) {
            skullItem = Items.ZOMBIE_HEAD;
        }
        else if (mob instanceof EntityCreeper) {
            skullItem = Items.CREEPER_HEAD;
        }
        
        // no skull found?
        if(skullItem == null)
            return;

        // drop it like it's hot
        EntityItem entityitemSkull = new EntityItem(mob.getEntityWorld(), mob.posX, mob.posY, mob.posZ, new ItemStack(skullItem, 1));
        entityitemSkull.setDefaultPickupDelay();
        event.getDrops().add(entityitemSkull);
    }
	
	public static void addAxeWitherHeads(LivingDropsEvent event){
		Entity mob = event.getEntityLiving();
		if(!(mob instanceof EntityWitherSkeleton))return;
		Entity source = event.getSource().getTrueSource();
		if(source == null || !(source instanceof EntityPlayer))
            return;
		
		EntityPlayer player = (EntityPlayer)source;
		ItemStack heldItem = player.getHeldItemMainhand();
    	if(!(heldItem.getItem() instanceof ItemAxe)) return;
    	
        int configChance = ModConfig.ENTITIES.Wither_Heads_Axe_Bonus.get();
        if(configChance <= 0)return;
        
        int rand = mob.getEntityWorld().rand.nextInt(Math.max(configChance, 1));
        if(!mob.getEntityWorld().getGameRules().getBoolean("doMobLoot") || rand != 0)
            return;
        
        Item skullItem = Items.WITHER_SKELETON_SKULL;
        // drop it like it's hot
        EntityItem entityitemSkull = new EntityItem(mob.getEntityWorld(), mob.posX, mob.posY, mob.posZ, new ItemStack(skullItem, 1));
        entityitemSkull.setDefaultPickupDelay();
        event.getDrops().add(entityitemSkull);
    }
	
	public static void addPlayerHead(PlayerDropsEvent event){
		ItemDropType configDropType = ModConfig.Entities.getDropType(ModConfig.ENTITIES.Player_Heads);
    	if(configDropType == ItemDropType.NONE)return;
    	Entity source = event.getSource().getTrueSource();
        if(configDropType == ItemDropType.KILLED){
	        if(source == null)
	            return;
	        if(!(source instanceof EntityPlayer))
	            return;
        }

        EntityPlayer player = event.getEntityPlayer();
        int configChance = ModConfig.ENTITIES.Player_Heads_Drop.get();
        if(configChance < 0)return;
        int chance = configChance / fixLooting(event.getLootingLevel());
        if(source !=null && source instanceof EntityPlayer){
        	EntityPlayer otherPlayer = (EntityPlayer)source;
        	if(otherPlayer.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemAxe){
        		chance /= ModConfig.ENTITIES.Player_Heads_Axe_Bonus.get();
    		}
        }
        int rand = player.getEntityWorld().rand.nextInt(Math.max(chance, 1));
        if(rand == 0){
	        // drop it like it's hot
	        EntityItem entityitemSkull = new EntityItem(player.getEntityWorld(), player.posX, player.posY, player.posZ, ProfileUtil.createPlayerSkull(player.getGameProfile()));
	        entityitemSkull.setDefaultPickupDelay();
	        event.getDrops().add(entityitemSkull);
        }
    }
	
}
