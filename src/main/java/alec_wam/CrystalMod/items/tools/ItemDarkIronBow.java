package alec_wam.CrystalMod.items.tools;

import javax.annotation.Nullable;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.grapple.EntityGrapplingHook;
import alec_wam.CrystalMod.items.tools.grapple.GrappleHandler;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;

public class ItemDarkIronBow extends ItemBow implements ICustomModel {

	public ItemDarkIronBow(){
		this.maxStackSize = 1;
        this.setMaxDamage(384*2);
		this.setCreativeTab(CrystalMod.tabTools);
		ModItems.registerItem(this, "darkironbow");
		
		addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
	      @Override
	      @SideOnly(Side.CLIENT)
	      public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
	        return updatePullProperty(stack, worldIn, entityIn);
	      }
	    });
	    addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter() {
	      @Override
	      @SideOnly(Side.CLIENT)
	      public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
	        return updatePullingProperty(stack, entityIn);
	      }
	    });
	    MinecraftForge.EVENT_BUS.register(this);
	}
	
	private float updatePullProperty(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
	    if (stack == null || stack.getItem().getClass() != getClass()) {
	      return 0;
	    }
	    float res = (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / (float) getDrawTime(stack);
	    return res;
	}

	private float updatePullingProperty(ItemStack stack, EntityLivingBase entityIn) {
	    float res = entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
	    return res;
	}

    /**
     * How long it takes to use or consume an item
     */
	@Override
	public int getMaxItemUseDuration(ItemStack stack)
    {
        return 72000;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
	@Override
	public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.BOW;
    }
    
    @Override
    public int getItemEnchantability() {
      return 1;
    }
    
    private float damageBonus = 0.0f;
    private float forceMultiplier = 1.5f;
    private float fovMultiplier = 0.35f;
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
    	return super.onItemRightClick(worldIn, playerIn, hand);
    }
    
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {

      if (!(entityLiving instanceof EntityPlayer)) {
        return;
      }
      
      
      
      EntityPlayer entityplayer = (EntityPlayer) entityLiving;
      
      boolean grapple = false;
      
      if(ItemStackTools.isValid(entityplayer.getHeldItemOffhand())){
    	  ItemStack off = entityplayer.getHeldItemOffhand();
    	  if(off.getItem() == Items.LEAD){
    		  grapple = true;
    	  }
      }
      boolean hasInfinateArrows = entityplayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
      ItemStack itemstack = getArrowsToShoot(entityplayer);
      int draw = getMaxItemUseDuration(stack) - timeLeft;
      draw = ForgeEventFactory.onArrowLoose(stack, worldIn, (EntityPlayer) entityLiving, draw, itemstack != null || hasInfinateArrows);
      
      if (draw < 0) {
    	 return;
      }

      if (itemstack == null && hasInfinateArrows) {
        itemstack = new ItemStack(Items.ARROW);
      }

      if (itemstack == null) {
        return;
      }

      float drawRatio = getCustumArrowVelocity(stack, draw);
      if(drawRatio < 0.1){
    	  if (!worldIn.isRemote) {
    		  EntityGrapplingHook entityhook = GrappleHandler.getHook(entityplayer, worldIn);
    		  if (entityhook != null) {
            		int id = entityhook.shootingEntityID;
            		if (!GrappleHandler.attached.contains(id)) {
            			GrappleHandler.setHook(entityplayer, null);
            			
            			if (!entityhook.isDead) {
            				entityhook.removeServer();
            				return;
            			}
            			
            			entityhook = null;
            		}
            		if (entityhook != null) {
	            		Entity shooter = worldIn.getEntityByID(entityhook.shootingEntityID);
	      				if(shooter !=null && shooter instanceof EntityPlayerMP){
	      					CrystalModNetwork.sendTo(new PacketEntityMessage(shooter, "GrappleUnattach"), (EntityPlayerMP)shooter);
	      				}
	      				GrappleHandler.attached.remove(new Integer(entityhook.shootingEntityID));
	      				GrappleHandler.setHook(entityplayer, null);
	      				entityhook = null;
	      				return;
            		}
            	}
    	  }
      }
      if (drawRatio >= 0.1) {
        boolean arrowIsInfinite = hasInfinateArrows && itemstack.getItem() instanceof ItemArrow;
        if (!worldIn.isRemote) {
          ItemArrow itemarrow = ((ItemArrow) (itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW));
          
          if(grapple){
    	    EntityGrapplingHook entityhook = GrappleHandler.getHook(entityplayer, worldIn);
          	
          	if (entityhook != null) {
          		int id = entityhook.shootingEntityID;
          		if (!GrappleHandler.attached.contains(id)) {
          			GrappleHandler.setHook(entityplayer, null);
          			
          			if (!entityhook.isDead) {
          				entityhook.removeServer();
          				return;
          			}
          			
          			entityhook = null;
          		}
          	}
          	
  			float f = 2.0F;
  			
  			if(entityhook !=null){
  				Entity shooter = worldIn.getEntityByID(entityhook.shootingEntityID);
  				if(shooter !=null && shooter instanceof EntityPlayerMP){
  					CrystalModNetwork.sendTo(new PacketEntityMessage(shooter, "GrappleUnattach"), (EntityPlayerMP)shooter);
  				}
  				GrappleHandler.attached.remove(new Integer(entityhook.shootingEntityID));
  				GrappleHandler.setHook(entityplayer, null);
  				entityhook = null;
  			}
  			
  			if (entityhook == null) {
  				entityhook = new EntityGrapplingHook(worldIn, entityplayer, entityplayer.getActiveHand());
  				entityhook.setHeadingFromThrower(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, drawRatio * entityhook.getVelocity(), 0.0F);
  				GrappleHandler.setHook(entityplayer, entityhook);
  				worldIn.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
  				
  				worldIn.spawnEntity(entityhook);
  			} else {
  				Entity shooter = worldIn.getEntityByID(entityhook.shootingEntityID);
  				if(shooter !=null && shooter instanceof EntityPlayerMP){
  					CrystalModNetwork.sendTo(new PacketEntityMessage(shooter, "GrappleUnattach"), (EntityPlayerMP)shooter);
  				}
  				GrappleHandler.attached.remove(new Integer(entityhook.shootingEntityID));
  				GrappleHandler.setHook(entityplayer, null);
  			}
          } else {
	          EntityArrow entityarrow = itemarrow.createArrow(worldIn, itemstack, entityplayer);
	          entityarrow.setAim(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, drawRatio * (3.0F + forceMultiplier), 0.25F);
	
	          if (drawRatio == 1.0F) {
	            entityarrow.setIsCritical(true);
	          }
	          int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
	          if (powerLevel > 0) {
	            entityarrow.setDamage(entityarrow.getDamage() + powerLevel * 0.5D + 0.5D);
	          }
	          int knockBack = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
	          if (knockBack > 0) {
	            entityarrow.setKnockbackStrength(knockBack);
	          }
	          if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
	            entityarrow.setFire(100);
	          }
	
	          stack.damageItem(1, entityplayer);
	
	          if (arrowIsInfinite) {
	            entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
	          }
	
	          entityarrow.setDamage(entityarrow.getDamage() + damageBonus);
	
	          worldIn.spawnEntity(entityarrow);
          }
        }

        worldIn.playSound((EntityPlayer) null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL,
            1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + drawRatio * 0.5F);

        if (!arrowIsInfinite) {
          ItemStackTools.incStackSize(itemstack, -1);
          if (ItemStackTools.isEmpty(itemstack)) {
            entityplayer.inventory.deleteStack(itemstack);
          }
        }
        entityplayer.addStat(StatList.getObjectUseStats(this));
      }

    }

    public int getDrawTime(ItemStack stack) {
    	return 30;
    }
    
    public float getCustumArrowVelocity(ItemStack stack, int charge) {
        float f = charge / (float) getDrawTime(stack);
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
          f = 1.0F;
        }

        return f;
    }

    private ItemStack getArrowsToShoot(EntityPlayer player) {
        if (isArrow(player.getHeldItem(EnumHand.OFF_HAND))) {
          return player.getHeldItem(EnumHand.OFF_HAND);
        } else if (isArrow(player.getHeldItem(EnumHand.MAIN_HAND))) {
          return player.getHeldItem(EnumHand.MAIN_HAND);
        } else {
          for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = player.inventory.getStackInSlot(i);
            if (isArrow(itemstack)) {
              return itemstack;
            }
          }
          return null;
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onFovUpdateEvent(FOVUpdateEvent fovEvt) {
      ItemStack currentItem = fovEvt.getEntity().getHeldItemMainhand();
      if (ItemStackTools.isNullStack(currentItem) || currentItem.getItem() != this || fovEvt.getEntity().getItemInUseCount() <= 0) {
        return;
      }

      int drawDuration = getMaxItemUseDuration(currentItem) - fovEvt.getEntity().getItemInUseCount();
      float ratio = drawDuration / (float) getDrawTime(currentItem);

      if (ratio > 1.0F) {
        ratio = 1.0F;
      } else {
        ratio *= ratio;
      }
      fovEvt.setNewfov((1.0F - ratio * fovMultiplier));

    }
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
		ModItems.initBasicModel(this);
    }
	
}
