package alec_wam.CrystalMod.tiles.machine.xpfountain;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.tiles.machine.BlockMachine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockXPFountain extends BlockMachine {

	public BlockXPFountain() {
		super(Material.IRON);
		setHardness(1.5F);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hX, float hY, float hZ){
		TileEntity tile = world.getTileEntity(pos);
		if(player.isSneaking()) return false;
		if(tile !=null && tile instanceof TileEntityXPFountain){
			if(!world.isRemote){
				player.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
			}
			return true;
		}
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityXPFountain();
	}
}
