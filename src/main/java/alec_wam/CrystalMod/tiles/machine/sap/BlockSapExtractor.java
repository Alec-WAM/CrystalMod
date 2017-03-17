package alec_wam.CrystalMod.tiles.machine.sap;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.tiles.machine.BlockMachine;
import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSapExtractor extends BlockMachine {

	public BlockSapExtractor() {
		super(Material.IRON);
		this.setHardness(1f).setResistance(10F);
		this.setCreativeTab(CrystalMod.tabCrops);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hX, float hY, float hZ){
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileEntityMachine){
			if(!world.isRemote){
				player.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
			}
			return true;
		}
		return false;
	}
	
	@Override
    public void breakBlock(World world, BlockPos pos, IBlockState blockState)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof IInventory)
        {
            ItemUtil.dropContent(0, (IInventory)tile, world, tile.getPos());
        }
        super.breakBlock(world, pos, blockState);
    }
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile !=null && tile instanceof TileSapExtractor){
			TileSapExtractor extractor = (TileSapExtractor)tile;
			extractor.updateTreeInfo();
		}
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileSapExtractor();
	}

}
