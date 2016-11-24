package alec_wam.CrystalMod.tiles.weather;


import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import alec_wam.CrystalMod.CrystalMod;

public class BlockWeather extends BlockContainer {

	public BlockWeather() {
		super(Material.IRON);
		this.setHardness(1f);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {
    	if(!player.isSneaking()){
    		player.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
    		return true;
    	}
    	return false;
    }
	
	public static MoonPhase getMoonPhaseByInt(final int I) {
        return MoonPhase.values()[I];
    }
    
    public MoonPhase getMoonPhase(final World world) {
        final long T = world.getWorldTime();
        final long D = T / 24000L;
        final int days = (int)D;
        final int phaseInt = days % 8;
        return getMoonPhaseByInt(phaseInt);
    }

    public EnumBlockRenderType getRenderType(IBlockState state){
    	return EnumBlockRenderType.MODEL;
    }
    
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityWeather();
	}

}
