package com.alec_wam.CrystalMod.tiles.machine.worksite;

import java.util.Locale;

import com.alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.IRotatableTile;
import com.alec_wam.CrystalMod.util.BlockUtil;
import com.alec_wam.CrystalMod.util.Util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemBlockWorksite extends ItemBlock {

  public ItemBlockWorksite(Block b) {
    super(b);
    this.setHasSubtypes(true);
    this.setMaxDamage(0);
  }
  
  public int getMetadata(int damage)
  {
      return damage;
  }

  @Override
  public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
    if(!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
      return false;
    }
    EnumFacing face = player.getHorizontalFacing().getOpposite();
    BlockPos pos1 = BlockUtil.moveForward(pos, face, 1);
    BlockPos pos2 = BlockUtil.moveForward(pos, face, 4);
    pos1 = BlockUtil.moveLeft(pos1, face, 2);
    pos2 = BlockUtil.moveRight(pos1, face, 4); 
    BlockPos min = BlockUtil.getMin(pos1, pos2);
    BlockPos max = BlockUtil.getMax(pos1, pos2);
    TileEntity te = world.getTileEntity(pos);
    if(te instanceof IWorkSite)
    {
    	((IWorkSite)te).setBounds(min, max);
    }
    if(te instanceof IRotatableTile)
    {
    	((IRotatableTile) te).setPrimaryFacing(face);
    }
    BlockUtil.markBlockForUpdate(world, pos);
    return true;
  }
  
  public String getUnlocalizedName(ItemStack stack)
  {
	  @SuppressWarnings("deprecation")
	  IBlockState state = block.getStateFromMeta(stack.getMetadata());
	  String name = state.getValue(BlockWorksite.WORKSITE_TYPE).toString().toLowerCase(Locale.US);
	  return super.getUnlocalizedName(stack) + "." + name;
  }

}
