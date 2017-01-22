package alec_wam.CrystalMod.util.fakeplayer;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class FakePlayerCM extends FakePlayer {
	
  public FakePlayerCM(World world) {
	  this(world, FakePlayerUtil.CRYSTALMOD);
  }
  
  private final ItemStack[] cachedHandInventory;
  private final ItemStack[] cachedArmorArray;
  
  public FakePlayerCM(World world, GameProfile profile) {
    super(FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(world.provider.getDimension()), profile);
    // ItemInWorldManager will access this field directly and can crash
    connection = new FakeNetHandlerPlayServer(this);
    this.setSize(0.0f, 0.0f);
    this.capabilities.disableDamage = true;
    cachedHandInventory = new ItemStack[2];
    cachedArmorArray = new ItemStack[4];
  }

  // These do things with packets...which crash since the net handler is null. Potion effects are not needed anyways.
  @Override
  protected void onNewPotionEffect(PotionEffect p_70670_1_) {
  }

  @Override
  protected void onChangedPotionEffect(PotionEffect p_70695_1_, boolean p_70695_2_) {
  }

  @Override
  protected void onFinishedPotionEffect(PotionEffect p_70688_1_) {
  }
  
  @Override
  protected void playEquipSound(@Nullable ItemStack stack) {  
  }
  
//  @Override
//  public boolean canPlayerEdit(BlockPos p_175151_1_, EnumFacing p_175151_2_, @Nullable ItemStack p_175151_3_) {
//    return true;
//  }

  
  public void setLocationSide(final BlockPos offset, final EnumFacing side) {
      final double r = 0.2;
      final double x = offset.getX() + 0.5 - side.getFrontOffsetX() * r;
      final double y = offset.getY() + 0.5 - side.getFrontOffsetY() * r;
      final double z = offset.getZ() + 0.5 - side.getFrontOffsetZ() * r;
      int pitch = 0;
      int yaw = 0;
      switch (side) {
          case DOWN: {
              pitch = 90;
              yaw = 0;
              break;
          }
          case UP: {
              pitch = -90;
              yaw = 0;
              break;
          }
          case NORTH: {
              yaw = 180;
              pitch = 0;
              break;
          }
          case SOUTH: {
              yaw = 0;
              pitch = 0;
              break;
          }
          case WEST: {
              yaw = 90;
              pitch = 0;
              break;
          }
          case EAST: {
              yaw = 270;
              pitch = 0;
              break;
          }
          default: {
              throw new RuntimeException("Invalid Side (" + side + ")");
          }
      }
      this.setLocationAndAngles(x, y, z, (float)yaw, (float)pitch);
  }
  
  public float getEyeHeight() {
      return 0.0f;
  }
  
  public Vec3d getVectorForRotationPublic(float pitch, float yaw){
	  return super.getVectorForRotation(pitch, yaw);
  }

  public void updateCooldown() {
      this.ticksSinceLastSwing = 20090;
  }
  
  public void updateAttributes() {
      for (final EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values()) {
    	  ItemStack itemstack = null;
    	  switch (entityequipmentslot.getSlotType()) {
	    	  case HAND: {
	    		  itemstack = this.cachedHandInventory[entityequipmentslot.getIndex()];
	    		  break;
	    	  }
	    	  case ARMOR: {
	    		  itemstack = this.cachedArmorArray[entityequipmentslot.getIndex()];
	    		  break;
	    	  }
	    	  default: {
	    		  break;
	    	  }
    	  }
    	  final ItemStack newStack = this.getItemStackFromSlot(entityequipmentslot);
    	  if (!ItemStack.areItemStacksEqual(newStack, itemstack)) {
    		  if (itemstack != null) {
    			  this.getAttributeMap().removeAttributeModifiers(itemstack.getAttributeModifiers(entityequipmentslot));
    		  }
    		  if (newStack != null) {
    			  this.getAttributeMap().applyAttributeModifiers(newStack.getAttributeModifiers(entityequipmentslot));
    		  }
    		  switch (entityequipmentslot.getSlotType()) {
	    		  case HAND: {
	    			  this.cachedHandInventory[entityequipmentslot.getIndex()] = ((newStack == null) ? null : newStack.copy());
	    			  break;
	    		  }
	    		  case ARMOR: {
	    			  this.cachedArmorArray[entityequipmentslot.getIndex()] = ((newStack == null) ? null : newStack.copy());
	    			  break;
	    		  }
    		  }
    	  }
      }
  }
  
}
