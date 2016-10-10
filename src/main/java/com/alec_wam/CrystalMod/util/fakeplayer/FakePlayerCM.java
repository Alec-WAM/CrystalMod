package com.alec_wam.CrystalMod.util.fakeplayer;

import javax.annotation.Nullable;

import com.enderio.core.common.util.BlockCoord;
import com.mojang.authlib.GameProfile;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class FakePlayerCM extends FakePlayer {

  public FakePlayerCM(World world, GameProfile profile) {
    super(FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(world.provider.getDimension()), profile);
    // ItemInWorldManager will access this field directly and can crash
    connection = new FakeNetHandlerPlayServer(this);
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

}
