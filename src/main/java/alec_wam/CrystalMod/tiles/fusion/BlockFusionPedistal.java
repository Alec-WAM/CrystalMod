package alec_wam.CrystalMod.tiles.fusion;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockFusionPedistal extends BlockPedistal {

	public BlockFusionPedistal() {
		super(Material.IRON);
		this.setHardness(2f).setResistance(20F);
		this.setHarvestLevel("pickaxe", 0);
		this.setCreativeTab(CrystalMod.tabBlocks);
		this.setSoundType(SoundType.METAL);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile !=null && tile instanceof TileFusionPedistal){
			TileFusionPedistal pedistal = (TileFusionPedistal)tile;
			if(playerIn.isSneaking() && ToolUtil.isToolEquipped(playerIn, hand)){
				pedistal.startCrafting(playerIn);
				return true;
			}
		}		
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ);
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileFusionPedistal();
	}
	
}
