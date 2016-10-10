package com.alec_wam.CrystalMod.handler;

import java.util.Iterator;

import com.alec_wam.CrystalMod.Config;
import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.api.block.IExplosionImmune;
import com.alec_wam.CrystalMod.capability.ExtendedPlayer;
import com.alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import com.alec_wam.CrystalMod.entities.minions.warrior.EntityMinionWarrior;
import com.alec_wam.CrystalMod.items.ItemDragonWings;
import com.alec_wam.CrystalMod.items.ModItems;
import com.alec_wam.CrystalMod.tiles.playercube.CubeManager;
import com.alec_wam.CrystalMod.tiles.playercube.PlayerCube;
import com.alec_wam.CrystalMod.tiles.playercube.TileEntityPlayerCubePortal;
import com.alec_wam.CrystalMod.util.ItemNBTHelper;
import com.alec_wam.CrystalMod.util.ItemUtil;
import com.alec_wam.CrystalMod.util.ModLogger;
import com.alec_wam.CrystalMod.util.PlayerUtil;
import com.alec_wam.CrystalMod.util.Util;
import com.alec_wam.CrystalMod.world.ModDimensions;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.SkeletonType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
    
    public void updateWings(EntityPlayer player){
    	NBTTagCompound nbt = PlayerUtil.getPersistantNBT(player);
    	ItemStack chest = player.inventory.armorItemInSlot(2);
		if(chest != null && ItemNBTHelper.verifyExistance(chest, ItemDragonWings.UPGRADE_NBT)){
			nbt.setByte(NBT_WINGS, (byte)2);
			
			ExtendedPlayer extPlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
			if(extPlayer !=null){
				//Copied from Ender Dragon
				extPlayer.prevWingAnimTime = extPlayer.wingAnimTime;
				float f10 = 0.2F / (MathHelper.sqrt_double(player.motionX * player.motionX + player.motionZ * player.motionZ) * 10.0F + 1.0F);
	            f10 = f10 * (float)Math.pow(2.0D, player.motionY);

	            
	            if(player.capabilities.isFlying){
	            	extPlayer.wingAnimTime+= f10*0.3f;
	            }else if(!player.onGround){
	               extPlayer.wingAnimTime += f10*0.4f;
	            }else{
	            	extPlayer.wingAnimTime=1f;
	            }
			}
		} else {
			if(nbt.hasKey(NBT_WINGS)){
				if(nbt.getByte(NBT_WINGS) !=(byte)1){
					nbt.setByte(NBT_WINGS, (byte)0);
				}
			} else nbt.setByte(NBT_WINGS, (byte)0);
		}
		
		if(nbt.hasKey(NBT_WINGS)){
			//MODE 2 = ON 1 = NORMAL 0 = NEEDS REMOVE
			byte mode = nbt.getByte(NBT_WINGS);
			if(mode == 2){
				if(!player.worldObj.isRemote){
					if(player instanceof EntityPlayerMP){
						if(!player.capabilities.allowFlying){
							player.capabilities.allowFlying = true;
							player.sendPlayerAbilities();
						}
					}
				}
			}
			if(mode == 0){
				if(!player.worldObj.isRemote){
					if(player instanceof EntityPlayerMP){
						if(!player.capabilities.isCreativeMode){
							ModLogger.info("Removed Wing Flight From "+player.getName());
							player.capabilities.allowFlying = false;
							player.capabilities.isFlying = false;
							player.sendPlayerAbilities();
						}
					}
				}
				nbt.setByte(NBT_WINGS, (byte)1);
			}
		}
    }
    
	@SubscribeEvent
    public void onEntityLivingDeath(LivingDeathEvent event)
    {
      if (event.getEntityLiving().worldObj.isRemote) {
        return;
      }
      addPlayerHeads(event);
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
	
	public void addPlayerHeads(LivingDeathEvent event){
 	   if(Config.playerHeadType == ItemDropType.NONE)return;
 	   Entity mob = event.getEntity();
 	   if(mob == null || mob.worldObj == null)return;
 	   if(!(mob instanceof EntityPlayer))return;
 	   
 	   Entity entity = event.getSource().getEntity();
 	   
 	   int rand = event.getEntityLiving().getRNG().nextInt(Math.max(Config.playerHeadDropChance, 1));
 	   
 	   if(Config.playerHeadDropChance < 0 || rand !=0)
            return;
 	   if(Config.playerHeadType == ItemDropType.KILLED){
     	   if(entity == null || !(entity instanceof EntityPlayer) || entity instanceof FakePlayer)
                return;
 	   }
		   // how much beheading chance do we have?
        EntityPlayer player = (EntityPlayer) mob;
        ItemStack skull = PlayerUtil.createPlayerHead(player);
        event.getEntityLiving().entityDropItem(skull, 1);
        
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
 			BlockPos pos = Util.getEntityLookedBlock(event.getEntityPlayer(), (float)CrystalMod.proxy.getReachDistanceForPlayer(event.getEntityPlayer()));
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
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
    public void onPlayerClone(PlayerEvent.Clone event) {
        EntityPlayer oldPlayer = event.getOriginal();
        EntityPlayer newPlayer = event.getEntityPlayer();
        if(!oldPlayer.getEntityWorld().isRemote) {
            ExtendedPlayer oldProps = ExtendedPlayerProvider.getExtendedPlayer(oldPlayer);
            ExtendedPlayer newProps = ExtendedPlayerProvider.getExtendedPlayer(newPlayer);
            if(oldPlayer != null && newProps != null) {
                newProps.readFromNBT(oldProps.writeToNBT());
            }
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
	
}
