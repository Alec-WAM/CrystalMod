package alec_wam.CrystalMod.tiles.shieldrack;

import alec_wam.CrystalMod.blocks.ItemBlockMeta;
import alec_wam.CrystalMod.tiles.machine.IFacingTile;
import alec_wam.CrystalMod.util.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockShieldRack extends ItemBlockMeta {

  public ItemBlockShieldRack(Block b) {
    super(b);
  }

  @Override
  public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
    if(!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
      return false;
    }
    if(side.getAxis() == Axis.Y)return false;
    
    TileEntity te = world.getTileEntity(pos);
    if(te instanceof IFacingTile) {
    	IFacingTile tile = (IFacingTile) te;
    	EnumFacing face = side;
    	tile.setFacing(face.getHorizontalIndex());
    	BlockUtil.markBlockForUpdate(world, pos);
    }
    return true;
  }

}
