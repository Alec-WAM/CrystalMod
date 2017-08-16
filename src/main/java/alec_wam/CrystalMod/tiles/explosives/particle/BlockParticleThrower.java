package alec_wam.CrystalMod.tiles.explosives.particle;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.util.ItemStackTools;
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

public class BlockParticleThrower extends BlockContainer {

	public BlockParticleThrower() {
		super(Material.IRON);
		this.setHardness(2.0F);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		if(!player.isSneaking() && ItemStackTools.isEmpty(player.getHeldItem(hand))){
			TileParticleThrower.throwBlocks(world, pos, 8);
			world.setBlockToAir(pos);
			return true;
		}
        return false;
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileParticleThrower();
	}

}
