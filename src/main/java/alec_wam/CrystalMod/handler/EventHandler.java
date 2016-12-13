package alec_wam.CrystalMod.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.block.IExplosionImmune;
import alec_wam.CrystalMod.api.tools.UpgradeData;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerInventory;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.capability.PacketExtendedPlayerInvSync;
import alec_wam.CrystalMod.entities.accessories.HorseAccessories;
import alec_wam.CrystalMod.entities.minions.warrior.EntityMinionWarrior;
import alec_wam.CrystalMod.integration.baubles.BaublesIntegration;
import alec_wam.CrystalMod.integration.baubles.ItemBaubleWings;
import alec_wam.CrystalMod.items.ItemDragonWings;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.backpack.IBackpack;
import alec_wam.CrystalMod.items.tools.backpack.ItemBackpackBase;
import alec_wam.CrystalMod.items.tools.backpack.types.BackpackNormal;
import alec_wam.CrystalMod.items.tools.backpack.types.InventoryBackpack;
import alec_wam.CrystalMod.items.tools.bat.BatHelper;
import alec_wam.CrystalMod.items.tools.bat.ModBats;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.tiles.endertorch.TileEnderTorch;
import alec_wam.CrystalMod.tiles.playercube.CubeManager;
import alec_wam.CrystalMod.tiles.playercube.PlayerCube;
import alec_wam.CrystalMod.tiles.playercube.TileEntityPlayerCubePortal;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.PlayerUtil;
import alec_wam.CrystalMod.util.Util;
import alec_wam.CrystalMod.world.ModDimensions;
import baubles.api.BaubleType;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.SkeletonType;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
    
    @SubscribeEvent
    public void entityUpdate(LivingUpdateEvent event){
    	if(event.getEntityLiving() !=null){
    		if(event.getEntityLiving() instanceof EntityPlayer){
    			EntityPlayer player = (EntityPlayer)event.getEntityLiving();
    			updateWings(player);
    		}
    	}
    }
    
    public static boolean hasDragonWings(EntityPlayer player){
    	ItemStack chest = player.inventory.armorItemInSlot(2);
		if(ItemStackTools.isValid(chest) && ItemNBTHelper.verifyExistance(chest, ItemDragonWings.UPGRADE_NBT)){
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
        return WINGED_PLAYERS.contains(player.getUniqueID()+(player.worldObj.isRemote ? "-Remote" : ""));
    }
    
    public static void removeWingsFromPlayer(EntityPlayer player){
        removeWingsFromPlayer(player, player.worldObj.isRemote);
    }
    
    public static void removeWingsFromPlayer(EntityPlayer player, boolean worldRemote){
        WINGED_PLAYERS.remove(player.getUniqueID()+(worldRemote ? "-Remote" : ""));
    }
    
    public static void addWingsToPlayer(EntityPlayer player){
        WINGED_PLAYERS.add(player.getUniqueID()+(player.worldObj.isRemote ? "-Remote" : ""));
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
                 //Allow the Player to fly when he has Wings equipped
                 player.capabilities.allowFlying = true;
                 
                 ExtendedPlayer extPlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
                 if(extPlayer !=null){
					//Copied from Ender Dragon
 					extPlayer.prevWingAnimTime = extPlayer.wingAnimTime;
 					float f10 = 0.2F * (MathHelper.sqrt_double(player.motionX * player.motionX + player.motionZ * player.motionZ) * 0.5F + 1.0F);
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
    	if(entity !=null && entity.worldObj !=null && !entity.worldObj.isRemote){
	    	if(EntityUtil.hasCustomData(entity)){
	    		CrystalModNetwork.sendToAllAround(new PacketEntityMessage(entity, "CustomDataSync"), entity);
	    	}
    	}
    }
    
    @SubscribeEvent
    public void onEntityLivingInteract(EntityInteract event)
    {
    	if(event.getTarget() == null)return;
    	
    	EntityPlayer player = event.getEntityPlayer();
    	ItemStack held = event.getItemStack();
    	Entity entity = event.getTarget();
        if(entity instanceof EntityHorse){
      	  EntityHorse horse = (EntityHorse)entity;
      	  if(HorseAccessories.handleHorseInteract(player, held, horse)){
      		  event.setCanceled(true);
      	  }
        }
    }
    
	@SubscribeEvent
    public void onEntityLivingDeath(LivingDeathEvent event)
    {
      if (event.getEntityLiving().worldObj.isRemote) {
        return;
      }
      EntityLivingBase entity = event.getEntityLiving();
      if(entity instanceof EntityHorse){
    	  EntityHorse horse = (EntityHorse)entity;
    	  HorseAccessories.onHorseDeath(horse);
      }
    }
	
	@SubscribeEvent
    public void onLivingDrop(LivingDropsEvent event)
    {
        if (event.getEntityLiving() == null || event.getEntityLiving().worldObj.isRemote)
            return;
        
        if(Util.notNullAndInstanceOf(event.getEntityLiving(), EntityDragon.class)){
        	ItemStack stack = new ItemStack(ModItems.wings);
        	EntityItem item = new EntityItem(event.getEntity().worldObj, event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, stack);
        	event.getDrops().add(item);
        }
        
        addMobHeads(event);
    }
	
	@SubscribeEvent
	public void playerDeath(PlayerDropsEvent event) {
		if (event.getEntity() instanceof EntityPlayer && !event.getEntity().worldObj.isRemote) {
			boolean keepInv = event.getEntity().worldObj.getGameRules().getBoolean("keepInventory");
			addPlayerHeads(event);
			if(!keepInv){
				ExtendedPlayer exPlayer = ExtendedPlayerProvider.getExtendedPlayer(event.getEntityPlayer()); 
				if(exPlayer !=null){
					ExtendedPlayerInventory inv = exPlayer.getInventory();
					
					ItemStack backpack = inv.getStackInSlot(ExtendedPlayerInventory.BACKPACK_SLOT_ID);
					
					boolean keepBackpack = false;
					if(ItemStackTools.isValid(backpack)){
						if(backpack.getItem() instanceof ItemBackpackBase){
							IBackpack type = ((ItemBackpackBase)backpack.getItem()).getBackpack();
							if(type instanceof BackpackNormal){
								BackpackNormal normal = (BackpackNormal)type;
								InventoryBackpack bpInv = normal.getInventory(event.getEntityPlayer(), backpack);
								ItemStack ret = ItemUtil.removeItems(bpInv, null, new ItemStack(Items.SKULL, 1, 1), 1);
								//If player has wither skulls remove one and allow them to keep the backpack
								//on death
								if(ItemStackTools.isValid(ret) && ItemStackTools.getStackSize(ret) == 1){
									keepBackpack = true;
									if(!event.getEntityPlayer().getEntityWorld().isRemote) {
										bpInv.save();
							    	}
								}
							}
						}
					}
					
					for (int i = 0; i < inv.getSlots(); ++i) {
						if (inv.getStackInSlot(i) != null) {
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
 	   	if(player == null || player.worldObj == null)return;
 	   
 	   	int rand = player.worldObj.rand.nextInt(Math.max(Config.playerHeadDropChance / fixLooting(event.getLootingLevel()), 1));
 	   
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
        int rand = mob.worldObj.rand.nextInt(Math.max(Config.mobHeadDropChance / fixLooting(event.getLootingLevel()), 1));
        // roll the dice
        if(!mob.worldObj.getGameRules().getBoolean("doMobLoot") || Config.mobHeadDropChance < 0 || rand != 0)
            return;

        Item skullItem = null;
        int skullId = -1;

        
        // skelly/witherskelly
        if (mob instanceof EntitySkeleton) {
            if(((EntitySkeleton) event.getEntityLiving()).func_189771_df() == SkeletonType.WITHER)return;
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
        EntityItem entityitemSkull = new EntityItem(mob.worldObj, mob.posX, mob.posY, mob.posZ, new ItemStack(skullItem, 1, skullId));
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
 					if(state.getBlock() == Blocks.WOOL && event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND) !=null && ItemUtil.itemStackMatchesOredict(event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND), "slimeball")){
 						
 						int waterCt = 0;
 						for(EnumFacing face : EnumFacing.VALUES){
 							IBlockState state2 = event.getWorld().getBlockState(pos.offset(face));
 							if(state2 !=null && state2.getBlock().getMaterial(state2) == Material.WATER){
 								waterCt++;
 							}
 						}
 						
 						if(waterCt >= 4){
 							event.getWorld().setBlockState(pos, Blocks.SPONGE.getDefaultState());
 							event.getEntityPlayer().swingArm(EnumHand.MAIN_HAND);
 							
 							if (!event.getWorld().isRemote)
 		                    {
 								event.getWorld().playEvent(2001, pos, Block.getStateId(state));
 		                    }
 							
 							if(!event.getEntityPlayer().capabilities.isCreativeMode)
 							event.getEntityPlayer().inventory.setInventorySlotContents(event.getEntityPlayer().inventory.currentItem, ItemUtil.consumeItem(event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND)));
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
			ExtendedPlayerInventory inventory = ePlayer.getInventory();
			String[] hashOld = syncCheck.get(player.getCachedUniqueIdString());
			
			boolean syncTick = player.ticksExisted % 10 == 0;
			
			for (int a = 0; a < inventory.getSlots(); a++) {
				ItemStack stack = inventory.getStackInSlot(a);
				/*if(!ItemStackTools.isNullStack(stack)){
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
				}*/
				
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
		if (isTeleportPrevented(event.getEntity().worldObj, event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, false)) {
			event.setCanceled(true);
		}
		if (isTeleportPrevented(event.getEntity().worldObj, event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
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
}
