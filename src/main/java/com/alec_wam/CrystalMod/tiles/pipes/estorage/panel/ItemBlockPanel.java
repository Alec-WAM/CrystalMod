package com.alec_wam.CrystalMod.tiles.pipes.estorage.panel;

import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemBlockPanel extends ItemBlock {

  public ItemBlockPanel(Block b) {
    super(b);
    this.setHasSubtypes(true);
    this.setMaxDamage(0);
  }
  
  public int getMetadata(int damage)
  {
      return damage;
  }

  @Override
  public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ,
      IBlockState newState) {
    if(!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
      return false;
    }
    TileEntity te = world.getTileEntity(pos);
    if(te instanceof TileEntityPanel) {
      TileEntityPanel teInvPanel = (TileEntityPanel) te;
      teInvPanel.facing = (side);
      if(!world.isRemote) {
        world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock());
      }
    }
    return true;
  }
  
  public String getUnlocalizedName(ItemStack stack)
  {
	  @SuppressWarnings("deprecation")
	  IBlockState state = block.getStateFromMeta(stack.getMetadata());
	  String name = state.getValue(BlockPanel.PANEL_TYPE).toString().toLowerCase(Locale.US);
	  return super.getUnlocalizedName(stack) + "." + name;
  }

}
