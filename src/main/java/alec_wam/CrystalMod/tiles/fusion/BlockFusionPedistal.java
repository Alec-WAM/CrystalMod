package alec_wam.CrystalMod.tiles.fusion;

import alec_wam.CrystalMod.util.ToolUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockFusionPedistal extends BlockPedistal {

	public BlockFusionPedistal(Properties builder) {
		super(builder);
	}
	
	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileEntityFusionPedistal){
			TileEntityFusionPedistal pedistal = (TileEntityFusionPedistal)tile;
			if(player.isSneaking() && ToolUtil.isHoldingWrench(player, hand)){
				pedistal.startCrafting(player);
				return true;
			}
		}		
		return super.onBlockActivated(state, world, pos, player, hand, side, hitX, hitY, hitZ);
    }
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new TileEntityFusionPedistal();
	}

}
