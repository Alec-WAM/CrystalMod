package alec_wam.CrystalMod.tiles.pipes;

import java.util.Locale;

import alec_wam.CrystalMod.tiles.pipes.BlockPipe.PipeType;
import alec_wam.CrystalMod.tiles.pipes.power.cu.TileEntityPipePowerCU;
import alec_wam.CrystalMod.tiles.pipes.power.rf.TileEntityPipePowerRF;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemColored;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockPipe extends ItemColored {

	public ItemBlockPipe(Block block) {
		super(block, true);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		@SuppressWarnings("deprecation")
		IBlockState state = block.getStateFromMeta(stack.getMetadata());
		PipeType type = state.getValue(BlockPipe.TYPE);
		String name = type.toString().toLowerCase(Locale.US);
		if(ItemNBTHelper.verifyExistance(stack, "Tier")){
			name+=".tier"+ItemNBTHelper.getInteger(stack, "Tier", 0);
		}
	    return super.getUnlocalizedName(stack) + "." + name;
	}
	
	@Override
	  public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ,
	      IBlockState newState) {
	    if(!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
	      return false;
	    }
	    TileEntity te = world.getTileEntity(pos);
	    if(te instanceof TileEntityPipePowerCU) {
	      TileEntityPipePowerCU tePipe = (TileEntityPipePowerCU) te;
	      tePipe.setSubType(ItemNBTHelper.getInteger(stack, "Tier", 0));
	      tePipe.onAdded();
	      if(!world.isRemote) {
	    	 BlockUtil.markBlockForUpdate(world, pos);
	      }
	    }
	    if(te instanceof TileEntityPipePowerRF) {
	      TileEntityPipePowerRF tePipe = (TileEntityPipePowerRF) te;
	      tePipe.setSubType(ItemNBTHelper.getInteger(stack, "Tier", 0));
	      tePipe.onAdded();
	      if(!world.isRemote) {
	    	  BlockUtil.markBlockForUpdate(world, pos);
	      }
	    }
	    return true;
	  }
	
}
