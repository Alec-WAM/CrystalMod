package alec_wam.CrystalMod.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.block.IExplosionImmune;
import alec_wam.CrystalMod.api.estorage.security.NetworkAbility;
import alec_wam.CrystalMod.api.tools.UpgradeData;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerInventory;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.capability.PacketExtendedPlayerInvSync;
import alec_wam.CrystalMod.entities.accessories.HorseAccessories;
import alec_wam.CrystalMod.entities.accessories.WolfAccessories;
import alec_wam.CrystalMod.entities.minions.warrior.EntityMinionWarrior;
import alec_wam.CrystalMod.integration.baubles.BaublesIntegration;
import alec_wam.CrystalMod.integration.baubles.ItemBaubleWings;
import alec_wam.CrystalMod.items.ItemCursedBone.BoneType;
import alec_wam.CrystalMod.items.ItemDragonWings;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.enchancements.ModEnhancements;
import alec_wam.CrystalMod.items.ItemCursedBone.BoneType;
import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.backpack.IBackpack;
import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.InventoryBackpackUpgrades;
import alec_wam.CrystalMod.items.tools.backpack.upgrade.ItemBackpackUpgrade.BackpackUpgrade;
import alec_wam.CrystalMod.items.tools.bat.BatHelper;
import alec_wam.CrystalMod.items.tools.bat.ModBats;
import alec_wam.CrystalMod.items.tools.projectiles.DamageSourceDarkarang;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.tiles.endertorch.TileEnderTorch;
import alec_wam.CrystalMod.tiles.jar.BlockJar;
import alec_wam.CrystalMod.tiles.pipes.estorage.EStorageNetwork;
import alec_wam.CrystalMod.tiles.pipes.estorage.TileEntityPipeEStorage;
import alec_wam.CrystalMod.tiles.playercube.CubeManager;
import alec_wam.CrystalMod.tiles.playercube.PlayerCube;
import alec_wam.CrystalMod.tiles.playercube.TileEntityPlayerCubePortal;
import alec_wam.CrystalMod.tiles.spawner.EntityEssenceInstance;
import alec_wam.CrystalMod.tiles.spawner.ItemMobEssence;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.PlayerUtil;
import alec_wam.CrystalMod.util.TimeUtil;
import alec_wam.CrystalMod.util.Util;
import alec_wam.CrystalMod.world.ModDimensions;
import baubles.api.BaubleType;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.CreateFluidSourceEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;

public class EventHandler {

	@SubscribeEvent
    public void coverWrap(final PlayerInteractEvent event){
		/*BlockPos pos = event.pos;
		EnumFacing face = event.face;
		Vec3 hitVec = null;
		if(pos == null){
			if(event.entityPlayer !=null){
				float f = event.entityPlayer.rotationPitch;
		        float f1 = event.entityPlayer.rotationYaw;
		        double d0 = event.entityPlayer.posX;
		        double d1 = event.entityPlayer.posY + (double)event.entityPlayer.getEyeHeight();
		        double d2 = event.entityPlayer.posZ;
		        Vec3 vec3 = new Vec3(d0, d1, d2);
		        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float)Math.PI);
		        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float)Math.PI);
		        float f4 = -MathHelper.cos(-f * 0.017453292F);
		        float f5 = MathHelper.sin(-f * 0.017453292F);
		        float f6 = f3 * f4;
		        float f7 = f2 * f4;
		        double d3 = 5.0D;
		        if (event.entityPlayer instanceof net.minecraft.entity.player.EntityPlayerMP)
		        {
		            d3 = ((net.minecraft.entity.player.EntityPlayerMP)event.entityPlayer).theItemInWorldManager.getBlockReachDistance();
		        }
		        Vec3 vec31 = vec3.addVector((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
		        if(event.getWorld() !=null){
		        	MovingObjectPosition mop = event.getWorld().rayTraceBlocks(vec3, vec31, false, true, false);
		        	if(mop !=null){
		        		pos = mop.getBlockPos();
		        		if(face == null){
		        			face = mop.sideHit;
		        		}
		        		if(hitVec == null){
		        			hitVec = mop.hitVec;
		        		}
		        	}
		        }
			}
			if(pos == null){
				return;
			}
		}
		World world = event.getWorld();
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileEntityPipe){
			TileEntityPipe pipe = (TileEntityPipe)tile;
			CoverData data = pipe.getCoverData(face);
			if(data !=null && data.getBlockState() !=null){
				if (event.entityPlayer.getCurrentEquippedItem() != null && !(event.useItem != net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW) && event.useItem != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY)
	            {
					ItemStack stack = event.entityPlayer.getCurrentEquippedItem();
	                int meta = stack.getMetadata();
	                int size = stack.stackSize;
	                boolean result = stack.onItemUse(event.entityPlayer, new PipeWorldWrapper(world, pos, face), pos, face, (float)hitVec.xCoord, (float)hitVec.yCoord, (float)hitVec.zCoord);
	                if (event.entityPlayer.capabilities.isCreativeMode)
	                {
	                    stack.setItemDamage(meta);
	                    stack.stackSize = size;
	                }
	                if (stack.stackSize <= 0 && event.entityPlayer instanceof EntityPlayerMP) net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem((EntityPlayerMP)event.entityPlayer, stack);
	            }
				event.setCanceled(true);
			}
		}*/
	}
	
	@SubscribeEvent
	public void onBreakSpeed(BreakSpeed event) {
	}
	
	@SubscribeEvent
	public void explosionDetonate(ExplosionEvent.Detonate event)
	{
		Iterator<BlockPos> iterator = event.getAffectedBlocks().iterator();

		while (iterator.hasNext())
		{
			BlockPos pos = iterator.next();

			if (event.getWorld().getBlockState(pos).getBlock() instanceof IExplosionImmune)
			{
				iterator.remove();
			}
		}
	}
	
