package alec_wam.CrystalMod.tiles.machine.power.engine;

import java.util.List;
import java.util.Locale;

import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockEngine extends ItemBlock {

  public ItemBlockEngine(Block b) {
    super(b);
    this.setMaxDamage(0);
    this.setHasSubtypes(true);
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
    if(te instanceof TileEntityEngineBase) {
      TileEntityEngineBase teInvPanel = (TileEntityEngineBase) te;
      teInvPanel.updateMulti(BlockEngine.tierMulti[ItemNBTHelper.getInteger(stack, "Tier", 0)]);
      BlockUtil.markBlockForUpdate(world, pos);
    }
    return true;
  }
  
  @Override
@SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
  {
      super.addInformation(stack, playerIn, tooltip, advanced);
      int multi = BlockEngine.tierMulti[ItemNBTHelper.getInteger(stack, "Tier", 0)];
      if(multi > 1){
    	  tooltip.add("Multiplier = "+multi);
      }
  }
  
  @Override
  public String getUnlocalizedName(ItemStack stack) {
	@SuppressWarnings("deprecation")
	IBlockState state = block.getStateFromMeta(stack.getMetadata());
	String name = state.getValue(BlockEngine.ENGINE_TYPE).toString().toLowerCase(Locale.US);
    return super.getUnlocalizedName(stack) + "." + name;
  }

}
