package alec_wam.CrystalMod.tiles.soundmuffler;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSoundMuffler extends BlockContainer {

	public BlockSoundMuffler() {
		super(Material.IRON);
		this.setHardness(2F);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		if(!playerIn.isSneaking()){
			playerIn.openGui(CrystalMod.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
        return false;
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileSoundMuffler();
	}

}
