package alec_wam.CrystalMod.tiles.pipes.estorage.stocker;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.estorage.security.NetworkAbility;
import alec_wam.CrystalMod.handler.GuiHandler;
import alec_wam.CrystalMod.tiles.machine.BlockMachine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockStocker extends BlockMachine {

	public BlockStocker() {
		super(Material.IRON);
		setHardness(0.5F);
		setCreativeTab(CrystalMod.tabBlocks);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side,float hX, float hY, float hZ){
		if(!player.isSneaking()){
			if(!world.isRemote)GuiHandler.openNetworkGui(world, pos, player, NetworkAbility.VIEW);
			return true;
		}
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityStocker();
	}

}