	@SubscribeEvent
	public void placed(BlockEvent.PlaceEvent event)
	{
		if(event.getWorld() !=null && !event.getWorld().isRemote && event.getWorld().provider.getDimension() == ModDimensions.CUBE_ID){
			PlayerCube cube = CubeManager.getInstance().getPlayerCubeFromPos(event.getWorld(), event.getPos());
			if(cube !=null){
				for(TileEntityPlayerCubePortal portal : cube.watchers){
					IBlockState eState = event.getPlacedBlock();
					@SuppressWarnings("deprecation")
					IBlockState state = eState.getBlock().getActualState(eState, event.getWorld(), event.getPos());
		            TileEntity tileentity = event.getWorld().getTileEntity(event.getPos());
		            if (tileentity != null || state.getBlock().hasTileEntity(state) && (tileentity = event.getWorld().getTileEntity(event.getPos())) != null) {
		                //tileentity.validate();
		            }
		            if (portal.mobileChunk.addBlockWithState(event.getPos(), state)) {
		                TileEntity tileClone = tileentity;
		                portal.mobileChunk.setTileEntity(event.getPos(), tileClone);
		            }
		            portal.mobileChunk.setChunkModified();
		            portal.mobileChunk.onChunkLoad();
				}
			}
		}
	}
	
	@SubscribeEvent
	public void breakE(BlockEvent.BreakEvent event)
	{
		if(event.getWorld() !=null && !event.getWorld().isRemote && event.getWorld().provider.getDimension() == ModDimensions.CUBE_ID){
			PlayerCube cube = CubeManager.getInstance().getPlayerCubeFromPos(event.getWorld(), event.getPos());
			if(cube !=null){
				for(TileEntityPlayerCubePortal portal : cube.watchers){
					if (portal.mobileChunk.addBlockWithState(event.getPos(), Blocks.AIR.getDefaultState())) {
		                portal.mobileChunk.removeChunkBlockTileEntity(event.getPos());
		            }
		            portal.mobileChunk.setChunkModified();
		            portal.mobileChunk.onChunkLoad();
				}
			}
		}
	}
	
	public static enum ItemDropType{
    	NONE, ALL, KILLED;
    }

    public static int fixLooting(int looting){
    	return looting == 0 ? 1 : looting;
    }

    public static String NBT_WINGS = "DragonWings.Flight";
	public static boolean blockClickEvent;
    
    @SubscribeEvent
    public void entityUpdate(LivingUpdateEvent event){
    	if(event.getEntityLiving() !=null){
    		if(event.getEntityLiving() instanceof EntityPlayer){
    			EntityPlayer player = (EntityPlayer)event.getEntityLiving();
    			updateWings(player);
    		}
    	}
    }
    
    @SubscribeEvent
    public void cancelLeftClick(final PlayerInteractEvent.LeftClickBlock event) {
        if (EventHandler.blockClickEvent) {
        	EventHandler.blockClickEvent = false;
            event.setUseBlock(Event.Result.DENY);
        }
    }
    
    public static boolean hasDragonWings(EntityPlayer player){
    	ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if(ItemStackTools.isValid(chest) && ModEnhancements.DRAGON_WINGS.isApplied(chest)){
			return true;
		}
		
		if(BaublesIntegration.instance().hasBaubles()){
			ItemStack baubleStack = BaublesIntegration.instance().getBauble(player, BaubleType.BODY);
			if(ItemStackTools.isValid(baubleStack) && baubleStack.getItem() instanceof ItemBaubleWings){
				return true;
			}
		}
		
    	return false;
    }
    
    public static final List<String> WINGED_PLAYERS = new ArrayList<String>();
    
    public static boolean isPlayerWinged(EntityPlayer player){
        return WINGED_PLAYERS.contains(player.getUniqueID()+(player.getEntityWorld().isRemote ? "-Remote" : ""));
    }
    
    public static void removeWingsFromPlayer(EntityPlayer player){
        removeWingsFromPlayer(player, player.getEntityWorld().isRemote);
    }
    
    public static void removeWingsFromPlayer(EntityPlayer player, boolean worldRemote){
        WINGED_PLAYERS.remove(player.getUniqueID()+(worldRemote ? "-Remote" : ""));
    }
    
    public static void addWingsToPlayer(EntityPlayer player){
        WINGED_PLAYERS.add(player.getUniqueID()+(player.getEntityWorld().isRemote ? "-Remote" : ""));
    }
    
    @SubscribeEvent
    public void onLogOutEvent(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent event){
        //Remove Player from Wings' Fly Permission List
        removeWingsFromPlayer(event.player, true);
        removeWingsFromPlayer(event.player, false);
    }
    
