package alec_wam.CrystalMod.tiles.pipes.estorage.panel;

import java.util.Locale;

import alec_wam.CrystalMod.util.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockPanel extends ItemBlock {

  public ItemBlockPanel(Block b) {
    super(b);
    this.setHasSubtypes(true);
    this.setMaxDamage(0);
  }
  
  @Override
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
        BlockUtil.markBlockForUpdate(world, pos, true);
      }
    }
    return true;
  }
  
  @Override
public String getUnlocalizedName(ItemStack stack)
  {
	  @SuppressWarnings("deprecation")
	  IBlockState state = block.getStateFromMeta(stack.getMetadata());
	  String name = state.getValue(BlockPanel.PANEL_TYPE).toString().toLowerCase(Locale.US);
	  return super.getUnlocalizedName(stack) + "." + name;
  }

}