    public void updateWings(EntityPlayer player){
    	boolean wingsEquipped = hasDragonWings(player);

         //If Player isn't (really) winged
         if(!isPlayerWinged(player)){
             if(wingsEquipped){
                 //Make the Player actually winged
                 addWingsToPlayer(player);
             }
         }
         //If Player is (or should be) winged
         else{
             if(wingsEquipped){
                 //Allow the Player to fly when they have Wings equipped
                 player.capabilities.allowFlying = true;
                 
                 ExtendedPlayer extPlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
                 if(extPlayer !=null){
					//Copied from Ender Dragon
 					extPlayer.prevWingAnimTime = extPlayer.wingAnimTime;
 					float f10 = 0.2F * (MathHelper.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ) * 0.5F + 1.0F);
 					f10 = f10 * (float)Math.pow(2.0D, player.motionY);

     	            
 					if(player.capabilities.isFlying){
 						extPlayer.wingAnimTime+= f10*0.3f;
 					}else if(!player.onGround){
 						extPlayer.wingAnimTime += f10*0.4f;
 					}else{
 						extPlayer.wingAnimTime+=0.002f;
 					}
 				}
                 
             }
             else{
                 //Make the Player not winged
                 removeWingsFromPlayer(player);
                 //Reset Player's Values
                 if(!player.capabilities.isCreativeMode){
                     player.capabilities.allowFlying = false;
                     player.capabilities.isFlying = false;
                     //Enables Fall Damage again (Automatically gets disabled for some reason)
                     player.capabilities.disableDamage = false;
                 }
             }
         }
    }
    
    @SubscribeEvent
    public void entityJoin(EntityJoinWorldEvent event){
    	Entity entity = event.getEntity();
    	if(EntityUtil.hasCustomData(entity)){
    		WolfAccessories.onEntityLoad(entity);
    	}
    }
    
    public static List<Entity> dirtyEntities = Lists.newArrayList();
    public static String NBT_NBTDIRTY = "CustomDataDirty";
    
    @SubscribeEvent
    public void sendData(PlayerEvent.StartTracking event){
    	Entity entity = event.getTarget();
    	if(EntityUtil.hasCustomData(entity) && event.getEntityPlayer() instanceof EntityPlayerMP){
    		CrystalModNetwork.sendTo(new PacketEntityMessage(entity, "CustomDataSync", EntityUtil.getCustomEntityData(entity)), (EntityPlayerMP)event.getEntityPlayer());
    	}
    }
    
    @SubscribeEvent
    public void postTick(TickEvent.WorldTickEvent event){
    	/*if(event.phase == Phase.END && event.side == Side.SERVER){
    		if(!(event.world instanceof WorldServer))return;
    		WorldServer wldServer = (WorldServer)event.world;
    		//We are on the server
    		for(Entity entity : dirtyEntities){
    			if(EntityUtil.hasCustomData(entity)){
    				NBTTagCompound nbt = EntityUtil.getCustomEntityData(entity);
    				for(EntityPlayerMP playerMP : wldServer.playerEntities){

    				}
    			}
    		}
    	}*/
    }
    
    public boolean canPlayerSee(Entity entity, EntityPlayerMP playerMP)
    {
        double d0 = playerMP.posX - (double)entity.posX / 4096.0D;
        double d1 = playerMP.posZ - (double)entity.posZ / 4096.0D;
        int i = 64;
        return d0 >= (double)(-i) && d0 <= (double)i && d1 >= (double)(-i) && d1 <= (double)i && entity.isSpectatedByPlayer(playerMP);
    }
    
    @SubscribeEvent
    public void onEntityLivingInteract(EntityInteract event)
    {
    	if(event.getTarget() == null)return;
    	
    	EntityPlayer player = event.getEntityPlayer();
    	ItemStack held = event.getItemStack();
    	Entity entity = event.getTarget();
        if(entity instanceof AbstractHorse){
      	  AbstractHorse horse = (AbstractHorse)entity;
      	  if(HorseAccessories.handleHorseInteract(player, held, event.getHand(), horse)){
      		  event.setCanceled(true);
      	  }
        }
        if(entity instanceof EntityWolf){
        	if(WolfAccessories.handleWolfInteract(player, held, event.getHand(), (EntityWolf)entity)){
        		event.setCanceled(true);
        	}
        }
        if(entity instanceof EntityShulkerBullet){
        	if(ItemStackTools.isValid(held)){
        		if(held.getItem() == Item.getItemFromBlock(ModBlocks.jar)){
        			NBTTagCompound nbt = new NBTTagCompound();
        			if(held.hasTagCompound() && held.getTagCompound().hasKey(BlockJar.TILE_NBT_STACK)){
        				nbt = ItemNBTHelper.getCompound(held).getCompoundTag(BlockJar.TILE_NBT_STACK);
        			}
        			if((!nbt.hasKey("IsShulker") || !nbt.getBoolean("IsShulker")) && (!nbt.hasKey("Count") || nbt.getInteger("Count") <=0)){
        				nbt.setBoolean("IsShulker", true);
        				if(ItemStackTools.getStackSize(held) == 1){
        					ItemNBTHelper.getCompound(held).setTag(BlockJar.TILE_NBT_STACK, nbt);
        				} else {
        					ItemStack newStack = ItemUtil.copy(held, 1);
        					ItemNBTHelper.getCompound(newStack).setTag(BlockJar.TILE_NBT_STACK, nbt);
        					player.setHeldItem(event.getHand(), ItemUtil.consumeItem(held));
        					if(!player.inventory.addItemStackToInventory(newStack)){
        						ItemUtil.dropFromPlayer(player, newStack, false);
        					}
        				}
        				entity.setDead();
        			}
        		}
        	}
        }
    }
    
	@SubscribeEvent
    public void onEntityLivingDeath(LivingDeathEvent event)
    {
      if (event.getEntityLiving().getEntityWorld().isRemote) {
        return;
      }
      EntityLivingBase entity = event.getEntityLiving();
      
      if(entity instanceof EntityLivingBase){
    	  Entity attacker = event.getSource().getSourceOfDamage();
    	  if(attacker !=null){
    		  if(attacker instanceof EntityPlayer){
    			  EntityPlayer player = (EntityPlayer)attacker;
    			  ItemStack offHand = player.getHeldItemOffhand();
    			  if(ItemStackTools.isValid(offHand)){
    				  if(offHand.getItem() == ModItems.emptyMobEssence){
    					  EntityEssenceInstance<?> essence = ItemMobEssence.getEntityEssence(entity);
    					  if(essence !=null){
    						  String id = essence.getID();
    						  if(Strings.isNullOrEmpty(id))id = "Pig";
    						  ItemStack essenceStack = ItemMobEssence.createStack(id);
    						  ItemNBTHelper.setInteger(essenceStack, ItemMobEssence.NBT_KILLCOUNT, 1);
    						  player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, essenceStack);
    					  }
    				  } else if(offHand.getItem() == ModItems.mobEssence){
    					  EntityEssenceInstance<?> essence = ItemMobEssence.getEntityEssence(entity);
    					  if(essence !=null){
    						  String id = essence.getID();
    						  if(Strings.isNullOrEmpty(id))id = "Pig";
    						  String heldID = ItemNBTHelper.getString(offHand, ItemMobEssence.NBT_ENTITYNAME, "");
    						  int currentKills = ItemNBTHelper.getInteger(offHand, ItemMobEssence.NBT_KILLCOUNT, 1);
    						  if(id.equals(heldID) && currentKills < essence.getNeededKills()){
    							  ItemNBTHelper.setInteger(offHand, ItemMobEssence.NBT_KILLCOUNT, currentKills+1);
    						  }
    					  }
    				  }
    			  }
    		  }
    	  }
      }
      
      if(entity instanceof AbstractHorse){
    	  AbstractHorse horse = (AbstractHorse)entity;
    	  HorseAccessories.onHorseDeath(horse);
      }
      if(entity instanceof EntityWolf){
    	  ItemStack armor = WolfAccessories.getWolfArmorStack((EntityWolf)entity);
    	  if(ItemStackTools.isValid(armor)){
    		  entity.entityDropItem(armor, 0);
    	  }
      }
    }
	
	@SubscribeEvent
    public void onLivingDrop(LivingDropsEvent event)
    {
        if (event.getEntityLiving() == null || event.getEntityLiving().getEntityWorld().isRemote)
            return;
        
        if(Util.notNullAndInstanceOf(event.getEntityLiving(), EntityDragon.class)){
        	ItemStack stack = new ItemStack(ModItems.wings);
        	EntityItem item = new EntityItem(event.getEntity().getEntityWorld(), event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, stack);
        	event.getDrops().add(item);
        }
        
        if(Util.notNullAndInstanceOf(event.getEntityLiving(), EntityWither.class)){
        	ItemStack stack = new ItemStack(ModItems.cursedBone, MathHelper.getInt(EntityUtil.rand, 10, 20), BoneType.BONE.getMetadata());
        	EntityItem item = new EntityItem(event.getEntity().getEntityWorld(), event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, stack);
        	event.getDrops().add(item);
        }
        
        if(Util.notNullAndInstanceOf(event.getEntityLiving(), EntityWitherSkeleton.class)){
        	ItemStack stack = new ItemStack(ModItems.cursedBone, MathHelper.getInt(EntityUtil.rand, 1, 3), BoneType.BONE.getMetadata());
        	EntityItem item = new EntityItem(event.getEntity().getEntityWorld(), event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, stack);
        	event.getDrops().add(item);
        }
        
        addMobHeads(event);
    }
	
	@SubscribeEvent
	public void handleNock(ArrowNockEvent event){
		if(event.getEntityPlayer() !=null){
			ExtendedPlayer exPlayer = ExtendedPlayerProvider.getExtendedPlayer(event.getEntityPlayer()); 
			if(exPlayer !=null){
				ExtendedPlayerInventory inv = exPlayer.getInventory();
				ItemStack backpack = inv.getStackInSlot(ExtendedPlayerInventory.BACKPACK_SLOT_ID);
				if(ItemStackTools.isValid(backpack)){
					InventoryBackpackUpgrades upgrades = BackpackUtil.getUpgradeInventory(event.getEntityPlayer(), backpack);
					if(upgrades !=null && upgrades.hasUpgrade(BackpackUpgrade.BOW)){
						InventoryBackpack backpackInv = BackpackUtil.getInventory(event.getEntityPlayer(), backpack);
						if(backpackInv !=null){
							for(int i = 0; i < backpackInv.getSize(); i++){
								ItemStack st = backpackInv.getStackInSlot(i);
								if(ItemStackTools.isValid(st) && st.getItem() instanceof ItemArrow){
									event.getEntityPlayer().setActiveHand(event.getHand());
									event.setAction(new ActionResult<ItemStack>(EnumActionResult.SUCCESS, event.getBow()));
									return;
								}
							}
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void handleLoose(ArrowLooseEvent event){
		if(event.getEntityPlayer() !=null){
			ExtendedPlayer exPlayer = ExtendedPlayerProvider.getExtendedPlayer(event.getEntityPlayer()); 
			if(exPlayer !=null){
				ExtendedPlayerInventory inv = exPlayer.getInventory();
				ItemStack backpack = inv.getStackInSlot(ExtendedPlayerInventory.BACKPACK_SLOT_ID);
				if(ItemStackTools.isValid(backpack)){
					InventoryBackpackUpgrades upgrades = BackpackUtil.getUpgradeInventory(event.getEntityPlayer(), backpack);
					if(upgrades !=null && upgrades.hasUpgrade(BackpackUpgrade.BOW)){
						InventoryBackpack backpackInv = BackpackUtil.getInventory(event.getEntityPlayer(), backpack);
						if(backpackInv !=null){
							for(int i = 0; i < backpackInv.getSize(); i++){
								ItemStack st = backpackInv.getStackInSlot(i);
								if(ItemStackTools.isValid(st) && st.getItem() instanceof ItemArrow){
									if(!EntityUtil.shootArrow(event.getWorld(), event.getEntityPlayer(), event.getBow(), st, event.getCharge())){
										backpackInv.decrStackSize(i, 1);
										backpackInv.markDirty();
										backpackInv.guiSave(event.getEntityPlayer());
										inv.setChanged(ExtendedPlayerInventory.BACKPACK_SLOT_ID, true);
										event.setCanceled(true);
										return;
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void handleDespawn(ItemExpireEvent event){
		ItemStack stack = event.getEntityItem().getEntityItem();
		if(ItemStackTools.isValid(stack)){
			InventoryBackpackUpgrades upgrades = BackpackUtil.getUpgradeInventory(stack);
			if(upgrades !=null && upgrades.hasUpgrade(BackpackUpgrade.DESPAWN)){
				event.getEntityItem().setNoDespawn();
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public void playerDeath(PlayerDropsEvent event) {
		if (event.getEntity() instanceof EntityPlayer && !event.getEntity().getEntityWorld().isRemote) {
			boolean keepInv = event.getEntity().getEntityWorld().getGameRules().getBoolean("keepInventory");
			addPlayerHeads(event);
			if(!keepInv){
				ExtendedPlayer exPlayer = ExtendedPlayerProvider.getExtendedPlayer(event.getEntityPlayer()); 
				if(exPlayer !=null){
					ExtendedPlayerInventory inv = exPlayer.getInventory();
					
					ItemStack backpack = inv.getStackInSlot(ExtendedPlayerInventory.BACKPACK_SLOT_ID);
					
					boolean keepBackpack = false;
					if(ItemStackTools.isValid(backpack)){
						InventoryBackpackUpgrades upgrades = BackpackUtil.getUpgradeInventory(event.getEntityPlayer(), backpack);
						if(upgrades !=null && upgrades.hasUpgrade(BackpackUpgrade.DEATH)){
							if(Config.backpackDeathUpgradeConsume){
								int index = upgrades.getUpgradeIndex(BackpackUpgrade.DEATH);
								if(index > -1){
									upgrades.decrStackSize(index, 1);
									upgrades.markDirty();
									upgrades.guiSave(event.getEntityPlayer());
									inv.setChanged(ExtendedPlayerInventory.BACKPACK_SLOT_ID, true);
									keepBackpack = true;
								}
							} else keepBackpack = true;
						}
					}
					
					for (int i = 0; i < inv.getSlots(); ++i) {
						if (ItemStackTools.isValid(inv.getStackInSlot(i))) {
							if(i == ExtendedPlayerInventory.BACKPACK_SLOT_ID){
								if(keepBackpack){
									continue;
								}
							}
							event.getDrops().add(ItemUtil.dropFromPlayer(event.getEntityPlayer(), inv.getStackInSlot(i).copy(), true));
							inv.setStackInSlot(i, ItemStackTools.getEmptyStack());
						}
					}
				}			
			}
		}
	}
	
	public void addPlayerHeads(PlayerDropsEvent event){
		EntityPlayer player = event.getEntityPlayer();
		DamageSource source = event.getSource();
		Entity attacker = source.getSourceOfDamage();
 	   	if(Config.playerHeadType == ItemDropType.NONE)return;
 	   	if(player == null || player.getEntityWorld() == null)return;
 	   
 	   	int rand = player.getEntityWorld().rand.nextInt(Math.max(Config.playerHeadDropChance / fixLooting(event.getLootingLevel()), 1));
 	   
 	   	if(Config.playerHeadDropChance < 0 || rand !=0)
 	   		return;
 	   	if(Config.playerHeadType == ItemDropType.KILLED){
 	   		if(attacker == null || !(attacker instanceof EntityPlayer) || attacker instanceof FakePlayer)
 	   			return;
 	   	}
 	   	ItemStack skull = PlayerUtil.createPlayerHead(player);
 	   	event.getDrops().add(ItemUtil.dropFromPlayer(player, skull, true));
    }
	
	public void addMobHeads(LivingDropsEvent event){
    	if(Config.mobHeadType == ItemDropType.NONE)return;
    	if(Config.mobHeadType == ItemDropType.KILLED){
	        Entity entity = event.getSource().getEntity();
	        if(entity == null)
	            return;
	        if(!(entity instanceof EntityPlayer))
	            return;
        }

        Entity mob = event.getEntityLiving();
        int rand = mob.getEntityWorld().rand.nextInt(Math.max(Config.mobHeadDropChance / fixLooting(event.getLootingLevel()), 1));
        if(!mob.getEntityWorld().getGameRules().getBoolean("doMobLoot") || Config.mobHeadDropChance < 0 || rand != 0)
            return;

        Item skullItem = null;
        int skullId = -1;

        
        if (mob instanceof EntitySkeleton) {
            skullItem = Items.SKULL;
            skullId = 0;
        }
        else if (mob instanceof EntityZombie) {
            skullItem = Items.SKULL;
            skullId = 2;
        }
        else if (mob instanceof EntityCreeper) {
            skullItem = Items.SKULL;
            skullId = 4;
        }
        
        // no skull found?
        if(skullItem == null)
            return;

        // drop it like it's hot
        EntityItem entityitemSkull = new EntityItem(mob.getEntityWorld(), mob.posX, mob.posY, mob.posZ, new ItemStack(skullItem, 1, skullId));
        entityitemSkull.setDefaultPickupDelay();
        event.getDrops().add(entityitemSkull);
    }
	
	@SuppressWarnings("deprecation")
	@SubscribeEvent(priority = EventPriority.LOW)
 	public void onPlayerInteract(PlayerInteractEvent event) {
 		if(event.getEntityPlayer() !=null && event.getWorld() !=null && event instanceof RightClickBlock){
 			BlockPos pos = EntityUtil.getEntityLookedBlock(event.getEntityPlayer(), (float)CrystalMod.proxy.getReachDistanceForPlayer(event.getEntityPlayer()));
 			if(pos !=null){
 				if(event.getWorld().isBlockLoaded(pos)){
 					IBlockState state = event.getWorld().getBlockState(pos);
 					if(state.getBlock() == Blocks.WOOL && ItemStackTools.isValid(event.getEntityPlayer().getHeldItem(event.getHand())) && ItemUtil.itemStackMatchesOredict(event.getEntityPlayer().getHeldItem(event.getHand()), "slimeball")){
 						
 						int waterCt = 0;
 						for(EnumFacing face : EnumFacing.VALUES){
 							IBlockState state2 = event.getWorld().getBlockState(pos.offset(face));
 							if(state2 !=null && state2.getBlock().getMaterial(state2) == Material.WATER){
 								waterCt++;
 							}
 						}
 						
 						if(waterCt >= 4){
 							event.getWorld().setBlockState(pos, Blocks.SPONGE.getDefaultState());
 							event.getEntityPlayer().swingArm(event.getHand());
 							
 							if (!event.getWorld().isRemote)
 		                    {
 								event.getWorld().playEvent(2001, pos, Block.getStateId(state));
 		                    }
 							
 							if(!event.getEntityPlayer().capabilities.isCreativeMode)
 							event.getEntityPlayer().setHeldItem(event.getHand(), ItemUtil.consumeItem(event.getEntityPlayer().getHeldItem(event.getHand())));
 						}
 					}
 				}
 			}
 		}
 	}
	
	@SubscribeEvent
    public void addEntityCapabilities(AttachCapabilitiesEvent.Entity event) {
        if(event.getEntity() instanceof EntityPlayer) {
        	if(!event.getCapabilities().containsKey(ExtendedPlayerProvider.KEY))	{
  	    	  try{
  	    		  event.addCapability(ExtendedPlayerProvider.KEY, new ExtendedPlayerProvider((EntityPlayer) event.getEntity()));
  	    	  }catch(Exception e){}
        	}
        }
    }
	
	@SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        EntityPlayer oldPlayer = event.getOriginal();
        EntityPlayer newPlayer = event.getEntityPlayer();
        ExtendedPlayer oldProps = ExtendedPlayerProvider.getExtendedPlayer(oldPlayer);
        ExtendedPlayer newProps = ExtendedPlayerProvider.getExtendedPlayer(newPlayer);
        if(oldPlayer != null && newProps != null) {
            newProps.readFromNBT(oldProps.writeToNBT());
        }
    }
	
	@SubscribeEvent
	public void onAttacked(LivingAttackEvent event) {
		if(event.getSource() != null) {
			Entity attacker = event.getSource().getEntity();
			if(attacker != null && attacker instanceof EntityMinionWarrior && attacker.getRidingEntity() == event.getEntityLiving())
				event.setCanceled(true);
		}
	}
	
	private Map<String, String[]> syncCheck = new HashMap<String, String[]>();
	
	@SubscribeEvent
	public void playerJoin(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityPlayer && !event.getWorld().isRemote) {		
			ExtendedPlayer ePlayer = ExtendedPlayerProvider.getExtendedPlayer((EntityPlayer) event.getEntity());	
			ExtendedPlayerInventory inventory = ePlayer.getInventory();
			for (int a=0;a<inventory.getSlots();a++) inventory.setChanged(a,true);
			
			for (EntityPlayer p : event.getEntity().getEntityWorld().playerEntities) {
				if (p.getEntityId() != event.getEntity().getEntityId()) {
					ExtendedPlayer ePlayer2 = ExtendedPlayerProvider.getExtendedPlayer(p);
					ExtendedPlayerInventory inventory2 = ePlayer2.getInventory();	
					for (int a=0;a<inventory2.getSlots();a++) inventory2.setChanged(a,true);
				}
			}
			
			String[] ia = new String[inventory.getSlots()];
			Arrays.fill(ia, "");
			syncCheck.put(event.getEntity().getCachedUniqueIdString(), ia);		
		}
	}
	
	@SubscribeEvent
	public void playerTick(PlayerEvent.LivingUpdateEvent event) {
		// player events
		if (event.getEntity() instanceof EntityPlayer) {			
			EntityPlayer player = (EntityPlayer) event.getEntity();
			ExtendedPlayer ePlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
			
			if(ePlayer !=null){
				boolean redstoneCore = player.inventory.hasItemStack(new ItemStack(ModBlocks.redstoneCore));
				
				if(redstoneCore){
					if(ePlayer.getRadiation() < TimeUtil.MINUTE * 4){
						ePlayer.setRadiation(ePlayer.getRadiation()+1);
					} 
					if(ePlayer.getRadiation() >= (TimeUtil.MINUTE * 4) - 600){
						if(player.isEntityAlive()){
							player.attackEntityFrom(new DamageSource("crystalmod.radiation").setDamageBypassesArmor(), 5.0F);
						} else {
							ePlayer.setRadiation(0);
						}
					}
				} else {
					if(ePlayer.getRadiation() > 0){
						ePlayer.setRadiation(ePlayer.getRadiation()-1);
					}
				}
				
				boolean dirtyValue = ePlayer.getLastRadiation() != ePlayer.getRadiation() && player.getEntityWorld().getTotalWorldTime() % 20 == 0;
				if(dirtyValue && player instanceof EntityPlayerMP){
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setInteger("Time", ePlayer.getRadiation());
					ePlayer.setLastRadiation(ePlayer.getRadiation());
					CrystalModNetwork.sendTo(new PacketEntityMessage(player, "#UpdateRadiation#", nbt), (EntityPlayerMP)player);
				}
			}
			
			ExtendedPlayerInventory inventory = ePlayer.getInventory();
			String[] hashOld = syncCheck.get(player.getCachedUniqueIdString());
			
			BackpackUtil.updateBackpack(player);
			boolean syncTick = player.ticksExisted % 10 == 0;
			for (int a = 0; a < inventory.getSlots(); a++) {
				ItemStack stack = inventory.getStackInSlot(a);
				if(ItemStackTools.isValid(stack)){
					if (!player.getEntityWorld().isRemote) {
						if (syncTick && !inventory.isChanged(a)) {							
							String s = stack.toString();
							if (stack.hasTagCompound()) s += stack.getTagCompound().toString();
							if (!s.equals(hashOld[a])) {
								inventory.setChanged(a,true);
							}
							hashOld[a] = s;							
						}						
					}
				}
				
				if (inventory.isChanged(a)) {
					try {
						CrystalModNetwork.sendToDimension(new PacketExtendedPlayerInvSync(player, a),
								player.getEntityWorld().provider.getDimension());
					} catch (Exception e) {	}				
				}
			}
				
		}
			
	}
	
	@SubscribeEvent
	public void addLooting(LootingLevelEvent event){
		DamageSource source = event.getDamageSource();
		Entity attacker = source.getSourceOfDamage();
		final int prevLooting = event.getLootingLevel();
		int looting = 0;
		if(attacker !=null){
			if(attacker instanceof EntityPlayer){
				EntityPlayer player = (EntityPlayer)attacker;
				ItemStack hand = player.getHeldItemMainhand();
				if(ItemStackTools.isValid(hand)){
					UpgradeData lapis = BatHelper.getBatUpgradeData(hand, ModBats.LAPIS);
					if(lapis !=null){
						looting+=(int) ModBats.LAPIS.getValue(lapis);
					}
				}
			}
		}
		event.setLootingLevel(prevLooting+looting);
	}
	
	public static final Map<Integer, List<BlockPos>> enderTorchBounds = Maps.newHashMap();
	
	@SubscribeEvent
	public void onEnderTeleport(EnderTeleportEvent event) {
		if (isTeleportPrevented(event.getEntity().getEntityWorld(), event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, false)) {
			event.setCanceled(true);
		}
		if (isTeleportPrevented(event.getEntity().getEntityWorld(), event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
			event.setCanceled(true);
		}
	}
	
	public static void addEnderTorch(int dim, BlockPos pos){
		List<BlockPos> list = enderTorchBounds.getOrDefault(dim, Lists.newArrayList());
		if(!list.contains(pos))list.add(pos);
		enderTorchBounds.put(dim, list);
	}
	
	public static void removeEnderTorch(int dim, BlockPos pos){
		List<BlockPos> list = enderTorchBounds.get(dim);
		if(list !=null){
			list.remove(pos);
			enderTorchBounds.put(dim, list);
		}
	}

	public boolean isTeleportPrevented(World worldObj, double posX, double posY, double posZ, boolean isTeleportTo) {
		if (!enderTorchBounds.isEmpty()) {
			for (BlockPos pos : enderTorchBounds.get(worldObj.provider.getDimension())) {
				if (worldObj.isBlockLoaded(pos)) {
					TileEntity te = worldObj.getTileEntity(pos);
					if (te !=null && te instanceof TileEnderTorch && ((TileEnderTorch) te).isActive() && ((TileEnderTorch) te).inRange(new Vec3d(posX, posY, posZ), isTeleportTo)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@SubscribeEvent
	public void itemPickup(EntityItemPickupEvent event){
		EntityItem ent = event.getItem();
    	if(ent == null)return;
    	if(!ItemStackTools.isValid(ent.getEntityItem()))return;
    	EntityPlayer player = event.getEntityPlayer();
    	if(player !=null && player.isEntityAlive()){
    		ItemStack backpack = BackpackUtil.getBackpackOnBack(player);
    		if(ItemStackTools.isValid(backpack)){
    			IBackpack backpackType = BackpackUtil.getType(backpack);
    			if(backpackType !=null && backpackType.handleItemPickup(event, player, backpack)){
    				event.setCanceled(true);
    			}
    		}
    	}
	}
	
	@SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent e) {
        if (!e.getWorld().isRemote) {
            for (EnumFacing facing : EnumFacing.VALUES) {
                TileEntity tile = e.getWorld().getTileEntity(e.getBlockSnapshot().getPos().offset(facing));

                if (tile != null && TileEntityPipeEStorage.isNetworkTile(tile)) {
                    EStorageNetwork network = TileEntityPipeEStorage.getTileNetwork(tile);
                    if (network != null && !network.hasAbility(e.getPlayer(), NetworkAbility.BUILD)) {
                        ChatUtil.sendNoSpam(e.getPlayer(), Lang.localize("gui.networkability."+NetworkAbility.BUILD.getId()));

                        e.setCanceled(true);

                        return;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent e) {
        if (!e.getWorld().isRemote) {
            TileEntity tile = e.getWorld().getTileEntity(e.getPos());

            if (tile != null && TileEntityPipeEStorage.isNetworkTile(tile)) {
            	 EStorageNetwork network = TileEntityPipeEStorage.getTileNetwork(tile);

                if (network != null && !network.hasAbility(e.getPlayer(), NetworkAbility.BUILD)) {
                	ChatUtil.sendNoSpam(e.getPlayer(), Lang.localize("gui.networkability."+NetworkAbility.BUILD.getId()));

                    e.setCanceled(true);
                }
            }
        }
    }
    
    @SubscribeEvent
    public void rightClickItem(PlayerInteractEvent.RightClickItem event){
    	EntityPlayer player = event.getEntityPlayer();
    	ItemStack stack = event.getItemStack();
    	EnumHand hand = event.getHand();
    	
    	if(player.isInsideOfMaterial(Material.WATER)){
    		if(ItemStackTools.isValid(stack) && stack.getItem() == Items.GLASS_BOTTLE){
    			player.setActiveHand(hand);
    			event.setCanceled(true);
    		}
    	}
    }
    
    @SubscribeEvent
    public void startUse(LivingEntityUseItemEvent.Start event){
    	EntityLivingBase entity = event.getEntityLiving();
    	ItemStack stack = event.getItem();
    	
    	if(entity.isInsideOfMaterial(Material.WATER)){
    		if(ItemStackTools.isValid(stack) && stack.getItem() == Items.GLASS_BOTTLE){
    			event.setDuration(32);
    		}
    	}
    }
    
    @SubscribeEvent
    public void monitorUse(LivingEntityUseItemEvent.Tick event){
    	EntityLivingBase entity = event.getEntityLiving();
    	ItemStack stack = event.getItem();
    	
    	if(!entity.isInsideOfMaterial(Material.WATER)){
    		if(ItemStackTools.isValid(stack) && stack.getItem() == Items.GLASS_BOTTLE){
    			event.setDuration(-1);
    			event.setCanceled(true);
    		}
    	}
    }
    
    @SubscribeEvent
    public void finishUse(LivingEntityUseItemEvent.Finish event){
    	EntityLivingBase entity = event.getEntityLiving();
    	ItemStack stack = event.getItem();
    	
    	if(entity.isInsideOfMaterial(Material.WATER)){
    		if(ItemStackTools.isValid(stack) && stack.getItem() == Items.GLASS_BOTTLE){
    			//Restore to full air
    			entity.setAir(300);
    			ItemStack waterbottle = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER);
    			if(ItemStackTools.getStackSize(stack) > 1){
	    			event.setResultStack(ItemUtil.consumeItem(stack));
	    			if(entity instanceof EntityPlayer){
	    				if(!((EntityPlayer)entity).inventory.addItemStackToInventory(waterbottle)){
	    					ItemUtil.spawnItemInWorldWithoutMotion(entity.getEntityWorld(), waterbottle, new BlockPos(entity));
	    				}
	    			} else {
	    				ItemUtil.spawnItemInWorldWithoutMotion(entity.getEntityWorld(), waterbottle, new BlockPos(entity));
	    			}
    			} else {
    				event.setResultStack(waterbottle);
    			}
    		}
    	}
    }
    
    @SubscribeEvent
    public void onLootLoad(LootTableLoadEvent event) {
        if (event.getName().equals(LootTableList.CHESTS_ABANDONED_MINESHAFT) ||
                event.getName().equals(LootTableList.CHESTS_IGLOO_CHEST) ||
                event.getName().equals(LootTableList.CHESTS_DESERT_PYRAMID) ||
                event.getName().equals(LootTableList.CHESTS_JUNGLE_TEMPLE) ||
                event.getName().equals(LootTableList.CHESTS_NETHER_BRIDGE) ||
                event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON) ||
                event.getName().equals(LootTableList.CHESTS_VILLAGE_BLACKSMITH)) {
            LootPool main = event.getTable().getPool("main");
            // Safety, check if the main lootpool is still present
            /*if (main != null) {
                main.addEntry(new LootEntry(Config.enhancementBookRarity, 0, new LootCondition[0], CrystalMod.resource("enhancementbook")){

					@Override
					public void addLoot(Collection<ItemStack> stacks, Random rand, LootContext context) {
						// TODO Auto-generated method stub
						
					}

					@Override
					protected void serialize(JsonObject json, JsonSerializationContext context) {
						// TODO Auto-generated method stub
						
					}
                	
                });
            }*/
        }
    }
    
    public static final Map<Integer, List<BlockPos>> ACTIVE_INFINITE_ENGINES = Maps.newHashMap();
    
    public static void addInfiniteEngine(int dim, BlockPos pos){
    	List<BlockPos> posList = ACTIVE_INFINITE_ENGINES.getOrDefault(dim, Lists.newArrayList());
    	if(!posList.contains(pos)){
    		posList.add(pos);
    		ACTIVE_INFINITE_ENGINES.put(dim, posList);
    	}
    }
    
    public static void removeInfiniteEngine(int dim, BlockPos pos){
    	if(!ACTIVE_INFINITE_ENGINES.containsKey(dim)) return;
    	List<BlockPos> posList = ACTIVE_INFINITE_ENGINES.getOrDefault(dim, Lists.newArrayList());
    	if(posList.contains(pos)){
    		posList.remove(pos);
    		ACTIVE_INFINITE_ENGINES.put(dim, posList);
    	}
    }
    
    public static final Map<Integer, List<BlockPos>> ACTIVE_FINITE_ENGINES = Maps.newHashMap();
    
    public static void addFiniteEngine(int dim, BlockPos pos){
    	List<BlockPos> posList = ACTIVE_FINITE_ENGINES.getOrDefault(dim, Lists.newArrayList());
    	if(!posList.contains(pos)){
    		posList.add(pos);
    		ACTIVE_FINITE_ENGINES.put(dim, posList);
    	}
    }
    
    public static void removeFiniteEngine(int dim, BlockPos pos){
    	if(!ACTIVE_FINITE_ENGINES.containsKey(dim)) return;
    	List<BlockPos> posList = ACTIVE_FINITE_ENGINES.getOrDefault(dim, Lists.newArrayList());
    	if(posList.contains(pos)){
    		posList.remove(pos);
    		ACTIVE_FINITE_ENGINES.put(dim, posList);
    	}
    }
    
    @SubscribeEvent
    public void engineHandle(CreateFluidSourceEvent event){
    	World world = event.getWorld();
    	BlockPos pos = event.getPos();
    	IBlockState state = event.getState();
    	if(world !=null && world.provider !=null){
    		int dimension = world.provider.getDimension();
    		if(state.getBlock() == Blocks.LAVA || state.getBlock() == Blocks.FLOWING_LAVA){
	    		List<BlockPos> activeInfiniteEngines = ACTIVE_INFINITE_ENGINES.get(dimension);
	    		if(activeInfiniteEngines !=null && !activeInfiniteEngines.isEmpty()){
	    			for(BlockPos engine : activeInfiniteEngines){
	    				if(inRangeOfEngine(engine, pos)){
	    					event.setResult(Result.ALLOW);
	    				}
	    			}
	    		}
    		}
    		if(state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.FLOWING_WATER){
	    		List<BlockPos> activeFiniteEngines = ACTIVE_FINITE_ENGINES.get(dimension);
	    		if(activeFiniteEngines !=null && !activeFiniteEngines.isEmpty()){
	    			for(BlockPos engine : activeFiniteEngines){
	    				if(inRangeOfEngine(engine, pos)){
	    					event.setResult(Result.DENY);
	    				}
	    			}
	    		}
    		}
    	}
    }
    
    public static boolean inRangeOfEngine(BlockPos engine, BlockPos pos){
    	List<BlockPos> checkList = BlockUtil.getBlocksInBB(engine, 10, 10, 10);
    	return checkList.contains(pos);
    }
}
